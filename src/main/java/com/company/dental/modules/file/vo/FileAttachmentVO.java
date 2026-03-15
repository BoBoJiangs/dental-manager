package com.company.dental.modules.file.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileAttachmentVO {

    private Long id;
    private Long clinicId;
    private String clinicName;
    private String bizType;
    private Long bizId;
    private Long patientId;
    private String patientName;
    private Long medicalRecordId;
    private String medicalRecordNo;
    private String fileName;
    private String fileExt;
    private String mimeType;
    private Long fileSize;
    private String storageType;
    private String bucketName;
    private String objectKey;
    private String fileUrl;
    private String uploadSource;
    private Long uploaderId;
    private Integer fileStatus;
    private LocalDateTime createdAt;
}
