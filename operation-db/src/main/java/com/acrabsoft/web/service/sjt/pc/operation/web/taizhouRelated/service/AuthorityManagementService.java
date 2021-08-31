package com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.service;

import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.JdbcTemplateUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MapUtil;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.service.AuthorityManagementService;
import com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.dao.AuthorityManagementDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.entity.*;
import org.springframework.data.jpa.domain.Specification;
import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import org.acrabsoft.common.BuildResult;
import java.util.*;
import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
* 权限管理( AuthorityManagementService )服务类
* @author wanghb
* @since 2021-8-31 10:54:56
*/
/**
* 权限管理( AuthorityManagementService )服务实现类
* @author wanghb
* @since 2021-8-31 10:54:56
*/
@Service("authorityManagementService")
public class AuthorityManagementService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private AuthorityManagementDao authorityManagementDao;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Resource
    private BaseDao baseDao;

    /**
    * @description  分页查询
    * @param  pageNo  一页个数
    * @param  pageSize  页码
    * @return  返回结果
    * @date  20/09/05 8:13
    * @author  wanghb
    * @edit
    */
    public Result getListPage(int pageNo, int pageSize,String name,String startDate,String endDate) {
        Pagination page = new Pagination(pageNo,pageSize);
        SQL sql = new SQL();
        sql.SELECT("l1.*");
        sql.FROM(AuthorityManagementEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        if (PowerUtil.isNotNull( name )) {
            sql.WHERE(new StringBuilder( " l1.createUser like '%" ).append( name ).append( "%'" ).toString());
        }
        if (PowerUtil.isNotNull( startDate )){
            sql.WHERE( " l1.create_time >= "+ JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.WHERE( " l1.create_time <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        sql.ORDER_BY( "l1.create_time desc" );
        baseDao.getPaginationByNactiveSql( sql, page);
        List<AuthorityManagementEntity> rows = MapUtil.toListBean( page.getRows(),AuthorityManagementEntity.class );
        page.setRows( rows );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }

    /**
    * @description  获取全部
    * @return  返回结果
    * @date  20/09/05 8:13
    * @author  wanghb
    * @edit
    */
    public List<AuthorityManagementEntity> getAllList() {
        SQL sql = new SQL();
        sql.SELECT("l1.*");
        sql.FROM(AuthorityManagementEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
        sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        List<AuthorityManagementEntity> rows =  MapUtil.toListBean( baseDao.getListByNactiveSql( sql ),AuthorityManagementEntity.class );
        return rows;
    }



    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-8-31 10:54:56
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        AuthorityManagementEntity authorityManagementEntity = this.baseDao.getById(AuthorityManagementEntity.class, id);
        if (authorityManagementEntity != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,authorityManagementEntity);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public AuthorityManagementEntity getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<AuthorityManagementEntity> list = baseDao.get(AuthorityManagementEntity.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param authorityManagementEntity 实体
    * @return 无返回值
    * @date 2021-8-31 10:54:56
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(AuthorityManagementEntity authorityManagementEntity) {
        String id = authorityManagementEntity.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( authorityManagementEntity, id, nowDate );
        } else {
            MapUtil.setUpdateBean( authorityManagementEntity, nowDate );
        }
        this.baseDao.update( authorityManagementEntity );
        this.authorityManagementDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2021-8-31 10:54:56
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        AuthorityManagementEntity authorityManagementEntity = new AuthorityManagementEntity();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,authorityManagementEntity);
    }


    /**
    * @description 保存
    * @param authorityManagementEntity 实体
    * @return 无返回值
    * @date 2021-8-31 10:54:56
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(AuthorityManagementEntity authorityManagementEntity) {
        Result result = saveOrUpdate( authorityManagementEntity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-8-31 10:54:56
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(AuthorityManagementEntity.class, id);
        this.authorityManagementDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-8-31 10:54:56
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(AuthorityManagementEntity.class, ids.toArray());
        this.authorityManagementDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-8-31 10:54:56
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        AuthorityManagementEntity authorityManagementEntity = this.baseDao.getById(AuthorityManagementEntity.class, id);
        if (authorityManagementEntity != null) {
            Date nowDate = new Date();
            authorityManagementEntity.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( authorityManagementEntity, nowDate );
            this.baseDao.update( authorityManagementEntity );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-8-31 10:54:56
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.authorityManagementDao.batchLogicDelete(ids);
        this.authorityManagementDao.batchLogicDeleteDetail(ids);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }




    /**
    * @description  根据条件查询数量
    * @param  id  id
    * @return  查询数据
    * @date  2020-10-29 14:29
    * @author  wanghb
    * @edit
    */
    public Integer getCount(String id) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( id )) {
            queryConditions.add(new QueryCondition("id",QueryCondition.NOEQ, id));
        }
        /*if (PowerUtil.isNotNull(  )) {
            queryConditions.add(new QueryCondition("", ));
        }*/
        Integer count = baseDao.getCount( AuthorityManagementEntity.class, queryConditions);
        return count;
    }

}
