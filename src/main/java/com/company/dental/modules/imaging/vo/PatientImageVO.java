package com.company.dental.modules.imaging.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientImageVO {

    private Long id;
    private Long orgId;
    private Long clinicId;
    private String clinicName;
    private Long patientId;
    private String patientName;
    private Long medicalRecordId;
    private String medicalRecordNo;
    private Long fileId;
    private String fileName;
    private String fileUrl;
    private String mimeType;
    private String imageType;
    private String imageGroupType;
    private LocalDateTime shotTime;
    private String toothPosition;
    private Integer sortNo;
    private String remark;
    private LocalDateTime createdAt;
}
