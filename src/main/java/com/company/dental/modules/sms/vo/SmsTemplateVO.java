package com.company.dental.modules.sms.vo;

import lombok.Data;

@Data
public class SmsTemplateVO {

    private Long id;
    private Long clinicId;
    private String templateCode;
    private String templateName;
    private String bizType;
    private String content;
    private String signName;
    private Integer enabledFlag;
    private String remark;
}
