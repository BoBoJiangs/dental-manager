package com.company.dental.modules.billing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("refund_record")
public class RefundRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private String refundNo;
    private Long chargeOrderId;
    private Long paymentRecordId;
    private Long patientId;
    private BigDecimal refundAmount;
    private String refundMethod;
    private String refundReason;
    private String refundStatus;
    private Long approvedBy;
    private LocalDateTime refundedAt;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
