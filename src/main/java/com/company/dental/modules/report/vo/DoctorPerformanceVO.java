package com.company.dental.modules.report.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DoctorPerformanceVO {

    private LocalDate statDate;
    private Long clinicId;
    private String clinicName;
    private Long doctorId;
    private String doctorName;
    private Integer appointmentCount;
    private Integer arrivedCount;
    private Integer firstVisitCount;
    private Integer revisitCount;
    private Integer treatmentCount;
    private BigDecimal performanceAmount;
}
