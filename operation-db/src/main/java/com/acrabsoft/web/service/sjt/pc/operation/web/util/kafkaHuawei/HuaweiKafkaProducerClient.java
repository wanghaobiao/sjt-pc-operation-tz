package com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.SpringContextUtil;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;
import org.acrabsoft.utils.LogUtil;
import org.acrabsoft.utils.StringUtil;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 专门针对华为Kafka队列封装的生产者客户端
 * 这个对象的实例化交给了Spring容器来管理，具体见configration包中的HuaweiKafkaConfiration.java
 */
public class HuaweiKafkaProducerClient {

    private static final Logger logger = LoggerFactory.getLogger(HuaweiKafkaProducerClient.class);

    private final KafkaProducer<Integer, String> producer;
    private final Properties props = new Properties();
    // Broker地址列表
    private final String bootstrapServers = "bootstrap.servers";
    // 客户端ID
    private final String clientId = "client.id";
    // Key序列化类
    private final String keySerializer = "key.serializer";
    // Value序列化类
    private final String valueSerializer = "value.serializer";
    // 协议类型:当前支持配置为SASL_PLAINTEXT或者PLAINTEXT
    private final String securityProtocol = "security.protocol";
    // 服务名
    private final String saslKerberosServiceName = "sasl.kerberos.service.name";
    // 域名
    private final String kerberosDomainName = "kerberos.domain.name";
    // 消息内容使用的反序列化类
    private final String valueDeserializer = "value.deserializer";
    //默认发送20条消息
    private final int messageNumToSend = 100;
    //用户自己申请的机机账号keytab文件名称
    private static final String USER_KEYTAB_FILE = "user.keytab";
    @Value("${huaweiUser}")
    private String huaweiUser;
    private KafkaProperties kafkaProperties;

    public HuaweiKafkaProducerClient(){
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
            //props.put(ProducerConfig.BUFFER_MEMORY_CONFIG,SpringContextUtil.getApplicationContext().getEnvironment().getProperty("spring.kafka.producer.retries") );
        }else{
            if (this.isSecurityModel()) {
                try {
                    logger.info("生产者进入securityPrepare该方法");
                    this.securityPrepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("进行华为Kafka初始化时出错！错误信息见上方↑↑↑↑↑↑↑↑↑↑打印的堆栈");
                }
            }
            LogUtil.info("System.getProperty ======> " + System.getProperty("user.dir"));
            kafkaProperties = KafkaProperties.getInstance(null);
            // Broker地址列表
            props.put(bootstrapServers, kafkaProperties.getValues(bootstrapServers, "localhost:21007"));
            // 客户端ID
            props.put(clientId, kafkaProperties.getValues(clientId, "DemoProducer"));
            // Key序列化类
            props.put(keySerializer, kafkaProperties.getValues(keySerializer, "org.apache.kafka.common.serialization.IntegerSerializer"));
            // Value序列化类
            props.put(valueSerializer, kafkaProperties.getValues(valueSerializer, "org.apache.kafka.common.serialization.StringSerializer"));
            // 协议类型:当前支持配置为SASL_PLAINTEXT或者PLAINTEXT
            props.put(securityProtocol, kafkaProperties.getValues(securityProtocol, "SASL_PLAINTEXT"));
            // 服务名
            props.put(saslKerberosServiceName, "kafka");
            // 域名
            props.put(kerberosDomainName, kafkaProperties.getValues(kerberosDomainName, "hadoop.hadoop.com"));
        }

        producer = new KafkaProducer<>(props);
        logger.info("============================>kafk生产者配置初始化成功<============================");

    }

    /**
     * 发送普通消息
     * @param topicName
     * @param messageContent
     */
    public void sendMessage(String topicName, String messageContent) {
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>(topicName, messageContent);
        try {
            producer.send( producerRecord ).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description  发送消息 (异步)
     * @param  topicName
     * @param  messageContent
     * @param  callback
     * @return  返回结果
     * @date  2021-7-8 14:18
     * @author  wanghb
     * @edit
     */
    public void sendMessageAsync(String topicName, String messageContent, Callback callback) {
        //createTopic( topicName );
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<Integer, String>(topicName, messageContent);
        producer.send(producerRecord, callback);
    }


    private Boolean isSecurityModel()
    {
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
        if (!file.exists())
        {
            logger.info("==================>文件没有创建"+krbFilePath);
            return false;
        }
        try
        {
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

    public void securityPrepare() throws IOException
    {
        String filePath = SpringContextUtil.getApplicationContext().getEnvironment().getProperty("huawei.kafka.configs.folder");
        if (StringUtil.isNullBlank(filePath)) {
            filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "kafka" + File.separator;
        } else {
            if (!filePath.endsWith(File.separator)) {
                filePath = filePath + File.separator;
            }
        }
        String krbFile = filePath + "krb5.conf";
        String userKeyTableFile = filePath + USER_KEYTAB_FILE;

        //windows路径下分隔符替换
        userKeyTableFile = userKeyTableFile.replace("\\", "\\\\");
        krbFile = krbFile.replace("\\", "\\\\");

        LoginUtil.setKrb5Config(krbFile);
        LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
        LoginUtil.setJaasFile(huaweiUser, userKeyTableFile);
    }



    public void createTopic(String topicName) {
        /*String zkConnectStr = kafkaProc.getValues("zookeeper.connect", "50.16.130.3:24002,50.16.130.2:24002,50.16.130.1:24002/kafka");
        ZkClient zkClient = new ZkClient(zkConnectStr, 250000, 300000, ZKStringSerializer$.MODULE$);
        ZkConnection zkConnection = new ZkConnection(zkConnectStr);
        ZkUtils zkUtils = new ZkUtils(zkClient, zkConnection, true);*/

        LogUtil.info("create Topic 开始,....");
        //50.16.130.3:24002,50.16.130.2:24002,50.16.130.1:24002/kafka
        ZkUtils zkUtils = ZkUtils.apply("50.16.189.12:24002,50.16.189.11:24002,50.16.189.13:24002/kafka", 30000, 30000, true);
        AdminUtils.createTopic(zkUtils, topicName, 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LogUtil.info("create Topic 结束,....");
    }
}
