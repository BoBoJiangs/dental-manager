package com.company.dental.modules.billing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("charge_item")
public class ChargeItemEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long chargeOrderId;
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
    private LocalDateTime createdAt;
}
