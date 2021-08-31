package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.AppLogDirtyDataEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.ServiceLogDirtyDataEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.ServiceLogPushEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.HbaseRowkeyMarkService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.*;
import com.alibaba.fastjson.JSON;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
* 应用日志表( appLogService )服务实现类
* @author wanghb
* @since 2020-11-23 14:34:51
*/
@Service("serviceLogService")
public class ServiceLogService extends BaseController {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Autowired
    private BaseDao baseDao;
    @Resource
    private HBaseService hBaseService;
    @Resource
    private SequenceService sequenceService;
    @Resource
    private HbaseRowkeyMarkService hbaseRowkeyMarkService;




    /**
     * @description  获取appCode
     * @param  appValue
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    @Transactional(rollbackOn = Exception.class)
    public synchronized String getAppCode(String appValue) {
        while (ScheduledTasks.rowKeyAppNoUseCache.size() == 0){
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String appCode = new ArrayList<>( ScheduledTasks.rowKeyAppNoUseCache).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyAppNoUseCache.size() - 1) );
        ScheduledTasks.rowKeyAppNoUseCache.remove( appCode );
        ScheduledTasks.rowKeyAppUseCache.put( appValue,appCode );
        hbaseRowkeyMarkService.updateAppCode( appCode ,appValue, ParamEnum.rowkeyType.type3.getCode());
        return appCode;
    }

    /**
     * @description  获取appCode
     * @param  appValue
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    @Transactional(rollbackOn = Exception.class)
    public synchronized String getPersonCodeCode(String appValue) {
        while (ScheduledTasks.rowKeyPersonCodeNoUseCache.size() == 0){
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String appCode = new ArrayList<>( ScheduledTasks.rowKeyPersonCodeNoUseCache ).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyPersonCodeNoUseCache.size() - 1) );
        ScheduledTasks.rowKeyPersonCodeNoUseCache.remove( appCode );
        ScheduledTasks.rowKeyPersonCodeUseCache.put( appValue,appCode );
        hbaseRowkeyMarkService.updateAppCode( appCode ,appValue, ParamEnum.rowkeyType.type2.getCode());
        return appCode;
    }

    /**
     * @description  获取appCode
     * @param  serviceValue
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    @Transactional(rollbackOn = Exception.class)
    public synchronized String getServiceCode(String serviceValue) {
        while (ScheduledTasks.rowKeyServiceNoUseCache.size() == 0){
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String appCode = new ArrayList<>( ScheduledTasks.rowKeyServiceNoUseCache).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyServiceNoUseCache.size() - 1) );
        ScheduledTasks.rowKeyServiceNoUseCache.remove( appCode );
        ScheduledTasks.rowKeyServiceUseCache.put( serviceValue,appCode );
        hbaseRowkeyMarkService.updateAppCode( appCode ,serviceValue, ParamEnum.rowkeyType.type4.getCode());
        return appCode;
    }


    /**
     * @description  获取appCode
     * @param  loadValue
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    @Transactional(rollbackOn = Exception.class)
    public synchronized String getLoadCode(String loadValue) {
        while (ScheduledTasks.rowKeyLoadNoUseCache.size() == 0){
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String appCode = new ArrayList<>( ScheduledTasks.rowKeyLoadNoUseCache).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyLoadNoUseCache.size() - 1) );
        ScheduledTasks.rowKeyLoadNoUseCache.remove( appCode );
        ScheduledTasks.rowKeyLoadUseCache.put( loadValue,appCode );
        hbaseRowkeyMarkService.updateAppCode( appCode ,loadValue, ParamEnum.rowkeyType.type5.getCode());
        return appCode;
    }

    /**
     * @description  服务日志进行消费
     * @param  records
     * @return  返回结果
     * @date  2021-3-24 16:22
     * @author  wanghb
     * @edit
     */
    ExecutorService serviceLogReceiverThreadPool = Executors.newFixedThreadPool( 300 );
    //@KafkaListener(topics = {"serviceLog"}, groupId = "kafka-producer" ,containerFactory="batchFactory")
    /*public void serviceLogReceiver(List<ConsumerRecord> records) {
        for (ConsumerRecord<Integer, String> record : records) {
            serviceLogReceiverThreadPool.execute( new Runnable() {
                @Override
                public void run(){
                    record.offset();
                    Map<String,Object> params = MapUtil.toMap(  record.value());

                    ServiceLogPushEntity serviceLogPushEntity = MapUtil.toBean( params,ServiceLogPushEntity.class );
                    String callTime = serviceLogPushEntity.getCallTime();
                    String callTimeShort = DateUtil.toShortString( callTime );
                    String appId = serviceLogPushEntity.getAppId();
                    String serviceId = serviceLogPushEntity.getServiceId();
                    String loadNum = serviceLogPushEntity.getLoadNum();
                    String appCode = ScheduledTasks.rowKeyAppUseCache.get( appId );
                    if(PowerUtil.isNull( appCode )){
                        appCode = getAppCode(appId);
                    }
                    String serviceCode = ScheduledTasks.rowKeyServiceUseCache.get( serviceId );
                    if(PowerUtil.isNull( serviceCode )){
                        serviceCode = getServiceCode(serviceId);
                    }
                    String loadCode = ScheduledTasks.rowKeyLoadUseCache.get( loadNum );
                    if(PowerUtil.isNull( loadCode )){
                        loadCode = getLoadCode(loadNum);
                    }
                    String serviceNum = sequenceService.getNum( ParamEnum.sequenceType.service.getCode() + callTimeShort);
                    String rowKey = new StringBuilder( appCode ).append( callTimeShort.replace( "-","" ) ).append( "-" ).append( serviceCode ).append( "~" ).append( loadCode ).append( serviceNum ).toString();
                    serviceLogPushEntity.setId( CodeUtils.getUUID32());
                    serviceLogPushEntity.setRowKey( rowKey );
                    //logger.info("消费结束");
                }
            });
        }
    }*/


