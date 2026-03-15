package com.company.dental.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sms_send_record")
public class SmsSendRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long smsTaskId;
    private String providerName;
    private String providerMsgId;
    private String sendStatus;
    private String responseCode;
    private String responseMsg;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
