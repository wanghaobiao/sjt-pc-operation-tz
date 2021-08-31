package com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 数据采集管理( DataCollectionManagerEntity )实体抽象类
* @author wanghb
* @since 2021-8-30 20:04:58
*/
@Entity
@Getter
@Setter
@Table(name = "yy_data_collection_manager")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "数据采集管理( DataCollectionManagerEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class DataCollectionManagerEntity extends  DataCollectionManagerEntityAbstract  implements Serializable {

	public  DataCollectionManagerEntity(){
		super();
	}

}
