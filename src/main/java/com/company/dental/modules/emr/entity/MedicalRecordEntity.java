package com.company.dental.modules.emr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.company.dental.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("medical_record")
public class MedicalRecordEntity extends BaseEntity {

    private Long orgId;
    private Long clinicId;
    private String recordNo;
    private Long patientId;
    private Long appointmentId;
    private Long doctorId;
    private Long assistantId;
    private String visitType;
    private LocalDateTime visitDate;
    private String chiefComplaint;
    private String presentIllness;
    private String oralExamination;
    private String auxiliaryExamination;
    private String preliminaryDiagnosis;
    private String treatmentAdvice;
    private String doctorAdvice;
    private String revisitAdvice;
    private LocalDate nextVisitDate;
    private String recordStatus;
    private Integer signedFlag;
    private LocalDateTime signedAt;
    private Integer printCount;
    private String remark;
}
