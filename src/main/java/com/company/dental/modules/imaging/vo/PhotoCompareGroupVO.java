package com.company.dental.modules.imaging.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PhotoCompareGroupVO {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long medicalRecordId;
    private String medicalRecordNo;
    private String groupName;
    private Long preImageId;
    private String preImageUrl;
    private Long postImageId;
    private String postImageUrl;
    private String compareDesc;
    private Long createdBy;
    private LocalDateTime createdAt;
}
