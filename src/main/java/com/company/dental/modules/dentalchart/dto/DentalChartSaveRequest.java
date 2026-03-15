package com.company.dental.modules.dentalchart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DentalChartSaveRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    @NotNull(message = "患者不能为空")
    private Long patientId;

    @NotBlank(message = "牙位图类型不能为空")
    private String chartType;

    @NotBlank(message = "牙位图状态不能为空")
    private String chartStatus;

    @Valid
    private List<DentalChartDetailSaveRequest> details;
}
