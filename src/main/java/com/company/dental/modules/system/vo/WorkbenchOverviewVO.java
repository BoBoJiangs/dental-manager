package com.company.dental.modules.system.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WorkbenchOverviewVO {

    private int todayAppointmentCount;
    private int todayArrivedCount;
    private int waitingPatientCount;
    private int todayChargeOrderCount;
    private BigDecimal todayReceivableAmount = BigDecimal.ZERO;
    private BigDecimal todayPaidAmount = BigDecimal.ZERO;
    private int todaySmsTaskCount;
    private int pendingSmsTaskCount;
}
