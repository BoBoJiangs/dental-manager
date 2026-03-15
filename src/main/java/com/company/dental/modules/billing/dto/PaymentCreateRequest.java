package com.company.dental.modules.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCreateRequest {

    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于 0")
    private BigDecimal amount;

    private String transactionNo;

    private String payerName;

    private Long cashierId;

    private String remark;
}
