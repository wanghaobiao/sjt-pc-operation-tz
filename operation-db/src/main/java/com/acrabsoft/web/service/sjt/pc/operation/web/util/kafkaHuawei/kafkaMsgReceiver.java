package com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei;


import com.acrabsoft.web.service.sjt.pc.operation.StartApp;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.ServiceLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import org.acrabsoft.utils.LogUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
//implements InitializingBean
public class kafkaMsgReceiver {
    Logger logger = LoggerFactory.getLogger( kafkaMsgReceiver.class);

    @Lazy
    @Autowired
    private HuaweiKafkaConsumerClient consumerClient;

    @Value("${kafka.topic.names}")
    private String listenTopicNames;

    @Resource
    private AppLogService appLogService;
    @Resource
    private ServiceLogService servicLogService;

    /**
     * @description 实现了 InitializingBean 接口  项目启动的时候会执行重写方法
     * @param
     * @return  返回结果
     * @date  2021-7-8 14:21
     * @author  wanghb
     * @edit
     */
    //@Override
    public void afterPropertiesSet() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(15); // 这里休眠15秒的目的是延迟kafkaConsumer的初始化工作，如果consumer在applicationContext初始化完成之前就开始，会有报错
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //这里维护监听的topic
            List<String> topicNames = Arrays.asList( ParamEnum.topic.appLog.getKafkaTopic(),ParamEnum.topic.customizeAppLog.getKafkaTopic(),ParamEnum.topic.serviceLog.getKafkaTopic());
            //List<String> topicNames = Arrays.asList(listenTopicNames.split(","));
            consumerClient.startListen(topicNames, this);
        }).start();
    }

    public void onReceive(ConsumerRecords<Integer, String> records, KafkaConsumer<Integer, String> consumer) {
        Integer count = 0;
        List<ConsumerRecord<Integer, String>> appLogList = new ArrayList<>();
        List<ConsumerRecord<Integer, String>> serviceLogList = new ArrayList<>();
        for (ConsumerRecord<Integer, String> record : records){
            count ++;
            LogUtil.info("开始消费:[" + record.topic() + "], Received message: (" + record.key() + ", " + record.value()+ ") at offset " + record.offset() + ", topicName :" + record.topic());
            if (ParamEnum.topic.appLog.getKafkaTopic().equals(record.topic())) {
                appLogList.add(record);
            } else  if (ParamEnum.topic.serviceLog.getKafkaTopic().equals(record.topic())) {
                serviceLogList.add(record);
            }
        }
        // 保存应用日志
        //appLogService.batchSaveData(appLogList);
        //servicLogService.batchSaveData(serviceLogList);
        logger.info("===================>本次消费:"+count+"条");
        consumer.commitSync();
        consumer.close();
    }

}