    /**
     * @description  保存
     * @param  params
     * @return  返回结果
     * @date  2021-4-12 16:49
     * @author  wanghb
     * @edit
     */
    public Result save(Map<String, Object> params) {
        /*List<ServiceLogPushEntity> list = new ArrayList<>();
        List<List<Object>> listDate = new ArrayList<>();
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用6","服务1","2021-01-07","16"));

        listDate.add(Arrays.asList( "应用7","服务2","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用7","服务2","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用7","服务2","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用7","服务2","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用7","服务2","2021-01-07","16"));

        listDate.add(Arrays.asList( "应用8","服务3","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用8","服务3","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用8","服务3","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用8","服务3","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用8","服务3","2021-01-07","16"));

        listDate.add(Arrays.asList( "应用9","服务4","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用9","服务4","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用9","服务4","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用9","服务4","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用9","服务4","2021-01-07","16"));

        listDate.add(Arrays.asList( "应用10","服务5","2021-01-11","16"));
        listDate.add(Arrays.asList( "应用10","服务5","2021-01-10","16"));
        listDate.add(Arrays.asList( "应用10","服务5","2021-01-09","16"));
        listDate.add(Arrays.asList( "应用10","服务5","2021-01-08","16"));
        listDate.add(Arrays.asList( "应用10","服务5","2021-01-07","16"));


        for (int x = 0; x < listDate.size(); x++) {
            List<Object> temp = listDate.get(x);
            Date date = DateUtil.toDate( PowerUtil.getString( temp.get( 2 ) ),DateUtil.DATE_SHORT);
            String nowDateStr = DateUtil.toString( date,DateUtil.DATE_SHORT );
            String appValue = PowerUtil.getString( temp.get( 0 ) );
            String serviceValue = PowerUtil.getString( temp.get( 1 ) );
            Integer transferSumCount = 0;
            //16个负载
            for (int i = 0; i < PowerUtil.getInt( temp.get( 3 ) ); i++) {
                for (int j = 1; j <= 24; j++) {
                    String loadValue = i + "";
                    String appCode = ScheduledTasks.rowKeyAppUseCache.get( appValue );
                    if(PowerUtil.isNull( appCode )){
                        appCode = getAppCode(appValue);
                    }
                    String serviceCode = ScheduledTasks.rowKeyServiceUseCache.get( serviceValue );
                    if(PowerUtil.isNull( serviceCode )){
                        serviceCode = getServiceCode(serviceValue);
                    }
                    String loadCode = ScheduledTasks.rowKeyLoadUseCache.get( loadValue );
                    if(PowerUtil.isNull( loadCode )){
                        loadCode = getLoadCode(loadValue);
                    }
                    Integer transferCount = RandomUtil.getRandom( 1,10 );
                    transferSumCount += transferCount;
                    for (int y = 0; y < transferCount; y++) {
                        String serviceLogNum = sequenceService.getNum( ParamEnum.sequenceType.service.getCode() + nowDateStr.replace( "-","" ) );
                        //XCD20210101-DSC~CD00000001
                        String rowKey = new StringBuilder( appCode ).append( nowDateStr.replace( "-","" ) ).append( "-" ).append( serviceCode ).append( "~" ).append( loadCode ).append( serviceLogNum ).toString();
                        ServiceLogPushEntity serviceLogPushEntity = new ServiceLogPushEntity();
                        serviceLogPushEntity.setId( CodeUtils.getUUID32());
                        serviceLogPushEntity.setRowKey( rowKey );

                        serviceLogPushEntity.setAppName(appValue);
                        serviceLogPushEntity.setAppId("");
                        serviceLogPushEntity.setUserId("");
                        serviceLogPushEntity.setServiceId(serviceValue);
                        serviceLogPushEntity.setLoadNum(loadValue);
                        serviceLogPushEntity.setCallTime(nowDateStr + " " + (j <= 9 ? "0" : "") + j+":00:00");
                        serviceLogPushEntity.setServiceAddress("http://"+serviceValue+RandomUtil.getRandom( 0,9 ));
                        serviceLogPushEntity.setRequestParam("");
                        serviceLogPushEntity.setRequestHeader("");
                        serviceLogPushEntity.setResponseTime(nowDateStr + " " + (j <= 9 ? "0" : "") + j+":00:00");
                        serviceLogPushEntity.setCallDuration(new BigDecimal( "16.86" ) );
                        serviceLogPushEntity.setIsError("true");
                        serviceLogPushEntity.setErrorInfo("");
                        serviceLogPushEntity.setResponseContent("");
                        serviceLogPushEntity.setResponseHeader("");
                        list.add(serviceLogPushEntity);
                    }
                    //logger.info("负载编号:"+loadValue+" ,负载rowKey:"+loadCode+" ,应用编号:"+appValue+" ,应用rowKey:"+appCode+" ,服务编号:"+serviceValue+" ,服务rowKey:"+serviceCode);
                }
            }
            logger.info("应用:"+appValue+" ,服务:"+serviceValue+" ,时间:"+nowDateStr+" ,调用次数:"+transferSumCount);
        }
        logger.info("开始插入");
        hBaseService.batchSave( "serviceLog", MapUtil.toListMap( list ) );
        logger.info("结束插入");
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list.size());*/
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
     * @description  服务日志入库(kafka)
     * @param  serviceLogList
     * @return  返回结果
     * @date  2021-7-12 10:42
     * @author  wanghb
     * @edit
     */
    public void batchSaveKafkaData(List<ConsumerRecord<String, String>> serviceLogList) throws Exception {
        if (serviceLogList == null || serviceLogList.size() == 0) {
            return;
        }
        List<Map<String, Object>> list = new ArrayList<>(serviceLogList.size());
        //其他服务日志 包括 部里的服务  和  苏警通站点 的服务
        List<Map<String, Object>> otherList = new ArrayList<>(serviceLogList.size());
        for (ConsumerRecord<String, String> temp : serviceLogList) {
            Map<String, Object> serviceLog = JSON.parseObject( temp.value() );
            if (checkData(serviceLog)) {
                throw new RuntimeException("参数异常");

            }
            Map<String, Object> serviceLogMap = getServiceLogMap(JSON.parseObject( temp.value() ),ParamEnum.networkAreaLog.networkArea3.getCode());
            String appType = PowerUtil.getString( serviceLogMap.get( "appType" ) );
            if (ParamEnum.appType.type0.getCode().equals( appType )) {
                list.add(serviceLogMap);
            }else {
                otherList.add(serviceLogMap);
            }

        }
        if (list.size() > 0) {
            hBaseService.batchSave( ParamEnum.topic.serviceLog.getHbaseTableName(),list );
        }
        if (otherList.size() > 0) {
            hBaseService.batchSave( ParamEnum.topic.otherServiceLog.getHbaseTableName(),list );
        }
    }


    /**
     * @description  校验数据
     * @param  serviceLog
     * @return  返回结果
     * @date  2021-7-24 11:33
     * @author  wanghb
     * @edit
     */
    public Boolean checkData(Map<String, Object> serviceLog) {
        String callTime = PowerUtil.getString( serviceLog.get("callTime") );
        String serviceId = PowerUtil.getString( serviceLog.get("serviceId") );
        String loadNum = PowerUtil.getString( serviceLog.get("loadNum") );
        String appId = PowerUtil.getString( serviceLog.get( "appId" ) );
        return "".equals( callTime ) || "".equals( serviceId ) || "".equals( loadNum )|| "".equals( appId );
    }


    /**
     * @description  服务日志入库(FTP)
     * @param  serviceLogList
     * @return  返回结果
     * @date  2021-7-12 10:42
     * @author  wanghb
     * @edit
     */
    public void batchSaveListData(List<Map<String, Object>> serviceLogList) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>(serviceLogList.size());
        //其他服务日志 包括 部里的服务  和  苏警通站点 的服务
        List<Map<String, Object>> otherList = new ArrayList<>(serviceLogList.size());

        for (Map<String, Object>  serviceLog : serviceLogList) {
            if (checkData(serviceLog)) {
                throw new RuntimeException("参数异常");
            }
            Map<String, Object> serviceLogMap = getServiceLogMap(serviceLog,ParamEnum.networkAreaLog.networkArea2.getCode());
            String appType = PowerUtil.getString( serviceLogMap.get( "appType" ) );
            if (ParamEnum.appType.type0.getCode().equals( appType )) {
                list.add(serviceLogMap);
            }else {
                otherList.add(serviceLogMap);
            }
        }
        if (list.size() > 0) {
            hBaseService.batchSave( ParamEnum.topic.serviceLog.getHbaseTableName(),list );
        }
        if (otherList.size() > 0) {
            hBaseService.batchSave( ParamEnum.topic.otherServiceLog.getHbaseTableName(),otherList );
        }
    }


