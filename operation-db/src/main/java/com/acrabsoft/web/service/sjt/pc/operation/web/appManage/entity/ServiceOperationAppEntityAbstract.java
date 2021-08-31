
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
import com.acrabsoft.web.service.sjt.pc.operation.web.util.MiddleEntity;
import io.swagger.annotations.ApiModelProperty;
import com.alibaba.fastjson.annotation.JSONField;

/**
* 服务操作应用表( ServiceOperationAppEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-6-4 17:04:58
*/
@MappedSuperclass
@Getter
@Setter
public class ServiceOperationAppEntityAbstract extends BaseEntity implements Serializable {

    @Basic
    @Column(name = "service_id")
    @ApiModelProperty(value="服务ID",name="serviceId",required=false)
    private String serviceId;

    @Basic
    @Column(name = "service_name")
    @ApiModelProperty(value="服务名称",name="serviceName",required=false)
    private String serviceName;

    @Basic
    @Column(name = "app_id")
    @ApiModelProperty(value="应用ID",name="appId",required=false)
    private String appId;

    @Basic
    @Column(name = "app_name")
    @ApiModelProperty(value="应用名称",name="appName",required=false)
    private String appName;

    @Basic
    @Column(name = "operation_type")
    @ApiModelProperty(value="操作类型",name="operationType",required=false)
    private String operationType;

    @Basic
    @Column(name = "operation_type_name")
    @ApiModelProperty(value="操作类型名称",name="operationTypeName",required=false)
    private String operationTypeName;

    @Basic
    @Column(name = "operator_id")
    @ApiModelProperty(value="操作人",name="operatorId",required=false)
    private String operatorId;

    @Basic
    @Column(name = "operator_name")
    @ApiModelProperty(value="操作人姓名",name="operatorName",required=false)
    private String operatorName;

    @Basic
    @Column(name = "operator_time")
    @ApiModelProperty(value="操作时间",name="operatorTime",required=false)
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date operatorTime;

    @Basic
    @Column(name = "operator_description")
    @ApiModelProperty(value="操作描述",name="operatorDescription",required=false)
    private String operatorDescription;
}
