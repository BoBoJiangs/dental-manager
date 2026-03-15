package com.company.dental.modules.billing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChargeOrderCreateRequest {

    @NotNull(message = "门诊不能为空")
    private Long clinicId;

    @NotNull(message = "患者不能为空")
    private Long patientId;

    private Long medicalRecordId;

    private Long treatmentPlanId;

    private Long cashierId;

    private LocalDateTime chargeTime;

    private String remark;

    @Valid
    @NotNull(message = "收费明细不能为空")
    private List<ChargeItemCreateRequest> items;
}
