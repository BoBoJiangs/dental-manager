package com.company.dental.modules.billing.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargeItemVO {

    private Long id;
    private Long treatmentItemId;
    private String itemCode;
    private String itemName;
    private String itemCategory;
    private String toothPosition;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal receivableAmount;
    private Long doctorId;
    private String itemStatus;
    private String remark;
}
