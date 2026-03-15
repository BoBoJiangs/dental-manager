package com.company.dental.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sms_task")
public class SmsTaskEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orgId;
    private Long clinicId;
    private String bizType;
    private Long bizId;
    private Long patientId;
    private String mobile;
    private Long templateId;
    private String templateParams;
    private String taskStatus;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private Integer retryCount;
    private String failReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
