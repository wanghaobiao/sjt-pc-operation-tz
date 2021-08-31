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
* 应用版本统计( AppVersionCountEntity )实体抽象类
* @author wanghb
* @since 2021-4-27 10:39:02
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_version_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用版本统计( AppVersionCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppVersionCountEntity extends  AppVersionCountEntityAbstract  implements Serializable {


	public  AppVersionCountEntity(){
		super();
	}

	@Transient
	@ApiModelProperty(value="personCodeSet",name="personCodeSet",required=false)
	private Set<String> personCodeSet;

}
