package com.acrabsoft.web.service.sjt.pc.operation.web.kafka.service;

import com.acrabsoft.web.annotation.LOG;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.AppLogPushEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.ServiceLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.HbaseRowkeyMarkService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.*;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.KafkaProperties;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.LoginUtil;
import org.acrabsoft.utils.StringUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Service("kafkaService")
public class KafkaService {
    Logger logger = LoggerFactory.getLogger( KafkaService.class);
    @Resource
    private HbaseRowkeyMarkService hbaseRowkeyMarkService;
    @Resource
    private AppLogService appLogService;
    @Resource
    private ServiceLogService servicLogService;
    @Resource
    private HBaseService hBaseService;
    @Resource
    private SequenceService sequenceService;
    @Value("${spring.profiles.active}")
    public String active;



    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.properties.group.id}")
    private String groupId;
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private String enableAutoCommit;
    @Value("${spring.kafka.consumer.auto-commit-interval-ms}")
    private String autoCommitIntervalMs;
    @Value("${spring.kafka.consumer.properties.session.timeout.ms}")
    private String sessionTimeoutMs;
    @Value("${huaweiUser}")
    private String huaweiUser;
    @Value("${huawei.kafka.configs.folder}")
    private String huaweiConfigsFolder;
    volatile public static Boolean isOpen = true;



    /**
     * @description  开始监听消息
     * @param
     * @return  返回结果
     * @date  2021-7-13 20:11
     * @author  wanghb
     * @edit
     */
    public void startListen() {
        if (ParamEnum.active.prd.getCode().equals( active )) {
            new Thread( () -> {
                onReceiver(ParamEnum.topic.appLog.getKafkaTopic());
            }).start();
            new Thread( () -> {
                onReceiver(ParamEnum.topic.serviceLog.getKafkaTopic());
            }).start();
            new Thread( () -> {
                onReceiver(ParamEnum.topic.customizeAppLog.getKafkaTopic());
            }).start();
        }
    }


    /**
     * @description  消费者线程
     * @param  kafkaTopic
     * @return  返回结果
     * @date  2021-7-13 20:09
     * @author  wanghb
     * @edit
     */
    public void  onReceiver(String kafkaTopic) {
        KafkaConsumer<String, String> consumer = getKafkaConsumer(kafkaTopic);
        consumer.subscribe(Collections.singletonList(kafkaTopic));
        Boolean isOpen = true;
        while (isOpen) {
            try {
                Thread.sleep( 180000 );//180000
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("===>  KafkaService.onReceiver 开始监听:" + kafkaTopic);
            List<ConsumerRecord<String, String>> appLogList = new ArrayList<>();
            List<ConsumerRecord<String, String>> serviceLogList = new ArrayList<>();
            try {
                ConsumerRecords<String, String> records = consumer.poll( 1000 );
                Integer count = 0;
                List<String> jsons = new ArrayList<>();
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("===>  KafkaService.onReceiver 消费:"+record.value());
                    jsons.add( record.value() );
                    if (ParamEnum.topic.appLog.getKafkaTopic().equals( kafkaTopic )) {
                        appLogList.add( record );
                    } else if (ParamEnum.topic.serviceLog.getKafkaTopic().equals( kafkaTopic )) {
                        serviceLogList.add( record );
                    }
                    //Map<String,Object> params = MapUtil.toMap(  record.value());
                    count++;
                }
                if (ParamEnum.topic.appLog.getKafkaTopic().equals( kafkaTopic )) {
                    appLogService.batchSaveKafkaData( appLogList );
                } else if (ParamEnum.topic.serviceLog.getKafkaTopic().equals( kafkaTopic )) {
                    servicLogService.batchSaveKafkaData( serviceLogList );
                }
                if(count != 0){
                    logger.info("本次消费主题:"+kafkaTopic+",消费条数:"+count+"条数据.");
                }
            }catch (Exception e){
                logger.info("===>  KafkaService.onReceiver 异常:"+e.getMessage());
                //批量异常的话 进行单独数据处理
                if (ParamEnum.topic.appLog.getKafkaTopic().equals( kafkaTopic )) {
                    for (ConsumerRecord<String, String> temp : appLogList) {
                        appLogService.saveData( temp );
                    }
                } else if (ParamEnum.topic.serviceLog.getKafkaTopic().equals( kafkaTopic )) {
                    for (ConsumerRecord<String, String> temp : serviceLogList) {
                        servicLogService.saveData( temp );
                    }
                }
                e.printStackTrace();
            }
            logger.info("===>  KafkaService.onReceiver 结束监听.");
            consumer.commitSync();
            //consumer.close();
        }
    }

    public void consumerClose() {
        kafkaConsumer.close();
    }

    KafkaConsumer<String, String> kafkaConsumer = null;
    /**
     * @description  获取消费者配置
     * @param
     * @return  返回结果
     * @date  2021-7-13 20:07
     * @author  wanghb
     * @edit
     */
    public synchronized KafkaConsumer<String, String> getKafkaConsumer() {
        if (null == kafkaConsumer)
        {
            Properties props = new Properties();

            if (ParamEnum.active.dev.getCode().equals( active )) {
                props.put("bootstrap.servers",bootstrapServers);
                props.put("group.id", groupId);
                // 是否自动确认offset
                props.put("enable.auto.commit", "false");
                // 自动确认offset的时间间隔
                props.put("auto.commit.interval.ms", autoCommitIntervalMs);
                props.put("session.timeout.ms", sessionTimeoutMs);
                props.put("auto.offset.reset", "latest");
                // 序列化类
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            }else{
                KafkaProperties kafkaProperties = KafkaProperties.getInstance(huaweiConfigsFolder);
                // Broker连接地址
                props.put("bootstrap.servers",kafkaProperties.getValues("bootstrap.servers", "localhost:21007"));
                // Group id
                props.put("group.id",groupId);//"acrabsoft-consumer"
                // 是否自动提交offset
                props.put("enable.auto.commit", "false");
                // 自动提交offset的时间间隔
                //props.put(autoCommitIntervalMs, "1000");
                // 会话超时时间
                props.put("session.timeout.ms", "30000");
                // 消息Key值使用的反序列化类
                props.put("key.deserializer","org.apache.kafka.common.serialization.IntegerDeserializer");
                // 消息内容使用的反序列化类
                props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
                // 安全协议类型
                props.put("security.protocol", kafkaProperties.getValues("security.protocol", "SASL_PLAINTEXT"));
                // 服务名
                props.put("sasl.kerberos.service.name", "kafka");
                // 域名
                props.put("kerberos.domain.name", kafkaProperties.getValues("kerberos.domain.name", "hadoop.hadoop.com"));
                // 如果没有消费过，从第一条消息开始消费
                props.put("auto.offset.reset", "earliest");
                if (this.isSecurityModel()) {
                    try {
                        logger.info("消费者进入securityPrepare该方法");
                        this.securityPrepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("进行华为Kafka消费者初始化时出错！错误信息见上方↑↑↑↑↑↑↑↑↑↑打印的堆栈");
                    }
                }
                logger.info("============================>kafk消费者配置初始化成功<============================");
            }
            kafkaConsumer = new KafkaConsumer<>(props);
            return kafkaConsumer;
        }
        return kafkaConsumer;
    }


    //键 : topci  值 : KafkaConsumer对象
    Map<String, KafkaConsumer<String, String>> kafkaConsumerCache = new HashMap<>();
    /**
     * @description  获取消费者配置
     * @param
     * @return  返回结果
     * @date  2021-7-13 20:07
     * @author  wanghb
     * @edit
     */
    public synchronized KafkaConsumer<String, String> getKafkaConsumer(String kafkaTopic) {
        if (!kafkaConsumerCache.containsKey( kafkaTopic )){
            Properties props = new Properties();

            if (ParamEnum.active.dev.getCode().equals( active )) {
                props.put("bootstrap.servers",bootstrapServers);
                props.put("group.id", groupId);
                // 是否自动确认offset
                props.put("enable.auto.commit", "false");
                // 自动确认offset的时间间隔
                props.put("auto.commit.interval.ms", autoCommitIntervalMs);
                props.put("session.timeout.ms", sessionTimeoutMs);
                props.put("auto.offset.reset", "latest");
                // 序列化类
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

            }else{
                KafkaProperties kafkaProperties = KafkaProperties.getInstance(huaweiConfigsFolder);
                // Broker连接地址
                props.put("bootstrap.servers",kafkaProperties.getValues("bootstrap.servers", "localhost:21007"));
                // Group id
                props.put("group.id",groupId);//"acrabsoft-consumer"
                // 是否自动提交offset
                props.put("enable.auto.commit", "false");
                // 自动提交offset的时间间隔
                //props.put(autoCommitIntervalMs, "1000");
                // 会话超时时间
                props.put("session.timeout.ms", "30000");
                // 消息Key值使用的反序列化类
                props.put("key.deserializer","org.apache.kafka.common.serialization.IntegerDeserializer");
                // 消息内容使用的反序列化类
                props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
                // 安全协议类型
                props.put("security.protocol", kafkaProperties.getValues("security.protocol", "SASL_PLAINTEXT"));
                // 服务名
                props.put("sasl.kerberos.service.name", "kafka");
                // 域名
                props.put("kerberos.domain.name", kafkaProperties.getValues("kerberos.domain.name", "hadoop.hadoop.com"));
                // 如果没有消费过，从第一条消息开始消费
                props.put("auto.offset.reset", "earliest");
                if (this.isSecurityModel()) {
                    try {
                        logger.info("消费者进入securityPrepare该方法");
                        this.securityPrepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("进行华为Kafka消费者初始化时出错！错误信息见上方↑↑↑↑↑↑↑↑↑↑打印的堆栈");
                    }
                }
                logger.info("============================>kafk消费者配置初始化成功<============================");
            }
            kafkaConsumerCache.put(kafkaTopic,new KafkaConsumer<>(props));
        }
        return kafkaConsumerCache.get( kafkaTopic );


    }


    private Boolean isSecurityModel(){
        if (StringUtil.isNullBlank(huaweiConfigsFolder)) {
            huaweiConfigsFolder = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "kafka" + File.separator;
        } else {
            if (!huaweiConfigsFolder.endsWith(File.separator)) {
                huaweiConfigsFolder = huaweiConfigsFolder + File.separator;
            }
        }
        logger.info("==================>filePath"+huaweiConfigsFolder);
        String krbFilePath = huaweiConfigsFolder + "kafkaSecurityMode";
        Properties securityProps = new Properties();
        // file does not exist.
        File file = new File(krbFilePath);
        if (!file.exists()){
            logger.info("==================>文件没有创建"+krbFilePath);
            return false;
        }
        try{
            securityProps.load(new FileInputStream(krbFilePath));
            if ("yes".equalsIgnoreCase(securityProps.getProperty("kafka.client.security.mode")))
            {
                return true;
            }
            logger.info("==================>no");
        }
        catch (Exception e)
        {
            //LOG.info("The Exception occured : {}.", e);
        }
        return false;
    }

    private void securityPrepare() throws IOException{
        String filePath = SpringContextUtil.getApplicationContext().getEnvironment().getProperty("huawei.kafka.configs.folder");
        if (StringUtil.isNullBlank(filePath)) {
            filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "kafka" + File.separator;
        } else {
            if (!filePath.endsWith(File.separator)) {
                filePath = filePath + File.separator;
            }
        }
        //String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources"+ File.separator + "kafka" + File.separator;
        String krbFile = filePath + "krb5.conf";
        String userKeyTableFile = filePath + "user.keytab";
        //windows路径下分隔符替换
        userKeyTableFile = userKeyTableFile.replace("\\", "\\\\");
        krbFile = krbFile.replace("\\", "\\\\");
        LoginUtil.setKrb5Config(krbFile);
        LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
        LoginUtil.setJaasFile(huaweiUser, userKeyTableFile);
        logger.info("==================>userKeyTableFile"+userKeyTableFile);
        logger.info("==================>安全登录完成");
    }



}
