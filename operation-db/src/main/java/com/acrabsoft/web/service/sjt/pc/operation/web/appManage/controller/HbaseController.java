package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.controller;

import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.AppDeviceCountEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HiveService;
import com.acrabsoft.web.service.sjt.pc.operation.web.kafka.service.KafkaService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.DateUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.JdbcTemplateUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MapUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
* 表控制类( HbaseController )控制类
* @author wanghbdeptName
* @since 2020-11-23 14:34:51
*/
@RestController
@RequestMapping("/appManage/hbase")
@Api(tags = "表控制类")
public class HbaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    protected HttpServletResponse response;
    @Resource
    protected HBaseService hBaseService;
    @Resource
    protected HiveService hiveService;
    @Resource
    @Qualifier("hiveJdbcTemplate")
    private JdbcTemplate hiveJdbcTemplate;
    @Resource
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate oracleJdbcTemplate;
    @Resource
    @Qualifier("oracleNameJdbcTemplate")
    private NamedParameterJdbcTemplate oracleNameJdbcTemplate;
    @Resource
    @Qualifier("mysqlJdbcTemplate")
    private JdbcTemplate mysqlJdbcTemplate;
    @Resource
    @Qualifier("mysqlNameJdbcTemplate")
    private NamedParameterJdbcTemplate mysqlNameJdbcTemplate;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private SequenceService sequenceService;

    @Resource
    private AppLogService appLogService;
    @Resource
    private KafkaService kafkaService;


    /**
    * @description   添加表
    * @return 无返回值
    * @date 2020-9-24 15:56:18
    * @author wanghb
    * @edit
    */
    @PostMapping("/addTable")
    @ResponseBody
    @ApiOperation(value = "添加表", notes = "添加表")
    public Result addTable() {
        //hBaseService.createTable( "serviceLog" );
        hBaseService.createTable( "appLogTemp" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
    * @description   创建关联表
    * @return 无返回值
    * @date 2020-9-24 15:56:18
    * @author wanghb
    * @edit
    */
    @PostMapping("/addContactTable")
    @ResponseBody
    @ApiOperation(value = "创建关联表", notes = "创建关联表")
    public Result addContactTable() {
        hiveJdbcTemplate.update( "create external table hbase_appLog(key string, name string, code string,areaCode string, areaName string,deptCode string, deptName string,remark string, theCompanyCode string,theCompanyName string, responsible string,phoe string, type string,businessType string, deleted string,createUser string, createTime string,updateUser string, updateTime string) " +
                "row format serde 'org.apache.hadoop.hive.hbase.HBaseSerDe' " +
                "stored by 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' with " +
                "serdeproperties('hbase.columns.mapping'=':key,info:name,info:code,info:areaCode,info:areaName,info:deptCode,info:deptName,info:remark,info:theCompanyCode,info:theCompanyName,info:responsible,info:phoe,info:type,info:businessType,info:deleted,info:createUser,info:createTime,info:updateUser,info:updateTime') " +
                "tblproperties('hbase.table.name'='appLog')" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description   测试
     * @return 无返回值
     * @date 2020-9-24 15:56:18
     * @author wanghb
     * @edit
     */
    @GetMapping ("/test")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "测试", notes = "测试")
    @Transactional
    public Result test() throws ExecutionException, InterruptedException {
        List<Map<String,Object>> maps = mysqlNameJdbcTemplate.queryForList( "select * from ha_module_type" ,new HashMap<>());
        List<Map<String,Object>> maps1 = oracleNameJdbcTemplate.queryForList( "select * from T_APP_INFO " ,new HashMap<>());
        maps1.addAll(maps);
        Date startDate = new Date();
        List<AppDeviceCountEntity> appDeviceCountEntities = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            AppDeviceCountEntity appDeviceCountEntity = new AppDeviceCountEntity();
            appDeviceCountEntity.setId( CodeUtils.getUUID32() );
            appDeviceCountEntity.setAppId( "123" );
            appDeviceCountEntities.add(appDeviceCountEntity);

        }
        List<FutureTask> futureTasks = new ArrayList<>();
        for (int i = 0; i < appDeviceCountEntities.size(); i++) {
            AppDeviceCountEntity appDeviceCountEntity = appDeviceCountEntities.get( i );
            //目的 测一下异常 是否回滚
            int finalI = i;
            FutureTask<Boolean> futureTask = new FutureTask<>( () -> {   //重写Callable接口中的call()方法
                if (finalI == 49){
                    Object o = null;
                    o.toString();
                }
                oracleNameJdbcTemplate.update( AppDeviceCountEntity.insertSql, JdbcTemplateUtil.beanPropSource( appDeviceCountEntity ) );

                return true;
            } );
            futureTasks.add(futureTask);
        }
        for (FutureTask futureTask : futureTasks) {
            new Thread(futureTask).start();
        }
        for (FutureTask futureTask : futureTasks) {
            futureTask.get();
        }

        logger.info("=================>共耗时"+(System.currentTimeMillis()  - startDate.getTime())+"毫秒");

        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    /**
    * @description   删除表
    * @return 无返回值
    * @date 2020-9-24 15:56:18
    * @author wanghb
    * @edit
    */
    @PostMapping("/delTable")
    @ResponseBody
    @ApiOperation(value = "删除表", notes = "删除表")
    public Result delTable() {
        //hBaseService.delTable( "serviceLog" );
        hBaseService.delTable( "appLogTemp" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description  查询列表
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping("/hiveList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public Result hiveList()  {
        List<Map<String, Object>> list = hiveJdbcTemplate.queryForList( "select * from hbase_appLog limit 0,10" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list);
    }


    /**
     * @description   保存
     * @return 无返回值
     * @date 2020-9-24 15:56:18
     * @author wanghb
     * @edit
     */
    @PostMapping("/hbaseSave")
    @ResponseBody
    @ApiOperation(value = "保存", notes = "保存")
    public Result hbaseSave() {
        return appLogService.save( new HashMap<>() );
    }




    /**
     * @description  查询列表
     * @param
     * @return  返回结果 SXDG20210331XC
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping("/hbaseList")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public Result hbaseList()  {
        List<Map<String, String>> list = new ArrayList<>();
        Long appLogCount = 0L;
        //XCD-A20210101DSCX00000001
        //应用1	BBA-A 应用2	BBC-K 应用3	BBB-B 应用4	BBE-N 应用5	BBD-E
        list = hBaseService.getListMap( "appLog", ".*20210107.*");

        //list = hBaseService.getListMap( "serviceLog");
        //list = hBaseService.getListMap( "appLog", ".*XC000000.*");
        //list = hBaseService.getListMap( "appLog",null, "BBA-A202102", "BBA-A202103");

        //appLogCount = hBaseService.getCount( "appLogTemp", ".*20210401.*" );
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool( 5 );


        /*String[] dates = new String[]{"20210111","20210110","20210109","20210108","20210107"};//
        String[] apps = new String[]{"1JC","J1S","0WS","EZ2","XU9"};
        for (int i = 0; i < apps.length; i++) {
            String app = apps[i];
            fixedThreadPool.execute( new Runnable() {
                @Override
                public void run() {
                    List<Map<String, String>> listTemp = hBaseService.getListMap( "serviceLog", app + ".*");
                    logger.info(app + "=================>" + listTemp.size());
                }
            } );
            *//*for (int j = 0; j < dates.length; j++) {
                String date = dates[j];
                String pararm = app + date;
                fixedThreadPool.execute( new Runnable() {
                    @Override
                    public void run() {
                        List<Map<String, String>> listTemp = hBaseService.getListMap( "serviceLog", pararm + ".*");
                        logger.info(pararm + "=================>" + listTemp.size());
                    }
                } );
            }*//*
        }*/
        logger.info(appLogCount+"");
        logger.info(list.size()+"");
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list.size() != 0 ? list.get(0) : "");
    }


    /**
     * @description   删除关联表
     * @return 无返回值
     * @date 2020-9-24 15:56:18
     * @author wanghb
     * @edit
     */
    @PostMapping("/delContactTable")
    @ResponseBody
    @ApiOperation(value = "删除关联表", notes = "删除关联表")
    public Result delContactTable() {
        hiveJdbcTemplate.update( "drop table hbase_appLog" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers","122.51.121.254:9092");
        props.put("group.id", "kafka-producer");
        /* 是否自动确认offset */
        props.put("enable.auto.commit", "true");
        /* 自动确认offset的时间间隔 */
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
//        props.put("auto.offset.reset", "earliest");
        props.put("auto.offset.reset", "latest");

        // 序列化类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("appLog"));

        try {
            Boolean isOpen = true;
            while (isOpen){
                ConsumerRecords<String, String> records = consumer.poll( 100);
                for (ConsumerRecord<String, String> record : records){
                            System.out.printf("消费消息：topic=%s, partition=%d, offset=%d, key=%s, value=%s\n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }

            }
        } finally {
            consumer.close();
        }
    }

}
