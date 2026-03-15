package com.company.dental.modules.emr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ElectronicSignatureCreateRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    private Long patientId;

    private Long medicalRecordId;

    @NotBlank(message = "签署人不能为空")
    private String signerName;

    @NotBlank(message = "签署人类型不能为空")
    private String signerType;

    private String relationToPatient;

    @NotNull(message = "签名文件不能为空")
    private Long signatureFileId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime signedAt;

    private String ipAddress;

    private String deviceInfo;

    private String remark;
}
