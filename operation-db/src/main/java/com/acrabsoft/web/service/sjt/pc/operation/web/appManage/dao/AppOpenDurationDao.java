
package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.dao;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import org.springframework.stereotype.Repository;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.AppOpenDurationEntity;
import com.acrabsoft.web.dao.base.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.JdbcTemplateUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.ParamEnum;
import com.acrabsoft.web.dao.base.SQL;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.*;
import java.util.Date;
import java.util.List;


/**
* 应用每日人员使用时长( AppOpenDurationDao )Dao类
* @author wanghb
* @since 2021-8-26 15:21:37
*/
@Repository
public class AppOpenDurationDao extends BaseController {
    @Autowired
    private BaseDao baseDao;

    /**
    * @description  删除明细
    * @param  id  父id
    * @return  返回结果
    * @date  2021-8-26 15:21:37
    * @author  wanghb
    * @edit
    */
    public void deleteDetail(String id) {
    }

    /**
    * @description  批量逻辑删除
    * @param  ids  id集合
    * @return  返回结果
    * @date  2021-8-26 15:21:37
    * @author  wanghb
    * @edit
    */
    public void batchLogicDelete(List<String> ids) {
        String userId = getBaseUser().getUserid();
        SQL sql = new SQL();
        sql.UPDATE( AppOpenDurationEntity.tableName );
        sql.SET( new StringBuilder( " deleted = '" ).append( ParamEnum.deleted.yesDel.getCode() ).append( "'" ).toString() );
        sql.SET( JdbcTemplateUtil.getOracleUpate( new Date(), userId ) );
        sql.WHERE( new StringBuilder( "id in (" ).append( JdbcTemplateUtil.toInParams( ids ) ).append( ")" ).toString() );
        baseDao.executeUpdateByNactiveSql( sql );
    }


    /**
    * @description  批量删除明细
    * @param  ids  父id
    * @return  返回结果
    * @date  2021-8-26 15:21:37
    * @author  wanghb
    * @edit
    */
    public void batchDeleteDetail(List<String> ids) {
    }


    /**
    * @description  批量逻辑删除明细
    * @param  ids  id集合
    * @return  返回结果
    * @date  2021-8-26 15:21:37
    * @author  wanghb
    * @edit
    */
    public void batchLogicDeleteDetail(List<String> ids) {
        String userId = getBaseUser().getUserid();
    }
}
