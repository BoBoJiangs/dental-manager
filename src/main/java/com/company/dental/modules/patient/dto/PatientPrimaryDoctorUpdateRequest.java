package com.company.dental.modules.patient.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PatientPrimaryDoctorUpdateRequest {

    @NotNull(message = "主治医生列表不能为空")
    private List<Long> doctorIds;
}
