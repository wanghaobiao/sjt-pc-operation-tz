package com.acrabsoft.web.service.sjt.pc.operation.web.system.service;
import java.math.BigDecimal;
import java.util.Date;

import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.*;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppInfoService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.AppOpenCountService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.ServiceInfoService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.*;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.acrabsoft.common.model.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import com.acrabsoft.web.dao.base.QueryCondition;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.dao.TaskRecordDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.entity.*;
import com.acrabsoft.web.dao.base.BaseDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import org.acrabsoft.common.BuildResult;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.acrabsoft.common.model.Pagination;
import org.acrabsoft.common.model.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
* 任务执行记录( TaskRecordService )服务类
* @author wanghb
* @since 2021-4-15 17:23:27
*/
/**
* 任务执行记录( TaskRecordService )服务实现类
* @author wanghb
* @since 2021-4-15 17:23:27
*/
@Service("taskRecordService")
public class TaskRecordService extends BaseController {

    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    @Resource
    private TaskRecordDao taskRecordDao;
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    @Qualifier("mysqlJdbcTemplate")
    private JdbcTemplate mysqlJdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Resource
    private AppOpenCountService appOpenCountService;
    @Value("${spring.profiles.active}")
    private String active;

    @Resource
    private BaseDao baseDao;
    @Autowired
    private AppInfoService appInfoService;
    @Autowired
    private ServiceInfoService serviceInfoService;

