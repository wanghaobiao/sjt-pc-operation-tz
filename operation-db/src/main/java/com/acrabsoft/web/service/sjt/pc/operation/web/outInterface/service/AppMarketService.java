package com.acrabsoft.web.service.sjt.pc.operation.web.outInterface.service;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.TaskRecordService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.*;
import com.google.common.collect.ImmutableMap;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
* 应用日志表( appLogService )服务实现类
* @author wanghb
* @since 2020-11-23 14:34:51
*/
@Service("appMarketService")
public class AppMarketService extends BaseController {
    /**
     * 服务对象
     */
    @Resource
    private TaskRecordService taskRecordService;
    @Value("${spring.profiles.active}")
    private String active;


    /**
     * @description  应用使用情况
     * @param appId 主键id
     * @param dateType 应用类型
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    public Result lastWeekForAndroid(String appId, String dateType) {
        Map<String, Object> params = new HashMap<>();
        params.put( "appId",appId) ;
        String startDate = "";
        String endDate = "";
        if (ParamEnum.indexDateType.day7.getCode().equals( dateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
            params.put( "endDate","2021-01-11" ) ;
        }
        if (ParamEnum.indexDateType.day30.getCode().equals( dateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
            params.put( "endDate","2021-02-05" ) ;
        }
        endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            params.put( "startDate","2021-01-05" ) ;
            params.put( "appName","通讯录" ) ;
        }else {
            params.put( "startDate",startDate ) ;
            params.put( "endDate",endDate ) ;
        }
        Map<String, Object> echartsData = (Map<String, Object>) taskRecordService.userCountChainRatioEcharts(params ).getData();
        Map<String, Object> option = new HashMap<>();
        Map<String, Object> xAxis = new HashMap<>();
        Map<String, Object> date = new HashMap<>();
        xAxis.put( "type", "category");
        List<String> xAxisData =(List<String>) echartsData.get( "xAxisData" );
        List<String> xAxisWeekData = new ArrayList<>();
        List<String> dateData = new ArrayList<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            Date temp = DateUtil.toDate( xAxisData.get(i),DateUtil.DATE_SHORT );
            if (ParamEnum.indexDateType.day7.getCode().equals( dateType )) {
                dateData.add(xAxisData.get(i).substring( 5 ));
                xAxisWeekData.add( "周" + DateUtil.toWeek( temp,DateUtil.weeksII ) );
            }else {
                xAxisWeekData.add(xAxisData.get(i).substring( 5 ));
                dateData.add( "周" + DateUtil.toWeek( temp,DateUtil.weeksII ) );
            }

        }
        xAxis.put( "data", xAxisWeekData);
        date.put( "data", dateData);
        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put( "type","value" );
        Map<String, Object> series = new HashMap<>();
        series.put( "type","bar" );
        series.put( "showBackground",true );
        series.put( "data", echartsData.get( "seriesData" ));
        option.put( "xAxis",xAxis );
        option.put( "date",date );
        option.put( "yAxis", yAxis);
        option.put( "series",series );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,option);
    }


    /**
     * @description  应用使用时长情况
     * @param personCode 主键id
     * @param dateType 应用类型
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    public Result lastWeekDurationForAndroid(String personCode, String dateType) {
        Map<String, Object> params = new HashMap<>();
        params.put( "personCode",personCode) ;
        String startDate = "";
        String endDate = "";
        if (ParamEnum.indexDateType.day7.getCode().equals( dateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
            params.put( "endDate","2021-01-11" ) ;
        }
        if (ParamEnum.indexDateType.day30.getCode().equals( dateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
            params.put( "endDate","2021-02-05" ) ;
        }
        endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            params.put( "startDate","2021-01-05" ) ;
            params.put( "personCode","1" ) ;
        }else {
            params.put( "startDate",startDate ) ;
            params.put( "endDate",endDate ) ;
        }
        Map<String, Object> echartsData = (Map<String, Object>) taskRecordService.userDurationChainRatioEcharts(params ).getData();
        Map<String, Object> option = new HashMap<>();
        Map<String, Object> xAxis = new HashMap<>();
        Map<String, Object> date = new HashMap<>();
        xAxis.put( "type", "category");
        List<String> xAxisData =(List<String>) echartsData.get( "xAxisData" );
        List<String> xAxisWeekData = new ArrayList<>();
        List<String> dateData = new ArrayList<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            Date temp = DateUtil.toDate( xAxisData.get(i),DateUtil.DATE_SHORT );
            dateData.add(xAxisData.get(i));
            xAxisWeekData.add( "周" + DateUtil.toWeek( temp,DateUtil.weeksII ) );
        }
        xAxis.put( "data", xAxisWeekData);
        date.put( "data", dateData);
        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put( "type","value" );
        Map<String, Object> series = new HashMap<>();
        series.put( "type","bar" );
        series.put( "showBackground",true );
        series.put( "data", echartsData.get( "seriesData" ));
        option.put( "xAxis",xAxis );
        option.put( "date",date );
        option.put( "yAxis", yAxis);
        option.put( "series",series );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,option);
    }



    /**
     * @description  活跃用户排行
     * @param appId 主键id
     * @param dateType 应用类型
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    private Result activeForAndroid(String appId, String dateType) {
        String startDate = "";
        if (ParamEnum.indexDateType.day7.getCode().equals( dateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        if (ParamEnum.indexDateType.day30.getCode().equals( dateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );
        Map<String, Object> echartsData = (Map<String, Object>) taskRecordService.appActiveRankEcharts( ImmutableMap.of("appId",appId,"startDate",startDate,"endDate",endDate) ).getData();
        Map<String, Object> option = new HashMap<>();
        Map<String, Object> xAxis = new HashMap<>();
        Map<String, Object> date = new HashMap<>();
        xAxis.put( "type", "category");

        date.put( "data", echartsData.get( "xAxisData" ));
        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put( "type","value" );
        Map<String, Object> series = new HashMap<>();
        series.put( "type","bar" );
        series.put( "showBackground",true );
        series.put( "data", echartsData.get( "seriesData" ));
        option.put( "date",date );
        option.put( "yAxis", yAxis);
        option.put( "series",series );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,option);
    }


    /**
     * @description  当天应用使用情况(人次)
     * @param personCode 人员编号
     * @param dateType 日期类型
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    public Result appDayDurationForAndroid(String personCode, String dateType) {
        Map<String, Object> option = new HashMap<>();
        Map<String, Object> echarsData = taskRecordService.getAppDayDurationMap( personCode, dateType );
        Map<String, Object> xAxis = new HashMap<>();
        xAxis.put( "type", "category");
        xAxis.put( "data", echarsData.get( "xAxisData" ));
        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put( "type","value" );
        Map<String, Object> series = new HashMap<>();
        series.put( "type","bar" );
        series.put( "showBackground",true );
        series.put( "data", echarsData.get( "seriesData" ));
        option.put( "xAxis",xAxis );
        option.put( "yAxis", yAxis);
        option.put( "series",series );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,option);
    }


    /**
     * @description echarts图表相关的接口
     * @param  params
     * @return  返回结果
     * @date  2021-7-26 9:42
     * @author  wanghb
     * @edit
     */
    public Result echartsDataForZt(Map<String, Object> params) {
        String type = PowerUtil.getString( params.get("type") );
        //质态情况-首页卡片
        if  (ParamEnum.echartsDataForZt.type0.getCode().equals( type )){
            return taskRecordService.countCardForApp();
        //质态情况-全省应用建设情况
        }else if  (ParamEnum.echartsDataForZt.type1.getCode().equals( type )){
            return  taskRecordService.appConstruction( params );
        //质态情况-各地区应用使用(人数)/授权人数占比
        }else if  (ParamEnum.echartsDataForZt.type2.getCode().equals( type )) {
            return taskRecordService.appAreaCountForApp(  );
        //质态情况-各地区应用建设数
        }else if  (ParamEnum.echartsDataForZt.type3.getCode().equals( type )) {
            return taskRecordService.appConstruction2( params );
        //质态情况-TOP 5 类型应用使用情况
        }else if  (ParamEnum.echartsDataForZt.type4.getCode().equals( type )) {
            return taskRecordService.appCategoryCountForApp( params );
        //质态情况-全省近一周应用使用人数/时长
        }else if  (ParamEnum.echartsDataForZt.type5.getCode().equals( type )) {
            return taskRecordService.appUserAndDuration( params );
        }else {
            return BuildResult.buildOutResult( ResultEnum.SUCCESS);
        }
    }


