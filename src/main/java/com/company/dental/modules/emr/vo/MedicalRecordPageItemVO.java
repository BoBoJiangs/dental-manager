package com.company.dental.modules.emr.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalRecordPageItemVO {

    private Long id;
    private Long orgId;
    private Long clinicId;
    private String clinicName;
    private String recordNo;
    private Long patientId;
    private String patientName;
    private Long appointmentId;
    private String appointmentNo;
    private Long doctorId;
    private String doctorName;
    private String visitType;
    private LocalDateTime visitDate;
    private String recordStatus;
    private Integer signedFlag;
    private LocalDateTime signedAt;
    private String chiefComplaint;
    private String preliminaryDiagnosis;
    private String remark;
}
