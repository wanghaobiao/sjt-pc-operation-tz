
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
* 服务信息( ServiceInfoEntityAbstract )实体抽象类
* @author wanghb
* @since 2021-4-26 10:56:24
*/
@MappedSuperclass
@Getter
@Setter
public class ServiceInfoEntityAbstract extends BaseEntity implements Serializable {

    public final static String tableName = "t_service_info";
    public final static String insertSql = "INSERT INTO t_service_info (app_id,approval_state,approval_time,approver_identifier,available_platform,call_frequency,cmc_id,collect_time,dep_code,description,formal_table_id,interface_address,interface_type,open_path,operator_type,owner_identifier,platform,power_type, regionalism_code,release_range,report_type,service_area,service_center,service_id,service_level,service_name,service_type,service_version,status,time_out,update_status,visibility,api_file_ids,id,deleted,create_user,create_time,update_user,update_time) VALUES (:appId,:approvalState,:approvalTime,:approverIdentifier,:availablePlatform,:callFrequency,:cmcId,:collectTime,:depCode,:description,:formalTableId,:interfaceAddress,:interfaceType,:openPath,:operatorType,:ownerIdentifier,:platform,:powerType,: regionalismCode,:releaseRange,:reportType,:serviceArea,:serviceCenter,:serviceId,:serviceLevel,:serviceName,:serviceType,:serviceVersion,:status,:timeOut,:updateStatus,:visibility,:apiFileIds,:id,:deleted,:createUser,:createTime,:updateUser,:updateTime)";
    public final static String updateSql = "UPDATE t_service_info SET app_id = :appId,approval_state = :approvalState,approval_time = :approvalTime,approver_identifier = :approverIdentifier,available_platform = :availablePlatform,call_frequency = :callFrequency,cmc_id = :cmcId,collect_time = :collectTime,dep_code = :depCode,description = :description,formal_table_id = :formalTableId,interface_address = :interfaceAddress,interface_type = :interfaceType,open_path = :openPath,operator_type = :operatorType,owner_identifier = :ownerIdentifier,platform = :platform,power_type = :powerType, regionalism_code = : regionalismCode,release_range = :releaseRange,report_type = :reportType,service_area = :serviceArea,service_center = :serviceCenter,service_id = :serviceId,service_level = :serviceLevel,service_name = :serviceName,service_type = :serviceType,service_version = :serviceVersion,status = :status,time_out = :timeOut,update_status = :updateStatus,visibility = :visibility,api_file_ids = :apiFileIds,deleted = :deleted,create_user = :createUser,create_time= :createTime,update_user= :updateUser,update_time= :updateTime WHERE id = :id";
    public final static String deleteSql = "DELETE from t_service_info WHERE 1 = 1 ";
    public final static String countSql1 = "select COUNT(1) from t_service_info where 1 = 1 ";

    public final static String selectSql1 = "select * from t_service_info where 1 = 1 ";
    public final static String selectSql2 = "select A.* from t_service_info A ,t_service_authorization_info B,t_app_info C  where A.deleted = 0 AND B.deleted = 0 AND C.deleted = 0  AND B.service_id = A.service_id  AND A.app_id = C.app_id ";
    @Basic
    @Column(name = "obj_id")
    @ApiModelProperty(value="主键id",name="objId",required=false)
    private String objId;

    @Basic
    @Column(name = "app_id")
    @ApiModelProperty(value="应用id",name="appId",required=false)
    private String appId;

    @Basic
    @Column(name = "approval_state")
    @ApiModelProperty(value="批准状态",name="approvalState",required=false)
    private String approvalState;

    @Basic
    @Column(name = "approval_time")
    @ApiModelProperty(value="批准时间",name="approvalTime",required=false)
    @JSONField(format = "yyyy-MM-dd HH:mm")
    private Date approvalTime;

    @Basic
    @Column(name = "approver_identifier")
    @ApiModelProperty(value="批准者标识符",name="approverIdentifier",required=false)
    private String approverIdentifier;

    @Basic
    @Column(name = "available_platform")
    @ApiModelProperty(value="可用网络区域平台",name="availablePlatform",required=false)
    private String availablePlatform;

