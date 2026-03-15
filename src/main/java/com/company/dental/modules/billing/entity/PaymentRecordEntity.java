package com.company.dental.modules.billing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_record")
public class PaymentRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private Long chargeOrderId;
    private String paymentNo;
    private String paymentMethod;
    private BigDecimal amount;
    private String transactionNo;
    private String payerName;
    private String paymentStatus;
    private LocalDateTime paidAt;
    private Long cashierId;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
