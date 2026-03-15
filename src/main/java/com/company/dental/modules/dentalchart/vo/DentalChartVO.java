package com.company.dental.modules.dentalchart.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class DentalChartVO {

    private Long id;
    private Long orgId;
    private Long clinicId;
    private String clinicName;
    private Long patientId;
    private String patientName;
    private Long medicalRecordId;
    private String medicalRecordNo;
    private String chartType;
    private Integer chartVersion;
    private String chartStatus;
    private LocalDateTime updatedAt;
    private List<DentalChartDetailVO> details = Collections.emptyList();
}
