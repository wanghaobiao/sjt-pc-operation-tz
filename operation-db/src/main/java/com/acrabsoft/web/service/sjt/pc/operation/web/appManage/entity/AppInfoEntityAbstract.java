
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
* 应用信息表( AppInfoEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-26 10:48:58
*/
@MappedSuperclass
@Getter
@Setter
public class AppInfoEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "t_app_info";
    public final static String insertSql = "INSERT INTO t_app_info (app_description,app_id,app_name,dept_code,app_type,app_url,area,category,network_area,person_obj_id,power_type,status,type,city,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appDescription,:appId,:appName,:deptCode,:appType,:appUrl,:area,:category,:networkArea,:personObjId,:powerType,:status,:type,:city,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE t_app_info SET app_description = :appDescription,app_id = :appId,app_name = :appName,dept_code = :deptCode,app_type = :appType,app_url = :appUrl,area = :area,category = :category,network_area = :networkArea,person_obj_id = :personObjId,power_type = :powerType,status = :status,type = :type,city = :city,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from t_app_info WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from t_app_info where 1 = 1 ";
    public final static String selectSql1 = "select * from t_app_info where 1 = 1 ";
    //各地市应用建设情况
    public final static String selectSql2 = "select d.label name, coalesce(ac.cnt, 0) value from t_dict_info d left join (select a.city, count(*) cnt  from sjt.t_app_info a  where a.status = '0' group by a.city) ac on d.value = ac.city where d.pvalue = 'APP_AREA' ";

    @Basic
    @Column(name = "obj_id")
    @ApiModelProperty(value="主键id",name="objId",required=false)
    private String objId;


    @Basic
    @Column(name = "app_description")
    @ApiModelProperty(value="应用说明",name="appDescription",required=false)
    private String appDescription;


    @Basic
    @Column(name = "app_id")
    @ApiModelProperty(value="应用id",name="appId",required=false)
    private String appId;

    @Basic
    @Column(name = "app_name")
    @ApiModelProperty(value="应用名称",name="appName",required=false)
    private String appName;

    @Basic
    @Column(name = "dept_code")
    @ApiModelProperty(value="部门编号",name="deptCode",required=false)
    private String deptCode;

    @Basic
    @Column(name = "app_type")
    @ApiModelProperty(value="应用类型：1.原生应用；2.H5应用",name="appType",required=false)
    private String appType;

    @Basic
    @Column(name = "app_url")
    @ApiModelProperty(value="应用地址，H5应用必填，为应用打开地址；原生应用为下载文件ID",name="appUrl",required=false)
    private String appUrl;

    @Basic
    @Column(name = "area")
    @ApiModelProperty(value="应用发布地区",name="area",required=false)
    private String area;

    @Basic
    @Column(name = "category")
    @ApiModelProperty(value="应用业务类型分类《苏警通字典附件》-7",name="category",required=false)
    private String category;

    @Basic
    @Column(name = "network_area")
    @ApiModelProperty(value="应用网络区域：1.Ⅰ类区；2.Ⅱ类区；3.Ⅲ类区",name="networkArea",required=false)
    private String networkArea;

    @Basic
    @Column(name = "person_obj_id")
    @ApiModelProperty(value="责任人信息主键",name="personObjId",required=false)
    private String personObjId;

    @Basic
    @Column(name = "power_type")
    @ApiModelProperty(value="应用授权方式",name="powerType",required=false)
    private String powerType;

    @Basic
    @Column(name = "status")
    @ApiModelProperty(value="信息状态：0.正常；1.应用注册",name="status",required=false)
    private String status;

    @Basic
    @Column(name = "type")
    @ApiModelProperty(value="应用种类：      * 1.普通应用：会发布到应用市场      * 2.服务应用：不会发布到应用市场",name="type",required=false)
    private String type;

    @Basic
    @Column(name = "city")
    @ApiModelProperty(value="地市",name="city",required=false)
    private String city;

    @Basic
    @Column(name = "police")
    @ApiModelProperty(value="警种",name="police",required=false)
    private String police;
}
