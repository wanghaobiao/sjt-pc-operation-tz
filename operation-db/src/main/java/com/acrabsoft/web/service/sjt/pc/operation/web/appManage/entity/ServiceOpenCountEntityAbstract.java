
package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.*;
import javax.persistence.OneToMany;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.alibaba.fastjson.annotation.JSONField;

/**
* 服务每日应用调用次数统计( ServiceOpenCountEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-21 14:46:49
*/
@MappedSuperclass
@Getter
@Setter
public class ServiceOpenCountEntityAbstract extends BaseEntity implements Serializable {

    @Basic
    @Column(name = "app_id")
    @ApiModelProperty(value="应用id",name="appId",required=false)
    private String appId;

    @Basic
    @Column(name = "app_name")
    @ApiModelProperty(value="应用名称",name="appName",required=false)
    private String appName;

    @Basic
    @Column(name = "service_name")
    @ApiModelProperty(value="服务名称",name="serviceName",required=false)
    private String serviceName;

    @Basic
    @Column(name = "service_id")
    @ApiModelProperty(value="服务id",name="serviceId",required=false)
    private String serviceId;

    @Basic
    @Column(name = "open_date")
    @ApiModelProperty(value="调用日期",name="openDate",required=false)
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date openDate;

    @Basic
    @Column(name = "area_name")
    @ApiModelProperty(value="地区名称",name="areaName",required=false)
    private String areaName;

    @Basic
    @Column(name = "open_count")
    @ApiModelProperty(value="正常调用次数",name="openCount",required=false)
    private Integer openCount;

    @Basic
    @Column(name = "error_open_count")
    @ApiModelProperty(value="异常调用次数",name="errorOpenCount",required=false)
    private Integer errorOpenCount;


    @Basic
    @Column(name = "load_num")
    @ApiModelProperty(value="负载编号",name="loadNum",required=false)
    private String loadNum;
}
