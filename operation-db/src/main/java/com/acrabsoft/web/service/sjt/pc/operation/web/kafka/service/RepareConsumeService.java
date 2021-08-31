package com.acrabsoft.web.service.sjt.pc.operation.web.kafka.service;

import com.alibaba.fastjson.JSONObject;
import org.acrabsoft.utils.LogUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 对部分topic的消息重新消费
 *
 * 要启用这个功能的时候，就不要在正常消费的地方去消费了，一个实例只做一样事情吧
 */
@Service
@AutoConfigureOrder(999999)
public class RepareConsumeService {

    @Value("${kafka.repare.topic.names}")
    private String repareTopicName;

    private KafkaConsumer<Integer, String> kafkaConsumer;

    public static final String ACRABSOFT_KAFKA_REPARE_GROUP_NAME = "acrabsoft-repare-group";


    /*public void repare(){
        LogUtil.info("开始重新消费消息===>" + repareTopicName);
        String groupName = ("acrabsoft_lgy_lgyRzxx".equals(repareTopicName) ? ACRABSOFT_KAFKA_REPARE_GROUP_NAME : ACRABSOFT_KAFKA_REPARE_GROUP_NAME + repareTopicName); // 短暂情况下的特殊处理，因为旅馆住宿的之前已经修复过一部分数据了，group那么不能变

        while (true) {
            ConsumerRecords<Integer, String> record = kafkaConsumer.poll(1000);
            kafkaConsumer.commitAsync();
        }
    }

    public JSONObject state(){
        JSONObject result = new JSONObject();
        if (kafkaConsumer != null) {
            List<PartitionInfo> partitions = kafkaConsumer.partitionsFor(repareTopicName);
            partitions.forEach((partitionInfo) -> {
                TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
                OffsetAndMetadata committed = kafkaConsumer.committed(topicPartition);
                if (committed != null) {
                    long position = kafkaConsumer.position(topicPartition);
                    LogUtil.info("topic ==>" + repareTopicName + ";partition ===>" + partitionInfo.partition() + ";position ===>" + committed.offset());

                    JSONObject jo = new JSONObject();
                    jo.put("partition", partitionInfo.partition());
                    jo.put("commitedPosition", committed.offset());
                    jo.put("nextPosition", position);
                    result.put("partition-" + partitionInfo.partition(), jo);
                }
            });
        }
        return result;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(15); // 这里休眠15秒的目的是延迟kafkaConsumer的初始化工作，如果consumer在applicationContext初始化完成之前就开始，会有报错
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.repare();
        }).start();
    }*/
}
