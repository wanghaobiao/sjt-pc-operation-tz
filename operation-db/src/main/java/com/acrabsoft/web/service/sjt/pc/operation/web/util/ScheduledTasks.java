package com.acrabsoft.web.service.sjt.pc.operation.web.util;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.DictInfoEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.*;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.HbaseRowkeyMarkService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.TaskRecordService;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Component
@EnableAsync
@EnableScheduling
public class ScheduledTasks {
    Logger logger = LoggerFactory.getLogger( ScheduledTasks.class);

    @Value("${spring.profiles.active}")
    public String active;

    @Resource
    private HbaseRowkeyMarkService hbaseRowkeyMarkService;
    @Resource
    private AppInfoService appInfoService;
    @Resource
    private AppLogService appLogService;
    @Resource
    private AppOpenDurationService appOpenDurationService;
    @Resource
    private ServiceLogService serviceLogService;
    @Resource
    private TaskRecordService taskRecordService;
    @Resource
    private ServiceInfoService serviceInfoService;
    @Resource
    @Qualifier("mysqlJdbcTemplate")
    private JdbcTemplate mysqlJdbcTemplate;
    @Value("${file.url}")
    private String fileUrl;
    @Value("${area}")
    private String area;

    /**
     * rowKey ??????????????????????????????  ??? : value ??? : code
     */
    public static Map<String, String> rowKeyCityUseCache = new Hashtable<>();
    public static Map<String, String> rowKeyPersonCodeUseCache = new Hashtable<>();
    public static Map<String, String> rowKeyAppUseCache = new Hashtable<>();
    public static Map<String, String> rowKeyServiceUseCache = new Hashtable<>();
    public static Map<String, String> rowKeyLoadUseCache = new Hashtable<>();

    /**
     * rowKey ??????????????????????????????
     */
    public static Set<String> rowKeyCityNoUseCache = new HashSet<>();
    public static Set<String> rowKeyPersonCodeNoUseCache = new HashSet<>();
    public static Set<String> rowKeyAppNoUseCache = new HashSet<>();
    public static Set<String> rowKeyServiceNoUseCache = new HashSet<>();
    public static Set<String> rowKeyLoadNoUseCache = new HashSet<>();



    // ??? : ????????????  ??? : ??? : ??????code  ??? : ????????????
    // ???????????? : params.get( ParamEnum.dicType.APP_AREA.getCode() ).get( "CS" )
    public static Map<String,Map<String, String>> dict = new HashMap<>();
    public static Map<String,List<DictInfoEntity>> dictList = new HashMap<>();
    /**
     * @description  ????????????
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    private static Object  refreshParamsLock = new Object();
    @Async
    @Scheduled(cron = "0 */5 * * * ?")
    public void refreshParams(){
        synchronized (refreshParamsLock){
            StringBuilder sb = new StringBuilder("select * from "+ DictInfoEntity.tableName +" where deleted = 0 ");
            List<DictInfoEntity> query = mysqlJdbcTemplate.query( sb.toString(), new BeanPropertyRowMapper<>( DictInfoEntity.class ) );
            dict = new HashMap<>();
            dictList = new HashMap<>();
            for (DictInfoEntity dictInfoEntity : query) {
                String pvalue = dictInfoEntity.getPvalue();
                String label = dictInfoEntity.getLabel();
                String value = dictInfoEntity.getValue();
                if (dict.containsKey( pvalue )) {
                    Map<String, String> temp = dict.get( pvalue );
                    temp.put( value,label );
                }else{
                    Map<String, String> temp = new HashMap<>();
                    temp.put( value,label );
                    dict.put( pvalue,temp );
                }
                if (dictList.containsKey( pvalue )) {
                    List<DictInfoEntity> dictInfoEntities = dictList.get( pvalue );
                    dictInfoEntities.add( dictInfoEntity );
                }else{
                    List<DictInfoEntity> dictInfoEntities = new ArrayList<>();
                    dictInfoEntities.add( dictInfoEntity );
                    dictList.put( pvalue,dictInfoEntities );
                }
                for(Map.Entry<String, List<DictInfoEntity>> entry : dictList.entrySet()){
                    List<DictInfoEntity> mapValue = entry.getValue();
                    Collections.sort(mapValue);
                }
            }
        }
    }

