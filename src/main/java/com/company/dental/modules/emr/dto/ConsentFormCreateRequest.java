package com.company.dental.modules.emr.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsentFormCreateRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    @NotNull(message = "患者不能为空")
    private Long patientId;

    private Long medicalRecordId;

    @NotNull(message = "模板不能为空")
    private Long templateId;

    private String formContent;

    private Long signerSignatureId;

    private Long doctorSignatureId;

    private String formStatus;
}
