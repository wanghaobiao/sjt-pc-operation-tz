package com.acrabsoft.web.service.sjt.pc.operation.web.kafka.controller;

import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.AppLogPushEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.ServiceLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.kafka.entity.KafkaDataEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.kafka.service.KafkaService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.FTPUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MapUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.HuaweiKafkaConsumerClient;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.HuaweiKafkaProducerClient;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.KafkaMsgSender;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kafka")
@Api(tags = "kafka控制类")
public class KafkaController {
    private static Logger logger = Logger.getLogger( KafkaController.class );

    //@Resource
    //KafkaTemplate kafkaTemplate;
    @Autowired
    private KafkaMsgSender KafkaHuawei;
    @Autowired
    private HBaseService hBaseService;
    @Autowired
    private KafkaService kafkaService;
    @Autowired
    private AppLogService appLogService;
    @Autowired
    private ServiceLogService serviceLogService;
    @Lazy
    @Autowired
    private HuaweiKafkaProducerClient huaweiKafkaProducerClient;


    @Autowired
    private FTPUtil fTPUtil;


    /**
     * @description  日志推送
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @PostMapping  ("/appLogPush")
    @NotAuthorize
    @ResponseBody
    public Result appLogPush(@RequestBody Map<String, Object> params) {
        //"{\"companyCode\":\"test123456\",\"deptName\":\"测试部门\",\"networkEnv\":\"2\",\"code\":\"\",\"startPerson\":\"测试人\",\"versionNum\":\"1.0\",\"companyName\":\"测试公司\",\"areaCode\":\"3205000000\",\"startPersonCode\":\"111111\",\"areaName\":\"南京市\",\"name\":\"AppStoreSDK\",\"imei\":\"865452047637688\",\"startTime\":\"2021-04-01 15:14:10\",\"deviceModel\":\"BLA-AL00\",\"id\":\"f37745a8e3454cf6915c94c88573fef91618797582887\",\"businessType\":\"2\",\"deptCode\":\"320500\"}"
        KafkaHuawei.sendMsg( ParamEnum.topic.serviceLog.getKafkaTopic(), JSON.toJSONString( params ));
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description  日志推送
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @PostMapping  ("/logPush")
    @NotAuthorize
    @ResponseBody
    public Result logPush(@RequestBody KafkaDataEntity kafkaDataEntity) {
        String kafkaTopic = kafkaDataEntity.getKafkaTopic();
        List<String> datas = kafkaDataEntity.getDatas();
        for (String data : datas) {
            try {
                Map<String, Object> dataMap = JSON.parseObject( data);
                dataMap.put( "networkAreaLog",ParamEnum.networkAreaLog.networkArea2.getCode()  );
                KafkaHuawei.sendMsg( kafkaTopic, JSON.toJSONString( dataMap ));
            }catch (Exception e){
                logger.info("===>  此消息消费异常,消费主题:"+kafkaTopic+",消费内容:"+data+",异常原因:"+e.getMessage());
            }
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
     * @description  消费
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping ("/consumer")
    @NotAuthorize
    @ResponseBody
    public Result consumer(@RequestParam(name = "topic", required =false) @ApiParam("例 : acrabsoft_yy_appLog,acrabsoft_yy_customizeAppLog,acrabsoft_yy_serviceLog") String topic) {
        kafkaService.onReceiver( topic );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
     * @description  ftp同步
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping ("/ftp")
    @NotAuthorize
    @ResponseBody
    public Result ftp() {
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
        /*Map<String, List<Map<String, Object>>> dateList;
        try {
            dateList = fTPUtil.getDateList( "/",ParamEnum.topic.appLog.getHbaseTableName() );
        } catch (Exception e) {
            logger.info("===>  KafkaController.ftp ftp文件读取异常:");
            e.printStackTrace();
            return BuildResult.buildOutResult( ResultEnum.ERROR);
        }
        for(Map.Entry<String, List<Map<String, Object>>> entry : dateList.entrySet()){
            String ftpFileName = entry.getKey();
            List<Map<String, Object>> mapValue = entry.getValue();
            try {
                appLogService.batchSaveListData( mapValue );
                fTPUtil.deleteFile( "/",ftpFileName );
            }catch (Exception e){
                fTPUtil.updateFileName( "/errorAppLog/",ftpFileName,ftpFileName );
                logger.info("===>  KafkaController.ftp 批量存入hbase异常");
                e.printStackTrace();
            }
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,dateList);*/
    }


    /**
     * @description  ftp同步
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    /*@GetMapping ("/serviceFtp")
    @NotAuthorize
    @ResponseBody
    public Result serviceFtp() {
        Map<String, List<Map<String, Object>>> dateList;
        try {
            dateList = fTPUtil.getDateList( "/",ParamEnum.topic.serviceLog.getHbaseTableName() );
            System.out.println(dateList.size());
        } catch (Exception e) {
            logger.info("===>  KafkaController.ftp ftp文件读取异常:");
            e.printStackTrace();
            return BuildResult.buildOutResult( ResultEnum.ERROR);
        }
        for(Map.Entry<String, List<Map<String, Object>>> entry : dateList.entrySet()){
            String ftpFileName = entry.getKey();
            List<Map<String, Object>> mapValue = entry.getValue();
            try {
                serviceLogService.batchSaveListData( mapValue );
                fTPUtil.deleteFile( "/",ftpFileName );
            }catch (Exception e){
                logger.info("===>  KafkaController.ftp 批量存入hbase异常");
                //异常的文件  修改名称  进行标注
                fTPUtil.updateFileName( "/errorServiceLog/",ftpFileName,ftpFileName );
                e.printStackTrace();
            }
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,dateList);
    }*/


    /**
     * @description  日志推送
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping ("/consumerClose")
    @NotAuthorize
    @ResponseBody
    public Result consumerClose() {
        kafkaService.consumerClose(  );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
     * @description  自定义日志推送
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @PostMapping("/customizeAppLogPush")
    @NotAuthorize
    public Result customizeAppLogPush(@RequestBody Map<String, Object> params) {
        KafkaHuawei.sendMsg( ParamEnum.topic.customizeAppLog.getKafkaTopic(),JSON.toJSONString( params ));
        //ListenableFuture listenableFuture = kafkaTemplate.send( "appLog", "{\"companyCode\":\"test123456\",\"deptName\":\"测试部门\",\"networkEnv\":\"2\",\"code\":\"\",\"startPerson\":\"测试人\",\"versionNum\":\"1.0\",\"companyName\":\"测试公司\",\"areaCode\":\"3205000000\",\"startPersonCode\":\"111111\",\"areaName\":\"南京市\",\"name\":\"AppStoreSDK\",\"imei\":\"865452047637688\",\"startTime\":\"2021-04-01 15:14:10\",\"deviceModel\":\"BLA-AL00\",\"id\":\"f37745a8e3454cf6915c94c88573fef91618797582887\",\"businessType\":\"2\",\"deptCode\":\"320500\"}" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
     * @description  服务消息日志推送
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @PostMapping("/serviceLogPush")
    @NotAuthorize
    public Result serviceLogPush(@RequestBody Map<String, Object> params){
        KafkaHuawei.sendMsg( ParamEnum.topic.serviceLog.getKafkaTopic(),JSON.toJSONString( params ));
        //ListenableFuture listenableFuture = kafkaTemplate.send( "serviceLog", "{\"id\":\"0A4A5F78F87640A4B4A9FC35829416DC\",\"appId\":\"fwzxapp1\",\"userId\":\"410184197910211212\",\"serviceId\":\"fwzxservice1\",\"loadNum\":\"1\",\"callTime\":\"2021-04-26 09:43:14\",\"serviceAddress\":\"http://127.0.0.1:20001/fwzx/v1.0.0/test\",\"requestParam\":\"fileId=15649013e00c4745b671cccc2fa2c8b5\",\"responseTime\":\"2021-04-26 09:43:14\",\"callDuration\":85,\"isError\":false,\"errorInfo\":\"\"}" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description  消息日志推送
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping("/createTable")
    @NotAuthorize
    public Result createTable(@RequestParam(name = "tableName", required =false) @ApiParam("例 : sjt_appLog,sjt_customizeAppLog,sjt_serviceLog,sjt_otherServiceLog") String tableName){
        hBaseService.createTable( tableName );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description   删除表
     * @return 无返回值
     * @date 2020-9-24 15:56:18
     * @author wanghb
     * @edit
     */
    @GetMapping("/delTable")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "删除表", notes = "删除表")
    public Result delTable(@RequestParam(name = "tableName", required =false) @ApiParam("例 : sjt_appLog,sjt_customizeAppLog,sjt_serviceLog,sjt_otherServiceLog") String tableName) {
        //hBaseService.delTable( "serviceLog" );
        hBaseService.delTable( tableName);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description   删除表
     * @return 无返回值
     * @date 2020-9-24 15:56:18
     * @author wanghb
     * @edit
     */
    @GetMapping("/saveData")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "写入数据", notes = "写入数据")
    public Result saveData() {
        List<AppLogPushEntity> appLogPushEntities = new ArrayList<>();
        AppLogPushEntity appLogPushEntity = new AppLogPushEntity();
        appLogPushEntity.setId( CodeUtils.getUUID32());
        appLogPushEntity.setRowKey( "20210102" );
        appLogPushEntities.add(appLogPushEntity);
        hBaseService.batchSave( "appLog", MapUtil.toListMap( appLogPushEntities ) );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description   查询数据
     * @return 无返回值
     * @date 2020-9-24 15:56:18
     * @author wanghb
     * @edit
     */
    @GetMapping("/list")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "查询数据", notes = "查询数据")
    public Result list(@RequestParam(name = "hbaseTable", required =false) @ApiParam("例 : sjt_appLog,sjt_customizeAppLog,sjt_serviceLog,sjt_otherServiceLog") String hbaseTable,
                       @RequestParam(name = "condition", required =false) @ApiParam("过滤条件 : 22701202.*") String condition) {
        List<Map<String, String>> appLogList = new ArrayList<>();
        //List<Map<String, String>> serviceLogList = new ArrayList<>();
        //List<Map<String, String>> list = hBaseService.getListMap( "appLogTemp", "20210102");
        appLogList = hBaseService.getListMap( hbaseTable, condition);
        //serviceLogList = hBaseService.getListMap( "appLogTemp", "22701202.*");
        logger.info("===>  KafkaController.list  查询数量:"+appLogList.size());
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,appLogList);
    }
}