    /**
     * @description  服务日志入库(单个入库)
     * @param  temp
     * @return  返回结果
     * @date  2021-7-12 10:42
     * @author  wanghb
     * @edit
     */
    @Transactional
    public void saveData(ConsumerRecord<String, String> temp) {
        Map<String, Object>  serviceLog = null;
        try {
            serviceLog = JSON.parseObject( temp.value() );
            if (checkData(serviceLog)) {
                throw new RuntimeException("参数异常");
            }
            Map<String, Object>  serviceLogMap = getServiceLogMap(serviceLog,ParamEnum.networkAreaLog.networkArea3.getCode());
            String rowKey = PowerUtil.getString( serviceLogMap.get( "rowKey" ) );
            String appType = PowerUtil.getString( serviceLogMap.get( "appType" ) );
            if (ParamEnum.appType.type0.getCode().equals( appType )) {
                hBaseService.save( ParamEnum.topic.serviceLog.getHbaseTableName(),rowKey,serviceLogMap );
            }else {
                hBaseService.save( ParamEnum.topic.otherServiceLog.getHbaseTableName(),rowKey,serviceLogMap );
            }
        } catch (Exception e) {
            ServiceLogDirtyDataEntity serviceLogDirtyDataEntity = new ServiceLogDirtyDataEntity();
            serviceLogDirtyDataEntity.setErrorData( JSON.toJSONString( serviceLog ) );
            serviceLogDirtyDataEntity.setType( ParamEnum.dirtyDataType.type0.getCode() );
            serviceLogDirtyDataEntity.setStatus( ParamEnum.dirtyDataStatus.status0.getCode() );
            serviceLogDirtyDataEntity.setErrorMsg( e.getMessage() );
            MapUtil.setCreateBean( serviceLogDirtyDataEntity, CodeUtils.getUUID32(),new Date() );
            baseDao.save( serviceLogDirtyDataEntity );
            e.printStackTrace();
        }
    }


