package com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 用来发送消息到Kafka的封装类
 */
@Service
public class KafkaMsgSender {

    private Logger logger = LoggerFactory.getLogger(KafkaMsgSender.class);

    @Lazy
    @Autowired
    private HuaweiKafkaProducerClient huaweiKafkaProducerClient;

    public void sendMsg(String topicName, String msgContent) {
        huaweiKafkaProducerClient.sendMessage(topicName, msgContent);
        logger.info("===>  KafkaMsgSender.sendMsg 消息发送成功 === 》 topic ==>" + topicName + "；messageContent ==》" + msgContent);
    }

}
