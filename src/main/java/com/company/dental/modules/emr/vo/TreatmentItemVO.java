package com.company.dental.modules.emr.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TreatmentItemVO {

    private Long id;
    private Long treatmentPlanId;
    private Long medicalRecordId;
    private String itemCode;
    private String itemName;
    private String itemCategory;
    private String toothPosition;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal discountAmount;
    private BigDecimal receivableAmount;
    private Integer executedFlag;
    private LocalDateTime executedAt;
    private String itemStatus;
    private Integer sortNo;
    private String remark;
}
