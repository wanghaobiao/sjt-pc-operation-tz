package com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.controller;

import com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.service.DataManagemerService;
import com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.entity.*;
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

/**
* 数据管理( DataManagemerController )控制类
* @author wanghbdeptName
* @since 2021-8-31 10:55:02
*/
@RestController
@RequestMapping("/taizhouRelated/dataManagemer")
@Api(value = "数据管理",  tags = "数据管理")
public class DataManagemerController {

    private static Logger logger = Logger.getLogger( DataManagemerController.class );

    /**
    * 服务对象
    */
    @Resource
    private DataManagemerService dataManagemerService;


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
                              @RequestParam(defaultValue = "") @ApiParam("名称") String name,
                              @RequestParam(defaultValue = "") @ApiParam("开始时间") String startDate,
                              @RequestParam(defaultValue = "") @ApiParam("结束时间") String endDate) {
        Result result = dataManagemerService.getListPage( pageNo,pageSize,name,startDate,endDate);
        return result;
    }


    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-8-31 10:55:02
    * @author wanghb
    * @edit
    */
    @GetMapping("/view")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result view(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = dataManagemerService.view( id );
        return result;
    }


    /**
    * @description 保存或更新
    * @param dataManagemerEntity 实体
    * @return 无返回值
    * @date 2021-8-31 10:55:02
    * @author wanghb
    * @edit
    */
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @ApiOperation(value = "保存或更新")
    public Result saveOrUpdate(@RequestBody DataManagemerEntity dataManagemerEntity) {
        Result result = dataManagemerService.saveOrUpdate( dataManagemerEntity );
        return result;
    }



    /**
    * @description 去保存页面
    * @return 实体对象
    * @date 2021-8-31 10:55:02
    * @author wanghb
    * @edit
    */
    @GetMapping("/goSave")
    @ResponseBody
    @ApiOperation(value = "详情")
    public Result goSave() {
        Result result = dataManagemerService.goSave(  );
        return result;
    }


    /**
    * @description 保存
    * @param dataManagemerEntity 实体
    * @return 无返回值
    * @date 2021-8-31 10:55:02
    * @author wanghb
    * @edit
    */
    @PostMapping("/save")
    @ResponseBody
    @ApiOperation(value = "保存")
    public Result save(@RequestBody DataManagemerEntity dataManagemerEntity) {
        Result result = dataManagemerService.save( dataManagemerEntity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-8-31 10:55:02
    * @author wanghb
    * @edit
    */
    @GetMapping("/delete")
    @ResponseBody
    @ApiOperation(value = "删除")
    public Result delete(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = dataManagemerService.delete( id );
        return result;
    }


    /**
    * @description 批量删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2021-8-31 10:55:02
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/batchDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchDelete(@RequestBody List<String> ids) {
        Result result = dataManagemerService.batchDelete( ids );
        return result;
    }*/


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-8-31 10:55:02
    * @author wanghb
    * @edit
    */
    /*@GetMapping("/logicDelete")
    @ResponseBody
    @ApiOperation(value = "逻辑删除")
    public Result logicDelete(@RequestParam(name = "id", required =false) @ApiParam("主键id") String id) {
        Result result = dataManagemerService.logicDelete( id );
        return result;
    }*/


    /**
    * @description 批量逻辑删除
    * @param ids 主键id
    * @return 实体对象
    * @date 2021-8-31 10:55:02
    * @author wanghb
    * @edit
    */
    /*@PostMapping("/batchLogicDelete")
    @ResponseBody
    @ApiOperation(value = "批量删除")
    public Result batchLogicDelete(@RequestBody List<String> ids) {
        Result result = dataManagemerService.batchLogicDelete( ids );
        return result;
    }*/


}
