package com.company.dental.modules.billing.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CashierShiftOpenRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    @NotNull(message = "收银员不能为空")
    private Long cashierId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate shiftDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    private String remark;
}
