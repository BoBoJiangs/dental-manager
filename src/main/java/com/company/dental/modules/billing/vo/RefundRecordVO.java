package com.company.dental.modules.billing.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundRecordVO {

    private Long id;
    private String refundNo;
    private Long paymentRecordId;
    private BigDecimal refundAmount;
    private String refundMethod;
    private String refundReason;
    private String refundStatus;
    private Long approvedBy;
    private LocalDateTime refundedAt;
    private Long operatorId;
    private String remark;
}
