package com.acrabsoft.web.service.sjt.pc.operation.web.taizhouRelated.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 数据分析形式( DataAnalysisFormEntity )实体抽象类
* @author wanghb
* @since 2021-8-30 20:05:04
*/
@Entity
@Getter
@Setter
@Table(name = "yy_data_analysis_form")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "数据分析形式( DataAnalysisFormEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class DataAnalysisFormEntity extends  DataAnalysisFormEntityAbstract  implements Serializable {

	public  DataAnalysisFormEntity(){
		super();
	}

}
