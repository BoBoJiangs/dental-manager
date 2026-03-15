package com.company.dental.modules.report.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClinicOverviewVO {

    private LocalDate statDate;
    private Long clinicId;
    private String clinicName;
    private Integer appointmentCount;
    private Integer arrivedCount;
    private Integer firstVisitCount;
    private Integer revisitCount;
    private Integer chargeOrderCount;
    private BigDecimal receivableAmount;
    private BigDecimal paidAmount;
    private BigDecimal refundAmount;
    private BigDecimal arrearsAmount;
}
