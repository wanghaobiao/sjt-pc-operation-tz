package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 服务操作应用表( ServiceOperationAppEntity )实体抽象类
* @author wanghb
* @since 2021-6-4 17:04:58
*/
@Entity
@Getter
@Setter
@Table(name = "yy_service_operation_app")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "服务操作应用表( ServiceOperationAppEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class ServiceOperationAppEntity extends  ServiceOperationAppEntityAbstract  implements Serializable {

	public final static String tableName = "yy_service_operation_app";
	public final static String insertSql = "INSERT INTO yy_service_operation_app (service_id,service_name,app_id,app_name,operation_type,operation_type_name,operator_id,operator_name,operator_time,operator_description,id,deleted,create_user,create_time,update_user,update_time) VALUES (:serviceId,:serviceName,:appId,:appName,:operationType,:operationTypeName,:operatorId,:operatorName,:operatorTime,:operatorDescription,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
	public final static String updateSql = "UPDATE yy_service_operation_app SET service_id = :serviceId,service_name = :serviceName,app_id = :appId,app_name = :appName,operation_type = :operationType,operation_type_name = :operationTypeName,operator_id = :operatorId,operator_name = :operatorName,operator_time = :operatorTime,operator_description = :operatorDescription,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
	public final static String deleteSql = "DELETE from yy_service_operation_app WHERE 1 = 1 ";
    public final static String countSql1 = "select id \"id\" from yy_service_operation_app where 1 = 1 ";

	public  ServiceOperationAppEntity(){
		super();
	}

}
