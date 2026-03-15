package com.company.dental.modules.billing.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChargeOrderPageItemVO {

    private Long id;
    private Long orgId;
    private Long clinicId;
    private String clinicName;
    private String chargeNo;
    private Long patientId;
    private String patientName;
    private Long medicalRecordId;
    private String medicalRecordNo;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal receivableAmount;
    private BigDecimal paidAmount;
    private BigDecimal arrearsAmount;
    private String orderStatus;
    private LocalDateTime chargeTime;
    private String remark;
}
