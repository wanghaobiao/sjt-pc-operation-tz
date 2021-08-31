package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.controller;

import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppInfoService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.*;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.BuildResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
* 应用信息表( AppInfoController )控制类
* @author wanghbdeptName
* @since 2021-4-26 10:48:57
*/
@RestController
@RequestMapping("/appManage/appInfo")
@Api(value = "应用信息表",  tags = "应用信息表")
public class AppInfoController {

    private static Logger logger = Logger.getLogger( AppInfoController.class );

    /**
    * 服务对象
    */
    @Resource
    private AppInfoService appInfoService;


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
                              @RequestParam(defaultValue = "10") @ApiParam("一页个数") int pageSize,
                              @RequestParam(defaultValue = "") @ApiParam("应用名称") String appName) {
        Result result = appInfoService.getListPage( pageNo,pageSize,appName);
        return result;
    }

    /**
    * @description  查询全部
    * @param  appName  一页个数
    * @return  返回结果
    * @date  20/09/05 8:04
    * @author  wanghb
    * @edit
    */
    @PostMapping("/getAllList")
    @ResponseBody
    @ApiOperation(value = "查询全部")
    public Result getAllList(@RequestParam(defaultValue = "") @ApiParam("应用名称") String appName) {
        List<AppInfoEntity> allList = appInfoService.getAllByCondition( appName );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,allList);
    }


    /**
    * @description 详情
    * @param objId 主键id
    * @return 实体对象
    * @date 2021-4-26 10:48:57
    * @author wanghb
    * @edit
    */
    @GetMapping("/view")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result view(@RequestParam(name = "objId", required =false) @ApiParam("主键id") String objId) {
        Result result = appInfoService.view( objId );
        return result;
    }


    /**
    * @description 保存或更新
    * @param appInfoEntity 实体
    * @return 无返回值
    * @date 2021-4-26 10:48:57
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/saveOrUpdate")
    @ResponseBody
    @ApiOperation(value = "保存或更新")
    public Result saveOrUpdate(@RequestBody AppInfoEntity appInfoEntity) {
        Result result = appInfoService.saveOrUpdate( appInfoEntity );
        return result;
    }



    *//**
    * @description 去保存页面
    * @return 实体对象
    * @date 2021-4-26 10:48:57
    * @author wanghb
    * @edit
    *//*
    @GetMapping("/goSave")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result goSave() {
        Result result = appInfoService.goSave(  );
        return result;
    }


    *//**
    * @description 保存
    * @param appInfoEntity 实体
    * @return 无返回值
    * @date 2021-4-26 10:48:57
    * @author wanghb
    * @edit
    *//*
    @PostMapping("/save")
    @ResponseBody
    @ApiOperation(value = "保存")
    public Result save(@RequestBody AppInfoEntity appInfoEntity) {
        Result result = appInfoService.save( appInfoEntity );
        return result;
    }


    *//**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-26 10:48:57
    * @author wanghb
    * @edit
    *//*
    @GetMapping("/delete")
    @ResponseBody
    @ApiOperation(value = "删除")
    public Result delete(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = appInfoService.delete( id );
        return result;
    }


    *//**
    * @description 批量删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2021-4-26 10:48:57
    * @author wanghb
    * @edit
    *//*
    @PostMapping("/batchDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchDelete(@RequestBody List<String> ids) {
        Result result = appInfoService.batchDelete( ids );
        return result;
    }*/


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-26 10:48:57
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/logicDelete")
    @ResponseBody
    @ApiOperation(value = "逻辑删除")
    public Result logicDelete(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = appInfoService.logicDelete( id );
        return result;
    }*/


    /**
    * @description 批量逻辑删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2021-4-26 10:48:57
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/batchLogicDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchLogicDelete(@RequestBody List<String> ids) {
        Result result = appInfoService.batchLogicDelete( ids );
        return result;
    }*/


}
