package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acrabsoft.common.model.ResultEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

import org.acrabsoft.common.BuildResult;
import java.util.*;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
* 服务信息( ServiceInfoService )服务类
* @author wanghb
* @since 2021-4-26 17:46:29
*/
/**
* 服务信息( ServiceInfoService )服务实现类
* @author wanghb
* @since 2021-4-26 17:46:29
*/
@Service("serviceInfoService")
public class ServiceInfoService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );


    @Resource
    @Qualifier("mysqlJdbcTemplate")
    private JdbcTemplate mysqlJdbcTemplate;
    @Resource
    @Qualifier("mysqlNameJdbcTemplate")
    private NamedParameterJdbcTemplate mysqlNameJdbcTemplate;
    @Value("${area}")
    private String area;

    public List<Map<String, Object>> getAllList() {
        StringBuilder sb = new StringBuilder(ServiceInfoEntity.selectSql1);
        List<Map<String, Object>> list = mysqlJdbcTemplate.queryForList( sb.toString() );
        return list;
    }


    /**
     * @description  获取应用信息分页
     * @param  pageNo
     * @param  pageSize
     * @param  serviceName
     * @param  appId
     * @return  返回结果
     * @date  2021-7-12 18:24
     * @author  wanghb
     * @edit
     */
    public Result getListPage(int pageNo, int pageSize, String serviceName,String appId) {
        Pagination page = new Pagination(pageNo,pageSize);
        StringBuilder sb = new StringBuilder(ServiceInfoEntity.selectSql1);
        if (PowerUtil.isNotNull( serviceName )) {
            sb.append( " and service_name like '%" +serviceName + "%'" );
        }
        sb.append( " and deleted = 0 " );
        sb.append( " and app_id = '" +appId + "'" );

        sb.append( " and status = 1 " );
        page.setTotal( mysqlJdbcTemplate.queryForObject( "select count(1) from (" + sb + ") temp ",Integer.class ) );
        sb.append( " limit " ).append(  page.getPageStartNo() ).append( "," ).append( page.getPageSize() );
        List<ServiceInfoEntity> query = mysqlJdbcTemplate.query( sb.toString(), new BeanPropertyRowMapper<>( ServiceInfoEntity.class ) );
        for (ServiceInfoEntity row : query) {
            row.setServiceLevelName( ParamEnum.serviceLevel.getNameByCode( row.getServiceLevel() ) );
            row.setPlatformName( ParamEnum.networkAreaLog.getNameByCode( row.getPlatform() ) );
        }
        page.setRows( query );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }


    /**
     * @description  获取应用信息分页
     * @param  pageNo
     * @param  pageSize
     * @param  serviceName
     * @param  appId
     * @return  返回结果
     * @date  2021-7-12 18:24
     * @author  wanghb
     * @edit
     */
    public Result get3ListPage(int pageNo, int pageSize, String serviceName,String appId) {
        Pagination page = new Pagination(pageNo,pageSize);
        StringBuilder sb = new StringBuilder(ServiceInfoEntity.selectSql2);
        if (PowerUtil.isNotNull( serviceName )) {
            sb.append( " and A.service_name like '%" +serviceName + "%'" );
        }
        sb.append( " and B.applicant_app_id = '" +appId + "'" );
        sb.append( " and C.status IN ( 0, 2, 4, 5, 6, 9, 13, 15, 16, 17, 18, 21, 22 ) " );
        sb.append( " and C.app_type = " + ParamEnum.app_type.appType.getCode() );
        sb.append( " and B.audit_status = 1 " );
        page.setTotal( mysqlJdbcTemplate.queryForObject( "select count(1) from (" + sb + ") temp ",Integer.class ) );
        sb.append( " limit " ).append(  page.getPageStartNo() ).append( "," ).append( page.getPageSize() );
        List<ServiceInfoEntity> query = mysqlJdbcTemplate.query( sb.toString(), new BeanPropertyRowMapper<>( ServiceInfoEntity.class ) );
        for (ServiceInfoEntity row : query) {
            row.setServiceLevelName( ParamEnum.serviceLevel.getNameByCode( row.getServiceLevel() ) );
            row.setPlatformName( ParamEnum.networkAreaLog.getNameByCode( row.getPlatform() ) );
        }
        page.setRows( query );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }


    /**
     * @description  获取详情页
     * @return  返回结果
     * @date  2021-7-12 18:24
     * @author  wanghb
     * @edit
     */
    public Result view(String objId) {
        StringBuilder sb = new StringBuilder(ServiceInfoEntity.selectSql1);
        sb.append( " and obj_id = '"+objId+"'" );
        List<ServiceInfoEntity> query = mysqlJdbcTemplate.query( sb.toString(), new BeanPropertyRowMapper<>( ServiceInfoEntity.class ) );
        ServiceInfoEntity serviceInfoEntity = query.size() == 0 ? new ServiceInfoEntity() : query.get( 0 );
        if (serviceInfoEntity != null) {
            serviceInfoEntity.setServiceLevelName( ParamEnum.serviceLevel.getNameByCode( serviceInfoEntity.getServiceLevel() ) );
            serviceInfoEntity.setPlatformName( ParamEnum.networkAreaLog.getNameByCode( serviceInfoEntity.getPlatform() ) );
            serviceInfoEntity.setInterfaceTypeName( ParamEnum.interfaceTypeName.getNameByCode( serviceInfoEntity.getInterfaceType() ) );
            serviceInfoEntity.setOperatorTypeName( ParamEnum.operatorType.getNameByCode( serviceInfoEntity.getOperatorType() ) );
            String availablePlatform = serviceInfoEntity.getAvailablePlatform();
            String availablePlatformName = availablePlatform.replace( ParamEnum.networkAreaLog.networkArea1.getCode(),ParamEnum.networkAreaLog.networkArea1.getName() );
            availablePlatformName = availablePlatformName.replace( ParamEnum.networkAreaLog.networkArea2.getCode(),ParamEnum.networkAreaLog.networkArea2.getName() );
            availablePlatformName = availablePlatformName.replace( ParamEnum.networkAreaLog.networkArea3.getCode(),ParamEnum.networkAreaLog.networkArea3.getName() );
            serviceInfoEntity.setAvailablePlatformName( availablePlatformName );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,serviceInfoEntity);
    }


    public Integer getCount(Object o) {
        StringBuilder sb = new StringBuilder(ServiceInfoEntity.selectSql1);
        sb.append( " and status = 1 " );
        return mysqlJdbcTemplate.queryForObject( "select count(1) from (" + sb + ") temp ",Integer.class ) ;
    }
}
