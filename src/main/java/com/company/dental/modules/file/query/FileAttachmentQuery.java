package com.company.dental.modules.file.query;

import lombok.Data;

@Data
public class FileAttachmentQuery {

    private Long clinicId;
    private String bizType;
    private Long bizId;
    private Long patientId;
    private Long medicalRecordId;
}
