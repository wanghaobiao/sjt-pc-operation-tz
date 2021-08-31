package com.acrabsoft.web.service.sjt.pc.operation.web.kafka.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KafkaDataEntity {
    private String kafkaTopic;
    private List<String> datas;
}
