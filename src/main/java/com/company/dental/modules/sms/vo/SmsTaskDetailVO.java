package com.company.dental.modules.sms.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class SmsTaskDetailVO {

    private Long id;
    private Long orgId;
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
    private String templateParamsJson;
    private Map<String, String> templateParams = Collections.emptyMap();
    private String taskStatus;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private Integer retryCount;
    private String failReason;
    private LocalDateTime createdAt;
    private List<SmsSendRecordVO> sendRecords = Collections.emptyList();
}
