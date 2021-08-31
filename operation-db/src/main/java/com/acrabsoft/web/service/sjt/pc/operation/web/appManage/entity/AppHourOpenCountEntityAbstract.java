
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
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MiddleEntity;
import io.swagger.annotations.ApiModelProperty;
import com.alibaba.fastjson.annotation.JSONField;

/**
* 应用分时人员启动次数统计( AppHourOpenCountEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-6-26 19:31:08
*/
@MappedSuperclass
@Getter
@Setter
public class AppHourOpenCountEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "yy_app_hour_open_count";
    public final static String insertSql = "INSERT INTO yy_app_hour_open_count (open_date,area_name,person_code,open_count,device_model,app_name,app_id,area_code,id,deleted,create_user,create_time,update_user,update_time) VALUES (:openDate,:areaName,:personCode,:openCount,:deviceModel,:appName,:appId,:areaCode,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE yy_app_hour_open_count SET open_date = :openDate,area_name = :areaName,person_code = :personCode,open_count = :openCount,device_model = :deviceModel,app_name = :appName,app_id = :appId,area_code = :areaCode,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from yy_app_hour_open_count WHERE 1 = 1 ";
    public final static String countSql = "select id \"id\" from yy_app_hour_open_count where 1 = 1 ";
    public final static String countSql1 = "select to_char(open_date,'"+ DateUtil.DATE_HM24 +"') \"name\",count(open_count) \"value\" from yy_app_hour_open_count where 1 = 1 ";
    public final static String countSql2 = "select to_char(open_date,'"+ DateUtil.DATE_HM24 +"') \"name\",sum(open_count) \"value\" from yy_app_hour_open_count where 1 = 1 ";
    public final static String countSql3 = "select to_char(open_date,'"+ DateUtil.DATE_MM +"') \"name\",sum(open_count) \"value\" from yy_app_hour_open_count where 1 = 1 ";


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
    @Column(name = "person_code")
    @ApiModelProperty(value="人员code 身份证号",name="PERSON_CODE",required=false)
    private String personCode;

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

    @Basic
    @Column(name = "area_code")
    @ApiModelProperty(value="地区编号",name="areaCode",required=false)
    private String areaCode;
}
