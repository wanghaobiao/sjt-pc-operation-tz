package com.acrabsoft.web.service.sjt.pc.operation.web.util.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "spring.datasource.db2", ignoreUnknownFields = false)
public class MysqlJdbcProperties {

    private String url;

    private String type;

    private String username;

    private String password;

    private String driverClassName;

}
