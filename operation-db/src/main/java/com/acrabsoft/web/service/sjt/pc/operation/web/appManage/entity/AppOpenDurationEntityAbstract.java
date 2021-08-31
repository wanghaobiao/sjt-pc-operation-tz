
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
* 应用人员使用时长( AppOpenDurationEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-8-30 14:31:37
*/
@MappedSuperclass
@Getter
@Setter
public class AppOpenDurationEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "yy_app_open_duration";
    public final static String insertSql = "INSERT INTO yy_app_open_duration (app_id,app_name,open_date,duration,kafka_send_time,person_code,person_name,app_version,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appId,:appName,:openDate,:duration,:kafkaSendTime,:personCode,:personName,:appVersion,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE yy_app_open_duration SET app_id = :appId,app_name = :appName,open_date = :openDate,duration = :duration,kafka_send_time = :kafkaSendTime,person_code = :personCode,person_name = :personName,app_version = :appVersion,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from yy_app_open_duration WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_app_open_duration where 1 = 1 ";


    @Basic
    @Column(name = "app_id")
    @ApiModelProperty(value="应用id",name="appId",required=false)
    private String appId;

    @Basic
    @Column(name = "app_name")
    @ApiModelProperty(value="应用名称",name="appName",required=false)
    private String appName;

    @Basic
    @Column(name = "open_date")
    @ApiModelProperty(value="使用日期(年月日时)",name="openDate",required=false)
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date openDate;

    @Basic
    @Column(name = "duration")
    @ApiModelProperty(value="使用时长",name="duration",required=false)
    private BigDecimal duration;

    @Basic
    @Column(name = "kafka_send_time")
    @ApiModelProperty(value="kafka发送时间",name="kafkaSendTime",required=false)
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date kafkaSendTime;

    @Basic
    @Column(name = "person_code")
    @ApiModelProperty(value="人员编号",name="personCode",required=false)
    private String personCode;

    @Basic
    @Column(name = "person_name")
    @ApiModelProperty(value="人员姓名",name="personName",required=false)
    private String personName;

    @Basic
    @Column(name = "app_version")
    @ApiModelProperty(value="应用版本",name="appVersion",required=false)
    private String appVersion;
}
