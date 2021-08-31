
package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.*;
import javax.persistence.OneToMany;
import io.swagger.annotations.ApiModel;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MiddleEntity;
import io.swagger.annotations.ApiModelProperty;
import com.alibaba.fastjson.annotation.JSONField;

/**
* 服务日志脏数据表( ServiceLogDirtyDataEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-7-27 18:20:43
*/
@MappedSuperclass
@Getter
@Setter
public class ServiceLogDirtyDataEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "yy_service_log_dirty_data";
    public final static String insertSql = "INSERT INTO yy_service_log_dirty_data (error_data,error_msg,status,type,id,deleted,create_user,create_time,update_user,update_time) VALUES (:errorData,:errorMsg,:status,:type,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE yy_service_log_dirty_data SET error_data = :errorData,error_msg = :errorMsg,status = :status,type = :type,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from yy_service_log_dirty_data WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_service_log_dirty_data where 1 = 1 ";


    @Basic
    @Column(name = "error_data")
    @ApiModelProperty(value="异常数据",name="errorData",required=false)
    private String errorData;

    @Basic
    @Column(name = "error_msg")
    @ApiModelProperty(value="异常原因",name="errorMsg",required=false)
    private String errorMsg;

    @Basic
    @Column(name = "status")
    @ApiModelProperty(value="处理状态  0  未处理  1已处理",name="status",required=false)
    private String status;

    @Basic
    @Column(name = "type")
    @ApiModelProperty(value="异常类型  0 程序异常  1  参数异常",name="type",required=false)
    private String type;
}
