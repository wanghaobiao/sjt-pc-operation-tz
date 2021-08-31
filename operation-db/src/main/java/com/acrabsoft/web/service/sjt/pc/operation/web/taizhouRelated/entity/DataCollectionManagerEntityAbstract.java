
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
* 数据采集管理( DataCollectionManagerEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-8-30 20:04:58
*/
@MappedSuperclass
@Getter
@Setter
public class DataCollectionManagerEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "yy_data_collection_manager";
    public final static String insertSql = "INSERT INTO yy_data_collection_manager (name,remark,details,id,deleted,create_user,create_time,update_user,update_time) VALUES (:name,:remark,:details,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE yy_data_collection_manager SET name = :name,remark = :remark,details = :details,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from yy_data_collection_manager WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_data_collection_manager where 1 = 1 ";


    @Basic
    @Column(name = "name")
    @ApiModelProperty(value="名称",name="name",required=false)
    private String name;

    @Basic
    @Column(name = "remark")
    @ApiModelProperty(value="备注",name="remark",required=false)
    private String remark;

    @Basic
    @Column(name = "details")
    @ApiModelProperty(value="详情",name="details",required=false)
    private String details;
}
