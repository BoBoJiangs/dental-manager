package com.company.dental.modules.imaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientImageCreateRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    @NotNull(message = "患者不能为空")
    private Long patientId;

    private Long medicalRecordId;

    @NotNull(message = "文件不能为空")
    private Long fileId;

    @NotBlank(message = "影像类型不能为空")
    private String imageType;

    private String imageGroupType;

    private LocalDateTime shotTime;

    private String toothPosition;

    private Integer sortNo;

    private String remark;
}
