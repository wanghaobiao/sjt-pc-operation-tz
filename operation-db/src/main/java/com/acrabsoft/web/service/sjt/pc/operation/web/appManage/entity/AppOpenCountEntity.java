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
* 应用每日人员启动次数统计( AppOpenCountEntity )实体抽象类
* @author wanghb
* @since 2021-6-26 17:35:37
*/
@Entity
@Getter
@Setter
@Table(name = "yy_app_open_count")//, schema = "YUNYING_BACKEND"
@ApiModel(description= "应用每日人员启动次数统计( AppOpenCountEntity )实体")
@EntityListeners(AuditingEntityListener.class)
public class AppOpenCountEntity extends  AppOpenCountEntityAbstract  implements Serializable {

	public final static String countSql1 = "select to_char(open_date,'"+ DateUtil.DATE_SHORT +"') \"name\",sum(open_count) \"value\" from yy_app_open_count where 1 = 1 ";
	public final static String countSql2 = "select to_char(open_date,'"+ DateUtil.DATE_SHORT +"') \"name\",count(person_code) \"value\" from yy_app_open_count where 1 = 1 ";
	public final static String countSql3 = "select to_char(open_date,'"+ DateUtil.DATE_SHORT +"') \"openDate\",person_code \"personCode\" from yy_app_open_count where 1 = 1 ";
	public final static String countSql4 = "select distinct person_code \"personCode\" from yy_app_open_count where 1 = 1 ";

	public final static String countSql5 = "select area_name \"areaName\",sum(open_count) \"value\" from yy_app_open_count where 1 = 1 ";
	public final static String countSql6 = "select area_name \"areaName\",count(distinct person_code) \"value\" from yy_app_open_count where 1 = 1 ";

	public final static String countSql7 = "select POLICE_CODE \"policeCode\",sum(open_count) \"value\" from YY_APP_OPEN_COUNT where 1 = 1 ";//_1
	public final static String countSql8 = "select POLICE_CODE \"policeCode\",count(distinct person_code) \"value\" from YY_APP_OPEN_COUNT where 1 = 1 ";//_1

	public final static String countSql9 = "select CATEGORY_CODE \"categoryCode\",sum(open_count) \"value\" from YY_APP_OPEN_COUNT where 1 = 1 ";//_1
	public final static String countSql10 = "select CATEGORY_CODE \"categoryCode\",count(distinct person_code) \"value\" from YY_APP_OPEN_COUNT where 1 = 1 ";//_1


	public  AppOpenCountEntity(){
		super();
	}

}
