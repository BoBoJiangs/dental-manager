package com.company.dental.modules.patient.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.company.dental.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("patient")
public class PatientEntity extends BaseEntity {

    private Long orgId;
    private String patientCode;
    private String patientName;
    private Integer gender;
    private LocalDate birthday;
    private String mobile;
    private String idNo;
    private String wechatOpenid;
    private String wechatUnionid;
    private String sourceCode;
    private Long firstClinicId;
    private LocalDateTime firstVisitAt;
    private LocalDateTime latestVisitAt;
    private Integer memberStatus;
    private Integer patientStatus;
    private String remark;
}