    @Resource
    private HBaseService hBaseService;

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
        sql.FROM(TaskRecordEntity.tableName + " l1 ");
        sql.WHERE(new StringBuilder( " l1.deleted = '" ).append( ParamEnum.deleted.noDel.getCode() ).append( "'" ).toString());
        /*if (PowerUtil.isNotNull( name )) {
            sql.WHERE(new StringBuilder( " l1.name like '%" ).append( name ).append( "%'" ).toString());
        }*/
        sql.ORDER_BY( "l1.create_time desc" );
        baseDao.getPaginationByNactiveSql( sql, page);
        List<TaskRecordEntity> rows = MapUtil.toListBean( page.getRows(),TaskRecordEntity.class );
        listPageStatus(rows);
        page.setRows( rows );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }

    /**
     * @description  状态处理
     * @param  rows
     * @return  返回结果
     * @date  2021-4-16 18:00
     * @author  wanghb
     * @edit
     */
    public void listPageStatus(List<TaskRecordEntity> rows) {
        for (int i = 0; i < rows.size(); i++) {
            TaskRecordEntity temp = rows.get(i);
            if (ParamEnum.taskRecordStatus.status1.getCode().equals( temp.getStatus() ) && PowerUtil.isNull( temp.getDuration() )) {
                temp.setDuration( PowerUtil.getBigDecimal( System.currentTimeMillis() - temp.getStartDate().getTime() ).divide( new BigDecimal( "1000" ),2, BigDecimal.ROUND_CEILING  ) );
            }
        }
    }

    /**
    * @description 详情
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    public Result view(String id) {
        TaskRecordEntity taskRecordEntity = this.baseDao.getById(TaskRecordEntity.class, id);
        if (taskRecordEntity != null) {
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,taskRecordEntity);
    }


    /**
    * @description 获取唯一
    * @param code
    * @return 实体对象
    * @date 2020-12-29 11:06:43
    * @author wanghb
    * @edit
    */
    public TaskRecordEntity getOne(String code) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(new QueryCondition("deleted", ParamEnum.deleted.noDel.getCode()));
        if (PowerUtil.isNotNull( code )) {
            queryConditions.add( new QueryCondition("code", code));
        }
        List<TaskRecordEntity> list = baseDao.get(TaskRecordEntity.class, queryConditions);
        return list.size() > 0 ? list.get( 0 ) : null;
    }

    /**
    * @description 保存或更新
    * @param taskRecordEntity 实体
    * @return 无返回值
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result saveOrUpdate(TaskRecordEntity taskRecordEntity) {
        String id = taskRecordEntity.getId();
        Date nowDate = new Date();
        if (PowerUtil.isNull( id )) {
            id = CodeUtils.getUUID32();
            MapUtil.setCreateBean( taskRecordEntity, id, nowDate );
        } else {
            MapUtil.setUpdateBean( taskRecordEntity, nowDate );
        }
        this.baseDao.update( taskRecordEntity );
        this.taskRecordDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description  去保存页面
    * @return  返回结果
    * @date  2021-4-15 17:23:27
    * @author  wanghb
    * @edit
    */
    public Result goSave() {
        TaskRecordEntity taskRecordEntity = new TaskRecordEntity();
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,taskRecordEntity);
    }


    /**
    * @description 保存
    * @param taskRecordEntity 实体
    * @return 无返回值
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result save(TaskRecordEntity taskRecordEntity) {
        Result result = saveOrUpdate( taskRecordEntity );
        return result;
    }


    /**
    * @description 删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result delete(String id) {
        this.baseDao.delete(TaskRecordEntity.class, id);
        this.taskRecordDao.deleteDetail( id );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchDelete(List<String> ids) {
        this.baseDao.delete(TaskRecordEntity.class, ids.toArray());
        this.taskRecordDao.batchDeleteDetail( ids );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 逻辑删除
    * @param id 主键id
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result logicDelete(String id) {
        TaskRecordEntity taskRecordEntity = this.baseDao.getById(TaskRecordEntity.class, id);
        if (taskRecordEntity != null) {
            Date nowDate = new Date();
            taskRecordEntity.setDeleted( ParamEnum.deleted.yesDel.getCode() );
            MapUtil.setUpdateBean( taskRecordEntity, nowDate );
            this.baseDao.update( taskRecordEntity );
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS );
    }


    /**
    * @description 批量逻辑删除
    * @param ids 主键ids
    * @return 实体对象
    * @date 2021-4-15 17:23:27
    * @author wanghb
    * @edit
    */
    @Transactional(rollbackOn = Exception.class)
    public Result batchLogicDelete(List<String> ids) {
        this.taskRecordDao.batchLogicDelete(ids);
        this.taskRecordDao.batchLogicDeleteDetail(ids);
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
        Integer count = baseDao.getCount( TaskRecordEntity.class, queryConditions);
        return count;
    }

    @Autowired
    private PlatformTransactionManager platformTransactionManager;


    /**
     * @description  统计应用启动次数(任务)
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool( 10 );
    @Transactional(rollbackOn = Exception.class)
    public Result appOpenCount(Map<String, Object> params) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        fixedThreadPool.execute( new Runnable() {
            Date date = new Date();
            @Override
            public void run() {
                //设置子线程共享
                RequestContextHolder.setRequestAttributes(servletRequestAttributes,true);
                TaskRecordEntity taskRecordEntity = saveTaskRecordThread(ParamEnum.taskRecord.appOpenCount,params,date);
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = platformTransactionManager.getTransaction(def);
                try {
                    String startDate = PowerUtil.getString( params.get( "startDate" ) );
                    String endDate = PowerUtil.getString( params.get( "endDate" ) );
                    /*startDate = "2021-01-07";
                    endDate = "2021-01-11";
                    startDate = "2021-04-01";
                    endDate = "2021-04-05";*/
                    //键 : 日期 + 应用名称 + personCode  值 : AppOpenCountEntity
                    Map<String, AppOpenCountEntity> appOpenCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称 值 : AppOpenCountEntity
                    Map<String, AppHourOpenCountEntity> appHourOpenCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称 值 : AppOpenCountEntity
                    Map<String, AppHourActiveCountEntity> appHourActiveCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称 + 设备型号 值 : AppDeviceCountEntity
                    Map<String, AppDeviceCountEntity> appDeviceCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称 + 应用版本 值 : AppVersionCountEntity
                    Map<String, AppVersionCountEntity> appVersionCountCache = new HashMap<>();
                    List<String> middleDate = DateUtil.getMiddleDate( DateUtil.toDate( startDate, DateUtil.DATE_SHORT ), DateUtil.toDate( endDate, DateUtil.DATE_SHORT ) );
                    BigDecimal schedule = middleDate.size() == 0 ? new BigDecimal( "100" ) : new BigDecimal( "100" ).divide(  PowerUtil.getBigDecimal( middleDate.size() ),0, BigDecimal.ROUND_CEILING );
                    for (int i = 0; i < middleDate.size(); i++) {
                        String openDate = middleDate.get(i);
                        Date currentTime = new Date();
                        List<Map<String, String>> list = hBaseService.getListMap( ParamEnum.topic.appLog.getHbaseTableName(), new StringBuilder( openDate.replaceAll( "-", "" ) ).reverse()+".*");
                        logger.info("查询条件:"+openDate.replaceAll( "-","" )+".*;结果条数:"+list.size()+";耗时:"+(System.currentTimeMillis()  - currentTime.getTime())+"毫秒;");
                        for (int j = 0; j < list.size(); j++) {
                            Map<String, String> temp = list.get(j);
                            String personCode = PowerUtil.getString( temp.get( "startPersonCode" ) );
                            String personName = PowerUtil.getString( temp.get( "startPerson" ) );

                            String appId = PowerUtil.getString( temp.get( "code" ) );
                            String appName = ScheduledTasks.appInfoIdToName.get( appId );
                            String police = ScheduledTasks.appInfoIdToPolice.get( appId );
                            String policeName = ScheduledTasks.dict.get( ParamEnum.dicType.APP_POLICE.getCode() ).get( police );
                            String category = ScheduledTasks.appInfoIdToCategory.get( appId );
                            String categoryName = ScheduledTasks.dict.get( ParamEnum.dicType.APP_CATEGORY.getCode() ).get( category );

                            String areaCode = ScheduledTasks.appInfoIdToArea.get( appId );
                            String areaName = ScheduledTasks.dict.get( ParamEnum.dicType.APP_AREA.getCode() ).get( areaCode );
                            String startTime = DateUtil.toDate( Long.parseLong( PowerUtil.getString( temp.get( "startTime" ))),DateUtil.DATE_LONG );
                            String deviceModel = PowerUtil.getString( temp.get( "deviceModel" ) );
                            String versionNum = PowerUtil.getString( temp.get( "versionNum" ) );
                            String startTimeHour = startTime.substring( 0,13 );
                            /*==========================================按天统计开始==========================================*/
                            String appOpenCountCacheKey = new StringBuilder(openDate ).append( appName ).append( personCode ).toString();
                            if (appOpenCountCache.containsKey( appOpenCountCacheKey )) {
                                AppOpenCountEntity appOpenCountEntity = appOpenCountCache.get( appOpenCountCacheKey );
                                appOpenCountEntity.setOpenCount( appOpenCountEntity.getOpenCount() + 1 );
                            }else{
                                AppOpenCountEntity appOpenCountEntity = new AppOpenCountEntity();
                                appOpenCountEntity.setAreaCode( areaCode );
                                appOpenCountEntity.setAppId( appId );
                                appOpenCountEntity.setAppName(appName);
                                appOpenCountEntity.setOpenDate(DateUtil.toDate( openDate,DateUtil.DATE_SHORT ));
                                appOpenCountEntity.setAreaName(areaName);
                                appOpenCountEntity.setPersonCode(personCode);
                                appOpenCountEntity.setPersonName( personName );
                                appOpenCountEntity.setOpenCount(1);
                                appOpenCountEntity.setPoliceCode( police );
                                appOpenCountEntity.setPoliceName( policeName );
                                appOpenCountEntity.setCategoryCode( category );
                                appOpenCountEntity.setCategoryName( categoryName );
                                appOpenCountEntity.setDeviceModel(deviceModel);
                                MapUtil.setCreateBean( appOpenCountEntity, CodeUtils.getUUID32(), date );
                                appOpenCountCache.put( appOpenCountCacheKey,appOpenCountEntity );
                            }
                            /*==========================================按天统计结束==========================================*/

                            /*==========================================按天统计终端开始==========================================*/
                            String appDeviceCountCacheKey = new StringBuilder(openDate ).append( appName ).append( deviceModel ).toString();
                            if (appDeviceCountCache.containsKey( appDeviceCountCacheKey )) {
                                AppDeviceCountEntity appDeviceCountEntity = appDeviceCountCache.get( appDeviceCountCacheKey );
                                appDeviceCountEntity.setDeviceCount( appDeviceCountEntity.getDeviceCount() + 1 );
                            }else{
                                AppDeviceCountEntity appDeviceCountEntity = new AppDeviceCountEntity();
                                appDeviceCountEntity.setAppName(appName);
                                appDeviceCountEntity.setOpenDate(DateUtil.toDate( openDate,DateUtil.DATE_SHORT ));
                                appDeviceCountEntity.setDeviceCount(1);
                                appDeviceCountEntity.setDeviceModel(deviceModel);
                                MapUtil.setCreateBean( appDeviceCountEntity, CodeUtils.getUUID32(), date );
                                appDeviceCountCache.put( appDeviceCountCacheKey,appDeviceCountEntity );
                            }
                            /*==========================================按天统计结束==========================================*/

                            /*==========================================按天统计应用版本开始==========================================*/
                            String appVersionCountCacheKey = new StringBuilder(openDate ).append( appName ).append( versionNum ).toString();
                            if (appVersionCountCache.containsKey( appVersionCountCacheKey )) {
                                AppVersionCountEntity appVersionCountEntity = appVersionCountCache.get( appVersionCountCacheKey );
                                appVersionCountEntity.setVersionCount( appVersionCountEntity.getVersionCount() + 1 );
                            }else{
                                AppVersionCountEntity appVersionCountEntity = new AppVersionCountEntity();
                                appVersionCountEntity.setAppId( appId );
                                appVersionCountEntity.setAppName(appName);
                                appVersionCountEntity.setOpenDate(DateUtil.toDate( openDate,DateUtil.DATE_SHORT ));
                                appVersionCountEntity.setVersionCount(1);
                                appVersionCountEntity.setVersionNum(versionNum);
                                MapUtil.setCreateBean( appVersionCountEntity, CodeUtils.getUUID32(), date );
                                appVersionCountCache.put( appVersionCountCacheKey,appVersionCountEntity );
                            }
                            /*==========================================按天统计应用版本结束==========================================*/

                            /*==========================================分时统计开始==========================================*/
                            String appHourOpenCountCacheKey = new StringBuilder(startTimeHour ).append( appName ).append( personCode ).toString();
                            if (appHourOpenCountCache.containsKey( appHourOpenCountCacheKey )) {
                                AppHourOpenCountEntity appHourOpenCountEntity = appHourOpenCountCache.get( appHourOpenCountCacheKey );
                                appHourOpenCountEntity.setOpenCount( appHourOpenCountEntity.getOpenCount() + 1 );
                            }else{
                                AppHourOpenCountEntity appHourOpenCountEntity = new AppHourOpenCountEntity();
                                appHourOpenCountEntity.setPersonCode( personCode );
                                appHourOpenCountEntity.setAppId( appId );
                                appHourOpenCountEntity.setAppName( appName );
                                appHourOpenCountEntity.setOpenDate(DateUtil.toDate( startTimeHour +":00:00",DateUtil.DATE_LONG ));
                                appHourOpenCountEntity.setAreaCode( areaCode );
                                appHourOpenCountEntity.setAreaName(areaName);
                                appHourOpenCountEntity.setOpenCount(1);
                                appHourOpenCountEntity.setDeviceModel(deviceModel);
                                MapUtil.setCreateBean( appHourOpenCountEntity, CodeUtils.getUUID32(), date );
                                appHourOpenCountCache.put( appHourOpenCountCacheKey,appHourOpenCountEntity );
                            }
                            /*==========================================分时统计结束==========================================*/

                            /*==========================================分时活跃用户统计开始==========================================*/
                            String appHourActiveCountCacheKey = new StringBuilder(startTimeHour ).append( appName ).toString();
                            if (appHourActiveCountCache.containsKey( appHourActiveCountCacheKey )) {
                                AppHourActiveCountEntity appHourActiveCountEntity = appHourActiveCountCache.get( appHourActiveCountCacheKey );
                                Set<String> personCodeSet = appHourActiveCountEntity.getPersonCodeSet();
                                personCodeSet.add( personCode );
                            }else{
                                AppHourActiveCountEntity appHourActiveCountEntity = new AppHourActiveCountEntity();
                                appHourActiveCountEntity.setAppId( appId );
                                appHourActiveCountEntity.setAppName(appName);
                                appHourActiveCountEntity.setOpenDate(DateUtil.toDate( startTimeHour +":00:00",DateUtil.DATE_LONG ));
                                appHourActiveCountEntity.setAreaName(areaName);
                                appHourActiveCountEntity.setPersonCodeSet(new HashSet<>(Arrays.asList( personCode )));
                                appHourActiveCountEntity.setDeviceModel(deviceModel);
                                MapUtil.setCreateBean( appHourActiveCountEntity, CodeUtils.getUUID32(), date );
                                appHourActiveCountCache.put( appHourActiveCountCacheKey,appHourActiveCountEntity );
                            }
                            /*==========================================分时活跃用户统计结束==========================================*/
                        }
                        taskRecordEntity.setSchedule( PowerUtil.getInt(  middleDate.size() - 1 == i ? 95 : schedule.multiply( PowerUtil.getBigDecimal( i + 1 ) ) ) );
                        upDateTaskRecordThread(taskRecordEntity);
                    }
                    /*==========================================按天统计开始==========================================*/
                    List<AppOpenCountEntity> appOpenCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppOpenCountEntity> entry : appOpenCountCache.entrySet()){
                        AppOpenCountEntity mapValue = entry.getValue() ;
                        appOpenCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppOpenCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( AppOpenCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appOpenCountEntities ) );
                    /*==========================================按天统计结束==========================================*/

                    /*==========================================按天统计终端开始==========================================*/
                    List<AppDeviceCountEntity> appDeviceCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppDeviceCountEntity> entry : appDeviceCountCache.entrySet()){
                        AppDeviceCountEntity mapValue = entry.getValue() ;
                        appDeviceCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppDeviceCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( AppDeviceCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appDeviceCountEntities ) );
                    /*==========================================按天统计终端结束==========================================*/

                    /*==========================================按天统计应用版本开始==========================================*/
                    List<AppVersionCountEntity> appVersionCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppVersionCountEntity> entry : appVersionCountCache.entrySet()){
                        AppVersionCountEntity mapValue = entry.getValue() ;
                        appVersionCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppVersionCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( AppVersionCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appVersionCountEntities ) );
                    /*==========================================按天统计应用版本结束==========================================*/

                    /*==========================================分时统计开始==========================================*/
                    List<AppHourOpenCountEntity> appHourOpenCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppHourOpenCountEntity> entry : appHourOpenCountCache.entrySet()){
                        AppHourOpenCountEntity mapValue = entry.getValue() ;
                        appHourOpenCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppHourOpenCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate +" 00:00:00",DateUtil.DATE_LONG))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate +" 23:59:59" ,DateUtil.DATE_LONG)) );
                    namedParameterJdbcTemplate.batchUpdate( AppHourOpenCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appHourOpenCountEntities ) );
                    /*==========================================分时统计结束==========================================*/

                    /*==========================================分时活跃用户统计开始==========================================*/
                    List<AppHourActiveCountEntity> appHourActiveCountEntities = new ArrayList<>();
                    for(Map.Entry<String, AppHourActiveCountEntity> entry : appHourActiveCountCache.entrySet()){
                        AppHourActiveCountEntity mapValue = entry.getValue() ;
                        mapValue.setOpenCount( mapValue.getPersonCodeSet().size()  );
                        //mapValue.setOpenCount( RandomUtil.getRandom(6,15) );
                        appHourActiveCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( AppHourActiveCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate +" 00:00:00",DateUtil.DATE_LONG))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate +" 23:59:59" ,DateUtil.DATE_LONG)) );
                    namedParameterJdbcTemplate.batchUpdate( AppHourActiveCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( appHourActiveCountEntities ) );
                    /*==========================================分时活跃用户统计结束==========================================*/

                    taskRecordEntity.setEndDate( new Date() );
                    taskRecordEntity.setDuration( (PowerUtil.getBigDecimal( System.currentTimeMillis()  - date.getTime() )).divide( new BigDecimal( "1000" ) ) );
                    taskRecordEntity.setSchedule( 100 );
                    taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status2.getCode() );
                    upDateTaskRecordThread(taskRecordEntity);
                    platformTransactionManager.commit(status);
                } catch (Exception e) {
                    taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status3.getCode() );
                    taskRecordEntity.setErrorInfo( e.getMessage() );
                    e.printStackTrace();
                    upDateTaskRecordThread(taskRecordEntity);
                }

            }
        } );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description  服务统计
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public static ExecutorService serviceThreadPool = Executors.newFixedThreadPool( 10 );
    @Transactional(rollbackOn = Exception.class)
    public Result serviceOpenCount(Map<String, Object> params) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        serviceThreadPool.execute( new Runnable() {
            Date date = new Date();
            @Override
            public void run() {
                //设置子线程共享
                RequestContextHolder.setRequestAttributes(servletRequestAttributes,true);
                TaskRecordEntity taskRecordEntity = saveTaskRecordThread(ParamEnum.taskRecord.serviceOpenCount,params,date);
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                TransactionStatus status = platformTransactionManager.getTransaction(def);
                try {
                    String startDate = PowerUtil.getString( params.get( "startDate" ) );
                    String endDate = PowerUtil.getString( params.get( "endDate" ) );
                    /*startDate = "2021-01-07";
                    endDate = "2021-01-11";*/
                    //键 : 日期 + 应用名称 + 负载编号  值 : AppOpenCountEntity
                    Map<String, ServiceOpenCountEntity> serviceOpenCountCache = new HashMap<>();
                    //键 : 日期(分时) + 应用名称  值 : ServiceHourOpenCountEntity
                    Map<String, ServiceHourOpenCountEntity> serviceHourOpenCountCache = new HashMap<>();
                    List<String> middleDate = DateUtil.getMiddleDate( DateUtil.toDate( startDate, DateUtil.DATE_SHORT ), DateUtil.toDate( endDate, DateUtil.DATE_SHORT ) );
                    BigDecimal schedule = middleDate.size() == 0 ? new BigDecimal( "100" ) : new BigDecimal( "100" ).divide(  PowerUtil.getBigDecimal( middleDate.size() ),0, BigDecimal.ROUND_CEILING );
                    for (int i = 0; i < middleDate.size(); i++) {
                        String openDate = middleDate.get(i);
                        Date currentTime = new Date();
                        List<Map<String, String>> list = hBaseService.getListMap( ParamEnum.topic.serviceLog.getHbaseTableName(), new StringBuilder( openDate.replaceAll( "-", "" ) ).reverse()+".*");
                        logger.info("查询条件:"+openDate.replaceAll( "-","" )+".*;结果条数:"+list.size()+";耗时:"+(System.currentTimeMillis()  - currentTime.getTime())+"毫秒;");
                        for (int j = 0; j < list.size(); j++) {
                            Map<String, String> temp = list.get(j);
                            String appObjId = PowerUtil.getString( temp.get( "appId" ) );
                            String appId = ScheduledTasks.appInfoObjIdToId.get( appObjId );
                            String appName = ScheduledTasks.appInfoObjIdToName.get( appObjId );
                            String serviceId =  PowerUtil.getString( temp.get( "serviceId" ) );
                            String serviceName = ScheduledTasks.serviceInfoIdToName.get( serviceId );
                            String loadNum = PowerUtil.getString( temp.get( "loadNum" ) );
                            String callTime = PowerUtil.getString( temp.get( "callTime" ) );
                            String callTimeHour = callTime.substring( 0,13 );
                            Boolean isError = Boolean.parseBoolean(  temp.get( "isError" ) );
                            String areaCode = ScheduledTasks.serviceInfoIdToAreaCode.get( serviceId );
                            String areaName = ScheduledTasks.dict.get( ParamEnum.dicType.APP_AREA.getCode() ).get( areaCode );
                            /*==========================================按天统计开始==========================================*/
                            String serviceOpenCountCacheKey = new StringBuilder(openDate ).append( appName ).append( loadNum ).toString();
                            if (serviceOpenCountCache.containsKey( serviceOpenCountCacheKey )) {
                                ServiceOpenCountEntity serviceOpenCountEntity = serviceOpenCountCache.get( serviceOpenCountCacheKey );
                                if (isError) {
                                    serviceOpenCountEntity.setErrorOpenCount( serviceOpenCountEntity.getErrorOpenCount() + 1 );
                                }else{
                                    serviceOpenCountEntity.setOpenCount( serviceOpenCountEntity.getOpenCount() + 1 );
                                }
                            }else{
                                ServiceOpenCountEntity serviceOpenCountEntity = new ServiceOpenCountEntity();
                                serviceOpenCountEntity.setAppId( appId );
                                serviceOpenCountEntity.setAppName(appName);
                                serviceOpenCountEntity.setServiceId( serviceId );
                                serviceOpenCountEntity.setServiceName( serviceName );
                                serviceOpenCountEntity.setOpenDate(DateUtil.toDate( openDate,DateUtil.DATE_SHORT ));
                                serviceOpenCountEntity.setAreaName(areaName);
                                serviceOpenCountEntity.setLoadNum( loadNum );
                                if (isError) {
                                    serviceOpenCountEntity.setErrorOpenCount( 1 );
                                    serviceOpenCountEntity.setOpenCount( 0 );
                                }else{
                                    serviceOpenCountEntity.setOpenCount( 1 );
                                    serviceOpenCountEntity.setErrorOpenCount( 0 );
                                }
                                MapUtil.setCreateBean( serviceOpenCountEntity, CodeUtils.getUUID32(), date );
                                serviceOpenCountCache.put( serviceOpenCountCacheKey,serviceOpenCountEntity );
                            }
                            /*==========================================按天统计结束==========================================*/

                            /*==========================================分时统计开始==========================================*/
                            String serviceHourOpenCountCacheKey = new StringBuilder(openDate ).append( appName ).append( callTimeHour ).toString();
                            if (serviceHourOpenCountCache.containsKey( serviceHourOpenCountCacheKey )) {
                                ServiceHourOpenCountEntity serviceHourOpenCountEntity = serviceHourOpenCountCache.get( serviceHourOpenCountCacheKey );
                                if (isError) {
                                    serviceHourOpenCountEntity.setErrorOpenCount( serviceHourOpenCountEntity.getErrorOpenCount() + 1 );
                                }else{
                                    serviceHourOpenCountEntity.setOpenCount( serviceHourOpenCountEntity.getOpenCount() + 1 );
                                }
                            }else{
                                ServiceHourOpenCountEntity serviceHourOpenCountEntity = new ServiceHourOpenCountEntity();
                                serviceHourOpenCountEntity.setAppId( appId );
                                serviceHourOpenCountEntity.setAppName(appName);
                                serviceHourOpenCountEntity.setServiceId( serviceId );
                                serviceHourOpenCountEntity.setServiceName( serviceName );
                                serviceHourOpenCountEntity.setOpenDate(DateUtil.toDate( callTimeHour + ":00:00",DateUtil.DATE_LONG ));
                                serviceHourOpenCountEntity.setAreaName(areaName);
                                if (isError) {
                                    serviceHourOpenCountEntity.setErrorOpenCount( 1 );
                                    serviceHourOpenCountEntity.setOpenCount( 0 );
                                }else{
                                    serviceHourOpenCountEntity.setOpenCount( 1 );
                                    serviceHourOpenCountEntity.setErrorOpenCount( 0 );
                                }
                                MapUtil.setCreateBean( serviceHourOpenCountEntity, CodeUtils.getUUID32(), date );
                                serviceHourOpenCountCache.put( serviceHourOpenCountCacheKey,serviceHourOpenCountEntity );
                            }
                            /*==========================================分时统计结束==========================================*/
                        }
                        taskRecordEntity.setSchedule( PowerUtil.getInt(  middleDate.size()-1 == i ? 95 : schedule.multiply( PowerUtil.getBigDecimal( i + 1 ) ) ) );
                        upDateTaskRecordThread(taskRecordEntity);
                    }
                    List<ServiceOpenCountEntity> serviceOpenCountEntities = new ArrayList<>();
                    for(Map.Entry<String, ServiceOpenCountEntity> entry : serviceOpenCountCache.entrySet()){
                        ServiceOpenCountEntity mapValue = entry.getValue() ;
                        serviceOpenCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( ServiceOpenCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate ,DateUtil.DATE_SHORT))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate ,DateUtil.DATE_SHORT)) );
                    namedParameterJdbcTemplate.batchUpdate( ServiceOpenCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( serviceOpenCountEntities ) );

                    List<ServiceHourOpenCountEntity> serviceHourOpenCountEntities = new ArrayList<>();
                    for(Map.Entry<String, ServiceHourOpenCountEntity> entry : serviceHourOpenCountCache.entrySet()){
                        ServiceHourOpenCountEntity mapValue = entry.getValue() ;
                        serviceHourOpenCountEntities.add( mapValue );
                    }
                    jdbcTemplate.update( ServiceHourOpenCountEntity.deleteSql + " AND open_date >= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( startDate +" 00:00:00",DateUtil.DATE_LONG))+" AND open_date <= "+JdbcTemplateUtil.getOracelToDate(DateUtil.toDate( endDate +" 23:59:59" ,DateUtil.DATE_LONG)) );
                    namedParameterJdbcTemplate.batchUpdate( ServiceHourOpenCountEntity.insertSql,JdbcTemplateUtil.ListBeanPropSource( serviceHourOpenCountEntities ) );

                    taskRecordEntity.setEndDate( new Date() );
                    taskRecordEntity.setDuration( (PowerUtil.getBigDecimal( System.currentTimeMillis()  - date.getTime() )).divide( new BigDecimal( "1000" ) ) );
                    taskRecordEntity.setSchedule( 100 );
                    taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status2.getCode() );
                    upDateTaskRecordThread(taskRecordEntity);
                    platformTransactionManager.commit(status);
                } catch (Exception e) {
                    taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status3.getCode() );
                    taskRecordEntity.setErrorInfo( e.getMessage() );
                    e.printStackTrace();
                    upDateTaskRecordThread(taskRecordEntity);
                }

            }
        } );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS);
    }


    /**
     * @description  统计应用启动次数
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appOpenCountEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        if(!ParamEnum.active.prd.getCode().equals( active )){
            date = "2021-01-11";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( date )){
            sql.append( " AND OPEN_DATE = "+JdbcTemplateUtil.getOracelToDate( date ));
        }
        sql.append( "  group by APP_NAME ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( openCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  警种统计饼状图Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result policePieEcharts(Map<String, Object> params) {
        String policeName = PowerUtil.getString( params.get( "policeName" ) );
        String policeCode = PowerUtil.getString( params.get( "policeCode" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            policeName = "治安";
            startDate = "2021-04-01";
            endDate = "2021-04-05";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( policeName )) {
                sql.append( " AND police_name = '"+policeName + "'");
            }
        }else {
            if (PowerUtil.isNotNull( policeCode )) {
                sql.append( " AND police_code = '"+policeCode+"'");
            }
        }
        sql.append( " GROUP BY APP_NAME,APP_ID ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            Map<String, Object> series = new HashMap<>();
            series.put("name",appName);
            series.put("value",openCount);
            seriesData.add( series );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  业务统计排行Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result policeRankEcharts(Map<String, Object> params) {
        String policeName = PowerUtil.getString( params.get( "policeName" ) );
        String policeCode = PowerUtil.getString( params.get( "policeCode" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            policeName = "治安";
            startDate = "2021-04-01";
            endDate = "2021-04-05";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( policeName )) {
                sql.append( " AND police_name = '"+policeName + "'");
            }
        }else {
            if (PowerUtil.isNotNull( policeCode )) {
                sql.append( " AND police_code = '"+policeCode+"'");
            }
        }
        sql.append( " GROUP BY APP_NAME,APP_ID ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( openCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  警种应用启动Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result policeLineECharts(Map<String, Object> params) {
        String policeName = PowerUtil.getString( params.get( "policeName" ) );
        String policeCode = PowerUtil.getString( params.get( "policeCode" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            policeName = "治安";
            startDate = "2021-03-29";
            endDate = "2021-04-07";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum1 );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( policeName )) {
                sql.append( " AND police_name = '"+policeName + "'");
            }
        }else {
            if (PowerUtil.isNotNull( policeCode )) {
                sql.append( " AND police_code = '"+policeCode+"'");
            }
        }
        sql.append( " GROUP BY APP_NAME,OPEN_DATE ) ORDER BY openDate desc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        Set<String> xAxisDataSet = new HashSet<>();
        Set<String> legendDataSet = new HashSet<>();
        //键 : 应用名称 + 日期  值 : 启动次数
        Map<String, String> seriesDataCache = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            seriesDataCache.put( appName + openDate, openCount );
            xAxisDataSet.add( openDate );
            legendDataSet.add( appName );
        }
        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        List<String> legendData = new ArrayList<>(legendDataSet);
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int j = 0; j < legendData.size(); j++) {
            String legendTemp = legendData.get(j);
            Map<String, Object> series = new HashMap<>();
            series.put("name",legendTemp);
            series.put("type","line");
            series.put("smooth","true");
            List<String> seriesList = new ArrayList<>();
            for (int i= 0; i < xAxisData.size(); i++) {
                String xAxisTemp = xAxisData.get(i);
                seriesList.add( seriesDataCache.getOrDefault( legendTemp + xAxisTemp,"0" ) );
            }
            series.put( "data" ,seriesList );
            seriesData.add(series);

        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("legendData",legendData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  服务统计饼状图Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result servicePieEcharts(Map<String, Object> params) {
        String serviceId = PowerUtil.getString( params.get( "serviceId" ) );
        String serviceName = PowerUtil.getString( params.get( "serviceName" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            serviceName = "寄递服务";
            startDate = "2021-04-01";
            endDate = "2021-04-05";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum2 );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( serviceName )) {
                sql.append( " AND service_name = '"+serviceName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( serviceId )) {
                sql.append( " AND service_id = '"+serviceId+"'");
            }
        }
        sql.append( " GROUP BY APP_NAME,APP_ID ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            Map<String, Object> series = new HashMap<>();
            series.put("name",appName);
            series.put("value",openCount);
            seriesData.add( series );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  业务统计排行Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result serviceRankEcharts(Map<String, Object> params) {
        String serviceId = PowerUtil.getString( params.get( "serviceId" ) );
        String serviceName = PowerUtil.getString( params.get( "serviceName" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            serviceName = "寄递服务";
            startDate = "2021-04-01";
            endDate = "2021-04-05";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum2 );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( serviceName )) {
                sql.append( " AND service_name = '"+serviceName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( serviceId )) {
                sql.append( " AND service_id = '"+serviceId+"'");
            }
        }
        sql.append( " GROUP BY APP_NAME,APP_ID ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( openCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  业务统计应用启动Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result serviceLineECharts(Map<String, Object> params) {
        String serviceId = PowerUtil.getString( params.get( "serviceId" ) );
        String serviceName = PowerUtil.getString( params.get( "serviceName" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            serviceName = "寄递服务";
            startDate = "2021-03-29";
            endDate = "2021-04-07";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum3 );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( serviceName )) {
                sql.append( " AND SERVICE_NAME = '"+serviceName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( serviceId )) {
                sql.append( " AND service_id = '"+serviceId+"'");
            }
        }
        sql.append( " GROUP BY APP_NAME,OPEN_DATE ) ORDER BY openDate desc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        Set<String> xAxisDataSet = new HashSet<>();
        Set<String> legendDataSet = new HashSet<>();
        //键 : 应用名称 + 日期  值 : 启动次数
        Map<String, String> seriesDataCache = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            seriesDataCache.put( appName + openDate, openCount );
            xAxisDataSet.add( openDate );
            legendDataSet.add( appName );
        }
        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        List<String> legendData = new ArrayList<>(legendDataSet);
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int j = 0; j < legendData.size(); j++) {
            String legendTemp = legendData.get(j);
            Map<String, Object> series = new HashMap<>();
            series.put("name",legendTemp);
            series.put("type","line");
            series.put("smooth","true");
            List<String> seriesList = new ArrayList<>();
            for (int i= 0; i < xAxisData.size(); i++) {
                String xAxisTemp = xAxisData.get(i);
                seriesList.add( seriesDataCache.getOrDefault( legendTemp + xAxisTemp,"0" ) );
            }
            series.put( "data" ,seriesList );
            seriesData.add(series);

        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("legendData",legendData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  业务统计饼状图Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result businessPieEcharts(Map<String, Object> params) {
        String categoryCode = PowerUtil.getString( params.get( "categoryCode" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            //categoryCode = "96";
            startDate = "2021-04-01";
            endDate = "2021-04-05";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (PowerUtil.isNotNull( categoryCode )) {
            sql.append( " AND CATEGORY_CODE = '"+categoryCode + "'");
        }
        sql.append( " GROUP BY APP_NAME,APP_ID ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            Map<String, Object> series = new HashMap<>();
            series.put("name",appName);
            series.put("value",openCount);
            seriesData.add( series );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  业务统计排行Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result businessRankEcharts(Map<String, Object> params) {
        String categoryCode = PowerUtil.getString( params.get( "categoryCode" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            //categoryCode = "96";
            startDate = "2021-04-01";
            endDate = "2021-04-05";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (PowerUtil.isNotNull( categoryCode )) {
            sql.append( " AND CATEGORY_CODE = '"+categoryCode + "'");
        }
        sql.append( " GROUP BY APP_NAME,APP_ID ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( openCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  业务统计应用启动Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result businessLineECharts(Map<String, Object> params) {
        String categoryCode = PowerUtil.getString( params.get( "categoryCode" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            //categoryCode = "96";
            startDate = "2021-03-29";
            endDate = "2021-04-07";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum1 );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (PowerUtil.isNotNull( categoryCode )) {
            sql.append( " AND CATEGORY_CODE = '"+categoryCode + "'");
        }
        sql.append( " GROUP BY APP_NAME,OPEN_DATE ) ORDER BY openDate desc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        Set<String> xAxisDataSet = new HashSet<>();
        Set<String> legendDataSet = new HashSet<>();
        //键 : 应用名称 + 日期  值 : 启动次数
        Map<String, String> seriesDataCache = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            seriesDataCache.put( appName + openDate, openCount );
            xAxisDataSet.add( openDate );
            legendDataSet.add( appName );
        }
        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        List<String> legendData = new ArrayList<>(legendDataSet);
        List<Map<String, Object>> seriesData = new ArrayList<>();
        for (int j = 0; j < legendData.size(); j++) {
            String legendTemp = legendData.get(j);
            Map<String, Object> series = new HashMap<>();
            series.put("name",legendTemp);
            series.put("type","line");
            series.put("smooth","true");
            List<String> seriesList = new ArrayList<>();
            for (int i= 0; i < xAxisData.size(); i++) {
                String xAxisTemp = xAxisData.get(i);
                seriesList.add( seriesDataCache.getOrDefault( legendTemp + xAxisTemp,"0" ) );
            }
            series.put( "data" ,seriesList );
            seriesData.add(series);

        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("legendData",legendData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  使用人数
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result userCountChainRatioEcharts(Map<String, Object> params) {
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );
        String appId = PowerUtil.getString( params.get( "appId" ) );
        StringBuilder sql = new StringBuilder(AppOpenCountEntity.countSql2);
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( appName )) {
                sql.append( " AND APP_NAME = '"+appName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( appId )) {
                sql.append( " AND app_id = '"+appId+"'");
            }
        }
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " GROUP BY open_date " );
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );

        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < xAxisData.size(); i++) {
            String date = xAxisData.get( i );
            Integer dateValue = 0;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String name = PowerUtil.getString( temp.get( "name" ) );
                if(date.equals( name )){
                    dateValue = value;
                }
            }
            seriesData.add(dateValue);
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  使用时长
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result userDurationChainRatioEcharts(Map<String, Object> params) {
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String personCode = PowerUtil.getString( params.get( "personCode" ) );
        StringBuilder sql = new StringBuilder(AppOpenDurationEntity.countSql2);
        sql.append( " AND person_code = '"+personCode+"'");
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " GROUP BY to_char(open_date,'yyyy-MM-dd')  " );
        sql.append( " ORDER BY to_char(open_date,'yyyy-MM-dd') ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );

        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        List<BigDecimal> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < xAxisData.size(); i++) {
            String date = xAxisData.get( i );
            BigDecimal dateValue = BigDecimal.ZERO;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                BigDecimal value = PowerUtil.getBigDecimal( temp.get( "value" ) ).divide(new BigDecimal( 3600 ),1, BigDecimal.ROUND_CEILING);
                String name = PowerUtil.getString( temp.get( "name" ) );
                if(date.equals( name )){
                    dateValue = value;
                }
            }
            seriesData.add(dateValue);
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  应用使用时长列表
     * @param  personCode
     * @param  dateType
     * @return  返回结果
     * @date  2021-8-30 15:38
     * @author  wanghb
     * @edit
     */
    public Result appDurationList(String personCode,String dateType) {
        String startDate = "";
        String endDate = "";
        if (ParamEnum.indexDateType.day.getCode().equals( dateType )) {
            startDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );
            endDate = "2021-01-05";
        }else if (ParamEnum.indexDateType.day7.getCode().equals( dateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
            endDate = "2021-01-11";
        }else if (ParamEnum.indexDateType.day30.getCode().equals( dateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
            endDate = "2021-02-05";
        }
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            personCode = "1";
            startDate = "2021-01-05";
        }else {
            endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );
        }

        StringBuilder sb = new StringBuilder(AppOpenDurationEntity.countSql4);
        sb.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sb.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sb.append( " AND person_code = "+personCode);
        sb.append( " GROUP BY app_name  " );

        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("("+sb+")");
        sql.ORDER_BY(" \"value\" desc ");
        List<Map<String, Object>> list = baseDao.getListByNactiveSql(sql);
        BigDecimal count = BigDecimal.ZERO;
        for (Map<String, Object> temp : list) {
            BigDecimal value = PowerUtil.getBigDecimal( temp.get( "value" ) ).divide(new BigDecimal( 3600 ),1, BigDecimal.ROUND_CEILING);
            count = count.add( value );
            temp.put( "value" ,value);
        }
        Map<String, Object> temp = new HashMap<>();
        temp.put("list",list);
        temp.put("count",count);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,temp);
    }


    /**
     * @description  活跃用户排行
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appActiveRankEcharts(Map<String, Object> params) {
        String appName = PowerUtil.getString( params.get( "appName" ) );
        String appId = PowerUtil.getString( params.get( "appId" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            appName = "通讯录";
            startDate = "";
            endDate = "";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum6 );
        if (PowerUtil.isNotNull( startDate )){
            sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        }
        if (PowerUtil.isNotNull( endDate )){
            sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        }
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( appName )) {
                sql.append( " AND APP_NAME = '"+appName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( appId )) {
                sql.append( " AND app_id = '"+appId+"'");
            }
        }
        sql.append( " GROUP BY person_name,person_code ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openCount = PowerUtil.getString( temp.get( "openCount" ) );
            appName = PowerUtil.getString( temp.get( "personName" ) );
            seriesData.add( openCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  质态-全省近一周应用使用人数/时长
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appUserAndDuration(Map<String, Object> params) {
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            startDate = "2021-01-05";
            endDate = "2021-01-12";
        }
        StringBuilder sql = new StringBuilder(AppOpenCountEntity.countSql2.replaceAll( DateUtil.DATE_SHORT,DateUtil.DATE_SHORT_MM ));
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " GROUP BY open_date " );
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );

        sql = new StringBuilder(AppOpenDurationEntity.countSql2);
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " GROUP BY to_char(open_date,'yyyy-MM-dd')  " );
        sql.append( " ORDER BY to_char(open_date,'yyyy-MM-dd') ASC" );
        List<Map<String, Object>> durationList = jdbcTemplate.queryForList( sql.toString() );

        List<String> xAxisData = DateUtil.getMiddleDate2( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        List<Integer> openCountSeriesData = new ArrayList<>();
        List<BigDecimal> durationSeriesData = new ArrayList<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            String date = xAxisData.get( i );
            Integer dateValue = 0;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String name = PowerUtil.getString( temp.get( "name" ) );
                if(date.equals( name )){
                    dateValue = value;
                }
            }
            openCountSeriesData.add(dateValue);

            BigDecimal durationValue = BigDecimal.ZERO;
            for (int j = 0; j < durationList.size(); j++) {
                Map<String, Object> temp = durationList.get(j);
                BigDecimal value = PowerUtil.getBigDecimal( temp.get( "value" ) );
                String name = PowerUtil.getString( temp.get( "name" ) ).substring(5);
                if(date.equals( name )){
                    durationValue = value.divide(new BigDecimal( 3600 ),1, BigDecimal.ROUND_CEILING);
                }
            }
            durationSeriesData.add(durationValue);
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("count",openCountSeriesData);
        echartsData.put("hour",durationSeriesData);
        echartsData.put("date",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  新增用户比
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appAddChainRatioEcharts(Map<String, Object> params) {
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appId = PowerUtil.getString( params.get( "appId" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            startDate = "2021-01-06";
            endDate = "2021-01-13";
            appName = "通讯录";
        }
        StringBuilder sql = new StringBuilder(AppOpenCountEntity.countSql3);
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( appName )) {
                sql.append( " AND APP_NAME = '"+appName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( appId )) {
                sql.append( " AND app_id = '"+appId+"'");
            }
        }
        sql.append( " AND OPEN_DATE < "+JdbcTemplateUtil.getOracelToDate( startDate ));
        List<Map<String, Object>> historyPersonCodeList = jdbcTemplate.queryForList( sql.toString() );
        Set<String> historyPersonCodeSet = new HashSet<>();
        for (int i = 0; i < historyPersonCodeList.size(); i++) {
            Map<String, Object> temp = historyPersonCodeList.get( i );
            String personCode = PowerUtil.getString( temp.get( "personCode" ) );
            historyPersonCodeSet.add( personCode );
        }

        sql = new StringBuilder(AppOpenCountEntity.countSql3);
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( appName )) {
                sql.append( " AND APP_NAME = '"+appName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( appId )) {
                sql.append( " AND app_id = '"+appId+"'");
            }
        }
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));

        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );

        //键 : 日期  值 : personCode Set
        Map<String, Set<String>> dateCache = new TreeMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get( i );
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            String personCode = PowerUtil.getString( temp.get( "personCode" ) );
            if (!dateCache.containsKey( openDate )) {
                dateCache.put( openDate , new HashSet<>(Arrays.asList( personCode )));
            }else{
                Set<String> personCodesTemp = dateCache.get( openDate );
                personCodesTemp.add( personCode );
            }
        }
        List<String> xAxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = 0 ;
        //键: 日期  值 : 数量
        Map<String, Integer> tempCache = new HashMap<>();
        for(Map.Entry<String, Set<String>> entry : dateCache.entrySet()){
            String mapKey = entry.getKey();
            Set<String> mapValue =  entry.getValue() ;
            Integer value = 0;
            for (String personCode : mapValue) {
                if (!historyPersonCodeSet.contains( personCode )) {
                    value ++;
                    historyPersonCodeSet.add(personCode);
                }
            }
            tempCache.put( mapKey, value);
        }
        //填充数据为空的数据为0
        List<String> xAxisDataTemp = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        for (int i = 0; i < xAxisDataTemp.size(); i++) {
            String date = xAxisDataTemp.get( i );
            xAxisData.add( date );
            seriesData.add( tempCache.getOrDefault( date,0 ) );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  终端机型分析
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appDeviceEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );

        startDate = "2021-04-01";
        endDate = "2021-04-05";

        StringBuilder sql = new StringBuilder(AppDeviceCountEntity.countSql1);
        sql.append( " AND APP_NAME = '"+appName+"'");
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        Set<String> yAxisDataSet = new HashSet<>();
        //键 : 日期 + 终端型号  值 : 数量
        Map<String, Integer> deviceCountCache = new HashMap<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            Integer deviceCount = PowerUtil.getInt( temp.get( "deviceCount" ) );
            String deviceModel = PowerUtil.getString( temp.get( "deviceModel" ) );
            String deviceCountkey = openDate + deviceModel;
            yAxisDataSet.add( deviceModel );
            deviceCountCache.put( deviceCountkey,deviceCount );
        }

        List<String> yAxisData = new ArrayList<>(yAxisDataSet);
        for (int i = 0; i < yAxisData.size(); i++) {
            String deviceModel = yAxisData.get( i );
            Map<String, Object> seriesTemp = new HashMap<>();
            seriesTemp.put("name",deviceModel);
            seriesTemp.put("type","bar");
            seriesTemp.put("stack","total");
            seriesTemp.put("label", ImmutableMap.of("show","true"));
            seriesTemp.put("emphasis", ImmutableMap.of("focus","series"));
            List<Integer> data = new ArrayList<>();
            for (int j = 0; j < xAxisData.size(); j++) {
                String openDate = xAxisData.get(j);
                String deviceCountkey = openDate + deviceModel;
                Integer deviceCount = PowerUtil.getInt( deviceCountCache.get( deviceCountkey ) );
                data.add( deviceCount );
            }
            seriesTemp.put("data" ,data);
            seriesData.add( seriesTemp );

        }
        //yAxisMin = value < yAxisMin ? value : yAxisMin;
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  应用版本分析
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appVersionEcharts(Map<String, Object> params) {
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );
        String appId = PowerUtil.getString( params.get( "appId" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            startDate = "2021-03-29";
            endDate = "2021-04-07";
            appName = "应用1";
        }
        StringBuilder sql = new StringBuilder(AppVersionCountEntity.countSql1);
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( appName )) {
                sql.append( " AND APP_NAME = '"+appName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( appId )) {
                sql.append( " AND app_id = '"+appId+"'");
            }
        }
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        Set<String> yAxisDataSet = new HashSet<>();
        //键 : 日期 + 终端型号  值 : 数量
        Map<String, Integer> versionCountCache = new HashMap<>();
        List<Map<String, Object>> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String openDate = PowerUtil.getString( temp.get( "openDate" ) );
            Integer versionCount = PowerUtil.getInt( temp.get( "versionCount" ) );
            String versionNum = PowerUtil.getString( temp.get( "versionNum" ) );
            String versionCountkey = openDate + versionNum;
            yAxisDataSet.add( versionNum );
            versionCountCache.put( versionCountkey,versionCount );
        }

        List<String> yAxisData = new ArrayList<>(yAxisDataSet);
        for (int i = 0; i < yAxisData.size(); i++) {
            String versionNum = yAxisData.get( i );
            Map<String, Object> seriesTemp = new HashMap<>();
            seriesTemp.put("name",versionNum);
            seriesTemp.put("type","bar");
            if(i == yAxisData.size() - 1){
                seriesTemp.put("itemStyle",ImmutableMap.of("normal",ImmutableMap.of("barBorderRadius",new Integer[]{9, 9, 0, 0})));
            }
            seriesTemp.put("stack","total");
            seriesTemp.put("barWidth","45%");
            seriesTemp.put("label", ImmutableMap.of("show","true"));
            seriesTemp.put("emphasis", ImmutableMap.of("focus","series"));
            List<Integer> data = new ArrayList<>();
            for (int j = 0; j < xAxisData.size(); j++) {
                String openDate = xAxisData.get(j);
                String versionCountkey = openDate + versionNum;
                Integer versionCount = PowerUtil.getInt( versionCountCache.get( versionCountkey ) );
                data.add( versionCount );
            }
            seriesTemp.put("data" ,data);
            seriesData.add( seriesTemp );

        }
        //yAxisMin = value < yAxisMin ? value : yAxisMin;
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }



    /**
     * @description  一天内的启动趋势折线图(人次)
     * @param  appId
     * @param  appName
     * @param  openDate
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> getAppUserFrequencyMap(String appId, String appName, String openDate) {
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            openDate = "2021-01-11";
        }
        StringBuilder sql = new StringBuilder(AppHourOpenCountEntity.countSql2);
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( appName )) {
                sql.append( " AND APP_NAME = '"+appName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( appId )) {
                sql.append( " AND app_id = '"+appId+"'");
            }
        }
        sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date",DateUtil.DATE_SHORT ) + " = '"+openDate+"'");
        sql.append( " GROUP BY (to_char(open_date,'"+ DateUtil.DATE_HM24 +"') )");
        sql.append( " ORDER BY (to_char(open_date,'"+ DateUtil.DATE_HM24 +"') )");
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );

        List<String> xAxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        Integer yAxisMan = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;

        Integer hourConnt = !openDate.equals( DateUtil.toString( new Date(),DateUtil.DATE_SHORT ) ) ? 23 : Integer.parseInt(  DateUtil.getNowHour() );
        for (int i = 0; i <= hourConnt; i++) {
            String nameTemp =  (i <= 9 ? "0" :"")+i+":00";
            Integer valueTemp = 0;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String name = PowerUtil.getString( temp.get( "name" ) ).trim();
                yAxisMin = value < yAxisMin ? value : yAxisMin;
                yAxisMan = value > yAxisMan ? value : yAxisMan;
                if(nameTemp.equals( name )){
                    valueTemp = value;
                    break;
                }
            }
            xAxisData.add( nameTemp );
            seriesData.add( valueTemp );
        }

        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("yAxisMax",yAxisMan + 2);
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return echartsData;
    }



    /**
     * @description 日使用时长
     * @param  personCode
     * @param  dateType
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> getAppDayDurationMap(String personCode, String dateType) {
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            dateType = "2021-01-05";
        }
        StringBuilder sql = new StringBuilder(AppOpenDurationEntity.countSql3);
        if (PowerUtil.isNotNull( personCode )) {
            sql.append( " AND person_code = '"+personCode+"'");
        }
        sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date",DateUtil.DATE_SHORT ) + " = '"+dateType+"'");
        sql.append( " GROUP BY (to_char(open_date,'"+ DateUtil.DATE_HM24 +"') )");
        sql.append( " ORDER BY (to_char(open_date,'"+ DateUtil.DATE_HM24 +"') )");
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );

        List<String> xAxisData = new ArrayList<>();
        List<BigDecimal> seriesData = new ArrayList<>();
        BigDecimal yAxisMin = list.size() > 0 ? PowerUtil.getBigDecimal(list.get(0).get("value")) : BigDecimal.ZERO;
        BigDecimal yAxisMan = list.size() > 0 ? PowerUtil.getBigDecimal(list.get(0).get("value")) : BigDecimal.ZERO ;

        Integer hourConnt = !dateType.equals( DateUtil.toString( new Date(),DateUtil.DATE_SHORT ) ) ? 23 : Integer.parseInt(  DateUtil.getNowHour() );
        for (int i = 0; i <= hourConnt; i++) {
            String nameTemp =  (i <= 9 ? "0" :"")+i+":00";
            BigDecimal valueTemp = BigDecimal.ZERO;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                BigDecimal value = PowerUtil.getBigDecimal( temp.get( "value" ) ).divide(new BigDecimal( 3600 ),1, BigDecimal.ROUND_CEILING);
                String name = PowerUtil.getString( temp.get( "name" ) ).trim();
                yAxisMin = (value.compareTo( yAxisMin ) == -1) ? value : yAxisMin;
                yAxisMan = (value.compareTo(yAxisMan ) == 1) ? value : yAxisMan;
                if(nameTemp.equals( name )){
                    valueTemp = value;
                    break;
                }
            }
            xAxisData.add( nameTemp );
            seriesData.add( valueTemp );
        }

        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin.subtract( yAxisMin.divide(new BigDecimal( 5 ))) );
        echartsData.put("yAxisMax",yAxisMan.add( new BigDecimal( 2 ) ));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return echartsData;
    }


    /**
     * @description  一天内活跃的启动趋势折线图(人数)
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> appActiveFrequencyEcharts(String appId, String appName, String openDate) {
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            openDate = "2021-01-11";
        }
        StringBuilder sql = new StringBuilder(AppHourOpenCountEntity.countSql1);
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( appName )) {
                sql.append( " AND APP_NAME = '"+appName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( appId )) {
                sql.append( " AND app_id = '"+appId+"'");
            }
        }
        sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date",DateUtil.DATE_SHORT ) + " = '"+openDate+"'");
        sql.append( " GROUP BY (to_char(open_date,'"+ DateUtil.DATE_HM24 +"') )");
        sql.append( " ORDER BY (to_char(open_date,'"+ DateUtil.DATE_HM24 +"') )");
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        Integer yAxisMan = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        Integer hourConnt = !openDate.equals( DateUtil.toString( new Date(),DateUtil.DATE_SHORT ) ) ? 23 : Integer.parseInt(  DateUtil.getNowHour() );
        for (int i = 0; i <= hourConnt; i++) {
            String nameTemp =  (i <= 9 ? "0" :"")+i+":00";
            Integer valueTemp = 0;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String name = PowerUtil.getString( temp.get( "name" ) ).trim();
                yAxisMin = value < yAxisMin ? value : yAxisMin;
                yAxisMan = value > yAxisMan ? value : yAxisMan;
                if(nameTemp.equals( name )){
                    valueTemp = value;
                    break;
                }
            }
            xAxisData.add( nameTemp );
            seriesData.add( valueTemp );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("yAxisMax",yAxisMan + 2);
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return echartsData;
    }


    /**
     * @description  一天内的服务启动趋势折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result serviceUserFrequencyEcharts(Map<String, Object> params) {
        String openDate = PowerUtil.getString( params.get( "openDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );
        String serviceId = PowerUtil.getString( params.get( "serviceId" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            openDate = "2021-01-11";
            appName = "应用6";
        }
        StringBuilder sql = new StringBuilder(ServiceHourOpenCountEntity.countSql1);
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( appName )) {
                sql.append( " AND APP_NAME = '"+appName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( serviceId )) {
                sql.append( " AND service_id = '"+serviceId+"'");
            }
        }
        sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date",DateUtil.DATE_SHORT ) + " = '"+openDate+"'");
        sql.append( " order by open_date asc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = Arrays.asList("异常调用", "正常调用" );
        List<Map<String, Object>> seriesData = new ArrayList<>();
        Map<String, Object> seriesErrTemp = new HashMap<>();
        seriesErrTemp.put("name","异常调用");
        seriesErrTemp.put("type","bar");
        seriesErrTemp.put("stack","total");
        seriesErrTemp.put("barWidth","30%");
        seriesErrTemp.put("label", ImmutableMap.of("show","true"));
        seriesErrTemp.put("emphasis", ImmutableMap.of("focus","series"));
        List<Integer> errorData = new ArrayList<>();
        seriesErrTemp.put("data" ,errorData);
        seriesData.add( seriesErrTemp );

        Map<String, Object> seriesTemp = new HashMap<>();
        seriesTemp.put("name","正常调用");
        seriesTemp.put("type","bar");
        seriesTemp.put("itemStyle",ImmutableMap.of("normal",ImmutableMap.of("barBorderRadius",new Integer[]{9, 9, 0, 0})));
        seriesTemp.put("stack","total");
        seriesTemp.put("barWidth","30%");
        seriesTemp.put("label", ImmutableMap.of("show","true"));
        seriesTemp.put("emphasis", ImmutableMap.of("focus","series"));
        List<Integer> data = new ArrayList<>();
        seriesTemp.put("data" ,data);
        seriesData.add( seriesTemp );
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        Integer hourConnt = !openDate.equals( DateUtil.toString( new Date(),DateUtil.DATE_SHORT ) ) ? 23 : Integer.parseInt(  DateUtil.getNowHour() );
        for (int i = 0; i <= hourConnt; i++) {
            String nameTemp =  (i <= 9 ? "0" :"")+i+":00";
            Integer valueTemp = 0;
            Integer errorValueTemp = 0;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                Integer errorValue = PowerUtil.getIntValue( temp.get( "errorValue" ) );
                String name = PowerUtil.getString( temp.get( "name" ) ).trim();
                if(nameTemp.equals( name )){
                    valueTemp = value;
                    errorValueTemp = errorValue;
                    break;
                }
            }
            xAxisData.add( nameTemp );
            data.add( valueTemp );
            errorData.add( errorValueTemp );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  服务负载情况统计
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result serviceLoadHappenEcharts(Map<String, Object> params) {
        String openDate = PowerUtil.getString( params.get( "openDate" ) );

        StringBuilder sql = new StringBuilder("select * from (").append( ServiceOpenCountEntity.countSql3 );
        if (PowerUtil.isNotNull( openDate )) {
            sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date",DateUtil.DATE_SHORT ) + " = '"+openDate+"'");
        }
        sql.append( " group by load_num " ).append( ")" );
        sql.append( " order by \"value\" desc " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> yAxisData = Arrays.asList("异常调用", "正常调用" );
        List<Map<String, Object>> seriesData = new ArrayList<>();
        Map<String, Object> seriesErrTemp = new HashMap<>();
        seriesErrTemp.put("name","异常调用");
        seriesErrTemp.put("type","bar");
        seriesErrTemp.put("stack","total");
        seriesErrTemp.put("label", ImmutableMap.of("show","true"));
        seriesErrTemp.put("emphasis", ImmutableMap.of("focus","series"));
        List<Integer> errorData = new ArrayList<>();
        seriesErrTemp.put("data" ,errorData);
        seriesData.add( seriesErrTemp );

        Map<String, Object> seriesTemp = new HashMap<>();
        seriesTemp.put("name","正常调用");
        seriesTemp.put("type","bar");
        seriesTemp.put("stack","total");
        seriesTemp.put("label", ImmutableMap.of("show","true"));
        seriesTemp.put("emphasis", ImmutableMap.of("focus","series"));
        List<Integer> data = new ArrayList<>();
        seriesTemp.put("data" ,data);
        seriesData.add( seriesTemp );

        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
            Integer errorValue = PowerUtil.getIntValue( temp.get( "errorValue" ) );
            String loadNum = PowerUtil.getString( temp.get( "loadNum" ) );
            data.add( value );
            errorData.add( errorValue );
            xAxisData.add( loadNum );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        echartsData.put("yAxisData",yAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  使用人次折线图
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appOpenFrequencyEcharts(Map<String, Object> params) {
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );
        String appName = PowerUtil.getString( params.get( "appName" ) );
        String appId = PowerUtil.getString( params.get( "appId" ) );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            startDate = "2021-01-05";
            endDate = "2021-01-11";
            appName = "通讯录";
        }
        Map<String, Object> echartsData = getAppOpenFrequencyMap(appId,appName,startDate,endDate);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  多日天内的启动趋势折线图
     * @param  appId
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> getAppOpenFrequencyMap(String appId, String appName, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder(AppOpenCountEntity.countSql1);
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            if (PowerUtil.isNotNull( appName )) {
                sql.append( " AND APP_NAME = '"+appName+"'");
            }
        }else {
            if (PowerUtil.isNotNull( appId )) {
                sql.append( " AND app_id = '"+appId+"'");
            }
        }
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " GROUP BY open_date " );
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < xAxisData.size(); i++) {
            String date = xAxisData.get( i );
            Integer dateValue = 0;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String name = PowerUtil.getString( temp.get( "name" ) );
                yAxisMin = value < yAxisMin ? value : yAxisMin;
                if(date.equals( name )){
                    dateValue = value;
                }
            }
            seriesData.add(dateValue);
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return echartsData;
    }


    /**
     * @description  近半年个人的应用使用次数
     * @param  personCode
     * @return  返回结果
     * @date  2021-7-27 15:26
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> getYearNumbForMap(String personCode) {
        String startDate = DateUtil.toString( DateUtil.countDate( new Date(), -6, Calendar.MONTH ), DateUtil.DATE_MONTH )+"-01";
        String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            personCode = "000000000000005";
            startDate = "2021-01-00";
            endDate = "2021-06-29";
        }
        StringBuilder sql = new StringBuilder(AppHourOpenCountEntity.countSql3);
        sql.append( " AND person_code = '"+personCode+"'");
        sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date",DateUtil.DATE_SHORT ) + " >= '"+startDate+"'");
        sql.append( " AND "+JdbcTemplateUtil.getOracelToChar( "open_date",DateUtil.DATE_SHORT ) + " <= '"+endDate+"'");
        sql.append( " GROUP BY (to_char(open_date,'"+ DateUtil.DATE_MM +"') )");
        sql.append( " ORDER BY (to_char(open_date,'"+ DateUtil.DATE_MM +"') )");
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );

        List<String> xAxisData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        Integer yAxisMan = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;

        for (int i = Integer.parseInt( startDate.substring( 5,7 ) ); i <= Integer.parseInt( endDate.substring( 5,7 ) ); i++) {
            //
            String nameTemp =  (i <= 9 ? "0" :"")+i;
            Integer valueTemp = 0;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String name = PowerUtil.getString( temp.get( "name" ) ).trim();
                yAxisMin = value < yAxisMin ? value : yAxisMin;
                yAxisMan = value > yAxisMan ? value : yAxisMan;
                if(nameTemp.equals( name )){
                    valueTemp = value;
                    break;
                }
            }
            xAxisData.add( DateUtil.monthArr[i-1] );
            seriesData.add( valueTemp );
        }

        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("yAxisMax",yAxisMan + 2);
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return echartsData;
    }


    /**
     * @description  多日天内的启动趋势折线图
     * @param  appId
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> getAppUseFrequencyMap(String appId, String appName, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder(AppOpenCountEntity.countSql2);
        if (PowerUtil.isNotNull( appId )) {
            sql.append( " AND app_id = '"+appId+"'");
        }
        if (PowerUtil.isNotNull( appName )) {
            sql.append( " AND APP_NAME = '"+appName+"'");
        }
        sql.append( " AND OPEN_DATE >= "+JdbcTemplateUtil.getOracelToDate( startDate ));
        sql.append( " AND OPEN_DATE <= "+JdbcTemplateUtil.getOracelToDate( endDate ));
        sql.append( " GROUP BY open_date " );
        sql.append( " ORDER BY open_date ASC " );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = DateUtil.getMiddleDate( DateUtil.toDate( startDate,DateUtil.DATE_SHORT ), DateUtil.toDate( endDate,DateUtil.DATE_SHORT ));
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < xAxisData.size(); i++) {
            String date = xAxisData.get( i );
            Integer dateValue = 0;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String name = PowerUtil.getString( temp.get( "name" ) );
                yAxisMin = value < yAxisMin ? value : yAxisMin;
                if(date.equals( name )){
                    dateValue = value;
                }
            }
            seriesData.add(dateValue);
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return echartsData;
    }

    /**
     * @description  活跃用户排名
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result activeUserEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        date = "2021-01-11";

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.countSql );
        if (PowerUtil.isNotNull( date )){
            sql.append( " AND OPEN_DATE = "+JdbcTemplateUtil.getOracelToDate( date ));
        }
        sql.append( "  group by aPP_NAME ) order by personCodeCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String personCodeCount = PowerUtil.getString( temp.get( "personCodeCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( personCodeCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  应用用户总量排名Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result userCountEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        date = "2021-01-11";

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.countSql );
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( date ) );
        sql.append( " group by APP_NAME ) order by personCodeCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<String> seriesData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String personCodeCount = PowerUtil.getString( temp.get( "personCodeCount" ) );
            String appName = PowerUtil.getString( temp.get( "appName" ) );
            seriesData.add( personCodeCount );
            xAxisData.add( appName );
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  首页-应用使用人数排行
     * @param  params
     * @return  返回结果
     * @date  2021-6-24 14:15
     * @author  wanghb
     * @edit
     */
    public Result userCountRank(Map<String, Object> params) {
        String indexDateType = PowerUtil.getString( params.get( "indexDateType" ) );
        String startDate = "";
        if (ParamEnum.indexDateType.day7.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        if (ParamEnum.indexDateType.day30.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            startDate = "2021-01-11";
            endDate = "2021-01-11";
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.countSql4 );
        if (!ParamEnum.indexDateType.sum.getCode().equals( indexDateType )) {
            sql= new StringBuilder(sql.toString().replace( ":dateCondition"," and open_date >= " + JdbcTemplateUtil.getOracelToDate( startDate ) + " and open_date <= " + JdbcTemplateUtil.getOracelToDate( endDate ) ));
        }else{
            sql= new StringBuilder(sql.toString().replace( ":dateCondition",""));
        }
        sql.append( " group by APP_NAME ) order by personCodeCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list);
    }

    /**
     * @description  首页-应用使用人次排行
     * @param  params
     * @return  返回结果
     * @date  2021-6-24 14:15
     * @author  wanghb
     * @edit
     */
    public Result userFrequencyRank(Map<String, Object> params) {
        String indexDateType = PowerUtil.getString( params.get( "indexDateType" ) );
        String startDate = "";
        if (ParamEnum.indexDateType.day7.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        if (ParamEnum.indexDateType.day30.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( TaskRecordEntity.selectSum );
        if (ParamEnum.active.prd.getCode().equals( active ) && !ParamEnum.indexDateType.sum.getCode().equals( indexDateType )) {
            sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sql.append( " group by APP_NAME,APP_ID ) order by openCount desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get( i );
            String APPID = PowerUtil.getString( temp.get( "APPID" ) );
            temp.put( "APPLOGO", ScheduledTasks.appInfoIdToAppLogo.get( APPID ));
            temp.put( "APPAREA", ScheduledTasks.dict.get( ParamEnum.dicType.AREA.getCode() ).get( ScheduledTasks.appInfoIdToArea.get( APPID ) ));
            temp.put("startColor","#FFFFFF");
            if(i == 0){
                temp.put("endColor","#F2CFA2");
            }else if(i == 1){
                temp.put("endColor","#BFCAF3");
            }else if(i == 2){
                temp.put("endColor","#D9C5B7");
            }else{
                temp.put("endColor","#FFFFFF");
            }
        }
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,list);
    }

    /**
     * @description  首页-应用使用人次排行
     * @param  params
     * @return  返回结果
     * @date  2021-6-24 14:15
     * @author  wanghb
     * @edit
     */
    public Result userFrequencyRankByPage(Map<String, Object> params) {
        String startDate = PowerUtil.getString(params.get("startDate"));
        String endDate = PowerUtil.getString(params.get("endDate"));
        Integer pageNo = PowerUtil.getInt(params.get("pageNo"));
        Integer pageSize = PowerUtil.getInt(params.get("pageSize"));

        StringBuilder sqlTemp = new StringBuilder();
        sqlTemp.append(TaskRecordEntity.selectSum );
        if (ParamEnum.active.prd.getCode().equals( active )) {
            sqlTemp.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sqlTemp.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sqlTemp.append( " group by APP_NAME,APP_ID" );
        Pagination page = new Pagination(pageNo,pageSize);
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("("+sqlTemp+")");
        sql.ORDER_BY(" openCount desc");
        baseDao.getPaginationByNactiveSql(sql,page);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,page);
    }

    /**
     * @description  应用地区分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result appAreaEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );
        date = "2021-01-11";
        StringBuilder sql = new StringBuilder( TaskRecordEntity.countSql1);
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( date ) );
        sql.append( " group by area_name " );
        List<Map<String, Object>> seriesData = jdbcTemplate.queryForList( sql.toString() );
        Integer visualMapMin = seriesData.size() > 0 ? PowerUtil.getIntValue(seriesData.get(0).get("value")) : 0 ;
        Integer visualMapMax = 0 ;
        for (int i = 0; i < seriesData.size(); i++) {
            Integer value = PowerUtil.getIntValue(seriesData.get( i ).get( "value" ));
            visualMapMin = value < visualMapMin ? value : visualMapMin;
            visualMapMax = value > visualMapMax ? value : visualMapMax;
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("visualMapMin",visualMapMin.equals( visualMapMax ) ? 0 : visualMapMin);
        echartsData.put("visualMapMax",visualMapMax);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  今日服务调用次数地区分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-4-16 9:43
     * @author  wanghb
     * @edit
     */
    public Result serviceOpenEcharts(Map<String, Object> params) {
        String date = PowerUtil.getString( params.get( "date" ) );

        date = "2021-01-11";
        StringBuilder sql = new StringBuilder( ServiceOpenCountEntity.countSql1);
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( date ) );
        sql.append( " group by area_name " );
        List<Map<String, Object>> seriesData = jdbcTemplate.queryForList( sql.toString() );
        Integer visualMapMin = seriesData.size() > 0 ? PowerUtil.getIntValue(seriesData.get(0).get("value")) : 0 ;
        Integer visualMapMax = 0 ;
        for (int i = 0; i < seriesData.size(); i++) {
            Integer value = PowerUtil.getIntValue(seriesData.get( i ).get( "value" ));
            visualMapMin = value < visualMapMin ? value : visualMapMin;
            visualMapMax = value > visualMapMax ? value : visualMapMax;
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("visualMapMin",visualMapMin.equals( visualMapMax ) ? 0 : visualMapMin);
        echartsData.put("visualMapMax",visualMapMax);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  首页-卡片数据
     * @param  params
     * @return  返回结果
     * @date  2021-4-22 9:21
     * @author  wanghb
     * @edit
     */
    public Result countCardEcharts(Map<String, Object> params) {
        String indexDateType = PowerUtil.getString( params.get( "indexDateType" ) );
        String startDate = "";
        if (ParamEnum.indexDateType.day7.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        if (ParamEnum.indexDateType.day30.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
        }String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            startDate = "2021-01-11";
            endDate = "2021-01-11";
        }
        StringBuilder sql = new StringBuilder( TaskRecordEntity.countSql2);
        if (!ParamEnum.indexDateType.sum.getCode().equals( indexDateType )) {
            sql.append( " and open_date >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " and open_date <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        Integer appUserCount = jdbcTemplate.queryForObject( sql.toString(),Integer.class );
        sql = new StringBuilder( ServiceOpenCountEntity.countSql2);
        if (!ParamEnum.indexDateType.sum.getCode().equals( indexDateType )) {
            sql.append( " and open_date >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " and open_date <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        Integer serverSumCount = jdbcTemplate.queryForObject( sql.toString(),Integer.class );
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("appCount",appInfoService.getCount( null,null ));
        echartsData.put("appUserCount",appUserCount);
        echartsData.put("serverCount",serviceInfoService.getCount( null ));
        echartsData.put("serverSumCount",serverSumCount);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }

    /**
     * @description  质态情况-首页卡片
     * @return  返回结果
     * @date  2021-4-22 9:21
     * @author  wanghb
     * @edit
     */
    public Result countCardForApp() {
        Map<String, Object> echartsData = new HashMap<>();
        String openDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );
        if (!ParamEnum.active.prd.getCode().equals( active )) {
            openDate = "2021-01-11";
        }
        StringBuilder sql = new StringBuilder( TaskRecordEntity.countSql2);
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( openDate ) );
        Integer appUserCount = jdbcTemplate.queryForObject( sql.toString(),Integer.class );
        sql = new StringBuilder( TaskRecordEntity.selectSum4);
        sql.append( " and open_date = " + JdbcTemplateUtil.getOracelToDate( openDate ) );
        Integer appCount = jdbcTemplate.queryForObject( sql.toString(),Integer.class );
        //今日使用总人数
        echartsData.put("appUserCount",appUserCount);
        //今日使用总人次
        echartsData.put("appCount",appCount);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);


    }
    /**
     * @description  独立事务提交任务日志
     * @param  taskRecord
     * @param  params
     * @param  startDate
     * @return  返回结果
     * @date  2021-4-16 14:54
     * @author  wanghb
     * @edit
     */
    public TaskRecordEntity saveTaskRecordThread(ParamEnum.taskRecord taskRecord, Map<String, Object> params,Date startDate) {
        //开启单独事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = platformTransactionManager.getTransaction(def);
        TaskRecordEntity taskRecordEntity = new TaskRecordEntity();
        taskRecordEntity.setName( taskRecord.getName() );
        taskRecordEntity.setCode( taskRecord.getCode() );
        taskRecordEntity.setConfigJson( JSON.toJSONString( params ) );
        taskRecordEntity.setSchedule( 0 );
        taskRecordEntity.setStatus( ParamEnum.taskRecordStatus.status1.getCode() );
        taskRecordEntity.setStartDate( startDate );
        taskRecordEntity.setEndDate( null );
        String id = CodeUtils.getUUID32();
        MapUtil.setCreateBean( taskRecordEntity, id, startDate );
        this.baseDao.update( taskRecordEntity );
        this.taskRecordDao.deleteDetail( id );
        platformTransactionManager.commit(status);
        return taskRecordEntity;
    }

    /**
     * @description  独立事务更新日志
     * @param  taskRecordEntity
     * @return  返回结果
     * @date  2021-4-16 15:03
     * @author  wanghb
     * @edit
     */
    public void upDateTaskRecordThread(TaskRecordEntity taskRecordEntity) {
        DefaultTransactionDefinition def2 = new DefaultTransactionDefinition();
        def2.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status2 = platformTransactionManager.getTransaction(def2);
        saveOrUpdate( taskRecordEntity );
        platformTransactionManager.commit(status2);
    }


    /**
     * @description  首页-各地区应用情况分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-6-22 18:21
     * @author  wanghb
     * @edit
     */
    public Result appAreaCountEcharts(Map<String, Object> params) {
        String indexDateType = PowerUtil.getString( params.get( "indexDateType" ) );
        String startDate = "";
        if (ParamEnum.indexDateType.day7.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        if (ParamEnum.indexDateType.day30.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );

        List<Map<String, Object>> appNameList = new ArrayList<>();
        List<DictInfoEntity> areaList = ScheduledTasks.dictList.get( ParamEnum.dicType.APP_AREA.getCode() );
        for (int i = 0; i < areaList.size(); i++) {
            DictInfoEntity dictInfoEntity = areaList.get( i );
            String label = dictInfoEntity.getLabel();
            String value = dictInfoEntity.getValue();
            label += !ParamEnum.active.prd.getCode().equals( active ) ? "市" : "";
            appNameList.add(ImmutableMap.of("areaName",label,"appCount",appInfoService.getCount( value,null )));
            label.replace( "省厅市","省厅" );
        }
        List<Object > productList = Arrays.asList( "product","上线应用数","使用人数" ,"使用人次");

        Map<String, List<Object>> tempCache = new LinkedHashMap<>();
        for (int i = 0; i < appNameList.size(); i++) {
            Map<String, Object> temp = appNameList.get(i);
            String areaName = PowerUtil.getString( temp.get( "areaName" ) );
            String value = PowerUtil.getString( temp.get( "appCount" ) );
            ArrayList<Object> objects = new ArrayList<>();
            objects.add( areaName );
            objects.add( value );
            if (!ParamEnum.active.prd.getCode().equals( active )) {
                if(!"南京市".equals( areaName ) && !"宿迁市".equals( areaName ) && !"苏州市".equals( areaName ) && !"扬州市".equals( areaName ) && !"无锡市".equals( areaName )){
                    objects.add( RandomUtil.getRandom( 100,200 ) );
                    objects.add( RandomUtil.getRandom( 500,999 ) );
                }
            }
            tempCache.put( areaName,objects );
        }
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( AppOpenCountEntity.countSql6 );
        if (ParamEnum.active.prd.getCode().equals( active ) && !ParamEnum.indexDateType.sum.getCode().equals( indexDateType )) {
            sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sql.append( " group by area_name ) order by \"areaName\" desc" );
        //打开人数
        List<Map<String, Object>> useList = jdbcTemplate.queryForList( sql.toString() );
        for (int i = 0; i < useList.size(); i++) {
            Map<String, Object> temp = useList.get(i);
            String areaName = PowerUtil.getString( temp.get( "areaName" ) );
            String value = PowerUtil.getString( temp.get( "value" ) );
            List<Object> objects = tempCache.get( areaName );
            objects.add( value );
        }
        sql = new StringBuilder();
        sql.append( "select * from (" ).append( AppOpenCountEntity.countSql5 );
        if (ParamEnum.active.prd.getCode().equals( active ) && !ParamEnum.indexDateType.sum.getCode().equals( indexDateType )) {
            sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sql.append( " group by area_name ) order by \"areaName\" desc" );
        //打开次数
        List<Map<String, Object>> openCountList = jdbcTemplate.queryForList( sql.toString() );

        for (int i = 0; i < openCountList.size(); i++) {
            Map<String, Object> temp = openCountList.get(i);
            String areaName = PowerUtil.getString( temp.get( "areaName" ) );
            String value = PowerUtil.getString( temp.get( "value" ) );
            List<Object> objects = tempCache.get( areaName );
            if (!ParamEnum.active.prd.getCode().equals( active )) {
                objects.add( RandomUtil.getRandom( 500,999 ) );
            }else {
                objects.add( value );
            }
        }
        List<List<Object>> sourceData = new ArrayList<>();
        sourceData.add(productList);
        for(Map.Entry<String, List<Object>> entry : tempCache.entrySet()){
            List<Object> mapValue =  entry.getValue() ;
            sourceData.add(mapValue);
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("sourceData",sourceData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }


    /**
     * @description  首页-各地区应用使用情况(app接口)
     * @return  返回结果
     * @date  2021-6-22 18:21
     * @author  wanghb
     * @edit
     */
    public Result appAreaCountForApp() {
        Map<String, Object> echartsData = appAreaCount( null );
        List<Integer> seriesData = (List<Integer>) echartsData.get( "seriesData" );
        List<String> xAxisData = (List<String>) echartsData.get( "xAxisData" );
        Map<String, Object> data = new HashMap<>();
        List<Object> dataList = new ArrayList<>();
        data.put("title","使用人数/授权人数占比");
        for (int i = 0; i < seriesData.size(); i++) {
            dataList.add(Arrays.asList( seriesData.get( i ),xAxisData.get( i  )));
        }
        data.put( "data",dataList );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,data);
    }

    /**
     * @description  首页-各地区应用使用情况(app接口)
     * @param  dateType
     * @return  返回结果
     * @date  2021-6-22 18:21
     * @author  wanghb
     * @edit
     */
    public  Map<String, Object> appAreaCount( String dateType) {
        String startDate = "";
        if (ParamEnum.active.prd.getCode().equals( active ) ) {
            if (ParamEnum.indexDateType.day.getCode().equals( dateType )) {
                startDate = DateUtil.toString(  new Date(), DateUtil.DATE_SHORT );
            }
            if (ParamEnum.indexDateType.day7.getCode().equals( dateType )) {
                startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
            }
            if (ParamEnum.indexDateType.day30.getCode().equals( dateType )) {
                startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
            }
        }
        String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( AppOpenCountEntity.countSql6 );
        if (ParamEnum.active.prd.getCode().equals( active ) && PowerUtil.isNotNull( dateType )) {
            sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sql.append( " group by area_name ) order by \"areaName\" desc" );
        List<Map<String, Object>> list = jdbcTemplate.queryForList( sql.toString() );
        List<String> xAxisData = new ArrayList<>();
        List<DictInfoEntity> areaList = ScheduledTasks.dictList.get( ParamEnum.dicType.APP_AREA.getCode() );
        for (int i = 0; i < areaList.size(); i++) {
            DictInfoEntity dictInfoEntity = areaList.get( i );
            String label = dictInfoEntity.getLabel();
            label += !ParamEnum.active.prd.getCode().equals( active ) ? "市" : "";
            xAxisData.add(label.replace( "省厅市","省厅" ));
        }
        List<Integer> seriesData = new ArrayList<>();
        Integer yAxisMin = list.size() > 0 ? PowerUtil.getIntValue(list.get(0).get("value")) : 0 ;
        for (int i = 0; i < xAxisData.size(); i++) {
            String date = xAxisData.get( i );
            Integer dateValue = 0;
            for (int j = 0; j < list.size(); j++) {
                Map<String, Object> temp = list.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String name = PowerUtil.getString( temp.get( "areaName" ) );
                yAxisMin = value < yAxisMin ? value : yAxisMin;
                if(date.equals( name )){
                    dateValue = value;
                }
            }
            seriesData.add(dateValue);
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("yAxisMin",yAxisMin - (yAxisMin / 5));
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return echartsData;

    }

    /**
     * @description  首页-各警种应用情况分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-6-22 18:21
     * @author  wanghb
     * @edit
     */
    public Result appPoliceCountEcharts(Map<String, Object> params) {
        String indexDateType = PowerUtil.getString( params.get( "indexDateType" ) );
        String startDate = "";
        if (ParamEnum.indexDateType.day7.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        if (ParamEnum.indexDateType.day30.getCode().equals( indexDateType )) {
            startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
        }
        String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );

        List<Map<String, Object>> appNameList = new ArrayList<>();
        List<DictInfoEntity> policeList = ScheduledTasks.dictList.get( ParamEnum.dicType.APP_POLICE.getCode() );
        for (int i = 0; i < policeList.size(); i++) {
            DictInfoEntity dictInfoEntity = policeList.get( i );
            String value = dictInfoEntity.getValue();
            appNameList.add(ImmutableMap.of("policeName",dictInfoEntity.getLabel(),"appCount",appInfoService.getCount( null, value)));
        }
        List<Object > productList = Arrays.asList( "product","上线应用数","使用人数" ,"使用人次");

        Map<String, List<Object>> tempCache = new LinkedHashMap<>();
        for (int i = 0; i < appNameList.size(); i++) {
            Map<String, Object> temp = appNameList.get(i);
            String policeName = PowerUtil.getString( temp.get( "policeName" ) );
            String value = PowerUtil.getString( temp.get( "appCount" ) );
            ArrayList<Object> objects = new ArrayList<>();
            objects.add( policeName );
            objects.add( value );
            tempCache.put( policeName,objects );
        }

        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( AppOpenCountEntity.countSql8 );
        if (ParamEnum.active.prd.getCode().equals( active ) && !ParamEnum.indexDateType.sum.getCode().equals( indexDateType )) {
            sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sql.append( " group by police_code ) order by \"policeCode\" desc" );
        //打开人数
        List<Map<String, Object>> useList = jdbcTemplate.queryForList( sql.toString() );
        for (int i = 0; i < useList.size(); i++) {
            Map<String, Object> temp = useList.get(i);
            String policeCode = PowerUtil.getString( temp.get( "policeCode" ) );
            String policeName = ScheduledTasks.dict.get( ParamEnum.dicType.APP_POLICE.getCode() ).get( policeCode );
            String value = PowerUtil.getString( temp.get( "value" ) );
            List<Object> objects = tempCache.get( policeName );
            objects.add( value );
        }

        sql = new StringBuilder();
        sql.append( "select * from (" ).append( AppOpenCountEntity.countSql7 );
        if (ParamEnum.active.prd.getCode().equals( active ) && !ParamEnum.indexDateType.sum.getCode().equals( indexDateType )) {
            sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sql.append( " group by police_code ) order by \"policeCode\" desc" );
        //打开次数
        List<Map<String, Object>> openCountList = jdbcTemplate.queryForList( sql.toString() );

        for (int i = 0; i < openCountList.size(); i++) {
            Map<String, Object> temp = openCountList.get(i);
            String policeCode = PowerUtil.getString( temp.get( "policeCode" ) );
            String policeName = ScheduledTasks.dict.get( ParamEnum.dicType.APP_POLICE.getCode() ).get( policeCode );
            String value = PowerUtil.getString( temp.get( "value" ) );
            List<Object> objects = tempCache.get( policeName );
            if (!ParamEnum.active.prd.getCode().equals( active )) {
                objects.add( RandomUtil.getRandom( 500,999 ) );
            }else {
                objects.add( value );
            }
        }

        List<List<Object>> sourceData = new ArrayList<>();
        sourceData.add(productList);
        for(Map.Entry<String, List<Object>> entry : tempCache.entrySet()){
            List<Object> mapValue =  entry.getValue() ;
            sourceData.add(mapValue);
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("sourceData",sourceData);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,echartsData);
    }



    /**
     * @description  各地区应用使用情况(app接口)
     * @param  params
     * @return  返回结果
     * @date  2021-6-22 18:21
     * @author  wanghb
     * @edit
     */
    public Result appPoliceCountForApp(Map<String, Object> params) {
        Map<String, Object> echartsData = appPoliceCount( params );
        Map<String, Object> option = new HashMap<>();
        Map<String, Object> xAxis = new HashMap<>();
        xAxis.put( "type", "category");
        xAxis.put( "data", echartsData.get( "xAxisData" ));
        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put( "type","value" );
        Map<String, Object> series = new HashMap<>();
        series.put( "type","BarChart" );
        series.put( "showBackground",true );
        series.put( "colors",Arrays.asList( "#5976E8","#333333") );
        series.put( "data", Arrays.asList( echartsData.get( "seriesData1" ),echartsData.get( "seriesData2" ) ));
        option.put( "xAxis",xAxis );
        option.put( "yAxis", yAxis);
        option.put( "series",series );
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,option);
    }



    /**
     * @description  各地区应用使用情况(app接口)
     * @param  params
     * @return  返回结果
     * @date  2021-6-22 18:21
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> appPoliceCount(Map<String, Object> params) {
        String dateType = PowerUtil.getString( params.get( "dateType" ) );
        String startDate = "";
        if (ParamEnum.active.prd.getCode().equals( active ) ) {
            if (ParamEnum.indexDateType.day.getCode().equals( dateType )) {
                startDate = DateUtil.toString(  new Date(), DateUtil.DATE_SHORT );
            }
            if (ParamEnum.indexDateType.day7.getCode().equals( dateType )) {
                startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
            }
            if (ParamEnum.indexDateType.day30.getCode().equals( dateType )) {
                startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
            }
        }
        String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );

        List<DictInfoEntity> policeList = ScheduledTasks.dictList.get( ParamEnum.dicType.APP_POLICE.getCode() );
        List<String> xAxisData = new ArrayList<>();
        for (int i = 0; i < policeList.size(); i++) {
            DictInfoEntity dictInfoEntity = policeList.get( i );
            String label = dictInfoEntity.getLabel();
            xAxisData.add(label);
        }

        //统计打开人数
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( AppOpenCountEntity.countSql8 );
        if (ParamEnum.active.prd.getCode().equals( active ) && !ParamEnum.indexDateType.sum.getCode().equals( dateType )) {
            sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sql.append( " group by police_code ) order by \"policeCode\" desc" );
        //打开人数
        List<Map<String, Object>> useList = jdbcTemplate.queryForList( sql.toString() );
        List<Integer> seriesData1 = new ArrayList<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            String date = xAxisData.get( i );
            Integer dateValue = 0;
            for (int j = 0; j < useList.size(); j++) {
                Map<String, Object> temp = useList.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String policeCode = PowerUtil.getString( temp.get( "policeCode" ) );
                String name = ScheduledTasks.dict.get( ParamEnum.dicType.APP_POLICE.getCode() ).get( policeCode );
                if(date.equals( name )){
                    dateValue = value;
                }
            }
            seriesData1.add(dateValue);
        }
        //统计打开人次
        sql = new StringBuilder();
        sql.append( "select * from (" ).append( AppOpenCountEntity.countSql7 );
        if (ParamEnum.active.prd.getCode().equals( active ) && !ParamEnum.indexDateType.sum.getCode().equals( dateType )) {
            sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sql.append( " group by police_code ) order by \"policeCode\" desc" );
        //打开次数
        List<Map<String, Object>> openCountList = jdbcTemplate.queryForList( sql.toString() );
        List<Integer> seriesData2 = new ArrayList<>();
        for (int i = 0; i < xAxisData.size(); i++) {
            String date = xAxisData.get( i );
            Integer dateValue = 0;
            for (int j = 0; j < openCountList.size(); j++) {
                Map<String, Object> temp = openCountList.get(j);
                Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                String policeCode = PowerUtil.getString( temp.get( "policeCode" ) );
                String name = ScheduledTasks.dict.get( ParamEnum.dicType.APP_POLICE.getCode() ).get( policeCode );
                if(date.equals( name )){
                    dateValue = value;
                }
            }
            seriesData2.add(dateValue);
        }

        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData1",seriesData1);
        echartsData.put("seriesData2",seriesData2);
        echartsData.put("xAxisData",xAxisData);
        return echartsData;

    }


    /**
     * @description  TOP 5 类型应用使用情况
     * @param  params
     * @return  返回结果
     * @date  2021-6-22 18:21
     * @author  wanghb
     * @edit
     */
    public Result appCategoryCountForApp(Map<String, Object> params) {
        String startDate = PowerUtil.getString( params.get( "startDate" ) );
        String endDate = PowerUtil.getString( params.get( "endDate" ) );

        StringBuilder sqlTemp = new StringBuilder();
        sqlTemp.append( AppOpenCountEntity.countSql9 );
        if (ParamEnum.active.prd.getCode().equals( active )) {
            sqlTemp.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sqlTemp.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sqlTemp.append( " group by CATEGORY_CODE " );
        SQL sql = new SQL();
        sql.SELECT("*");
        sql.FROM("("+sqlTemp+")");
        sql.ORDER_BY(" \"value\" desc");
        Pagination page = new Pagination(1,5);

        baseDao.getPaginationByNactiveSql(sql,page);
        List<Map<String, Object>> list = page.getRows();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> temp = list.get(i);
            String name = ScheduledTasks.dict.get( ParamEnum.dicType.APP_CATEGORY.getCode() ).get( PowerUtil.getString( temp.get( "categorycode" ) ) );
            temp.put( "name",name );
            temp.remove( "categorycode" );
        }
        Map<String, Object> data = new HashMap<>();
        data.put("title","TOP 5 类型应用使用情况");
        data.put("data",list);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,data);
    }



    /**
     * @description  首页-各警种应用情况分布Echarts
     * @param  params
     * @return  返回结果
     * @date  2021-6-22 18:21
     * @author  wanghb
     * @edit
     */
    public Map<String, Object> appCategoryCount(Map<String, Object> params) {
        String dateType = PowerUtil.getString( params.get( "dateType" ) );
        String countType = PowerUtil.getString( params.get( "countType" ) );
        String startDate = "";
        if (ParamEnum.active.prd.getCode().equals( active ) ) {
            if (ParamEnum.indexDateType.day.getCode().equals( dateType )) {
                startDate = DateUtil.toString(  new Date(), DateUtil.DATE_SHORT );
            }
            if (ParamEnum.indexDateType.day7.getCode().equals( dateType )) {
                startDate = DateUtil.toString( DateUtil.countDate( new Date(), -7, Calendar.DATE ), DateUtil.DATE_SHORT );
            }
            if (ParamEnum.indexDateType.day30.getCode().equals( dateType )) {
                startDate = DateUtil.toString( DateUtil.countDate( new Date(), -30, Calendar.DATE ), DateUtil.DATE_SHORT );
            }
        }
        String endDate = DateUtil.toString( new Date(),DateUtil.DATE_SHORT );

        List<DictInfoEntity> policeList = ScheduledTasks.dictList.get( ParamEnum.dicType.APP_CATEGORY.getCode() );
        List<String> xAxisData = new ArrayList<>();
        for (int i = 0; i < policeList.size(); i++) {
            DictInfoEntity dictInfoEntity = policeList.get( i );
            String label = dictInfoEntity.getLabel();
            xAxisData.add(label);
        }

        //统计打开人数
        StringBuilder sql = new StringBuilder();
        sql.append( "select * from (" ).append( AppOpenCountEntity.countSql9 );
        if (ParamEnum.active.prd.getCode().equals( active ) && !ParamEnum.indexDateType.sum.getCode().equals( dateType )) {
            sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
            sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
        }
        sql.append( " group by CATEGORY_CODE ) order by \"categoryCode\" desc" );
        //打开人数
        List<Integer> seriesData = new ArrayList<>();
        if (ParamEnum.countType.type0.getCode().equals( countType )) {
            List<Map<String, Object>> useList = jdbcTemplate.queryForList( sql.toString() );
            for (int i = 0; i < xAxisData.size(); i++) {
                String date = xAxisData.get( i );
                Integer dateValue = 0;
                for (int j = 0; j < useList.size(); j++) {
                    Map<String, Object> temp = useList.get(j);
                    Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                    String categoryCode = PowerUtil.getString( temp.get( "categoryCode" ) );
                    String name = ScheduledTasks.dict.get( ParamEnum.dicType.APP_CATEGORY.getCode() ).get( categoryCode );
                    if(date.equals( name )){
                        dateValue = value;
                    }
                }
                seriesData.add(dateValue);
            }
        }

        //统计打开人次
        if (ParamEnum.countType.type1.getCode().equals( countType )) {
            sql = new StringBuilder();
            sql.append( "select * from (" ).append( AppOpenCountEntity.countSql10 );
            if (ParamEnum.active.prd.getCode().equals( active ) && !ParamEnum.indexDateType.sum.getCode().equals( dateType )) {
                sql.append( " AND OPEN_DATE >= " + JdbcTemplateUtil.getOracelToDate( startDate ) );
                sql.append( " AND OPEN_DATE <= " + JdbcTemplateUtil.getOracelToDate( endDate ) );
            }
            sql.append( " group by CATEGORY_CODE ) order by \"categoryCode\" desc" );
            //打开次数
            List<Map<String, Object>> openCountList = jdbcTemplate.queryForList( sql.toString() );
            for (int i = 0; i < xAxisData.size(); i++) {
                String date = xAxisData.get( i );
                Integer dateValue = 0;
                for (int j = 0; j < openCountList.size(); j++) {
                    Map<String, Object> temp = openCountList.get(j);
                    Integer value = PowerUtil.getIntValue( temp.get( "value" ) );
                    String categoryCode = PowerUtil.getString( temp.get( "categoryCode" ) );
                    String name = ScheduledTasks.dict.get( ParamEnum.dicType.APP_CATEGORY.getCode() ).get( categoryCode );
                    if(date.equals( name )){
                        dateValue = value;
                    }
                }
                seriesData.add(dateValue);
            }
        }
        Map<String, Object> echartsData = new HashMap<>();
        echartsData.put("seriesData",seriesData);
        echartsData.put("xAxisData",xAxisData);
        return echartsData;

    }

    /**
     * @description  质态情况-全省应用建设情况
     * @param  params
     * @return  返回结果
     * @date  2021-8-27 9:50
     * @author  wanghb
     * @edit
     */
    public Result appConstruction(Map<String, Object> params) {
        List<Map<String, Object>> list = mysqlJdbcTemplate.queryForList( AppInfoEntity.selectSql2 );
        Map<String, Object> data = new HashMap<>();
        data.put("title","全省应用建设情况");
        List<Object> dataList = new ArrayList<>();
        for (Map<String, Object> temp : list) {
            String name = PowerUtil.getString( temp.get( "name" ) );
            String value = PowerUtil.getString( temp.get( "value" ) );
            dataList.add(Arrays.asList( name,value ));
        }
        data.put("data",dataList);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,data);
    }

    /**
     * @description  质态情况-全省应用建设数
     * @param  params
     * @return  返回结果
     * @date  2021-8-27 9:50
     * @author  wanghb
     * @edit
     */
    public Result appConstruction2(Map<String, Object> params) {
        List<Map<String, Object>> list = mysqlJdbcTemplate.queryForList( AppInfoEntity.selectSql2 );
        Map<String, Object> data = new HashMap<>();
        data.put("title","各地区应用建设情况");
        List<Object> dataList = new ArrayList<>();
        data.put("data",list);
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,data);
    }


}
