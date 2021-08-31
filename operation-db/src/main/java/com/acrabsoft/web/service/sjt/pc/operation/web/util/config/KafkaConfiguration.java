package com.acrabsoft.web.service.sjt.pc.operation.web.util.config;


import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.HuaweiKafkaProducerClient;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.KafkaProperties;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.LoginUtil;
import org.acrabsoft.utils.LogUtil;
import org.acrabsoft.utils.StringUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/*import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;*/


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author tianshl
 * @version 2017/9/1 下午04:07
 */
@Configuration
//@EnableKafka
public class KafkaConfiguration {
    private static final Logger logger = LoggerFactory.getLogger( HuaweiKafkaProducerClient.class);


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

    @Value("${spring.kafka.consumer.auto-commit-interval-ms}")
    private Integer autoCommitInterval;

    @Value("${spring.kafka.consumer.properties.group.id}")
    private String groupId;

    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka.producer.retries}")
    private Integer retries;

    @Value("${spring.kafka.producer.batch-size}")
    private Integer batchSize;

    @Value("${spring.kafka.producer.buffer-memory}")
    private Integer bufferMemory;

    @Value("${spring.profiles.active}")
    public String active;
    @Value("${huawei.kafka.configs.folder}")
    public String kafkaConfFolder;


    // Broker地址列表
    private final String bootstrapServersName = "bootstrap.servers";
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
    //默认发送20条消息
    private final int messageNumToSend = 100;
    //用户自己申请的机机账号keytab文件名称
    private static final String USER_KEYTAB_FILE = "user.keytab";
    @Value("${huaweiUser}")
    private String huaweiUser;
    /**
     *  生产者模板
     */
    /*@Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        ProducerFactory<String, String> producerFactory;
        if (ParamEnum.active.dev.getCode().equals( active )) {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.ACKS_CONFIG, "0");
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ProducerConfig.RETRIES_CONFIG, retries);
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
            props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
            props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            producerFactory = new DefaultKafkaProducerFactory<>( props );
        }else {
            Map<String, Object> props = new HashMap<>();
            if (this.isSecurityModel()) {
                try {
                    this.securityPrepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("进行华为Kafka初始化时出错！错误信息见上方↑↑↑↑↑↑↑↑↑↑打印的堆栈");
                }
            }
            LogUtil.info("System.getProperty ======> " + System.getProperty("user.dir"));
            KafkaProperties kafkaProc = KafkaProperties.getInstance(kafkaConfFolder);
            // Broker地址列表
            props.put(bootstrapServersName, kafkaProc.getValues(bootstrapServersName, "localhost:21007"));
            // 客户端ID
            props.put(clientId, kafkaProc.getValues(clientId, "DemoProducer"));
            // Key序列化类
            props.put(keySerializer,kafkaProc.getValues(keySerializer, "org.apache.kafka.common.serialization.IntegerSerializer"));
            // Value序列化类
            props.put(valueSerializer,
            kafkaProc.getValues(valueSerializer, "org.apache.kafka.common.serialization.StringSerializer"));
            // 协议类型:当前支持配置为SASL_PLAINTEXT或者PLAINTEXT
            props.put(securityProtocol, kafkaProc.getValues(securityProtocol, "SASL_PLAINTEXT"));
            // 服务名
            props.put(saslKerberosServiceName, "kafka");
            // 域名
            props.put(kerberosDomainName, kafkaProc.getValues(kerberosDomainName, "hadoop.hadoop.com"));
            producerFactory = new DefaultKafkaProducerFactory<>( props );
            logger.info("==========================>初始化完成");
        }
        return new KafkaTemplate<>(producerFactory);
    }*/


    /**
     * @description  是否安全
     * @param
     * @return  返回结果
     * @date  2021-7-6 20:09
     * @author  wanghb
     * @edit
     */
    public Boolean isSecurityModel()  {
        Boolean isSecurity = false;
        if (StringUtil.isNullBlank( kafkaConfFolder )) {
            kafkaConfFolder = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "kafka" + File.separator;
        } else {
            if (!kafkaConfFolder.endsWith(File.separator)) {
                kafkaConfFolder = kafkaConfFolder + File.separator;
            }
        }
        String krbFilePath = kafkaConfFolder + "kafkaSecurityMode";

        Properties securityProps = new Properties();

        // file does not exist.
        if (!isFileExists(krbFilePath))
        {
            return isSecurity;
        }
        try
        {
            securityProps.load(new FileInputStream(krbFilePath));
            if ("yes".equalsIgnoreCase(securityProps.getProperty("kafka.client.security.mode")))
            {
                isSecurity = true;
            }
        }
        catch (Exception e)
        {
        }
        return isSecurity;
    }


    public void securityPrepare() throws IOException {
        logger.info("============>配置路径"+kafkaConfFolder);
        if (StringUtil.isNullBlank( kafkaConfFolder )) {
            kafkaConfFolder = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "kafka" + File.separator;
        } else {
            if (!kafkaConfFolder.endsWith(File.separator)) {
                kafkaConfFolder = kafkaConfFolder + File.separator;
            }
        }
        String krbFile = kafkaConfFolder + "krb5.conf";
        String userKeyTableFile = kafkaConfFolder + USER_KEYTAB_FILE;

        //windows路径下分隔符替换
        userKeyTableFile = userKeyTableFile.replace("\\", "\\\\");
        krbFile = krbFile.replace("\\", "\\\\");

        LoginUtil.setKrb5Config(krbFile);
        LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
        LoginUtil.setJaasFile(huaweiUser, userKeyTableFile);
        logger.info("============>securityPrepare方法成功");

    }


    /**
     * @description  文件是否存在
     * @param  fileName
     * @return  返回结果
     * @date  2021-7-6 18:41
     * @author  wanghb
     * @edit
     */
    public boolean isFileExists(String fileName){
        File file = new File(fileName);
        return file.exists();
    }


    /**
     *  消费者配置信息
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 120000);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 180000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    /**
     *  消费者批量工程
     */
    /*@Bean
    public KafkaListenerContainerFactory<?> batchFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs()));
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(true);
        return factory;
    }*/


}
