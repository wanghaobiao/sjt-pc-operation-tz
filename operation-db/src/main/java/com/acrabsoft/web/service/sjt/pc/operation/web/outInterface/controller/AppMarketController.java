package com.acrabsoft.web.service.sjt.pc.operation.web.outInterface.controller;

import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HiveService;
import com.acrabsoft.web.service.sjt.pc.operation.web.outInterface.service.AppMarketService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ScheduledTasks;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
* 应用市场相关接口控制类
* @author wanghbdeptName
* @since 2020-11-23 14:34:51
*/
@RestController
@RequestMapping("/outInterface/appMarket")
@Api(tags = "应用市场相关接口")
public class AppMarketController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    /**
    * 服务对象
    */
    @Resource
    private AppMarketService appMarketService;
    @Resource
    protected HttpServletResponse response;
    @Resource
    protected HBaseService hBaseService;
    @Resource
    protected HiveService hiveService;
    @Resource
    protected SequenceService sequenceService;
    @Autowired
    @Qualifier("hiveJdbcTemplate")
    private JdbcTemplate hiveJdbcTemplate;
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate oracleJdbcTemplate;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Value("${spring.profiles.active}")
    private String active;

    /**
     * @description 安卓charts汇总接口
     * @param params
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    @PostMapping("/androidChartsData")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "app汇总接口")
    public Result androidChartsData(@RequestBody Map<String, Object> params ) {
        Result result = appMarketService.androidChartsData(params );
        return result;
    }

    /**
     * @description echarts图表(质态相关的)
     * @param params
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    @PostMapping("/echartsDataForZt")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "echarts图表(质态相关的)")
    public Result echartsDataForZt(@RequestBody Map<String, Object> params ) {
        Result result = appMarketService.echartsDataForZt(params );
        return result;
    }



    /**
     * @description tableData 表相关的接口
     * @param params
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    @PostMapping("/tableData")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "tableData 表相关的接口")
    public Result tableData(@RequestBody Map<String, Object> params ) {
        Result result = appMarketService.tableData(params );
        return result;
    }

    /**
     * @description 应用市场web端-汇总接口
     * @param params
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    @PostMapping("/echartsDataForWeb")
    @ResponseBody
    @NotAuthorize
    @ApiOperation(value = "应用市场web端-汇总接口")
    public Result echartsDataForWeb(@RequestBody Map<String, Object> params ) {
        Result result = appMarketService.echartsDataForWeb(params );
        return result;
    }





}
