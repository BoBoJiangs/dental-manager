package com.company.dental.modules.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppointmentCancelRequest {

    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;

    private String remark;
}
