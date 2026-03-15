package com.company.dental.modules.billing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("receivable_record")
public class ReceivableRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private Long chargeOrderId;
    private Long patientId;
    private BigDecimal receivableAmount;
    private BigDecimal receivedAmount;
    private BigDecimal outstandingAmount;
    private LocalDate dueDate;
    private String receivableStatus;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