    @Autowired
    private FTPUtil fTPUtilForAppFtp;
    /**
     * @description  ????????????ftp??????
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    private static Object appFtpSyncLock = new Object();
    @Async
    @Scheduled(cron = "0 */1 * * * ?")
    //@Scheduled(cron = "0/1 * * * * ?")
    public void appFtpSync(){
        synchronized (appFtpSyncLock){
            if (ParamEnum.active.prd.getCode().equals( active ) && ParamEnum.areaCode.code00.getCode().equals( area )) {
                logger.info("================================================>  ???????????? appLog  <================================================");
                Map<String, List<Map<String, Object>>> dateList = new HashMap<>();
                try {
                    dateList = fTPUtilForAppFtp.getDateList( "/", ParamEnum.topic.appLog.getHbaseTableName() );
                    logger.info("===>  ?????? appLog : " + dateList.size());
                } catch (Exception e) {
                    logger.info( "===>  KafkaController.ftp ftp??????????????????:" );
                    e.printStackTrace();
                }
                for (Map.Entry<String, List<Map<String, Object>>> entry : dateList.entrySet()) {
                    String ftpFileName = entry.getKey();
                    List<Map<String, Object>> mapValue = entry.getValue();
                    try {
                        appLogService.batchSaveListData( mapValue );
                        fTPUtilForAppFtp.deleteFile( "/", ftpFileName );
                    } catch (Exception e) {
                        logger.info( "===>  KafkaController.ftp ????????????hbase??????" );
                        fTPUtilForAppFtp.updateFileName( "/errorAppLog/", ftpFileName, ftpFileName );
                        e.printStackTrace();
                    }
                }
                logger.info("================================================>  ???????????? appLog  <================================================");
            }
        }
    }

    @Autowired
    private FTPUtil fTPUtilForAppDurationLog;
    /**
     * @description  ????????????ftp??????
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    private static Object appDurationLogFtpSyncLock = new Object();
    @Async
    @Scheduled(cron = "0 */1 * * * ?")
    //@Scheduled(cron = "0/1 * * * * ?")
    public void appDurationLogFtpSync(){
        synchronized (appDurationLogFtpSyncLock){
            if (ParamEnum.active.prd.getCode().equals( active ) && ParamEnum.areaCode.code00.getCode().equals( area )) {
                logger.info("================================================>  ???????????? appDurationLog  <================================================");
                Map<String, List<Map<String, Object>>> dateList = new HashMap<>();
                try {
                    dateList = fTPUtilForAppDurationLog.getDateList( "/", ParamEnum.topic.appDurationLog.getHbaseTableName() );
                    logger.info("===>  ?????? appDurationLog : " + dateList.size());
                } catch (Exception e) {
                    logger.info( "===>  KafkaController.ftp ftp??????????????????:" );
                    e.printStackTrace();
                }
                for (Map.Entry<String, List<Map<String, Object>>> entry : dateList.entrySet()) {
                    String ftpFileName = entry.getKey();
                    List<Map<String, Object>> mapValue = entry.getValue();
                    try {
                        appOpenDurationService.batchSave( mapValue );
                        fTPUtilForAppDurationLog.deleteFile( "/", ftpFileName );
                    } catch (Exception e) {
                        logger.info( "===>  KafkaController.ftp ????????????hbase??????" );
                        fTPUtilForAppDurationLog.updateFileName( "/errorAppDurationLog/", ftpFileName, ftpFileName );

                        e.printStackTrace();
                    }
                }
                logger.info("================================================>  ???????????? appDurationLog  <================================================");

            }
        }
    }


    @Autowired
    private FTPUtil fTPUtilForServiceFtp;
    /**
     * @description  ??????ftp??????
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    private static Object serviceFtpSyncLock = new Object();
    @Async
    @Scheduled(cron = "0 */1 * * * ?")
    //@Scheduled(cron = "0/1 * * * * ?")
    public void serviceFtpSync(){
        synchronized (serviceFtpSyncLock) {
            if (ParamEnum.active.prd.getCode().equals( active ) && ParamEnum.areaCode.code00.getCode().equals( area )) {
                logger.info("================================================>  ???????????? serviceLog  <================================================");
                Map<String, List<Map<String, Object>>> dateList = new HashMap<>();
                try {
                    dateList = fTPUtilForServiceFtp.getDateList( "/", ParamEnum.topic.serviceLog.getHbaseTableName() );
                    logger.info("===>  ?????? serviceLog : " + dateList.size());
                } catch (Exception e) {
                    logger.info( "===>  KafkaController.ftp ftp??????????????????:" );
                    e.printStackTrace();
                }
                for (Map.Entry<String, List<Map<String, Object>>> entry : dateList.entrySet()) {
                    String ftpFileName = entry.getKey();
                    List<Map<String, Object>> mapValue = entry.getValue();
                    try {
                        serviceLogService.batchSaveListData( mapValue );
                        fTPUtilForServiceFtp.deleteFile( "/", ftpFileName );
                    } catch (Exception e) {
                        logger.info( "===>  KafkaController.ftp ????????????hbase??????" );
                        //???????????????  ????????????  ????????????
                        fTPUtilForServiceFtp.updateFileName( "/errorServiceLog/", ftpFileName, ftpFileName );
                        e.printStackTrace();
                    }
                }
                logger.info("================================================>  ???????????? serviceLog  <================================================");
            }
        }
    }


