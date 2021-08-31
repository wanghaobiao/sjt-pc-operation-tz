package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.controller;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.ScheduledTasks;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;


/**
 * 应用版本统计( AppVersionCountController )控制类
 * @author wanghbdeptName
 * @since 2021-4-27 10:39:02
 */
@RestController
@RequestMapping("/appManage/dict")
@Api(value = "应用版本统计",  tags = "应用版本统计")
public class DictController {

    private static Logger logger = Logger.getLogger( DictController.class );

    /**
     * @description 获取参数下拉
     * @param pvalue 参数类型
     * @return 实体对象
     * @date 2021-4-27 10:39:02
     * @author wanghb
     * @edit
     */
    @GetMapping("/getSelect")
    @ResponseBody
    @ApiOperation(value = "获取参数下拉")
    public Result getSelect(@RequestParam(name = "pvalue", required =false) @ApiParam("参数类型") String pvalue) {
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,ScheduledTasks.dictList.get( pvalue ));
    }
}
