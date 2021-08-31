package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 服务信息( ServiceInfoEntity )实体抽象类
* @author wanghb
* @since 2021-4-26 10:56:24
*/
@Entity
@Getter
@Setter
@Table(name = "t_service_info")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "服务信息( ServiceInfoEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class ServiceInfoEntity extends  ServiceInfoEntityAbstract  implements Serializable {


	@Transient
	@ApiModelProperty(value="服务级别名称",name="serviceLevelName",required=false)
	private String serviceLevelName;

	@Transient
	@ApiModelProperty(value="服务级别名称",name="platformName",required=false)
	private String platformName;

	@Transient
	@ApiModelProperty(value="可用网路区域",name="availablePlatform",required=false)
	private String availablePlatformName;

	@Transient
	@ApiModelProperty(value="接口类型",name="interfaceTypeName",required=false)
	private String interfaceTypeName;
	@Transient
	@ApiModelProperty(value="操作类型",name="operatorTypeName",required=false)
	private String operatorTypeName;


	public  ServiceInfoEntity(){
		super();
	}

}
