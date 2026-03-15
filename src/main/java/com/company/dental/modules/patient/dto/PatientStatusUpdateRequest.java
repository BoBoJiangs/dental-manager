package com.company.dental.modules.patient.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PatientStatusUpdateRequest {

    @NotNull(message = "患者状态不能为空")
    private Integer patientStatus;

    private String remark;
}
