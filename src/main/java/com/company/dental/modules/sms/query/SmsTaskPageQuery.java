package com.company.dental.modules.sms.query;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SmsTaskPageQuery {

    private Long clinicId;
    private String bizType;
    private Long patientId;
    private String taskStatus;
    private String mobile;

    @Min(value = 1, message = "页码不能小于 1")
    private long current = 1;

    @Min(value = 1, message = "分页大小不能小于 1")
    @Max(value = 100, message = "分页大小不能超过 100")
    private long size = 10;
}