    /**
     * @description  ??????????????????(???????????????????????????)
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    private static Object appTaskRecordLock = new Object();
    @Async
    @Scheduled(cron = "0 0 0/1 * * ? ")
    public synchronized void appTaskRecord(){
        if (ParamEnum.areaCode.code00.getCode().equals( area )) {
            synchronized (appTaskRecordLock) {
                if (ParamEnum.active.prd.getCode().equals( active )) {
                    Map<String, Object> params = new HashMap<>();
                    params.put( "startDate", DateUtil.toString( new Date(), DateUtil.DATE_SHORT ) );
                    params.put( "endDate", DateUtil.toString( new Date(), DateUtil.DATE_SHORT ) );
                    taskRecordService.appOpenCount( params );
                }
            }
        }
    }


    /**
     * @description  ??????????????????(???????????????????????????)
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    private static Object serviceTaskRecordLock = new Object();
    @Async
    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void serviceTaskRecord(){
        if (ParamEnum.areaCode.code00.getCode().equals( area )) {
            synchronized (serviceTaskRecordLock) {
                if (ParamEnum.active.prd.getCode().equals( active )) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("startDate",DateUtil.toString( new Date() ,DateUtil.DATE_SHORT));
                    params.put("endDate",DateUtil.toString( new Date() ,DateUtil.DATE_SHORT));
                    taskRecordService.serviceOpenCount(params);
                }
            }
        }
    }

    /**
     * @description  ??????????????????
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    public static Map<String, String> appInfoIdToName = new Hashtable<>();
    public static Map<String, String> appInfoIdToAppLogo = new Hashtable<>();
    public static Map<String, String> appInfoIdToArea = new Hashtable<>();
    public static Map<String, String> appInfoObjIdToId = new Hashtable<>();
    public static Map<String, String> appInfoObjIdToName = new Hashtable<>();
    public static Map<String, String> appInfoIdToPolice = new Hashtable<>();
    public static Map<String, String> appInfoIdToCategory = new Hashtable<>();
    @Async
    @Scheduled(cron = "0 */5 * * * ?")
    public void refreshAppInfo(){
        List<Map<String, Object>>  allList = appInfoService.getAllList();
        for (Map<String, Object> temp : allList) {
            String appId = PowerUtil.getString( temp.get("app_id") );
            String appName = PowerUtil.getString( temp.get("app_name") );
            String objId = PowerUtil.getString( temp.get("obj_id") );
            String area = PowerUtil.getString( temp.get("city") );
            String police = PowerUtil.getString( temp.get("police") );
            String category = PowerUtil.getString( temp.get("category") );
            String appLogo = PowerUtil.getString( temp.get("app_logo") );

            appInfoIdToName.put( appId,appName );
            appInfoIdToArea.put( appId,area );
            appInfoIdToPolice.put( appId,police );
            appInfoIdToCategory.put( appId,category );
            appInfoIdToAppLogo.put( appId,fileUrl+appLogo );
            appInfoObjIdToId.put( objId,appId );
            appInfoObjIdToName.put( objId,appName );
        }
    }

    /**
     * @description  ??????????????????
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    public static Map<String, String> serviceInfoIdToName = new Hashtable<>();
    public static Map<String, String> serviceInfoIdToAreaCode = new Hashtable<>();
    @Scheduled(cron = "0 */5 * * * ?")
    public void refreshServiceInfo(){
        List<Map<String, Object>> allList = serviceInfoService.getAllList();
        for (Map<String, Object> temp : allList) {
            String serviceId = PowerUtil.getString( temp.get("service_id") );
            String serviceName = PowerUtil.getString( temp.get("service_name") );
            String areaCode = PowerUtil.getString( temp.get("area_code") );
            serviceInfoIdToName.put( serviceId,serviceName );
            serviceInfoIdToAreaCode.put( serviceId,areaCode );
        }
    }


    /**
     * @description  ???30???????????????????????????
     * @date  20/07/16 10:26
     * @author  wanghb
     * @edit
     */
    private static Object refreshRowkeyMarkLock = new Object();
    @Async
    @Scheduled(cron = "0 */30 * * * ?")
    public void refreshRowkeyMark(){
        synchronized (refreshRowkeyMarkLock){
            logger.info("===========================>rowKey????????????<===========================");
            hbaseRowkeyMarkService.refreshRowkeyByType(1000,ParamEnum.rowkeyType.type1.getCode());
            hbaseRowkeyMarkService.refreshRowkeyByType(1000,ParamEnum.rowkeyType.type2.getCode());
            hbaseRowkeyMarkService.refreshRowkeyByType(1000,ParamEnum.rowkeyType.type3.getCode());
            hbaseRowkeyMarkService.refreshRowkeyByType(1000,ParamEnum.rowkeyType.type4.getCode());
            hbaseRowkeyMarkService.refreshRowkeyByType(1000,ParamEnum.rowkeyType.type5.getCode());
            logger.info("===========================>rowKey????????????<===========================");
        }
    }

}
