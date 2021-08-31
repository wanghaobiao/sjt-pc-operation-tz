package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
* 参数表( DictInfoEntity )实体抽象类
* @author wanghb
* @since 2021-4-26 10:48:57
*/
@Getter
@Setter
@Table(name = "T_DICT_INFO")
@ApiModel(description= "参数表( DictInfoEntity )实体")
public class DictInfoEntity  implements Serializable , Comparable<DictInfoEntity> {

	public final static String tableName = "T_DICT_INFO";
	public final static String insertSql = "INSERT INTO T_DICT_INFO (app_description,app_id,app_name,dept_code,app_type,app_url,area,category,network_area,person_obj_id,power_type,status,type,city,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appDescription,:appId,:appName,:deptCode,:appType,:appUrl,:area,:category,:networkArea,:personObjId,:powerType,:status,:type,:city,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String updateSql = "UPDATE T_DICT_INFO SET app_description = :appDescription,app_id = :appId,app_name = :appName,dept_code = :deptCode,app_type = :appType,app_url = :appUrl,area = :area,category = :category,network_area = :networkArea,person_obj_id = :personObjId,power_type = :powerType,status = :status,type = :type,city = :city,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from T_DICT_INFO WHERE 1 = 1 ";
	public final static String countSql1 = "select id \"id\" from T_DICT_INFO where 1 = 1 ";
	public final static String selectSql1 = "select * from T_DICT_INFO where 1 = 1 ";

	@Basic
	@Column(name = "pvalue")
	@ApiModelProperty(value="参数类型",name="pvalue",required=false)
	private String pvalue;

	@Basic
	@Column(name = "value")
	@ApiModelProperty(value="参数值",name="value",required=false)
	private String value;

	@Basic
	@Column(name = "label")
	@ApiModelProperty(value="参数名称",name="label",required=false)
	private String label;
	
	@Basic
	@Column(name = "order_no")
	@ApiModelProperty(value="排序",name="order_no",required=false)
	private Integer orderNo;

	@Basic
	@Column(name = "deleted")
	@ApiModelProperty(value="是否删除",name="deleted",required=false)
	private Integer deleted;

	@Override
    public int compareTo(DictInfoEntity dictInfoEntity) {
        return this.orderNo - dictInfoEntity.getOrderNo();
    }

}
