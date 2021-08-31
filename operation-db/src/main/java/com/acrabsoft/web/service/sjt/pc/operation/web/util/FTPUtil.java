package com.acrabsoft.web.service.sjt.pc.operation.web.util;


import io.swagger.models.auth.In;
import org.apache.commons.net.ftp.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

@Service("fTPUtil")
@Scope("prototype")
@PropertySource({"classpath:application.properties"})
public class FTPUtil {
    private static Logger logger = Logger.getLogger( FTPUtil.class );

    private FTPClient ftpClient;

    @Value("${ftp.ip}")
    private String ftpIp;
    @Value("${ftp.port}")
    private  Integer ftpPort;
    @Value("${ftp.username}")
    private  String ftpUsername;
    @Value("${ftp.password}")
    private  String ftpPassword;
    @Value("${zip.encode}")
    private  String zipEncode;
    @Value("${ftp.read.count}")
    private Integer ftpReadCount;

    private static String SERVER_CHARSET = "ISO-8859-1";
    /** 本地字符编码 */
    private static String LOCAL_CHARSET = "GBK";

    /**
     * @description  验证登录
     * @return  返回结果
     * @date  20/08/17 10:58
     * @author  wanghb
     * @edit
     */
    public  boolean login() throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(ftpIp, ftpPort);
        boolean isLogin = ftpClient.login( ftpUsername, ftpPassword );
        if(!isLogin){
            return false;
        }
        // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
        if (FTPReply.isPositiveCompletion( ftpClient.sendCommand("OPTS UTF8", "ON"))) {
            LOCAL_CHARSET = "UTF-8";
        }
        ftpClient.setControlEncoding(LOCAL_CHARSET);
        //ftp设置为被动上传
        ftpClient.enterLocalPassiveMode();
        return true;
    }


    /**
     * @description  向FTP服务器上传文件
     * @param  path  需要上传的路径
     * @param  file  需要上传的文件
     * @return  返回结果
     * @date  20/08/17 16:56
     * @author  wanghb
     * @edit
     */
    public boolean uploadFile(String path,File file) throws IOException{
        FileInputStream fileInputStream = null;
        try {
            //logger.info( "=================>登陆ftp");
            if(!login()){
                return false;
            }
            //logger.info( "=================>登陆成功");
            //设置上传文件的类型为二进制类型
            ftpClient.setFileType( FTP.BINARY_FILE_TYPE);
            String[] paths = path.split("/");
            for (int i = 0; i < paths.length; i++){
                String pathTemp = paths[i];
                ftpClient.makeDirectory(pathTemp);
                ftpClient.changeWorkingDirectory(pathTemp);
            }
            String fileName = new String(file.getName().getBytes(LOCAL_CHARSET), SERVER_CHARSET );
            fileName = fileName.substring(0, fileName.indexOf("_")) + fileName.substring(fileName.indexOf("."));
            fileInputStream = new FileInputStream( file );
            if(fileName == null){
                //logger.info("======>压缩文件名为空");
            }
            if(fileInputStream == null){
                //logger.info("======>压缩文件fileInputStream为空");
            }
            //logger.info( "=================>准备就绪");
            Boolean isSuccess = ftpClient.storeFile(fileName, fileInputStream);
            //logger.info( "=================>完成");
            return isSuccess;
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            closeClient();
        }
    }


    /**
     * @description  向FTP服务器下载文件
     * @param  fpath  文件路径
     * @param  localpath  本地路径
     * @return  返回结果
     * @date  20/08/17 17:01
     * @author  wanghb
     * @edit
     */
    public boolean downloadFileList(String fpath,String localpath) throws IOException{
        if(!login()){
            return false;
        }
        boolean flag = false;
        BufferedOutputStream bufferRead = null;
        if(fpath.startsWith("/") && fpath.endsWith("/")){
            try {
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //切换到当前目录
                ftpClient.changeWorkingDirectory(fpath);
                ftpClient.enterLocalActiveMode();
                FTPFile[] ftpFiles = ftpClient.listFiles();
                for (FTPFile file : ftpFiles) {
                    if (file.isFile()) {
                        String fileName = new String(file.getName().getBytes(LOCAL_CHARSET),SERVER_CHARSET);
                        File localFile = new File(localpath + "/" + file.getName());
                        FileOutputStream fileOutputStream = new FileOutputStream( localFile );
                        bufferRead = new BufferedOutputStream(fileOutputStream);
                        ftpClient.retrieveFile(fileName, bufferRead);
                        bufferRead.flush();
                        fileOutputStream.close();
                    }
                }
                ftpClient.logout();
                flag = true;
            } finally{
                closeClient();
            }
        }
        return flag;
    }

    /**
     * @description  获取数据
     * @param  fpath  文件路径
     * @return  返回结果
     * @date  20/08/17 17:01
     * @author  wanghb
     * @edit
     */
    public Map<String, List<Map<String, Object>>>  getDateList(String fpath,String fileType) throws  Exception {
        //键 : 文件名  值 : 读取到的文件数据
        Map<String,List<Map<String, Object>>> ftpData = new HashMap<>();
        if(!login()){
            return ftpData;
        }
        try {
            if(fpath.startsWith("/") && fpath.endsWith("/")){
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalActiveMode();
                FTPFileFilter ftpFileFilter = new FTPFileFilter() {
                    @Override
                    public boolean accept(FTPFile ftpFile) {
                        return ftpFile.getName().startsWith( fileType );
                    }
                };
                FTPFile [] ftpFiles = ftpClient.listFiles(fpath,ftpFileFilter);
                Integer fileCountTemp = 0;
                for (int i = 0; i < ftpFiles.length; i++) {
                    FTPFile ftpFile = ftpFiles[i];
                    BufferedOutputStream bufferRead = null;
                    FileOutputStream fileOutputStream = null;
                    if (ftpFile.isFile()) {
                        String fileName = new String(ftpFile.getName().getBytes(LOCAL_CHARSET),SERVER_CHARSET);
                        File localFile = File.createTempFile("ftp",".zip");
                        localFile.deleteOnExit();
                        fileOutputStream = new FileOutputStream( localFile );
                        bufferRead = new BufferedOutputStream( fileOutputStream );
                        ftpClient.retrieveFile(fileName, bufferRead);
                        bufferRead.flush();
                        List<Map<String, Object>> packageSerialJson = FileEncryptUtil.getList( localFile,zipEncode );
                        ftpData.put(ftpFile.getName(), packageSerialJson );
                        if (bufferRead != null) {
                            bufferRead.close();
                        }
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        localFile.delete();
                        fileCountTemp ++;
                        if (fileCountTemp.equals(ftpReadCount  )){
                            break;
                        }
                    }
                }
                ftpClient.logout();
            }
            return ftpData;
        } finally{
            closeClient();
        }
    }

    /**
     * @description  删除ftp文件夹下面的视频文件
     * @param  path  文件路径
     * @param  name  文件名
     * @return  返回结果
     * @date  20/08/18 18:11
     * @author  wanghb
     * @edit
     */
    public boolean deleteFile(String path,String name){
        boolean isAppend = false;
        try {
            if(!login()){
                return isAppend;
            }
            path = new String(path.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
            ftpClient.changeWorkingDirectory(path);
            name = new String(name.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
            ftpClient.dele(name);
            ftpClient.logout();
            isAppend = true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            closeClient();
        }
        return isAppend;
    }

    /**
     * @description  修改文件名名称
     * @param  path  文件路径
     * @param  oldName  文件名
     * @param  newName  新名称
     * @return  返回结果
     * @date  20/08/18 18:11
     * @author  wanghb
     * @edit
     */
    public boolean updateFileName(String path,String oldName,String newName){
        boolean isAppend = false;
        try {
            if(!login()){
                return isAppend;
            }
            String[] paths = path.split("/");
            for (int i = 0; i < paths.length; i++){
                String pathTemp = paths[i];
                ftpClient.makeDirectory(pathTemp);
            }
            oldName = new String(oldName.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
            ftpClient.rename( oldName,path+newName );
            ftpClient.logout();
            isAppend = true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            closeClient();
        }
        return isAppend;
    }


    /**
     * @description  断开与远程服务器的连接
     * @return  返回结果
     * @date  20/08/17 11:32
     * @author  wanghb
     * @edit
     */
    public void closeClient(){
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        //文件删除
        //fTPUtil.deleteFile( "/data", "ftp7500632415927387638.zip" );
        //文件解析
        //List<UploadDataInfo> dateList = fTPUtil.getDateList( "/test/" );
        //文件下载
        //fTPUtil.downloadFileList("/test/","C:\\Users\\Administrator\\Desktop\\");
        String month = "sdfsdfsdf_123123123123.zip";
        String beginMonth = month.substring(0, month.indexOf("_")) + month.substring(month.indexOf("."));//截取从字符‘-’位置开始的字符串
        logger.info(beginMonth);
    }

}
