package com.company.dental.modules.emr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.company.dental.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("treatment_plan")
public class TreatmentPlanEntity extends BaseEntity {

    private Long orgId;
    private Long clinicId;
    private String planNo;
    private Long patientId;
    private Long medicalRecordId;
    private Long doctorId;
    private String planName;
    private BigDecimal totalAmount;
    private String planStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String remark;
}
