package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.AppLogDirtyDataEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.AppLogPushEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.HbaseRowkeyMarkService;
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
import java.io.IOException;
import java.util.*;


/**
* 应用日志表( appLogService )服务实现类
* @author wanghb
* @since 2020-11-23 14:34:51
*/
@Service("appLogService")
public class AppLogService extends BaseController {


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
     * @description  应用日志进行消费
     * @param  records
     * @return  返回结果
     * @date  2021-3-24 16:22
     * @author  wanghb
     * @edit
     */
    /*@KafkaListener(topics = {"appLog"}, groupId = "kafka-producer" , id = "appLogId",containerFactory="batchFactory")
    public void registryReceiver(List<ConsumerRecord> records) {
        Date startDate = new Date();
        for (ConsumerRecord<Integer, String> record : records) {
            try {
                Map<String,Object> params = MapUtil.toMap(  record.value());
                AppLogPushEntity appLogPushEntity = MapUtil.toBean( params,AppLogPushEntity.class );
                String startTime = DateUtil.toShortString( appLogPushEntity.getStartTime() );
                String areaName = appLogPushEntity.getAreaName();
                String imei = appLogPushEntity.getImei();
                String appCode = ScheduledTasks.rowKeyAppUseCache.get( areaName );
                if(PowerUtil.isNull( appCode )){
                    appCode = getAppCode(areaName);
                }
                String userCode = ScheduledTasks.rowKeyImeiUseCache.get( imei );
                if(PowerUtil.isNull( userCode )){
                    userCode = getPersonCodeCode(imei);
                }
                //XCD20210101DSCX00000001
                String appLogNum = sequenceService.getNum( startTime );
                String rowKey = appCode + startTime.replaceAll( "-","" ) + userCode + appLogNum;

                appLogPushEntity.setId( CodeUtils.getUUID32());
                appLogPushEntity.setRowKey( rowKey );
                appLogPushEntities.add( appLogPushEntity );
            } catch (Exception e) {
                e.printStackTrace();
            }
            serviceLogReceiverThreadPool.execute( new Runnable() {
                @Override
                public void run(){
                    try {
                        hBaseService.save( "appLogTemp",rowKey, MapUtil.toMap( appLogPushEntity ) );

                        synchronized (lock){
                            appLogPushEntities.add( appLogPushEntity );
                            count ++;
                            if(count == 10000){
                                logger.info(appLogPushEntities.size());
                                hBaseService.batchSave( "appLogTemp", MapUtil.toListMap( appLogPushEntities ) );
                                logger.info("最终结束耗时:"+new BigDecimal(System.currentTimeMillis()  - startDate.getTime()).divide( new BigDecimal( "1000" ) ) +"秒" );
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            if (appLogPushEntities.size() == 1000) {
                hBaseService.batchSave( "appLogTemp", MapUtil.toListMap( appLogPushEntities ) );
                appLogPushEntities = new ArrayList<>();
                logger.info("最终结束耗时:"+new BigDecimal(System.currentTimeMillis()  - startDate.getTime()).divide( new BigDecimal( "1000" ) ) +"秒" );
            }
        }

    }*/



