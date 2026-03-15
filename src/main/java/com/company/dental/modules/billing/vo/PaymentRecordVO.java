package com.company.dental.modules.billing.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRecordVO {

    private Long id;
    private String paymentNo;
    private String paymentMethod;
    private BigDecimal amount;
    private String transactionNo;
    private String payerName;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private Long cashierId;
    private String remark;
}
