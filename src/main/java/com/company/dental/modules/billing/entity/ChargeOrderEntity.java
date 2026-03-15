package com.company.dental.modules.billing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.company.dental.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("charge_order")
public class ChargeOrderEntity extends BaseEntity {

    private Long orgId;
    private Long clinicId;
    private String chargeNo;
    private Long patientId;
    private Long medicalRecordId;
    private Long treatmentPlanId;
    private Long cashierId;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal receivableAmount;
    private BigDecimal paidAmount;
    private BigDecimal arrearsAmount;
    private String orderStatus;
    private LocalDateTime chargeTime;
    private LocalDateTime settledAt;
    private String remark;
}