    @Basic
    @Column(name = "call_frequency")
    @ApiModelProperty(value="呼叫频率",name="callFrequency",required=false)
    private String callFrequency;

    @Basic
    @Column(name = "cmc_id")
    @ApiModelProperty(value="cmcId",name="cmcId",required=false)
    private String cmcId;

    @Basic
    @Column(name = "collect_time")
    @ApiModelProperty(value="收集时间",name="collectTime",required=false)
    private String collectTime;

    @Basic
    @Column(name = "dep_code")
    @ApiModelProperty(value="部门代码",name="depCode",required=false)
    private String depCode;

    @Basic
    @Column(name = "description")
    @ApiModelProperty(value="说明",name="description",required=false)
    private String description;

    @Basic
    @Column(name = "formal_table_id")
    @ApiModelProperty(value="表ID",name="formalTableId",required=false)
    private String formalTableId;

    @Basic
    @Column(name = "interface_address")
    @ApiModelProperty(value="接口地址",name="interfaceAddress",required=false)
    private String interfaceAddress;

    @Basic
    @Column(name = "interface_type")
    @ApiModelProperty(value="接口类型",name="interfaceType",required=false)
    private String interfaceType;

    @Basic
    @Column(name = "open_path")
    @ApiModelProperty(value="开放路径",name="openPath",required=false)
    private String openPath;

    @Basic
    @Column(name = "operator_type")
    @ApiModelProperty(value="操作类型",name="operatorType",required=false)
    private String operatorType;

    @Basic
    @Column(name = "owner_identifier")
    @ApiModelProperty(value="所有者标识",name="ownerIdentifier",required=false)
    private String ownerIdentifier;

    @Basic
    @Column(name = "platform")
    @ApiModelProperty(value="站台",name="platform",required=false)
    private String platform;


    @Basic
    @Column(name = "power_type")
    @ApiModelProperty(value="电源类型",name="powerType",required=false)
    private String powerType;

    @Basic
    @Column(name = " regionalism_code")
    @ApiModelProperty(value="区域主义法典",name=" regionalismCode",required=false)
    private String  regionalismCode;

    @Basic
    @Column(name = "release_range")
    @ApiModelProperty(value="释放范围",name="releaseRange",required=false)
    private String releaseRange;

    @Basic
    @Column(name = "report_type")
    @ApiModelProperty(value="报表类型",name="reportType",required=false)
    private String reportType;

    @Basic
    @Column(name = "service_area")
    @ApiModelProperty(value="服务区",name="serviceArea",required=false)
    private String serviceArea;

    @Basic
    @Column(name = "service_center")
    @ApiModelProperty(value="服务中心",name="serviceCenter",required=false)
    private String serviceCenter;

    @Basic
    @Column(name = "service_id")
    @ApiModelProperty(value="服务ID",name="serviceId",required=false)
    private String serviceId;

    @Basic
    @Column(name = "service_level")
    @ApiModelProperty(value="服务级别",name="serviceLevel",required=false)
    private String serviceLevel;

    @Basic
    @Column(name = "service_name")
    @ApiModelProperty(value="服务名称",name="serviceName",required=false)
    private String serviceName;

    @Basic
    @Column(name = "service_type")
    @ApiModelProperty(value="服务类型",name="serviceType",required=false)
    private String serviceType;

    @Basic
    @Column(name = "service_version")
    @ApiModelProperty(value="服务版本",name="serviceVersion",required=false)
    private String serviceVersion;


    @Basic
    @Column(name = "status")
    @ApiModelProperty(value="状态",name="status",required=false)
    private String status;

    @Basic
    @Column(name = "time_out")
    @ApiModelProperty(value="超时",name="timeOut",required=false)
    private Integer timeOut;

    @Basic
    @Column(name = "update_status")
    @ApiModelProperty(value="更新状态",name="updateStatus",required=false)
    private String updateStatus;

    @Basic
    @Column(name = "visibility")
    @ApiModelProperty(value="能见度",name="visibility",required=false)
    private String visibility;

    @Basic
    @Column(name = "api_file_ids")
    @ApiModelProperty(value="API文件ID",name="apiFileIds",required=false)
    private String apiFileIds;
}
