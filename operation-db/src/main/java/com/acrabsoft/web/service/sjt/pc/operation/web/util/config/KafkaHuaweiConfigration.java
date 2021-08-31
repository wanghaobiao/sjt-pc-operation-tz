package com.acrabsoft.web.service.sjt.pc.operation.web.util.config;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.HuaweiKafkaConsumerClient;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.HuaweiKafkaProducerClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Date;

@AutoConfigureOrder(9999)
@Configuration
public class KafkaHuaweiConfigration {

    @Lazy
    @Bean
    public HuaweiKafkaConsumerClient getKafkaConsumerClient(){
        return new HuaweiKafkaConsumerClient();
    }

    @Lazy
    @Bean
    public HuaweiKafkaProducerClient getKafkaProducerClient(){
        return new HuaweiKafkaProducerClient();
    }

}