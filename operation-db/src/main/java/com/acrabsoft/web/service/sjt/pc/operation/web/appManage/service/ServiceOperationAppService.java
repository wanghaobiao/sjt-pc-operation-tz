package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service;

import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MapUtil;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.ServiceOperationAppService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.dao.ServiceOperationAppDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.*;
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
* 服务操作应用表( ServiceOperationAppService )服务类
* @author wanghb
* @since 2021-6-4 17:04:58
*/
/**
* 服务操作应用表( ServiceOperationAppService )服务实现类
* @author wanghb
* @since 2021-6-4 17:04:58
*/
@Service("serviceOperationAppService")
public class ServiceOperationAppService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private ServiceOperationAppDao serviceOperationAppDao;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
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
    public Result getListPage(int pageNo, int pageSize) {
        Pagination page = new Pagination(pageNo,pageSize);
        SQL sql = new SQL();
        sql.SELECT("l1.*");
        sql.FROM(ServiceOperationAppEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
            sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        baseDao.getPaginationByNactiveSql( sql, page);
        List<ServiceOperationAppEntity> rows = MapUtil.toListBean( page.getRows(),ServiceOperationAppEntity.class );
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
    public List<ServiceOperationAppEntity> getAllList() {
        SQL sql = new SQL();
        sql.SELECT("l1.*");
        sql.FROM(ServiceOperationAppEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
        sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        List<ServiceOperationAppEntity> rows =  MapUtil.toListBean( baseDao.getListByNactiveSql( sql ),ServiceOperationAppEntity.class );
        return rows;
    }



    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-6-4 17:04:58
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        ServiceOperationAppEntity serviceOperationAppEntity = this.baseDao.getById(ServiceOperationAppEntity.class, id);
        if (serviceOperationAppEntity != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,serviceOperationAppEntity);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public ServiceOperationAppEntity getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<ServiceOperationAppEntity> list = baseDao.get(ServiceOperationAppEntity.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param serviceOperationAppEntity 实体
    * @return 无返回值
    * @date 2021-6-4 17:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(ServiceOperationAppEntity serviceOperationAppEntity) {
        String id = serviceOperationAppEntity.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( serviceOperationAppEntity, id, nowDate );
        } else {
            MapUtil.setUpdateBean( serviceOperationAppEntity, nowDate );
        }
        this.baseDao.update( serviceOperationAppEntity );
        this.serviceOperationAppDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2021-6-4 17:04:58
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        ServiceOperationAppEntity serviceOperationAppEntity = new ServiceOperationAppEntity();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,serviceOperationAppEntity);
    }


    /**
    * @description 保存
    * @param serviceOperationAppEntity 实体
    * @return 无返回值
    * @date 2021-6-4 17:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(ServiceOperationAppEntity serviceOperationAppEntity) {
        Result result = saveOrUpdate( serviceOperationAppEntity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-6-4 17:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(ServiceOperationAppEntity.class, id);
        this.serviceOperationAppDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-6-4 17:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(ServiceOperationAppEntity.class, ids.toArray());
        this.serviceOperationAppDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-6-4 17:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        ServiceOperationAppEntity serviceOperationAppEntity = this.baseDao.getById(ServiceOperationAppEntity.class, id);
        if (serviceOperationAppEntity != null) {
            Date nowDate = new Date();
            serviceOperationAppEntity.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( serviceOperationAppEntity, nowDate );
            this.baseDao.update( serviceOperationAppEntity );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-6-4 17:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.serviceOperationAppDao.batchLogicDelete(ids);
        this.serviceOperationAppDao.batchLogicDeleteDetail(ids);
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
        Integer count = baseDao.getCount( ServiceOperationAppEntity.class, queryConditions);
        return count;
    }

}
