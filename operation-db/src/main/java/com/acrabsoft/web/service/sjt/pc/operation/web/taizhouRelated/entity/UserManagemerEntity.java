package com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 用户管理( UserManagemerEntity )实体抽象类
* @author wanghb
* @since 2021-8-31 10:54:51
*/
@Entity
@Getter
@Setter
@Table(name = "yy_user_managemer")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "用户管理( UserManagemerEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class UserManagemerEntity extends  UserManagemerEntityAbstract  implements Serializable {

	public  UserManagemerEntity(){
		super();
	}

}
