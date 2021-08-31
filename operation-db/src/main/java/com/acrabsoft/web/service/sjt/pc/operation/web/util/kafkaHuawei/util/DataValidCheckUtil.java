package com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.acrabsoft.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * 对数据的格式进行校验的工具类
 */
public class DataValidCheckUtil {

    private static JSONObject dictContent = new JSONObject();

    private static JSONObject areaDict = new JSONObject();

    /**
     * 校验dataType对应的数据类型的格式的正确性
     * @param dataType
     * @param dataItem
     * @return
     */
    public static boolean check(String dataType, JSONObject dataItem) {
        if (!dictContent.containsKey(dataType)) {
            throw new IllegalStateException("数据类型非法，请确认您的请求路径是否正确");
        }
        JSONObject dictJo = dictContent.getJSONObject(dataType);
        Set<String> keySet = dictJo.keySet();
        for (String key : keySet) {
            JSONObject dictItem = dictJo.getJSONObject(key);
            if (dictItem.getBoolean("require")) { // 这个值是必填的，校验一下
                if (dataItem.containsKey(key) && StringUtils.isNotBlank(dataItem.getString(key))) {
                    continue;
                } else {
                    throw new IllegalStateException(key + "[" + dictItem.getString("memo") + "]不能为空，请确认");
                }
            }
        }
        return true;
    }

    /**
     * 校验是否合法的Area代码
     * @param area
     * @return
     */
    public static boolean veriferyArea(String area){
        return areaDict.containsKey(area);
    }

    public synchronized static void initDict(){

        FileSystemResource fileSystemResource = new FileSystemResource("DATA_VERIFY_DICT.json");
        InputStream is;
        if (fileSystemResource.exists()) {
            try {
                LogUtil.error("从Jar包同级目录读取配置文件DATA_VERIFY_DICT.json，将忽略jar包中的配置....");
                is = fileSystemResource.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.error("从Jar包同级目录读取配置DATA_VERIFY_DICT.json失败，将读取项目中的该文件");
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream("DATA_VERIFY_DICT.json");
            }
        } else {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("DATA_VERIFY_DICT.json");
        }

        String content = new JSONReader(new InputStreamReader(is)).readString();
        dictContent = JSON.parseObject(content);
        LogUtil.info("数据格式字典初始化成功...");
        LogUtil.info(dictContent.toJSONString());

        String areaDictStr = "{\"3200\":\"省厅\",\n" +
                "\"3201\":\"南京市\",\n" +
                "\"3202\":\"无锡市\",\n" +
                "\"3203\":\"徐州市\",\n" +
                "\"3204\":\"常州市\",\n" +
                "\"3205\":\"苏州市\",\n" +
                "\"3206\":\"南通市\",\n" +
                "\"3207\":\"连云港市\",\n" +
                "\"3208\":\"淮安市\",\n" +
                "\"3209\":\"盐城市\",\n" +
                "\"3210\":\"扬州市\",\n" +
                "\"3211\":\"镇江市\",\n" +
                "\"3212\":\"泰州市\",\n" +
                "\"3213\":\"宿迁市\"}";
        areaDict = JSON.parseObject(areaDictStr);
    }



}
