package com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.SpringContextUtil;
import org.acrabsoft.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class KafkaProperties
{
    private static final Logger LOG = LoggerFactory.getLogger(KafkaProperties.class);
    
    // Topic名称，安全模式下，需要以管理员用户添加当前用户的访问权限
//    public final static String TOPIC = "example-metric1";
    private static Properties serverProps = new Properties();
    private static Properties producerProps = new Properties();
    private static Properties consumerProps = new Properties();
    private static Properties clientProps = new Properties();
    private static KafkaProperties instance = null;

    private KafkaProperties(String kafkaConfFolder){
        String filePath = PowerUtil.isNotNull( kafkaConfFolder ) ? kafkaConfFolder : SpringContextUtil.getApplicationContext().getEnvironment().getProperty("huawei.kafka.configs.folder");
        if (StringUtil.isNullBlank(filePath)) {
            filePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "kafka" + File.separator;
        } else {
            if (!filePath.endsWith(File.separator)) {
                filePath = filePath + File.separator;
            }
        }
        try
        {
            File proFile = new File(filePath + "producer.properties");
        
            if (proFile.exists())
            {
                producerProps.load(new FileInputStream(filePath + "producer.properties"));
            }
        
            File conFile = new File(filePath + "producer.properties");
        
            if (conFile.exists())
            {
                consumerProps.load(new FileInputStream(filePath + "consumer.properties"));
            }
        
            File serFile = new File(filePath + "server.properties");
            
            if (serFile.exists())
            {
            	serverProps.load(new FileInputStream(filePath + "server.properties"));
            }

            File cliFile = new File(filePath + "client.properties");
            
            if (cliFile.exists())
            {
            	clientProps.load(new FileInputStream(filePath + "client.properties"));
            }
        }
        catch (IOException e)
        {
            LOG.info("The Exception occured.", e);
        }
    }
    
    public synchronized static KafkaProperties getInstance(String kafkaConfFolder)
    {
        if (null == instance)
        {
            instance = new KafkaProperties(kafkaConfFolder);
        }
        
        return instance;
    }
    
    /**
    * 获取参数值
    * @param key properites的key值
    * @param defValue 默认值
    * @return
    */
    public String getValues(String key, String defValue)
    {
        String rtValue = null;
        
        if (null == key)
        {
            LOG.error("key is null");
        }
        else
        {
            rtValue = getPropertiesValue(key);
        }
        
        if (null == rtValue)
        {
            LOG.warn("KafkaProperties.getValues return null, key is " + key);
            rtValue = defValue;
        }
        
        LOG.info("KafkaProperties.getValues: key is " + key + "; Value is " + rtValue);
        
        return rtValue;
    }
    
    /**
    * 根据key值获取server.properties的值
    * @param key
    * @return
    */
    private String getPropertiesValue(String key)
    {
        String rtValue = serverProps.getProperty(key);
        
        // server.properties中没有，则再向producer.properties中获取
        if (null == rtValue)
        {
            rtValue = producerProps.getProperty(key);
        }
        
        // producer中没有，则再向consumer.properties中获取
        if (null == rtValue)
        {
            rtValue = consumerProps.getProperty(key);
        }
        
        // consumer没有，则再向client.properties中获取
        if (null == rtValue)
        {
        	rtValue = clientProps.getProperty(key);
        }
        
        return rtValue;
    }
}
