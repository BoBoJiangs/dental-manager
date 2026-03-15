package com.company.dental.modules.emr.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ElectronicSignatureVO {

    private Long id;
    private Long clinicId;
    private String clinicName;
    private Long patientId;
    private String patientName;
    private Long medicalRecordId;
    private String medicalRecordNo;
    private String signerName;
    private String signerType;
    private String relationToPatient;
    private Long signatureFileId;
    private String signatureFileName;
    private String signatureFileUrl;
    private LocalDateTime signedAt;
    private String ipAddress;
    private String deviceInfo;
    private String remark;
    private LocalDateTime createdAt;
}
