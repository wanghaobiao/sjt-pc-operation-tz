
package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.BaseEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.*;
import javax.persistence.OneToMany;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.alibaba.fastjson.annotation.JSONField;

/**
* 应用分时活跃用户统计( AppHourActiveCountEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-23 14:22:40
*/
@MappedSuperclass
@Getter
@Setter
public class AppHourActiveCountEntityAbstract extends BaseEntity implements Serializable {
    public final static String tableName = "yy_app_hour_active_count";
    public final static String insertSql = "INSERT INTO yy_app_hour_active_count (open_date,area_name,open_count,device_model,app_name,app_id,id,deleted,create_user,create_time,update_user,update_time) VALUES (:openDate,:areaName,:openCount,:deviceModel,:appName,:appId,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE yy_app_hour_active_count SET open_date = :openDate,area_name = :areaName,open_count = :openCount,device_model = :deviceModel,app_name = :appName,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from yy_app_hour_active_count WHERE 1 = 1 ";
    public final static String countSql = "select id \"id\" from yy_app_hour_active_count where 1 = 1 ";


    @Basic
    @Column(name = "open_date")
    @ApiModelProperty(value="启动日期(分时)",name="openDate",required=false)
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date openDate;

    @Basic
    @Column(name = "area_name")
    @ApiModelProperty(value="地区名称",name="areaName",required=false)
    private String areaName;

    @Basic
    @Column(name = "open_count")
    @ApiModelProperty(value="启动次数",name="openCount",required=false)
    private Integer openCount;

    @Basic
    @Column(name = "device_model")
    @ApiModelProperty(value="终端型号",name="deviceModel",required=false)
    private String deviceModel;

    @Basic
    @Column(name = "app_name")
    @ApiModelProperty(value="应用名称",name="appName",required=false)
    private String appName;

    @Basic
    @Column(name = "app_id")
    @ApiModelProperty(value="应用id",name="appId",required=false)
    private String appId;

}