    /**
     * @description  构建服务hbase
     * @param  serviceLog
     * @return  返回结果
     * @date  2021-7-19 11:58
     * @author  wanghb
     * @edit  
     */
    private Map<String, Object> getServiceLogMap(Map<String, Object> serviceLog,String networkAreaLog) throws Exception {
        String callTime = PowerUtil.getString( serviceLog.get("callTime") );
        String serviceId = PowerUtil.getString( serviceLog.get("serviceId") );
        String loadNum = PowerUtil.getString( serviceLog.get("loadNum") );
        String appId = PowerUtil.getString( serviceLog.get( "appId" ) );
        String appType = PowerUtil.getString( serviceLog.get( "appType" ) );
        String callTimeShort = DateUtil.toShortString( callTime );
        String serviceLogNum;
        String appCode;
        if (ParamEnum.appType.type0.getCode().equals( appType )) {
            appId = appId.length() > 40 ? appId :  ScheduledTasks.appInfoObjIdToId.get( appId );
            serviceLogNum = sequenceService.getNum( ParamEnum.sequenceType.service.getCode() + callTimeShort.replace( "-","" ) );
        } else {
            serviceLogNum = sequenceService.getNum( ParamEnum.sequenceType.otherService.getCode() + callTimeShort.replace( "-","" ) );
        }

        appCode = ScheduledTasks.rowKeyAppUseCache.get( appId );
        if(PowerUtil.isNull( appCode )){
            appCode = getAppCode(appId);
        }
        String serviceCode = ScheduledTasks.rowKeyServiceUseCache.get( serviceId );
        if(PowerUtil.isNull( serviceCode )){
            serviceCode = getServiceCode(serviceId);
        }
        String loadCode = ScheduledTasks.rowKeyLoadUseCache.get( loadNum );
        if(PowerUtil.isNull( loadCode )){
            loadCode = getLoadCode(loadNum);
        }
        //20210101_XCD-CD~DSC00000001
        //日期反转+服务code+负载code+应用code
        //日期字符串反转
        String nowDateRowkey = new StringBuilder( callTimeShort.replace( "-", "" ) ).reverse().toString();
        String rowKey = new StringBuilder( nowDateRowkey ).append( "_" ).append(serviceCode).append( "-" ).append(  loadCode).append( "~" ).append( appCode ).append( serviceLogNum ).toString();
        serviceLog.put( "id",CodeUtils.getUUID32());
        serviceLog.put( "rowKey",rowKey );
        if (!serviceLog.containsKey( networkAreaLog)) {
            serviceLog.put( "networkAreaLog",networkAreaLog );
        }
        //剔除一些磁盘占用过高的字段
        serviceLog.remove( "requestParam" );
        serviceLog.remove( "requestHeader" );
        serviceLog.remove( "responseContent" );
        serviceLog.remove( "responseHeader" );
        return serviceLog;
    }



}
