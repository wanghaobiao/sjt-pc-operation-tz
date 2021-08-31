package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 应用分时活跃用户统计( AppHourActiveCountEntity )实体抽象类
* @author wanghb
* @since 2021-4-23 14:22:40
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_hour_active_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用分时活跃用户统计( AppHourActiveCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppHourActiveCountEntity extends  AppHourActiveCountEntityAbstract  implements Serializable {


	public  AppHourActiveCountEntity(){
		super();
	}

	@Transient
	@ApiModelProperty(value="personCodeSet集合",name="personCodeSet",required=false)
	private Set<String> personCodeSet;

}
