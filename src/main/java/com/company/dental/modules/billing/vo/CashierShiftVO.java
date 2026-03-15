package com.company.dental.modules.billing.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CashierShiftVO {

    private Long id;
    private Long clinicId;
    private String clinicName;
    private String shiftNo;
    private Long cashierId;
    private String cashierName;
    private LocalDate shiftDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal cashAmount;
    private BigDecimal wechatAmount;
    private BigDecimal alipayAmount;
    private BigDecimal cardAmount;
    private BigDecimal balanceAmount;
    private BigDecimal refundAmount;
    private BigDecimal netAmount;
    private String shiftStatus;
    private Long closedBy;
    private String closedByName;
    private LocalDateTime closedAt;
    private String remark;
}
