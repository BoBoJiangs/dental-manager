package com.company.dental.modules.sms.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsTaskPageItemVO {

    private Long id;
    private Long clinicId;
    private String clinicName;
    private String bizType;
    private Long bizId;
    private Long patientId;
    private String patientName;
    private String mobile;
    private Long templateId;
    private String templateCode;
    private String templateName;
    private String taskStatus;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private Integer retryCount;
    private String failReason;
    private LocalDateTime createdAt;
}
