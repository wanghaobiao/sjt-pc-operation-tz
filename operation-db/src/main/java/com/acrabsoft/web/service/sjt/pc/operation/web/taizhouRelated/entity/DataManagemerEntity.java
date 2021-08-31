package com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 数据管理( DataManagemerEntity )实体抽象类
* @author wanghb
* @since 2021-8-31 10:55:02
*/
@Entity
@Getter
@Setter
@Table(name = "yy_data_managemer")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "数据管理( DataManagemerEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class DataManagemerEntity extends  DataManagemerEntityAbstract  implements Serializable {

	public  DataManagemerEntity(){
		super();
	}

}
