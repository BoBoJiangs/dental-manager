package com.company.dental.modules.sms.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsSendRecordVO {

    private Long id;
    private String providerName;
    private String providerMsgId;
    private String sendStatus;
    private String responseCode;
    private String responseMsg;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