    /**
     * @description  获取appCode
     * @param  appValue
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    private static Object getAppCodeLock = new Object();
    @Transactional(rollbackOn = Exception.class)
    public  String getAppCode(String appValue) {
        synchronized(getAppCodeLock){
            while (ScheduledTasks.rowKeyAppNoUseCache.size() == 0){
                try {
                    Thread.sleep( 500 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String appCode = new ArrayList<>( ScheduledTasks.rowKeyAppNoUseCache).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyAppNoUseCache.size() - 1)) ;
            ScheduledTasks.rowKeyAppNoUseCache.remove( appCode );
            ScheduledTasks.rowKeyAppUseCache.put( appValue,appCode );
            hbaseRowkeyMarkService.updateAppCode( appCode ,appValue, ParamEnum.rowkeyType.type3.getCode());
            return appCode;
        }
    }

    /**
     * @description  获取appCode
     * @param  personCode
     * @return  返回结果
     * @date  2021-4-12 16:40
     * @author  wanghb
     * @edit
     */
    private static Object getPersonCodeCodeLock = new Object();
    @Transactional(rollbackOn = Exception.class)
    public String getPersonCodeCode(String personCode) {
        synchronized(getPersonCodeCodeLock) {
            while (ScheduledTasks.rowKeyPersonCodeNoUseCache.size() == 0){
                try {
                    Thread.sleep( 500 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String personCodeCode = new ArrayList<>( ScheduledTasks.rowKeyPersonCodeNoUseCache ).get( RandomUtil.getRandom( 0 ,ScheduledTasks.rowKeyPersonCodeNoUseCache.size() - 1)) ;
            ScheduledTasks.rowKeyPersonCodeNoUseCache.remove( personCodeCode );
            ScheduledTasks.rowKeyPersonCodeUseCache.put( personCode,personCodeCode );
            hbaseRowkeyMarkService.updateAppCode( personCodeCode ,personCode, ParamEnum.rowkeyType.type2.getCode());
            return personCodeCode;
        }
    }


    /**
     * @description  保存1
     * @param  params
     * @return  返回结果
     * @date  2021-4-12 16:49
     * @author  wanghb
     * @edit
     */
    public Result save(Map<String, Object> params) {
        save2(  );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
     * @description  第一种模拟数据保存
     * @param
     * @return  返回结果
     * @date  2021-4-27 9:51
     * @author  wanghb
     * @edit
     */
    private void save1() {
        List<AppLogPushEntity> list = new ArrayList<>();
        List<List<Object>> listDate = new ArrayList<>();
        /*listDate.add(Arrays.asList( "应用1","A","南京市","2021-01-11","10000","15" ));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-01-10","8000","10" ));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-01-09","5000","12" ));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-01-08","12000","8" ));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-01-07","7000","14" ));

        listDate.add(Arrays.asList( "应用1","A","南京市","2021-02-11","10000","15" ));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-02-10","8000","10" ));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-02-09","5000","12" ));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-02-08","12000","8" ));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-02-07","7000","14" ));

        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-01-11","13000","6" ));
        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-01-10","3000","12" ));
        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-01-09","4000","5" ));
        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-01-08","12000","7" ));
        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-01-07","7000","4" ));

        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-01-11","19000","12" ));
        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-01-10","17000","7" ));
        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-01-09","3000","8" ));
        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-01-08","8000","8" ));
        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-01-07","10000","9" ));

        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-01-11","12000","13" ));
        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-01-10","7000","9" ));
        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-01-09","9000","6" ));
        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-01-08","6000","12" ));
        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-01-07","7000","10" ));*/

        /*listDate.add(Arrays.asList( "应用5","E","苏州市","2021-01-11","8000","11" ));
        listDate.add(Arrays.asList( "应用5","E","苏州市","2021-01-10","3000","6" ));
        listDate.add(Arrays.asList( "应用5","E","苏州市","2021-01-09","12000","12" ));
        listDate.add(Arrays.asList( "应用5","E","苏州市","2021-01-08","20000","9" ));
        listDate.add(Arrays.asList( "应用5","E","苏州市","2021-01-07","15000","10" ));

        for (int x = 0; x < listDate.size(); x++) {
            List<Object> temp = listDate.get(x);
            Date date = DateUtil.toDate( PowerUtil.getString( temp.get( 3 ) ),DateUtil.DATE_SHORT);
            Integer imeiCount = RandomUtil.getRandom( 10, 15 );
            Integer imeiOpenCount = 0;
            for (int j = 1; j <= imeiCount; j++) {
                for (int y = 1; y <= 24; y++) {
                    Integer openCount = RandomUtil.getRandom( 300, 400 );
                    imeiOpenCount += openCount ;
                    for (int i = 0; i < openCount; i++) {
                        AppLogPushEntity logPushEntity = new AppLogPushEntity();
                        String nowDateStr = DateUtil.toString( date,DateUtil.DATE_SHORTS );
                        String appLogNum = sequenceService.getNum( ParamEnum.sequenceType.app.getCode() + nowDateStr.replace( "-","" ) );
                        String appValue = PowerUtil.getString( temp.get( 0 ) );
                        String areaCode = PowerUtil.getString( temp.get( 1 ) );
                        String areaName = PowerUtil.getString( temp.get( 2 ) );
                        String userValue = "0000000000000" + (j <= 9 ? "0" : "") + j;
                        String appCode = ScheduledTasks.rowKeyAppUseCache.get( appValue );
                        if(PowerUtil.isNull( appCode )){
                            appCode = getAppCode(appValue);
                        }
                        String userCode = ScheduledTasks.rowKeyImeiUseCache.get( userValue );
                        if(PowerUtil.isNull( userCode )){
                            userCode = getPersonCodeCode(userValue);
                        }
                        //旧 : XCD-A20210101DSCX00000001 新 : XCD20210101DSCX00000001
                        String rowKey = appCode + nowDateStr + userCode + appLogNum;
                        logPushEntity.setId( CodeUtils.getUUID32());
                        logPushEntity.setName(appValue);
                        logPushEntity.setCode(appCode);
                        logPushEntity.setAreaCode(areaCode);
                        logPushEntity.setAreaName(areaName);
                        logPushEntity.setImei(userValue);
                        logPushEntity.setDeviceModel( "华为Mate 30" );
                        logPushEntity.setStartTime(temp.get( 3 ) + " " + (y <= 9 ? "0" : "") + y+":00:00");

                        logPushEntity.setRowKey( rowKey );
                        logPushEntity.setDeptCode("");
                        logPushEntity.setDeptName("");
                        logPushEntity.setCompanyCode("");
                        logPushEntity.setCompanyName("");
                        logPushEntity.setBusinessType("");


                        logPushEntity.setStartPersonCode("");
                        logPushEntity.setStartPerson("");
                        logPushEntity.setNetworkEnv("");
                        logPushEntity.setVersionNum("");
                        list.add(logPushEntity);
                    }
                }
            }
            logger.info( "时间:"+temp.get( 3 )+", imei 数量:" + imeiCount + ", 启动总次数:" + imeiOpenCount );
        }
        logger.info("本次总插入"+list.size());
        logger.info("开始插入");
        hBaseService.batchSave( "appLog", MapUtil.toListMap( list ) );
        logger.info("结束插入");*/
    }

    /**
     * @description  应用日志入库
     * @param  appLogList
     * @return  返回结果
     * @date  2021-7-12 10:42
     * @author  wanghb
     * @edit
     */
    public void batchSaveKafkaData(List<ConsumerRecord<String, String>> appLogList) {
        if (appLogList == null || appLogList.size() == 0) {
            return ;
        }
        List<Map<String, Object>> list = new ArrayList<>(appLogList.size());
        for (ConsumerRecord<String, String> temp : appLogList) {
            Map<String, Object> appLog = JSON.parseObject( temp.value() );
            if (checkData(appLog)) {
                throw new RuntimeException("参数异常");
            }
            list.add(getAppLogMap(appLog,ParamEnum.networkAreaLog.networkArea3.getCode()));
        }
        if (appLogList.size() > 0) {
            logger.info("===>  hbase存入的数据 "+JSON.toJSONString( list ));
            hBaseService.batchSave( ParamEnum.topic.appLog.getHbaseTableName(),list );
        }
    }


    /**
     * @description  校验数据
     * @param  appLog
     * @return  返回结果
     * @date  2021-7-24 11:33
     * @author  wanghb
     * @edit
     */
    public Boolean checkData(Map<String, Object> appLog) {
        String code = PowerUtil.getString( appLog.get( "code" ) );
        String startTime = PowerUtil.getString( appLog.get( "startTime" ) );
        String personCode = PowerUtil.getString( appLog.get( "startPersonCode" ) );
        String personName = PowerUtil.getString( appLog.get( "startPerson" ) );
        return "".equals( code ) || "".equals( startTime ) || "".equals( personCode ) || "".equals( personName );
    }


    /**
     * @description  应用日志入库(ftp)
     * @param  appLogList
     * @return  返回结果
     * @date  2021-7-12 10:42
     * @author  wanghb
     * @edit
     */
    public void batchSaveListData(List<Map<String, Object>> appLogList) {
        for (Map<String, Object>  appLog : appLogList) {
            if (checkData(appLog)) {
                throw new RuntimeException("参数异常");
            }
            getAppLogMap(appLog,ParamEnum.networkAreaLog.networkArea2.getCode());
        }
        if (appLogList.size() > 0) {
            hBaseService.batchSave( ParamEnum.topic.appLog.getHbaseTableName(),appLogList );
        }
    }


    /**
     * @description  应用日志入库
     * @param  temp
     * @return  返回结果
     * @date  2021-7-12 10:42
     * @author  wanghb
     * @edit
     */
    @Transactional
    public void saveData(ConsumerRecord<String, String> temp) {
        Map<String, Object> appLog = null;
        try {
            appLog = JSON.parseObject( temp.value() );
            if (checkData(appLog)) {
                throw new RuntimeException("参数异常");
            }
            Map<String, Object> appLogMap = getAppLogMap(appLog,ParamEnum.networkAreaLog.networkArea3.getCode());
            String rowKey = PowerUtil.getString( appLogMap.get( "rowKey" ) );
            hBaseService.save( ParamEnum.topic.appLog.getHbaseTableName(),rowKey,appLogMap );
        } catch (IOException e) {
            AppLogDirtyDataEntity appLogDirtyDataEntity = new AppLogDirtyDataEntity();
            appLogDirtyDataEntity.setErrorData( JSON.toJSONString( appLog ) );
            appLogDirtyDataEntity.setType( ParamEnum.dirtyDataType.type0.getCode() );
            appLogDirtyDataEntity.setStatus( ParamEnum.dirtyDataStatus.status0.getCode() );
            appLogDirtyDataEntity.setErrorMsg( e.getMessage() );
            MapUtil.setCreateBean( appLogDirtyDataEntity, CodeUtils.getUUID32(),new Date() );
            baseDao.save( appLogDirtyDataEntity );
            e.printStackTrace();
        }
    }

    /**
     * @description  构建hbase数据
     * @param  appLog
     * @return  返回结果
     * @date  2021-7-19 11:37
     * @author  wanghb
     * @edit
     */
    private Map<String, Object> getAppLogMap(Map<String, Object> appLog,String networkAreaLog) {
        String appId = PowerUtil.getString( appLog.get( "code" ) );
        String startTime = PowerUtil.getString( appLog.get( "startTime" ) );
        String personCode = PowerUtil.getString( appLog.get( "startPersonCode" ) );
        String nowDateStr = "";
        if(!"".equals( startTime )){
            nowDateStr = DateUtil.toDate( Long.parseLong( PowerUtil.getString( appLog.get( "startTime" ))),DateUtil.DATE_SHORTS ) ;
        }
        appId = appId.length() > 40 ? appId :  ScheduledTasks.appInfoObjIdToId.get( appId );
        String appCode = ScheduledTasks.rowKeyAppUseCache.get( appId );
        if(PowerUtil.isNull( appCode )){
            appCode = getAppCode(appId);
        }
        String personCodeCode = ScheduledTasks.rowKeyPersonCodeUseCache.get( personCode );
        if(PowerUtil.isNull( personCodeCode )){
            personCodeCode = getPersonCodeCode(personCode);
        }
        String appLogNum = sequenceService.getNum( ParamEnum.sequenceType.app.getCode() + nowDateStr.replace( "-","" ) );
        //20210101-XCD~DSCX00000001
        //日期反转+应用code+imeiCode
        String nowDateRowkey = new StringBuilder( nowDateStr.replaceAll( "-", "" ) ).reverse().toString();
        String rowKey = nowDateRowkey +"-"+ appCode + "~" + personCodeCode + appLogNum;
        appLog.put( "rowKey",rowKey );
        if (!appLog.containsKey( networkAreaLog)) {
            appLog.put( "networkAreaLog",networkAreaLog );
        }
        return appLog;

    }

    /**
     * @description  第二种模拟数据保存
     * @param
     * @return  返回结果
     * @date  2021-4-27 9:51
     * @author  wanghb
     * @edit
     */
    private void save2() {
        /*List<AppLogPushEntity> list = new ArrayList<>();
        List<List<Object>> listDate = new ArrayList<>();
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-04-01"));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-04-02"));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-04-03"));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-04-04"));
        listDate.add(Arrays.asList( "应用1","A","南京市","2021-04-05"));

        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-04-01"));
        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-04-02"));
        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-04-03"));
        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-04-04"));
        listDate.add(Arrays.asList( "应用2","K","扬州市","2021-04-05"));

        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-04-01"));
        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-04-02"));
        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-04-03"));
        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-04-04"));
        listDate.add(Arrays.asList( "应用3","B","无锡市","2021-04-05"));

        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-04-01"));
        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-04-02"));
        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-04-03"));
        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-04-04"));
        listDate.add(Arrays.asList( "应用4","N","宿迁市","2021-04-05"));

        listDate.add(Arrays.asList( "应用5","E","苏州市","2021-04-01"));
        listDate.add(Arrays.asList( "应用5","E","苏州市","2021-04-02"));
        listDate.add(Arrays.asList( "应用5","E","苏州市","2021-04-03"));
        listDate.add(Arrays.asList( "应用5","E","苏州市","2021-04-04"));
        listDate.add(Arrays.asList( "应用5","E","苏州市","2021-04-05"));


        for (int x = 0; x < listDate.size(); x++) {
            List<Object> temp = listDate.get(x);
            Date date = DateUtil.toDate( PowerUtil.getString( temp.get( 3 ) ),DateUtil.DATE_SHORT);
            Integer modelCount = RandomUtil.getRandom( 4, 10 );
            Integer openSumCount = 0;
            for (int j = 1; j <= modelCount; j++) {
                Integer versionCount = RandomUtil.getRandom( 3, 7 );
                for (int y = 1; y <= versionCount; y++) {
                    Integer openCount = RandomUtil.getRandom( 100, 200 );
                    openSumCount += openCount ;
                    for (int i = 0; i < openCount; i++) {
                        AppLogPushEntity logPushEntity = new AppLogPushEntity();
                        String nowDateStr = DateUtil.toString( date,DateUtil.DATE_SHORTS );
                        String appLogNum = sequenceService.getNum( nowDateStr );
                        String appValue = PowerUtil.getString( temp.get( 0 ) );
                        String areaCode = PowerUtil.getString( temp.get( 1 ) );
                        String areaName = PowerUtil.getString( temp.get( 2 ) );
                        String deviceModel = "model" + j;
                        String versionNum = "version" + y;
                        String userValue = "000000000000001";
                        String appCode = ScheduledTasks.rowKeyAppUseCache.get( appValue );
                        if(PowerUtil.isNull( appCode )){
                            appCode = getAppCode(appValue);
                        }
                        String userCode = ScheduledTasks.rowKeyImeiUseCache.get( userValue );
                        if(PowerUtil.isNull( userCode )){
                            userCode = getImeiCode(userValue);
                        }
                        //旧 : XCD-A20210101DSCX00000001 新 : XCD20210101DSCX00000001
                        String rowKey = appCode + nowDateStr + userCode + appLogNum;
                        logPushEntity.setId( CodeUtils.getUUID32());
                        logPushEntity.setName(appValue);
                        logPushEntity.setCode(appCode);
                        logPushEntity.setAreaCode(areaCode);
                        logPushEntity.setAreaName(areaName);
                        logPushEntity.setImei(userValue);
                        logPushEntity.setDeviceModel( deviceModel );
                        logPushEntity.setVersionNum(versionNum);
                        logPushEntity.setStartTime(temp.get( 3 )+" 00:00:00");

                        logPushEntity.setRowKey( rowKey );
                        logPushEntity.setDeptCode("");
                        logPushEntity.setDeptName("");
                        logPushEntity.setCompanyCode("");
                        logPushEntity.setCompanyName("");
                        logPushEntity.setBusinessType("");

                        logPushEntity.setStartPersonCode("");
                        logPushEntity.setStartPerson("");
                        logPushEntity.setNetworkEnv("");

                        list.add(logPushEntity);
                    }
                }
                logger.info( "应用名称:"+temp.get( 0 )+"时间:"+temp.get( 3 )+", 型号数量:" + modelCount +", 版本数量:" + versionCount );
            }

            logger.info( temp.get( 0 ) + "启动总次数:" + openSumCount );
        }
        logger.info("本次总插入"+list.size());
        logger.info("开始插入");
        hBaseService.batchSave( "appLog", MapUtil.toListMap( list ) );
        logger.info("结束插入");*/
    }


}
