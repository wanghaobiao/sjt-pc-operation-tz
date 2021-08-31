
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
* 权限管理( AuthorityManagementEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-8-31 10:54:56
*/
@MappedSuperclass
@Getter
@Setter
public class AuthorityManagementEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "yy_authority_management";
    public final static String insertSql = "INSERT INTO yy_authority_management (name,mark,remark,id,deleted,create_user,create_time,update_user,update_time) VALUES (:name,:mark,:remark,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE yy_authority_management SET name = :name,mark = :mark,remark = :remark,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from yy_authority_management WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_authority_management where 1 = 1 ";


    @Basic
    @Column(name = "name")
    @ApiModelProperty(value="角色名称",name="name",required=false)
    private String name;

    @Basic
    @Column(name = "mark")
    @ApiModelProperty(value="角色标识",name="mark",required=false)
    private String mark;

    @Basic
    @Column(name = "remark")
    @ApiModelProperty(value="角色说明",name="remark",required=false)
    private String remark;
}
