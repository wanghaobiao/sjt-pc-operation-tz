package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 服务日志脏数据表( ServiceLogDirtyDataEntity )实体抽象类
* @author wanghb
* @since 2021-7-27 18:20:43
*/
@Entity
@Getter
@Setter
@Table(name = "yy_service_log_dirty_data")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "服务日志脏数据表( ServiceLogDirtyDataEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class ServiceLogDirtyDataEntity extends  ServiceLogDirtyDataEntityAbstract  implements Serializable {

	public  ServiceLogDirtyDataEntity(){
		super();
	}

}
