package com.company.dental.modules.emr.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TreatmentItemCreateRequest {

    @NotBlank(message = "项目编码不能为空")
    private String itemCode;

    @NotBlank(message = "项目名称不能为空")
    private String itemName;

    private String itemCategory;

    private String toothPosition;

    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.00", message = "单价不能小于 0")
    private BigDecimal unitPrice;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量不能小于 1")
    private Integer quantity;

    @DecimalMin(value = "0.00", message = "优惠金额不能小于 0")
    private BigDecimal discountAmount;

    private Integer sortNo;

    private String remark;
}
