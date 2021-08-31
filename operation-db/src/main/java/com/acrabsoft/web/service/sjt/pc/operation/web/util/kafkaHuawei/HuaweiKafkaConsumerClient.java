package com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.SpringContextUtil;
import com.alibaba.fastjson.JSON;
import kafka.utils.ShutdownableThread;
import org.acrabsoft.utils.LogUtil;
import org.acrabsoft.utils.RandomUtils;
import org.acrabsoft.utils.StringUtil;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

/**
 * 专门针对华为Kafka队列封装的生产者客户端
 * 这个对象的实例化交给了Spring容器来管理，具体见configration包中的HuaweiKafkaConfiration.java
 */
public class HuaweiKafkaConsumerClient {
    private static final Logger logger = LoggerFactory.getLogger(HuaweiKafkaConsumerClient.class);

    private final KafkaConsumer<Integer, String> consumer;

    // 一次请求的最大等待时间
    private final int waitTime = 1000;
    // Broker连接地址
    private final String bootstrapServers = "bootstrap.servers";
    // 消息内容使用的反序列化类
    private final String valueDeserializer = "value.deserializer";
    // 消息Key值使用的反序列化类
    private final String keyDeserializer = "key.deserializer";
    // 协议类型:当前支持配置为SASL_PLAINTEXT或者PLAINTEXT
    private final String securityProtocol = "security.protocol";
    // 服务名
    private final String saslKerberosServiceName = "sasl.kerberos.service.name";
    // 域名
    private final String kerberosDomainName = "kerberos.domain.name";
    // 是否自动提交offset
    private final String enableAutoCommit = "enable.auto.commit";
    // 自动提交offset的时间间隔
    private final String autoCommitIntervalMs = "auto.commit.interval.ms";
    // 会话超时时间
    private final String sessionTimeoutMs = "session.timeout.ms";

    //用户自己申请的机机账号keytab文件名称-
    private static final String USER_KEYTAB_FILE = "user.keytab";

    @Value("${huaweiUser}")
    private String huaweiUser;
    // 当前消费者是否可用
    private boolean isConsumerEnabled;
    // Key序列化类
    private final String keySerializer = "key.serializer";
    // Value序列化类
    private final String valueSerializer = "value.serializer";


