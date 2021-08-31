package com.acrabsoft.web.service.sjt.pc.operation.web.system.controller;

import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.TaskRecordService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import javax.annotation.Resource;
import java.util.Map;

/**
* 任务执行记录( TaskRecordController )控制类
* @author wanghbdeptName
* @since 2021-4-15 17:23:27
*/
@RestController
@RequestMapping("/system/taskRecord")
@Api(value = "任务执行记录",  tags = "任务执行记录")
public class TaskRecordController {

    private static Logger logger = Logger.getLogger( TaskRecordController.class );

    /**
    * 服务对象
    */
    @Resource
    private TaskRecordService taskRecordService;
    @Value("${spring.profiles.active}")
    private String active;

    /**
    * @description  分页查询
    * @param  pageNo  一页个数
    * @param  pageSize  页码
    * @return  返回结果
    * @date  20/09/05 8:04
    * @author  wanghb
    * @edit
    */
    @PostMapping("/getListPage")
    @ResponseBody
    @ApiOperation(value = "分页查询")
    public Result getListPage(@RequestParam(defaultValue = "1") @ApiParam("页码") int pageNo,
                              @RequestParam(defaultValue = "10") @ApiParam("一页个数") int pageSize) {
        Result result = taskRecordService.getListPage( pageNo,pageSize);
        return result;
    }


    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @GetMapping("/view")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result view(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = taskRecordService.view( id );
        return result;
    }


    /**
    * @description 保存或更新
    * @param taskRecordEntity 实体
    * @return 无返回值
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/saveOrUpdate")
    @ResponseBody
    @ApiOperation(value = "保存或更新")
    public Result saveOrUpdate(@RequestBody TaskRecordEntity taskRecordEntity) {
        Result result = taskRecordService.saveOrUpdate( taskRecordEntity );
        return result;
    }*/



    /**
    * @description 去保存页面
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/goSave")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result goSave() {
        Result result = taskRecordService.goSave(  );
        return result;
    }*/


    /**
    * @description 保存
    * @param taskRecordEntity 实体
    * @return 无返回值
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/save")
    @ResponseBody
    @ApiOperation(value = "保存")
    public Result save(@RequestBody TaskRecordEntity taskRecordEntity) {
        Result result = taskRecordService.save( taskRecordEntity );
        return result;
    }*/


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @GetMapping("/delete")
    @ResponseBody
    @ApiOperation(value = "删除")
    public Result delete(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = taskRecordService.delete( id );
        return result;
    }


    /**
    * @description 批量删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/batchDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchDelete(@RequestBody List<String> ids) {
        Result result = taskRecordService.batchDelete( ids );
        return result;
    }*/


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/logicDelete")
    @ResponseBody
    @ApiOperation(value = "逻辑删除")
    public Result logicDelete(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = taskRecordService.logicDelete( id );
        return result;
    }*/


    /**
    * @description 批量逻辑删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/batchLogicDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchLogicDelete(@RequestBody List<String> ids) {
        Result result = taskRecordService.batchLogicDelete( ids );
        return result;
    }*/

