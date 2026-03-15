package com.company.dental.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sms_template")
public class SmsTemplateEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private String templateCode;
    private String templateName;
    private String bizType;
    private String content;
    private String signName;
    private Integer enabledFlag;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
