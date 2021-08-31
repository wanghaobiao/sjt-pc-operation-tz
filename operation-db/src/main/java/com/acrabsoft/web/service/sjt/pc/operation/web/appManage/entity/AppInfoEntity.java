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
* 应用信息表( AppInfoEntity )实体抽象类
* @author wanghb
* @since 2021-4-26 10:48:57
*/
@Entity
@Getter
@Setter
@Table(name = "t_app_info")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用信息表( AppInfoEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppInfoEntity extends  AppInfoEntityAbstract  implements Serializable {
	@Transient
	@ApiModelProperty(value="发布地区名称",name="areaName",required=false)
	private String areaName;

	@Transient
	@ApiModelProperty(value="网络区域名称",name="networkAreaName",required=false)
	private String networkAreaName;

	@Transient
	@ApiModelProperty(value="警种名称",name="policeName",required=false)
	private String policeName;

	@Transient
	@ApiModelProperty(value="业务类型",name="categoryName",required=false)
	private String categoryName;


	public  AppInfoEntity(){
		super();
	}

}
