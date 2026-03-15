package com.company.dental.modules.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SmsTaskCreateRequest {

    private Long clinicId;

    @NotBlank(message = "业务类型不能为空")
    private String bizType;

    private Long bizId;

    private Long patientId;

    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @NotNull(message = "短信模板不能为空")
    private Long templateId;

    private Map<String, String> templateParams;

    private LocalDateTime scheduledAt;
}