    /**
     * @description  统计应用启动次数
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appOpenCount")
    @ResponseBody
    @ApiOperation(value = "统计应用启动次数")
    public Result appOpenCount(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appOpenCount( params );
        return result;
    }

    /**
     * @description  统计服务调用次数
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/serviceOpenCount")
    @ResponseBody
    @ApiOperation(value = "统计服务调用次数")
    public Result serviceOpenCount(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.serviceOpenCount( params );
        return result;
    }

    /**
     * @description  统计应用启动Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appOpenCountEcharts")
    @ResponseBody
    @ApiOperation(value = "统计应用启动次数")
    public Result appOpenCountEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appOpenCountEcharts( params );
        return result;
    }

    /**
     * @description  服务统计饼状图Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/servicePieEcharts")
    @ResponseBody
    @ApiOperation(value = "服务统计饼状图Echarts")
    public Result servicePieEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.servicePieEcharts( params );
        return result;
    }

    /**
     * @description  服务统计排行Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/serviceRankEcharts")
    @ResponseBody
    @ApiOperation(value = "服务统计排行Echarts")
    public Result serviceRankEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.serviceRankEcharts( params );
        return result;
    }

    /**
     * @description  统计应用启动Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/serviceLineEcharts")
    @ResponseBody
    @ApiOperation(value = "统计应用启动次数")
    public Result serviceLineEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.serviceLineECharts( params );
        return result;
    }


    /**
     * @description  业务统计饼状图Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/businessPieEcharts")
    @ResponseBody
    @ApiOperation(value = "业务统计饼状图Echarts")
    public Result businessPieEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.businessPieEcharts( params );
        return result;
    }

    /**
     * @description  业务统计排行Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/businessRankEcharts")
    @ResponseBody
    @ApiOperation(value = "业务统计排行Echarts")
    public Result businessRankEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.businessRankEcharts( params );
        return result;
    }

    /**
     * @description  统计应用启动Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/businessLineEcharts")
    @ResponseBody
    @ApiOperation(value = "统计应用启动次数")
    public Result businessLineEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.businessLineECharts( params );
        return result;
    }

    /**
     * @description  业务统计饼状图Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/policePieEcharts")
    @ResponseBody
    @ApiOperation(value = "业务统计饼状图Echarts")
    public Result policePieEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.policePieEcharts( params );
        return result;
    }

    /**
     * @description  业务统计排行Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/policeRankEcharts")
    @ResponseBody
    @ApiOperation(value = "业务统计排行Echarts")
    public Result policeRankEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.policeRankEcharts( params );
        return result;
    }

    /**
     * @description  统计应用启动Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/policeLineEcharts")
    @ResponseBody
    @ApiOperation(value = "统计应用启动次数")
    public Result policeLineEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.policeLineECharts( params );
        return result;
    }

    /**
     * @description  活跃用户排行
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appActiveRankEcharts")
    @ResponseBody
    @ApiOperation(value = "活跃用户")
    public Result appActiveRankEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appActiveRankEcharts( params );
        return result;
    }


    /**
     * @description  使用人数
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/userCountChainRatioEcharts")
    @ResponseBody
    @ApiOperation(value = "使用人数")
    public Result userConutChainRatioEcharts(@RequestBody Map<String,Object> params) {
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            params.put( "startDate","2021-01-05" ) ;
            params.put( "endDate","2021-01-11" ) ;
            params.put( "appName","通讯录" ) ;
        }
        Result result = taskRecordService.userCountChainRatioEcharts( params );
        return result;
    }

    /**
     * @description  终端机型分析
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appDeviceEcharts")
    @ResponseBody
    @ApiOperation(value = "终端机型分析")
    public Result appDeviceEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appDeviceEcharts( params );
        return result;
    }

    /**
     * @description  应用版本分析
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appVersionEcharts")
    @ResponseBody
    @ApiOperation(value = "应用版本分析")
    public Result appVersionEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appVersionEcharts( params );
        return result;
    }

    /**
     * @description  新增用户环比
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appAddChainRatioEcharts")
    @ResponseBody
    @ApiOperation(value = "新增用户环比")
    public Result appAddChainRatioEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appAddChainRatioEcharts( params );
        return result;
    }

    /**
     * @description  一天内的启动趋势折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appUserFrequencyEcharts")
    @ResponseBody
    @ApiOperation(value = "一天内的启动趋势折线图")
    public Result appUserFrequencyEcharts(@RequestBody Map<String,Object> params) {
        String openDate = PowerUtil.getString( params.get( "openDate" ) );
        String appId = PowerUtil.getString( params.get( "appId" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            openDate = "2021-01-11";
        }
        Map<String, Object> echartsData = taskRecordService.getAppUserFrequencyMap( appId,"应用1",openDate );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);

    }


    /**
     * @description  一天内活跃的启动趋势折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appActiveFrequencyEcharts")
    @ResponseBody
    @ApiOperation(value = "一天内活跃的启动趋势折线图")
    public Result appActiveFrequencyEcharts(@RequestBody Map<String,Object> params) {
        String openDate = PowerUtil.getString( params.get( "openDate" ) );
        String appId = PowerUtil.getString( params.get( "appId" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            openDate = "2021-01-11";
        }
        Map<String, Object> echartsData =  taskRecordService.appActiveFrequencyEcharts( appId,"应用1",openDate );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  一天内的启动趋势折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/serviceUserFrequencyEcharts")
    @ResponseBody
    @ApiOperation(value = "一天内的启动趋势折线图")
    public Result serviceUserFrequencyEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.serviceUserFrequencyEcharts( params );
        return result;
    }

    /**
     * @description  服务负载情况统计
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/serviceLoadHappenEcharts")
    @ResponseBody
    @ApiOperation(value = "服务负载情况统计")
    public Result serviceLoadHappenEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.serviceLoadHappenEcharts( params );
        return result;
    }

    /**
     * @description  多日的启动趋势折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appOpenFrequencyEcharts")
    @ResponseBody
    @ApiOperation(value = "多日的启动趋势折线图")
    public Result appOpenFrequencyEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appOpenFrequencyEcharts( params );
        return result;
    }


    /**
     * @description  活跃用户排名Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/activeUserEcharts")
    @ResponseBody
    @ApiOperation(value = "活跃用户排名Echarts")
    public Result activeUserEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.activeUserEcharts( params );
        return result;
    }

    /**
     * @description  应用用户总量排名Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/userCountEcharts")
    @ResponseBody
    @ApiOperation(value = "应用用户总量排名Echarts")
    public Result userCountEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.userCountEcharts( params );
        return result;
    }


    /**
     * @description  使用人数排行(用户总量)
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/userCountRank")
    @ResponseBody
    @ApiOperation(value = "使用人数排行")
    public Result userCountRank(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.userCountRank( params );
        return result;
    }



    /**
     * @description  首页-使用人次排行
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/userFrequencyRank")
    @ResponseBody
    @ApiOperation(value = "使用人次排行")
    public Result userFrequencyRank(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.userFrequencyRank( params );
        return result;
    }

    /**
     * @description  应用地区分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appAreaEcharts")
    @ResponseBody
    @ApiOperation(value = "应用地区分布Echarts")
    public Result appAreaEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appAreaEcharts( params );
        return result;
    }


    /**
     * @description  首页-各地区应用情况分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appAreaCountEcharts")
    @ResponseBody
    @ApiOperation(value = "首页-各地区应用情况分布Echarts")
    public Result appAreaCountEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appAreaCountEcharts( params );
        return result;
    }


    /**
     * @description  首页-各警种应用情况分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/appPoliceCountEcharts")
    @ResponseBody
    @ApiOperation(value = "各地区应用情况分布Echarts")
    public Result appPoliceCountEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.appPoliceCountEcharts( params );
        return result;
    }


    /**
     * @description  服务调用次数地区分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/serviceOpenEcharts")
    @ResponseBody
    @ApiOperation(value = "服务调用次数地区分布Echarts")
    public Result serviceOpenEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.serviceOpenEcharts( params );
        return result;
    }

    /**
     * @description  首页-头部卡片
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    @PostMapping("/countCardEcharts")
    @ResponseBody
    @ApiOperation(value = "首页-头部卡片")
    public Result countCardEcharts(@RequestBody Map<String,Object> params) {
        Result result = taskRecordService.countCardEcharts( params );
        return result;
    }


}
