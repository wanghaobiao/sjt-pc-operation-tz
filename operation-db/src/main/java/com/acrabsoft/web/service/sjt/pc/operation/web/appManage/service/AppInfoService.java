package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ScheduledTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acrabsoft.common.model.ResultEnum;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.dao.AppInfoDao;
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
* 应用信息表( AppInfoService )服务类
* @author wanghb
* @since 2021-4-26 17:46:24
*/
/**
* 应用信息表( AppInfoService )服务实现类
* @author wanghb
* @since 2021-4-26 17:46:24
*/
@Service("appInfoService")
public class AppInfoService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private AppInfoDao appInfoDao;
    @Resource
    @Qualifier("mysqlJdbcTemplate")
    private JdbcTemplate mysqlJdbcTemplate;
    @Resource
    @Qualifier("mysqlNameJdbcTemplate")
    private NamedParameterJdbcTemplate mysqlNameJdbcTemplate;

    @Value("${area}")
    private String area;

    /**
     * @description
     * @param
     * @return  返回结果
     * @date  2021-7-12 18:11
     * @author  wanghb
     * @edit
     */
    public List<Map<String, Object>> getAllList() {
        StringBuilder sb = new StringBuilder("select * from "+ AppInfoEntity.tableName);

        List<Map<String, Object>> list = mysqlJdbcTemplate.queryForList( sb.toString() );
        return list;
    }


    /**
     * @description
     * @param
     * @return  返回结果
     * @date  2021-7-12 18:11
     * @author  wanghb
     * @edit
     */
    public List<AppInfoEntity> getAllByCondition(String appName) {
        StringBuilder sb = new StringBuilder(AppInfoEntity.selectSql1 );
        if (PowerUtil.isNotNull( appName )) {
            sb.append( " and app_name like '%" +appName + "%'" );
        }
        sb.append( " and status IN ( 0, 2, 4, 5, 6, 9, 13, 15, 16, 17, 18, 21, 22 ) " );
        sb.append( " and app_type = " + ParamEnum.app_type.appType.getCode() );
        sb.append( " and deleted = 0 " );

        List<AppInfoEntity> query = mysqlJdbcTemplate.query( sb.toString(), new BeanPropertyRowMapper<>( AppInfoEntity.class ) );
        for (AppInfoEntity row : query) {
            row.setAreaName( ScheduledTasks.dict.get( ParamEnum.dicType.APP_AREA.getCode() ).get( row.getCity() ) );
            row.setNetworkAreaName( ParamEnum.networkArea.getNameByCode( row.getNetworkArea() ) );
            row.setPoliceName( ScheduledTasks.dict.get( ParamEnum.dicType.APP_POLICE.getCode() ).get( row.getPolice() )  );
        }
        return query;
    }

    /**
     * @description  分页查询
     * @param  pageNo
     * @param  pageSize
     * @param  appName
     * @return  返回结果
     * @date  2021-7-12 20:27
     * @author  wanghb
     * @edit
     */
    public Result getListPage(int pageNo, int pageSize, String appName) {
        Pagination page = new Pagination(pageNo,pageSize);
        StringBuilder sb = new StringBuilder(AppInfoEntity.selectSql1);
        if (PowerUtil.isNotNull( appName )) {
            sb.append( " and app_name like '%" +appName + "%'" );
        }
        sb.append( " and status IN ( 0, 2, 4, 5, 6, 9, 13, 15, 16, 17, 18, 21, 22 ) " );
        sb.append( " and app_type = " + ParamEnum.app_type.appType.getCode() );
        sb.append( " and deleted = 0 " );
        page.setTotal( mysqlJdbcTemplate.queryForObject( "select count(1) from (" + sb + ") temp ",Integer.class ) );
        sb.append( " limit " ).append(  page.getPageStartNo() ).append( "," ).append( page.getPageSize() );
        List<AppInfoEntity> query = mysqlJdbcTemplate.query( sb.toString(), new BeanPropertyRowMapper<>( AppInfoEntity.class ) );
        for (AppInfoEntity row : query) {
            row.setAreaName( ScheduledTasks.dict.get( ParamEnum.dicType.APP_AREA.getCode() ).get( row.getCity() ) );
            row.setNetworkAreaName( ParamEnum.networkArea.getNameByCode( row.getNetworkArea() ) );
            row.setPoliceName( ScheduledTasks.dict.get( ParamEnum.dicType.APP_POLICE.getCode() ).get( row.getPolice() )  );
            row.setCategoryName( ScheduledTasks.dict.get( ParamEnum.dicType.APP_CATEGORY.getCode() ).get( row.getCategory() )  );
        }
        page.setRows( query );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }


    /**
     * @description  详情
     * @param  objId
     * @return  返回结果
     * @date  2021-7-12 20:27
     * @author  wanghb
     * @edit
     */
    public Result view(String objId) {
        StringBuilder sb = new StringBuilder(AppInfoEntity.selectSql1);
        sb.append( " and obj_id = '"+objId+"'" );
        List<AppInfoEntity> query = mysqlJdbcTemplate.query( sb.toString(), new BeanPropertyRowMapper<>( AppInfoEntity.class ) );
        AppInfoEntity appInfoEntity = query.size() == 0 ? new AppInfoEntity() : query.get( 0 );
        if (appInfoEntity != null) {
            appInfoEntity.setAreaName( ScheduledTasks.dict.get( ParamEnum.dicType.APP_AREA.getCode() ).get( appInfoEntity.getCity() )  );
            appInfoEntity.setNetworkAreaName( ParamEnum.networkArea.getNameByCode( appInfoEntity.getNetworkArea() ) );
            appInfoEntity.setPoliceName(ScheduledTasks.dict.get( ParamEnum.dicType.APP_POLICE.getCode() ).get( appInfoEntity.getPolice() ) );
            appInfoEntity.setCategoryName( ScheduledTasks.dict.get( ParamEnum.dicType.APP_CATEGORY.getCode() ).get( appInfoEntity.getCategory() )  );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,appInfoEntity);
    }

    public Integer getCount(String areaCode,String police) {
        StringBuilder sb = new StringBuilder(AppInfoEntity.selectSql1);
        if (PowerUtil.isNotNull( areaCode )) {
            sb.append( " and city = '"+areaCode+"'" );
        }
        if (PowerUtil.isNotNull( police )) {
            sb.append( " and police = '"+police+"'" );
        }
        return  mysqlJdbcTemplate.queryForObject( "select count(1) from (" + sb + ") temp ",Integer.class ) ;
    }
}
