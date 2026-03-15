package com.company.dental.modules.emr.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TreatmentPlanPageItemVO {

    private Long id;
    private Long clinicId;
    private String clinicName;
    private String planNo;
    private Long patientId;
    private String patientName;
    private String patientMobile;
    private Long medicalRecordId;
    private String medicalRecordNo;
    private Long doctorId;
    private String doctorName;
    private String planName;
    private BigDecimal totalAmount;
    private String planStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String remark;
    private LocalDateTime createdAt;
}
