
package com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.entity;

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
* 用户管理( UserManagemerEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-8-31 10:54:51
*/
@MappedSuperclass
@Getter
@Setter
public class UserManagemerEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "yy_user_managemer";
    public final static String insertSql = "INSERT INTO yy_user_managemer (name,gender,police_no,phone,id_card,e_mail,demp_name,id,deleted,create_user,create_time,update_user,update_time) VALUES (:name,:gender,:policeNo,:phone,:idCard,:eMail,:dempName,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE yy_user_managemer SET name = :name,gender = :gender,police_no = :policeNo,phone = :phone,id_card = :idCard,e_mail = :eMail,demp_name = :dempName,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from yy_user_managemer WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_user_managemer where 1 = 1 ";


    @Basic
    @Column(name = "name")
    @ApiModelProperty(value="用户名称",name="name",required=false)
    private String name;

    @Basic
    @Column(name = "gender")
    @ApiModelProperty(value="性别",name="gender",required=false)
    private String gender;

    @Basic
    @Column(name = "police_no")
    @ApiModelProperty(value="警号",name="policeNo",required=false)
    private String policeNo;

    @Basic
    @Column(name = "phone")
    @ApiModelProperty(value="手机号",name="phone",required=false)
    private String phone;

    @Basic
    @Column(name = "id_card")
    @ApiModelProperty(value="身份证号 ",name="idCard",required=false)
    private String idCard;

    @Basic
    @Column(name = "e_mail")
    @ApiModelProperty(value="邮箱",name="eMail",required=false)
    private String eMail;

    @Basic
    @Column(name = "demp_name")
    @ApiModelProperty(value="部门名称",name="dempName",required=false)
    private String dempName;
}
