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
import com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.service.DataCollectionManagerService;
import com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.dao.DataCollectionManagerDao;
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
* 数据采集管理( DataCollectionManagerService )服务类
* @author wanghb
* @since 2021-8-30 20:04:58
*/
/**
* 数据采集管理( DataCollectionManagerService )服务实现类
* @author wanghb
* @since 2021-8-30 20:04:58
*/
@Service("dataCollectionManagerService")
public class DataCollectionManagerService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private DataCollectionManagerDao dataCollectionManagerDao;
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
        sql.FROM(DataCollectionManagerEntity.tableName + " l1 ");
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
        List<DataCollectionManagerEntity> rows = MapUtil.toListBean( page.getRows(),DataCollectionManagerEntity.class );
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
    public List<DataCollectionManagerEntity> getAllList() {
        SQL sql = new SQL();
        sql.SELECT("l1.*");
        sql.FROM(DataCollectionManagerEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
        sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        List<DataCollectionManagerEntity> rows =  MapUtil.toListBean( baseDao.getListByNactiveSql( sql ),DataCollectionManagerEntity.class );
        return rows;
    }



    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-8-30 20:04:58
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        DataCollectionManagerEntity dataCollectionManagerEntity = this.baseDao.getById(DataCollectionManagerEntity.class, id);
        if (dataCollectionManagerEntity != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,dataCollectionManagerEntity);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public DataCollectionManagerEntity getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<DataCollectionManagerEntity> list = baseDao.get(DataCollectionManagerEntity.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param dataCollectionManagerEntity 实体
    * @return 无返回值
    * @date 2021-8-30 20:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(DataCollectionManagerEntity dataCollectionManagerEntity) {
        String id = dataCollectionManagerEntity.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( dataCollectionManagerEntity, id, nowDate );
        } else {
            MapUtil.setUpdateBean( dataCollectionManagerEntity, nowDate );
        }
        this.baseDao.update( dataCollectionManagerEntity );
        this.dataCollectionManagerDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2021-8-30 20:04:58
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        DataCollectionManagerEntity dataCollectionManagerEntity = new DataCollectionManagerEntity();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,dataCollectionManagerEntity);
    }


    /**
    * @description 保存
    * @param dataCollectionManagerEntity 实体
    * @return 无返回值
    * @date 2021-8-30 20:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(DataCollectionManagerEntity dataCollectionManagerEntity) {
        Result result = saveOrUpdate( dataCollectionManagerEntity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-8-30 20:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(DataCollectionManagerEntity.class, id);
        this.dataCollectionManagerDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-8-30 20:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(DataCollectionManagerEntity.class, ids.toArray());
        this.dataCollectionManagerDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-8-30 20:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        DataCollectionManagerEntity dataCollectionManagerEntity = this.baseDao.getById(DataCollectionManagerEntity.class, id);
        if (dataCollectionManagerEntity != null) {
            Date nowDate = new Date();
            dataCollectionManagerEntity.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( dataCollectionManagerEntity, nowDate );
            this.baseDao.update( dataCollectionManagerEntity );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-8-30 20:04:58
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.dataCollectionManagerDao.batchLogicDelete(ids);
        this.dataCollectionManagerDao.batchLogicDeleteDetail(ids);
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
        Integer count = baseDao.getCount( DataCollectionManagerEntity.class, queryConditions);
        return count;
    }

}
