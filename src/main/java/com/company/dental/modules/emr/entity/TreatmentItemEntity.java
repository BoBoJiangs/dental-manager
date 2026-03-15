package com.company.dental.modules.emr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("treatment_item")
public class TreatmentItemEntity {

    @TableId(type = IdType.AUTO)
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