    /**
     * @description  tableData 表相关的接口
     * @param  params
     * @return  返回结果
     * @date  2021-8-26 11:29
     * @author  wanghb
     * @edit
     */
    public Result tableData(Map<String, Object> params) {
        String type = PowerUtil.getString( params.get("type") );
        if  (ParamEnum.echartsDataForZt.type0.getCode().equals( type )) {
            return taskRecordService.userFrequencyRankByPage( params );
        }else{
            return BuildResult.buildOutResult( ResultEnum.SUCCESS);
        }
    }


    /**
     * @description  安卓图标相关的接口
     * @param  params
     * @return  返回结果
     * @date  2021-8-26 11:29
     * @author  wanghb
     * @edit
     */
    public Result androidChartsData(Map<String, Object> params) {
        String type = PowerUtil.getString( params.get("type") );
        String appId = PowerUtil.getString( params.get("appId") );
        String personCode = PowerUtil.getString( params.get("personCode") );
        String dateType = PowerUtil.getString( params.get("dateType") );
        //应用使用情况
        if(ParamEnum.androidChartsData.type0.getCode().equals( type )){
            if (ParamEnum.indexDateType.active.getCode().equals( dateType )) {
                return activeForAndroid(appId,dateType);
            }else{
                return lastWeekForAndroid(appId,dateType);
            }
        //应用运行时长
        }else if  (ParamEnum.androidChartsData.type1.getCode().equals(type)){
            if (ParamEnum.indexDateType.day.getCode().equals( dateType )) {
                return appDayDurationForAndroid(personCode,dateType);
            }else{
                return lastWeekDurationForAndroid(personCode,dateType);
            }
        }else if  (ParamEnum.androidChartsData.type2.getCode().equals(type)){
            return taskRecordService.appDurationList(personCode,dateType);
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }




    /**
     * @description  应用市场web端-汇总接口
     * @param  params
     * @return  返回结果
     * @date  2021-7-26 9:42
     * @author  wanghb
     * @edit
     */
    public Result echartsDataForWeb(Map<String, Object> params) {
        String type = PowerUtil.getString( params.get("type") );
        String id = PowerUtil.getString( params.get("id") );
        String personCode = PowerUtil.getString( params.get("personCode") );
        String startDate = PowerUtil.getString( params.get("startDate") );
        String endDate = PowerUtil.getString( params.get("endDate") );
        String date = PowerUtil.getString( params.get("date") );
        String dateType = PowerUtil.getString( params.get("dateType") );
        String appId  = ScheduledTasks.appInfoObjIdToId.get( id );

        /*if  (ParamEnum.echartsDataForZt.type0.getCode().equals( type )){
            return taskRecordService.countCardForApp();
            //质态情况-应用排行(人次)
        }else if  (ParamEnum.echartsDataForZt.type1.getCode().equals( type )){
            return  taskRecordService.userFrequencyRank( params );
            //质态情况-各地区应用使用(人数)
        }else if  (ParamEnum.echartsDataForZt.type2.getCode().equals( type )) {
            return taskRecordService.appAreaCountForApp( dateType );
            //质态情况-各应用类别应用(人数和人次)
        }else if  (ParamEnum.echartsDataForZt.type3.getCode().equals( type )) {
            return taskRecordService.appCategoryCountForApp( params );
            //质态情况-各应用警种应用(人数和人次)
        }else if  (ParamEnum.echartsDataForZt.type4.getCode().equals( type )) {
            return taskRecordService.appPoliceCountForApp( params );
        }else {
            return BuildResult.buildOutResult( ResultEnum.SUCCESS);
        }*/
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }



}
