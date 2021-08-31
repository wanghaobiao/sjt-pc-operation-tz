package com.acrabsoft.web.service.sjt.pc.operation.web.util.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class HiveDataSource {

    @Autowired
    private HiveJdbcProperties hiveJdbcProperties;

    @Autowired
    private HiveCommonProperties hiveCommonProperties;

    @Bean("hiveDruidDataSource") //新建bean实例
    @Qualifier("hiveDruidDataSource")//标识
    public DataSource dataSource(){
        DruidDataSource datasource = new DruidDataSource();

        //配置数据源属性
        datasource.setUrl(hiveJdbcProperties.getUrl());
        datasource.setUsername(hiveJdbcProperties.getUsername());
        datasource.setPassword(hiveJdbcProperties.getPassword());
        datasource.setDriverClassName(hiveJdbcProperties.getDriverClassName());

        //配置统一属性
        datasource.setInitialSize( hiveCommonProperties.getInitialSize());
        datasource.setMinIdle( hiveCommonProperties.getMinIdle());
        datasource.setMaxActive( hiveCommonProperties.getMaxActive());
        datasource.setMaxWait( hiveCommonProperties.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis( hiveCommonProperties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis( hiveCommonProperties.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery( hiveCommonProperties.getValidationQuery());
        datasource.setTestWhileIdle( hiveCommonProperties.isTestWhileIdle());
        datasource.setTestOnBorrow( hiveCommonProperties.isTestOnBorrow());
        datasource.setTestOnReturn( hiveCommonProperties.isTestOnReturn());
        datasource.setPoolPreparedStatements( hiveCommonProperties.isPoolPreparedStatements());
        try {
            datasource.setFilters( hiveCommonProperties.getFilters());
        } catch (SQLException e) {
        }
        return datasource;
    }
}