    /**
     * @description
     * @param
     * @return  返回结果
     * @date  2021-7-8 14:26
     * @author  wanghb
     * @edit
     */
    public HuaweiKafkaConsumerClient(){
        Properties props = new Properties();
        String active = SpringContextUtil.getApplicationContext().getEnvironment().getProperty("spring.profiles.active");
        if (ParamEnum.active.dev.getCode().equals( active )) {
            props.put( ProducerConfig.ACKS_CONFIG, "0");
            // 会话超时时间
            props.put("session.timeout.ms", "30000");
            // 消息Key值使用的反序列化类
            props.put("key.deserializer","org.apache.kafka.common.serialization.IntegerDeserializer");
            // 消息内容使用的反序列化类
            props.put(valueSerializer, "org.apache.kafka.common.serialization.StringSerializer");
            props.put(keySerializer, "org.apache.kafka.common.serialization.IntegerSerializer");
            props.put(valueDeserializer,"org.apache.kafka.common.serialization.StringDeserializer");
            props.put("group.id", SpringContextUtil.getApplicationContext().getEnvironment().getProperty("spring.kafka.consumer.properties.group.id"));
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SpringContextUtil.getApplicationContext().getEnvironment().getProperty("spring.kafka.bootstrap-servers"));
            props.put(ProducerConfig.RETRIES_CONFIG, SpringContextUtil.getApplicationContext().getEnvironment().getProperty("spring.kafka.producer.retries"));
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, SpringContextUtil.getApplicationContext().getEnvironment().getProperty("spring.kafka.producer.batch-size"));
            props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
            //props.put(ProducerConfig.BUFFER_MEMORY_CONFIG,1024 );

        }else{
            KafkaProperties kafkaProperties = KafkaProperties.getInstance(null);
            // Broker连接地址
            props.put(bootstrapServers,kafkaProperties.getValues(bootstrapServers, "localhost:21007"));

            // Group id
            //props.put(groupId,"acrabsoft-consumer");
            props.put("group.id", SpringContextUtil.getApplicationContext().getEnvironment().getProperty("spring.kafka.consumer.properties.group.id"));
            // 是否自动提交offset
            props.put(enableAutoCommit, "false");
            // 自动提交offset的时间间隔
            //props.put(autoCommitIntervalMs, "1000");
            // 会话超时时间
            props.put(sessionTimeoutMs, "30000");
            // 消息Key值使用的反序列化类
            props.put(keyDeserializer,"org.apache.kafka.common.serialization.IntegerDeserializer");
            // 消息内容使用的反序列化类
            props.put(valueDeserializer,"org.apache.kafka.common.serialization.StringDeserializer");
            // 安全协议类型
            props.put(securityProtocol, kafkaProperties.getValues(securityProtocol, "SASL_PLAINTEXT"));
            // 服务名
            props.put(saslKerberosServiceName, "kafka");
            // 域名
            props.put(kerberosDomainName, kafkaProperties.getValues(kerberosDomainName, "hadoop.hadoop.com"));
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
        }
        consumer = new KafkaConsumer<>(props);
        logger.info("============================>kafk消费者配置初始化成功<============================");
    }


    /**
     * @description  开始监听
     * @param  topics
     * @param  callback
     * @return  返回结果
     * @date  2021-7-8 14:23
     * @author  wanghb
     * @edit
     */
    public void startListen(Collection<String> topics, kafkaMsgReceiver callback) {
        // 监听是阻塞线程的，所以这里单独开一个线程去监听消息
        new ShutdownableThread("huawei-kafka-consumer-" + RandomUtils.getCode(), true) {
            @Override
            public void doWork() {
                consumer.subscribe(topics);
                isConsumerEnabled = true;
                //consumer.subscribe(Collections.singletonList(topicName));
                while (isConsumerEnabled) {
                    //logger.info("消费心跳=========================>"+isConsumerEnabled);
                    ConsumerRecords<Integer, String> records = consumer.poll(waitTime);
                    if (records != null && !records.isEmpty()) {
                        try {
                            callback.onReceive(records, consumer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                LogUtil.info("消费者已被关闭，本实例将停止消费topic:" + JSON.toJSONString(topics) + "，请检查上方的报错信息....");
            }
        }.start();
    }


    private Boolean isSecurityModel(){
        String filePath = SpringContextUtil.getApplicationContext().getEnvironment().getProperty("huawei.kafka.configs.folder");
        if (StringUtil.isNullBlank(filePath)) {
            filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "kafka" + File.separator;
        } else {
            if (!filePath.endsWith(File.separator)) {
                filePath = filePath + File.separator;
            }
        }
        logger.info("==================>filePath"+filePath);
        String krbFilePath = filePath + "kafkaSecurityMode";
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
        logger.info("==================>1");
        String filePath = SpringContextUtil.getApplicationContext().getEnvironment().getProperty("huawei.kafka.configs.folder");
        if (StringUtil.isNullBlank(filePath)) {
            filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "kafka" + File.separator;
        } else {
            if (!filePath.endsWith(File.separator)) {
                filePath = filePath + File.separator;
            }
        }
        logger.info("==================>2");
        //String filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources"+ File.separator + "kafka" + File.separator;
        String krbFile = filePath + "krb5.conf";
        String userKeyTableFile = filePath + USER_KEYTAB_FILE;
        logger.info("==================>3");
        //windows路径下分隔符替换
        userKeyTableFile = userKeyTableFile.replace("\\", "\\\\");
        krbFile = krbFile.replace("\\", "\\\\");
        logger.info("==================>4");
        LoginUtil.setKrb5Config(krbFile);
        LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
        LoginUtil.setJaasFile(huaweiUser, userKeyTableFile);
        logger.info("==================>userKeyTableFile"+userKeyTableFile);
        logger.info("==================>5");
    }


    /**
     * @description  获取KafkaConsumer
     * @param
     * @return  返回结果
     * @date  2021-7-8 14:26
     * @author  wanghb
     * @edit
     */
    public KafkaConsumer<Integer, String> getCurrentConsumer(){
        return this.consumer;
    }

    /**
     * @description  停止监听
     * @param
     * @return  返回结果
     * @date  2021-7-8 14:25
     * @author  wanghb
     * @edit
     */
    public void stopListen(){
        if (this.consumer != null) {
            this.consumer.unsubscribe();
            this.consumer.close();
            isConsumerEnabled = false;
        }
    }
}
