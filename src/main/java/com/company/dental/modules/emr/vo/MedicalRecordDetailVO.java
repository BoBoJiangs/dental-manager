package com.company.dental.modules.emr.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class MedicalRecordDetailVO {

    private Long id;
    private Long orgId;
    private Long clinicId;
    private String clinicName;
    private String recordNo;
    private Long patientId;
    private String patientName;
    private String patientMobile;
    private Long appointmentId;
    private String appointmentNo;
    private Long doctorId;
    private String doctorName;
    private Long assistantId;
    private String assistantName;
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
    private List<DiagnosisRecordVO> diagnoses = Collections.emptyList();
}
