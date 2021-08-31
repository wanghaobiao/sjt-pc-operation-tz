package com.acrabsoft.web.service.sjt.pc.operation;

import com.acrabsoft.web.App;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.kafka.service.KafkaService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableJpaAuditing
public class StartApp
{
    @Value("${spring.profiles.active}")
    private static String active;
    public static void main( String[] args )
    {
    	SpringApplication.run( App.class, args);
        SpringContextUtil.getBean( ScheduledTasks.class ).refreshParams();
        SpringContextUtil.getBean( ScheduledTasks.class ).refreshServiceInfo();
        SpringContextUtil.getBean( ScheduledTasks.class ).refreshAppInfo();
        SpringContextUtil.getBean( ScheduledTasks.class ).refreshRowkeyMark();
        SpringContextUtil.getBean( ScheduledTasks.class ).appFtpSync();
        SpringContextUtil.getBean( ScheduledTasks.class ).serviceFtpSync();
        SpringContextUtil.getBean( ScheduledTasks.class ).appDurationLogFtpSync();
        System.out.println("===============================>项目启动成功<===============================");
        if(ParamEnum.active.prd.getCode().equals( active )) {
            SpringContextUtil.getBean( KafkaService.class ).getKafkaConsumer();
            SpringContextUtil.getBean( HBaseService.class ).getHbaseConnection();
            SpringContextUtil.getBean( KafkaService.class ).startListen();
        }

    }

}
