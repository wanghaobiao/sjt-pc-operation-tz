package com.acrabsoft.web.service.sjt.pc.operation.web.util.config;


import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean( "oracleJdbcTemplate")
    @Qualifier("oracleJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource){
        JdbcTemplate  jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    @Primary
    @Bean( "oracleNameJdbcTemplate")
    @Qualifier("oracleNameJdbcTemplate")
    public NamedParameterJdbcTemplate nameJdbcTemplate(@Qualifier("dataSource") DataSource dataSource){
        NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return namedParameterJdbcTemplate;
    }

    @Bean("hiveJdbcTemplate")
    @Qualifier("hiveJdbcTemplate")
    public JdbcTemplate hiveJdbcTemplate(@Qualifier("hiveDruidDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean( "hiveNameJdbcTemplate")
    @Qualifier("hiveNameJdbcTemplate")
    public NamedParameterJdbcTemplate hiveNameJdbcTemplate(@Qualifier("hiveDruidDataSource") DataSource dataSource){
        NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return namedParameterJdbcTemplate;
    }

    @Bean("mysqlJdbcTemplate")
    @Qualifier("mysqlJdbcTemplate")
    public JdbcTemplate mysqlJdbcTemplate(@Qualifier("mysqlDruidDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean( "mysqlNameJdbcTemplate")
    @Qualifier("mysqlNameJdbcTemplate")
    public NamedParameterJdbcTemplate mysqlNameJdbcTemplate(@Qualifier("mysqlDruidDataSource") DataSource dataSource){
        NamedParameterJdbcTemplate  namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return namedParameterJdbcTemplate;
    }




}
