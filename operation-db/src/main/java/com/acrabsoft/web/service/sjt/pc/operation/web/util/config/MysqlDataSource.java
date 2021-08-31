package com.acrabsoft.web.service.sjt.pc.operation.web.util.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class MysqlDataSource {

    @Autowired
    private MysqlJdbcProperties mysqlJdbcProperties;

    @Autowired
    private HiveCommonProperties hiveCommonProperties;

    @Bean("mysqlDruidDataSource") //新建bean实例
    @Qualifier("mysqlDruidDataSource")//标识
    public DataSource dataSource(){
        DruidDataSource datasource = new DruidDataSource();

        //配置数据源属性
        datasource.setUrl(mysqlJdbcProperties.getUrl());
        datasource.setUsername(mysqlJdbcProperties.getUsername());
        datasource.setPassword(mysqlJdbcProperties.getPassword());
        datasource.setDriverClassName(mysqlJdbcProperties.getDriverClassName());

        try {
            datasource.setFilters( hiveCommonProperties.getFilters());
        } catch (SQLException e) {
        }
        return datasource;
    }
}
