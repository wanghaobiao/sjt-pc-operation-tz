package com.acrabsoft.web.service.sjt.pc.operation.web.kafka.service;

import org.acrabsoft.utils.UUIDUtil;

import java.util.Properties;

public class KafkaPropertiesService {

    // Broker连接地址
    private static final String bootstrapServers = "bootstrap.servers";

    // 域名
    private static final String kerberosDomainName = "kerberos.domain.name";
    // Group id
    private static final String groupId = "group.id";
    // 消息内容使用的反序列化类
    private static final String valueDeserializer = "value.deserializer";
    // 消息Key值使用的反序列化类
    private static final String keyDeserializer = "key.deserializer";
    // 协议类型:当前支持配置为SASL_PLAINTEXT或者PLAINTEXT
    private static final String securityProtocol = "security.protocol";
    // 服务名
    private static final String saslKerberosServiceName = "sasl.kerberos.service.name";
    // 是否自动提交offset
    private final static String enableAutoCommit = "enable.auto.commit";
    // 自动提交offset的时间间隔
    private final static String autoCommitIntervalMs = "auto.commit.interval.ms";

    // 会话超时时间
    private final static String sessionTimeoutMs = "session.timeout.ms";


    public static final String MONITOR_GROUP = "acrabsoft-monitor-consumer";


    public static Properties getKafkaProerties(String consumerGroup, String brokerServer, boolean isSecurityMode) {
        Properties props = new Properties();
        // Broker连接地址
        props.put(bootstrapServers, brokerServer);
        // Group id
        props.put(groupId, consumerGroup);
        // 是否自动提交offset
        props.put(enableAutoCommit, "false");
        // 自动提交offset的时间间隔
//        props.put(autoCommitIntervalMs, "1000");
        // 会话超时时间
        props.put(sessionTimeoutMs, "30000");
        // 消息Key值使用的反序列化类
        props.put(keyDeserializer, "org.apache.kafka.common.serialization.IntegerDeserializer");
        // 消息内容使用的反序列化类
        props.put(valueDeserializer, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("client.id", UUIDUtil.getUUID32());
        props.put("auto.offset.reset", "earliest");

        if (isSecurityMode) {
            // 安全协议类型
            props.put(securityProtocol, "SASL_PLAINTEXT");
                // 服务名
            props.put(saslKerberosServiceName, "kafka");
                // 域名
            props.put(kerberosDomainName, "hadoop.hadoop.com");
        }

        return props;
    }


}
