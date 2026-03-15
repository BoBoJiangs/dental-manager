package com.company.dental.modules.dentalchart.vo;

import lombok.Data;

@Data
public class DentalChartDetailVO {

    private Long id;
    private String toothNo;
    private String toothSurface;
    private String toothStatus;
    private Integer diagnosisFlag;
    private Integer treatmentFlag;
    private Long treatmentItemId;
    private String notes;
}
