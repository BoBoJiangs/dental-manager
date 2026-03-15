package com.company.dental.modules.dentalchart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DentalChartDetailSaveRequest {

    @NotBlank(message = "牙位编号不能为空")
    private String toothNo;

    private String toothSurface;

    @NotBlank(message = "牙位状态不能为空")
    private String toothStatus;

    private Integer diagnosisFlag;

    private Integer treatmentFlag;

    private Long treatmentItemId;

    private String notes;
}
