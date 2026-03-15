package com.company.dental.modules.billing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("cashier_shift_record")
public class CashierShiftRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private String shiftNo;
    private Long cashierId;
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
    private LocalDateTime closedAt;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
