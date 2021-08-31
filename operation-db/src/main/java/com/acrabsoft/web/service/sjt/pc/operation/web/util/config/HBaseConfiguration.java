package com.acrabsoft.web.service.sjt.pc.operation.web.util.config;

import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.kafka.service.HbaseHuaweiService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.hbaseHuawei.HbaseLoginUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

@Configuration
public class HBaseConfiguration {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Value("${hbase.zookeeper.quorum}")
    private String zookeeperQuorum;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String clientPort;

    @Value("${zookeeper.znode.parent}")
    private String znodeParent;

    @Value("${spring.profiles.active}")
    private String active;
    @Value("${hbase.config.path}")
    private String dir;
    @Value("${hbaseUser}")
    private String hbaseUser;

    @Bean
    public HbaseTemplate hbaseTemplate() {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        if (active.equals( ParamEnum.active.dev.getCode() )) {
            conf.set( "hbase.zookeeper.quorum", zookeeperQuorum );
            conf.set( "hbase.zookeeper.property.clientPort", clientPort );
            conf.set( "zookeeper.znode.parent", znodeParent );
        }else if (active.equals( ParamEnum.active.test.getCode() )) {
            conf.addResource(new Path(dir + "core-site.xml"));
            conf.addResource(new Path(dir + "hdfs-site.xml"));
            conf.addResource(new Path(dir + "hbase-site.xml"));
            if (User.isHBaseSecurityEnabled(conf)) {
                try {
                    String userName = hbaseUser;
                    String userKeytabFile = dir + "user.keytab";
                    logger.info("userKeytabFile:" + userKeytabFile);
                    String krb5File = dir + "krb5.conf";
                    logger.info("krb5File:" + krb5File);
                    //HbaseLoginUtil.setJaasConf( HBaseService.ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME, userName, userKeytabFile);
                    HbaseLoginUtil.setZookeeperServerPrincipal(HbaseHuaweiService.ZOOKEEPER_SERVER_PRINCIPAL_KEY, HbaseHuaweiService.ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
                    logger.info("==================================>3");
                    HbaseLoginUtil.login(userName, userKeytabFile, krb5File, conf);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new HbaseTemplate(conf);
    }
}
