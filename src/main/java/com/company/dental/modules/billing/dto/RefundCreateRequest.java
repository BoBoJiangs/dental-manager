package com.company.dental.modules.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundCreateRequest {

    private Long paymentRecordId;

    @NotNull(message = "退款金额不能为空")
    @DecimalMin(value = "0.01", message = "退款金额必须大于 0")
    private BigDecimal refundAmount;

    @NotBlank(message = "退款方式不能为空")
    private String refundMethod;

    @NotBlank(message = "退款原因不能为空")
    private String refundReason;

    private String remark;
}
