package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

import com.acrabsoft.web.service.sjt.pc.operation.web.util.DateUtil;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
* 应用每日人员使用时长( AppOpenDurationEntity )实体抽象类
* @author wanghb
* @since 2021-8-26 15:39:10
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_open_duration")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用每日人员使用时长( AppOpenDurationEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppOpenDurationEntity extends  AppOpenDurationEntityAbstract  implements Serializable {

	public final static String countSql2 = "select to_char(open_date,'"+ DateUtil.DATE_SHORT +"') \"name\",sum(duration) \"value\" from yy_app_open_duration where 1 = 1 ";
	public final static String countSql3= "select to_char(open_date,'"+ DateUtil.DATE_HM24 +"') \"name\",sum(duration) \"value\" from yy_app_open_duration where 1 = 1 ";
	public final static String countSql4 = "select app_name \"name\",sum(duration) \"value\" from yy_app_open_duration where 1 = 1 ";


	public  AppOpenDurationEntity(){
		super();
	}

}
