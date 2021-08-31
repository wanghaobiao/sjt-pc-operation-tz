
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
* 应用每日人员启动次数统计( AppOpenCountEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-6-26 17:35:37
*/
@MappedSuperclass
@Getter
@Setter
public class AppOpenCountEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "yy_app_open_count";
    public final static String insertSql = "INSERT INTO yy_app_open_count (app_id,app_name,open_date,area_code,area_name,person_code,person_name,open_count,device_model,police_code,police_name,category_code,category_name,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appId,:appName,:openDate,:areaCode,:areaName,:personCode,:personName,:openCount,:deviceModel,:policeCode,:policeName,:categoryCode,:categoryName,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE yy_app_open_count SET app_id = :appId,app_name = :appName,open_date = :openDate,area_code = :areaCode,area_name = :areaName,person_code = :personCode,person_name = :personName,open_count = :openCount,device_model = :deviceModel,police_code = :policeCode,police_name = :policeName,category_code = :categoryCode,category_name = :categoryName,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from yy_app_open_count WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_app_open_count where 1 = 1 ";


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
    @ApiModelProperty(value="启动日期",name="openDate",required=false)
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date openDate;

    @Basic
    @Column(name = "area_code")
    @ApiModelProperty(value="地区编号",name="areaCode",required=false)
    private String areaCode;

    @Basic
    @Column(name = "area_name")
    @ApiModelProperty(value="地区名称",name="areaName",required=false)
    private String areaName;

    @Basic
    @Column(name = "person_code")
    @ApiModelProperty(value="人员code 身份证号",name="personCode",required=false)
    private String personCode;

    @Basic
    @Column(name = "person_name")
    @ApiModelProperty(value="人员姓名",name="personName",required=false)
    private String personName;

    @Basic
    @Column(name = "open_count")
    @ApiModelProperty(value="启动次数",name="openCount",required=false)
    private Integer openCount;

    @Basic
    @Column(name = "device_model")
    @ApiModelProperty(value="终端型号",name="deviceModel",required=false)
    private String deviceModel;

    @Basic
    @Column(name = "police_code")
    @ApiModelProperty(value="警种编号",name="policeCode",required=false)
    private String policeCode;

    @Basic
    @Column(name = "police_name")
    @ApiModelProperty(value="警种名称",name="policeName",required=false)
    private String policeName;

    @Basic
    @Column(name = "category_code")
    @ApiModelProperty(value="ҵ�����ͱ��",name="categoryCode",required=false)
    private String categoryCode;

    @Basic
    @Column(name = "category_name")
    @ApiModelProperty(value="ҵ����������",name="categoryName",required=false)
    private String categoryName;
}
