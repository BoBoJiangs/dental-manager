package com.company.dental.modules.patient.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientVisitRecordVO {

    private Long id;
    private String recordNo;
    private Long clinicId;
    private String clinicName;
    private Long appointmentId;
    private String appointmentNo;
    private Long doctorId;
    private String doctorName;
    private String visitType;
    private LocalDateTime visitDate;
    private String recordStatus;
    private Integer signedFlag;
    private String chiefComplaint;
    private String preliminaryDiagnosis;
    private String remark;
}
