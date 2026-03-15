package com.company.dental.modules.emr.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TreatmentPlanStatusUpdateRequest {

    @NotBlank(message = "计划状态不能为空")
    private String planStatus;

    private String remark;
}
