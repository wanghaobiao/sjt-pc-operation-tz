package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.controller;

import com.acrabsoft.web.annotation.NotAuthorize;
import com.acrabsoft.web.pojo.user.BasicUser;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HiveService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.ServiceLogService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.kafkaHuawei.KafkaMsgSender;
import com.acrabsoft.web.utils.JwtUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
* 服务日志信息( AppLogController )控制类
* @author wanghbdeptName
* @since 2020-11-23 14:34:51
*/
@RestController
@RequestMapping("/appManage/serviceLog")
@Api(tags = "服务日志信息")
public class ServiceLogController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    /**
    * 服务对象
    */
    @Resource
    private ServiceLogService serviceLogService;

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
    @Autowired
    private KafkaMsgSender KafkaHuawei;
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
    * @description   保存
    * @param params 实体
    * @return 无返回值
    * @date 2020-9-24 15:56:18
    * @author wanghb
    * @edit
    */
    @PostMapping("/save")
    @ResponseBody
    @ApiOperation(value = "保存", notes = "保存")
    public Result save(@RequestBody Map<String, Object> params) {
        return serviceLogService.save(params);
    }




    /**
     * @description  查询列表
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询列表", notes = "查询列表")
    public Result list()  {
        List<Map<String, Object>> list = hiveJdbcTemplate.queryForList( "select * from hbase_students" );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list);
    }


    /**
     * @description  日志推送
     * @param
     * @return  返回结果
     * @date  2021-3-25 14:19
     * @author  wanghb
     * @edit
     */
    @PostMapping ("/logPush")
    @NotAuthorize
    @ResponseBody
    public Result appLogPush(@RequestBody Map<String, Object> params) {
        //"{\"companyCode\":\"test123456\",\"deptName\":\"测试部门\",\"networkEnv\":\"2\",\"code\":\"\",\"startPerson\":\"测试人\",\"versionNum\":\"1.0\",\"companyName\":\"测试公司\",\"areaCode\":\"3205000000\",\"startPersonCode\":\"111111\",\"areaName\":\"南京市\",\"name\":\"AppStoreSDK\",\"imei\":\"865452047637688\",\"startTime\":\"2021-04-01 15:14:10\",\"deviceModel\":\"BLA-AL00\",\"id\":\"f37745a8e3454cf6915c94c88573fef91618797582887\",\"businessType\":\"2\",\"deptCode\":\"320500\"}"
        KafkaHuawei.sendMsg( ParamEnum.topic.serviceLog.getKafkaTopic(), JSON.toJSONString( params ));
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    public static void main(String[] args) {
        BasicUser basicUser = new BasicUser();
        basicUser.setUserid( "13629848747" );
        String jwtstr = JwtUtil.getJwtStrByPreFix( JSON.toJSONString(basicUser));
        System.out.println(jwtstr);
    }

}
