package com.company.dental.modules.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QueueStatusUpdateRequest {

    @NotBlank(message = "候诊状态不能为空")
    private String queueStatus;

    private String remark;
}
