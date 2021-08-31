package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 应用分时人员启动次数统计( AppHourOpenCountEntity )实体抽象类
* @author wanghb
* @since 2021-6-26 19:31:08
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_hour_open_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用分时人员启动次数统计( AppHourOpenCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppHourOpenCountEntity extends  AppHourOpenCountEntityAbstract  implements Serializable {



    public  AppHourOpenCountEntity(){
		super();
	}

}
