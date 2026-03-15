package com.company.dental.modules.emr.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsentFormVO {

    private Long id;
    private Long orgId;
    private Long clinicId;
    private String clinicName;
    private Long patientId;
    private String patientName;
    private Long medicalRecordId;
    private String medicalRecordNo;
    private String formCode;
    private String formName;
    private String formContent;
    private Long signerSignatureId;
    private Long doctorSignatureId;
    private String formStatus;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;
}
