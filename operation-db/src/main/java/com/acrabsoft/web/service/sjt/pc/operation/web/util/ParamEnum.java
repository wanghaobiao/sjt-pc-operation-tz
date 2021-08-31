package com.acrabsoft.web.service.sjt.pc.operation.web.util;


import com.google.common.collect.ImmutableMap;

import java.util.*;

/**
 * @Title: 对应数据字典的枚举类
 * @author wanghb
 * @date 2019-07-18
 */
public interface ParamEnum {
    //用户 1  zazd_dsj
    //用户 2  zazd_xmzx
    String huaweiUser = "zazd_xmzx";
    String hbaseUser = "zazd_xmzx";
    /**
     * 字典项type
     * @author wanghb
     */


    enum dicType {
        ACCOUNT_STATUS( "ACCOUNT_STATUS","账号状态"),
        ACCOUNT_TYPE( "ACCOUNT_TYPE","账号类型"),
        APP_AREA( "APP_AREA","应用地区"),
        APP_CATEGORY( "APP_CATEGORY","应用业务类型分类"),
        APP_DOC( "APP_DOC","应用文档"),
        APP_LOGO_BG1( "APP_LOGO_BG1","各网络区域应用背景色1"),
        APP_LOGO_BG2( "APP_LOGO_BG2","各网络区域应用背景色2"),
        APP_MARK_COLOR1( "APP_MARK_COLOR1","各网络区域角标背景色1"),
        APP_MARK_COLOR2( "APP_MARK_COLOR2","各网络区域角标背景色2"),
        APP_MARKTEXT_COLOR( "APP_MARKTEXT_COLOR","各网络区域角标字体色"),
        APP_POLICE( "APP_POLICE","应用警种"),
        APP_SOURCE( "APP_SOURCE","应用数据来源警种"),
        APP_STATUS( "APP_STATUS","应用状态"),
        APP_TAG( "APP_TAG","应用标签"),
        APP_TYPE( "APP_TYPE","应用类型"),
        APP_VERSION_STATUS( "APP_VERSION_STATUS","应用版本状态"),
        APPTYPE( "APPTYPE","应用种类"),
        AREA( "AREA","地区"),
        AREA_CENTER( "AREA_CENTER","分中心对应关系"),
        AREA_PROVINCE( "AREA_PROVINCE","全国代码"),
        AREA_URL( "AREA_URL","各地市应用服务地址"),
        AUTH_STATUS( "AUTH_STATUS","授权结果"),
        AUTHORIZATION_TYPE( "AUTHORIZATION_TYPE","授权方式"),
        BANNER_TYPE( "BANNER_TYPE","BANNER类型"),
        CENTER_INFO( "CENTER_INFO","中心信息"),
        COLOR( "COLOR","颜色值"),
        DATE_RANGE( "DATE_RANGE","时间范围"),
        FILE_TYPE( "FILE_TYPE","文件类型"),
        FLOW_FLOW( "FLOW_FLOW","关联流程"),
        FLOW_TYPE( "FLOW_TYPE","流程适用类型"),
        HZ( "HZ","频率"),
        INTERFACE_TYPE( "INTERFACE_TYPE","接口类型"),
        MARKET_VERSION_STATUS( "MARKET_VERSION_STATUS","应用市场版本状态"),
        MODULE_TYPE( "MODULE_TYPE","模块类型"),
        NETWORK_AREA( "NETWORK_AREA","网络区域"),
        NETWORK_AREA_TYPE( "NETWORK_AREA_TYPE","网络区域类型"),
        OPERATOR_TYPE( "OPERATOR_TYPE","操作类型"),
        SERVICE_TYPE( "SERVICE_TYPE","资源服务类型"),
        SIMCARD_OPERATOR( "SIMCARD_OPERATOR","运营商"),
        TEMPLATE_SCOPE( "TEMPLATE_SCOPE","模板使用范围"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        dicType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (dicType item : dicType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (dicType item : dicType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * 字典项type
     * @author wanghb
     */
    enum active {
        dev( "dev","本地环境"),
        prd( "prd","生产环境"),
        test( "prd","测试环境"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        active(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (active item : active.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (active item : active.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * 字典项type
     * @author wanghb
     */
    enum dirtyDataType {
        type0( "0","程序异常"),
        type1( "1","参数异常"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        dirtyDataType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (dirtyDataType item : dirtyDataType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (dirtyDataType item : dirtyDataType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * 字典项type
     * @author wanghb
     */
    enum dirtyDataStatus  {
        status0( "0","未处理"),
        status1( "1","已处理"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        dirtyDataStatus(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (dirtyDataStatus item : dirtyDataStatus.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (dirtyDataStatus item : dirtyDataStatus.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 字典项type
     * @author wanghb
     */
    enum appType  {
        type0( "0","苏警通应用"),
        type1( "1","部里应用"),
        type2( "2","苏警通站点"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        appType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (appType item : appType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (appType item : appType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 字典项type
     * @author wanghb
     */
    enum androidChartsData {
        type0( "type0","应用使用情况"),
        type1( "type1","应用使用时长"),
        type2( "type2","应用使用时长列表"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        androidChartsData(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (androidChartsData item : androidChartsData.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (androidChartsData item : androidChartsData.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }
    /**
     * 字典项type
     * @author wanghb
     */
    enum echartsDataForZt {
        type0( "type0","质态情况-首页卡片"),
        type1( "type1","质态情况-全省应用建设情况"),
        type2( "type2","质态情况-各地区应用使用(人数)/授权人数占比"),
        type3( "type3","质态情况-各地区应用建设数"),
        type4( "type4","质态情况-各应用类别应用(人数和人次)"),
        type5( "type5","质态情况-全省近一周应用使用人数/时长"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        echartsDataForZt(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (echartsDataForZt item : echartsDataForZt.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (echartsDataForZt item : echartsDataForZt.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 字典项type
     * @author wanghb
     */
    enum tableData {
        type0( "type0","应用-热门排行"),
        type1( "type1",""),
        type2( "type2",""),
        type3( "type3",""),
        type4( "type4",""),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        tableData(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (tableData item : tableData.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (tableData item : tableData.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 逻辑删除字段
     * @author wanghb
     */
    enum deleted {
        noDel( "0","未删除"),
        yesDel( "1","已删除"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        deleted(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (deleted item : deleted.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (deleted item : deleted.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * rowkey类型
     * @author wanghb
     */
    enum rowkeyType {
        type1( "city","地市"),
        type2( "personCode","人员code 身份证号"),
        type3( "app","应用"),
        type4( "service","服务"),
        type5( "load","负载"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        rowkeyType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (rowkeyType item : rowkeyType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (rowkeyType item : rowkeyType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * 首页切换时间类型
     * @author wanghb
     */
    enum indexDateType {
        sum( "sum","总计"),
        day( "day","今日"),
        day7( "day7","近7天"),
        day30( "day30","近30天"),
        active( "active","前五名活跃用户"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        indexDateType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (indexDateType item : indexDateType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (indexDateType item : indexDateType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * 首页切换时间类型
     * @author wanghb
     */
    enum countType {
        type0( "0","人数"),
        type1( "1","人次"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        countType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (countType item : countType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (countType item : countType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }


    /**
     * 定时任务
     * @author wanghb
     */
    enum taskRecord {
        appOpenCount( "appOpenCount","应用统计"),
        serviceOpenCount( "serviceOpenCount","服务统计"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        taskRecord(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (taskRecord item : taskRecord.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (taskRecord item : taskRecord.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 任务状态
     * @author wanghb
     */
    enum taskRecordStatus {
        status0( "0","待执行"),
        status1( "1","执行中"),
        status2( "2","执行完毕"),
        status3( "3","执行失败"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        taskRecordStatus(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (taskRecordStatus item : taskRecordStatus.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (taskRecordStatus item : taskRecordStatus.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 服务类型
     * @author wanghb
     */
    enum sequenceType {
        app( "app","应用类型"),
        service( "service","服务类型"),
        otherService( "otherService","其他服务日志 包括 部里的服务  和  苏警通站点 的服务"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        sequenceType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (sequenceType item : sequenceType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (sequenceType item : sequenceType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 消费主题
     * @author wanghb
     */
    enum topic {
        //应用日志
        appLog( "acrabsoft_yy_appLog","sjt_appLog"),
        //应用使用日志
        appDurationLog( "acrabsoft_yy_appDurationLog","sjt_appDurationLog"),
        //自定义日志
        customizeAppLog( "acrabsoft_yy_customizeAppLog","sjt_customizeAppLog"),
        //服务日志
        serviceLog( "acrabsoft_yy_serviceLog","sjt_serviceLog"),
        //其他服务日志 包括 部里的服务  和  苏警通站点 的服务
        otherServiceLog( "","sjt_otherServiceLog"),
        test( "acrabsoft_lgy_test1","test1"),
        ;
        private String kafkaTopic;
        private String hbaseTableName;
        public String getKafkaTopic() {
            return kafkaTopic;
        }
        public String getHbaseTableName() {
            return hbaseTableName;
        }
        topic(String code, String name) {
            this.kafkaTopic = code;
            this.hbaseTableName = name;
        }
        public static String getNameByCode(String code) {
            for (topic item : topic.values()) {
                if (item.getKafkaTopic().equals(code)) {
                    return item.getHbaseTableName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (topic item : topic.values()) {
                list.add( ImmutableMap.of("value",item.getKafkaTopic(),"label",item.getHbaseTableName() ));
            }
            return list;
        }
    }


    /**
     * 网路区域
     * @author wanghb
     */
    enum networkAreaLog {
        networkArea1( "1","一类区"),
        networkArea2( "2","二类区"),
        networkArea3( "3","三类区"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        networkAreaLog(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (networkAreaLog item : networkAreaLog.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (networkAreaLog item : networkAreaLog.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }
    /**
     * 服务操作类型
     * @author wanghb
     */
    enum operatorType {
        code1( "1","查询"),
        code2( "2","写入"),
        code3( "3","更新"),
        code4( "4","删除"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        operatorType(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (operatorType item : operatorType.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (operatorType item : operatorType.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }
    /**
     * 日志来自哪个网络区域
     * @author wanghb
     */
    enum interfaceTypeName {
        code11( "11","HTTP"),
        code12( "12","HTTPS"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        interfaceTypeName(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (interfaceTypeName item : interfaceTypeName.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (interfaceTypeName item : interfaceTypeName.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 日志来自哪个网络区域
     * @author wanghb
     */
    enum app_type {
        appType( "1","普通应用"),
        serviceType( "2","服务应用"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        app_type(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (app_type item : app_type.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (app_type item : app_type.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

    /**
     * 行政区编码
     * @author wanghb
     */
    enum areaCode {
        code00( "3200","省厅应用"),
        code01( "3201","南京市"),
        code02( "3202","无锡市"),
        code03( "3203","徐州市"),
        code04( "3204","常州市"),
        code05( "3205","苏州市"),
        code06( "3206","南通市"),
        code07( "3207","连云港"),
        code08( "3208","淮安市"),
        code09( "3209","盐城市"),
        code10( "3210","扬州市"),
        code11( "3211","镇江市"),
        code12( "3212","泰州市"),
        code13( "3213","宿迁市"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        areaCode(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (areaCode item : areaCode.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (areaCode item : areaCode.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }
    /**
     * 行政区编码
     * @author wanghb
     */
    enum police {
        police01( "01","国保"),
        police02( "02","经侦"),
        police03( "03","治安"),
        police04( "04","边防"),
        police05( "05","刑侦"),
        police06( "06","出入境"),
        police07( "07","消防"),
        police08( "08","警卫"),
        police09( "09","铁路公安"),
        police10( "10","网安"),
        police11( "11","行动技术"),
        police12( "12","监管"),
        police13( "13","交通公安"),
        police14( "14","民航公安"),
        police15( "15","森林公安"),
        police16( "16","法制"),
        police17( "17","情报"),
        police18( "18","装财"),
        police19( "19","禁毒"),
        police20( "20","信息通信"),
        police21( "21","反恐"),
        police22( "22","纪委"),
        police23( "23","监察"),
        police24( "24","督察"),
        police25( "25","政工"),
        police98( "98","派出所"),
        police99( "99","其他"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        police(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (police item : police.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (police item : police.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }
    /**
     * 行政区编码
     * @author wanghb
     */
    enum networkArea {
        code01( "1","Ⅰ类区"),
        code02( "2","Ⅱ类区"),
        code03( "3","Ⅲ类区"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        networkArea(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (networkArea item : networkArea.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (networkArea item : networkArea.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }
    /**
     * 服务级别
     * @author wanghb
     */
    enum serviceLevel {
        code01( "city","市级"),
        ;
        private String code;
        private String name;
        public String getCode() {
            return code;
        }
        public String getName() {
            return name;
        }
        serviceLevel(String code, String name) {
            this.code = code;
            this.name = name;
        }
        public static String getNameByCode(String code) {
            for (serviceLevel item : serviceLevel.values()) {
                if (item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
        public static List<Map<String, Object>> getList() {
            List<Map<String, Object>> list = new ArrayList<>();
            for (serviceLevel item : serviceLevel.values()) {
                list.add( ImmutableMap.of("value",item.getCode(),"label",item.getName() ));
            }
            return list;
        }
    }

}

