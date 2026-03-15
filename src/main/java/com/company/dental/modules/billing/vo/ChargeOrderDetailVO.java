package com.company.dental.modules.billing.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class ChargeOrderDetailVO {

    private Long id;
    private Long orgId;
    private Long clinicId;
    private String clinicName;
    private String chargeNo;
    private Long patientId;
    private String patientName;
    private String patientMobile;
    private Long medicalRecordId;
    private String medicalRecordNo;
    private Long treatmentPlanId;
    private Long cashierId;
    private String cashierName;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal receivableAmount;
    private BigDecimal paidAmount;
    private BigDecimal arrearsAmount;
    private String orderStatus;
    private LocalDateTime chargeTime;
    private LocalDateTime settledAt;
    private String remark;
    private List<ChargeItemVO> items = Collections.emptyList();
    private List<PaymentRecordVO> payments = Collections.emptyList();
    private List<RefundRecordVO> refunds = Collections.emptyList();
}
