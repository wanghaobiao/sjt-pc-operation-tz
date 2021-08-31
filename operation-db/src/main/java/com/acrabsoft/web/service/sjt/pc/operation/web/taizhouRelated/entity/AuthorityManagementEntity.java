package com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 权限管理( AuthorityManagementEntity )实体抽象类
* @author wanghb
* @since 2021-8-31 10:54:56
*/
@Entity
@Getter
@Setter
@Table(name = "yy_authority_management")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "权限管理( AuthorityManagementEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AuthorityManagementEntity extends  AuthorityManagementEntityAbstract  implements Serializable {

	public  AuthorityManagementEntity(){
		super();
	}

}
