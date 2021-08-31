
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
* 数据管理( DataManagemerEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-8-31 10:55:02
*/
@MappedSuperclass
@Getter
@Setter
public class DataManagemerEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "yy_data_managemer";
    public final static String insertSql = "INSERT INTO yy_data_managemer (name,code,type_code,type_name,remark,id,deleted,create_user,create_time,update_user,update_time) VALUES (:name,:code,:typeCode,:typeName,:remark,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE yy_data_managemer SET name = :name,code = :code,type_code = :typeCode,type_name = :typeName,remark = :remark,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from yy_data_managemer WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_data_managemer where 1 = 1 ";


    @Basic
    @Column(name = "name")
    @ApiModelProperty(value="参数名称",name="name",required=false)
    private String name;

    @Basic
    @Column(name = "code")
    @ApiModelProperty(value="参数编码",name="code",required=false)
    private String code;

    @Basic
    @Column(name = "type_code")
    @ApiModelProperty(value="参数类型",name="typeCode",required=false)
    private String typeCode;

    @Basic
    @Column(name = "type_name")
    @ApiModelProperty(value="类型名称",name="typeName",required=false)
    private String typeName;

    @Basic
    @Column(name = "remark")
    @ApiModelProperty(value="参数说明",name="remark",required=false)
    private String remark;
}
