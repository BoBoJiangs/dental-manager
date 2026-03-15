package com.company.dental.modules.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import com.company.dental.config.SmsRabbitMqConfig;
import com.company.dental.integration.sms.SmsSendRequest;
import com.company.dental.integration.sms.SmsSendResult;
import com.company.dental.integration.sms.SmsService;
import com.company.dental.modules.sms.entity.SmsSendRecordEntity;
import com.company.dental.modules.sms.entity.SmsTaskEntity;
import com.company.dental.modules.sms.entity.SmsTemplateEntity;
import com.company.dental.modules.sms.mapper.SmsSendRecordMapper;
import com.company.dental.modules.sms.mapper.SmsTaskMapper;
import com.company.dental.modules.sms.mapper.SmsTemplateMapper;
import com.company.dental.modules.sms.message.SmsTaskMessage;
import com.company.dental.modules.sms.service.SmsTaskDispatchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Service
public class SmsTaskDispatchServiceImpl implements SmsTaskDispatchService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_SENDING = "SENDING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAIL = "FAIL";

    private final RabbitTemplate rabbitTemplate;
    private final SmsTaskMapper smsTaskMapper;
    private final SmsTemplateMapper smsTemplateMapper;
    private final SmsSendRecordMapper smsSendRecordMapper;
    private final SmsService smsService;
    private final ObjectMapper objectMapper;
    private final boolean smsMessagingEnabled;

    public SmsTaskDispatchServiceImpl(RabbitTemplate rabbitTemplate,
                                      SmsTaskMapper smsTaskMapper,
                                      SmsTemplateMapper smsTemplateMapper,
                                      SmsSendRecordMapper smsSendRecordMapper,
                                      SmsService smsService,
                                      ObjectMapper objectMapper,
                                      @Value("${dental.messaging.sms-enabled:false}") boolean smsMessagingEnabled) {
        this.rabbitTemplate = rabbitTemplate;
        this.smsTaskMapper = smsTaskMapper;
        this.smsTemplateMapper = smsTemplateMapper;
        this.smsSendRecordMapper = smsSendRecordMapper;
        this.smsService = smsService;
        this.objectMapper = objectMapper;
        this.smsMessagingEnabled = smsMessagingEnabled;
    }

    @Override
    public void enqueue(Long taskId) {
        if (!smsMessagingEnabled) {
            dispatchNow(taskId);
            return;
        }
        try {
            rabbitTemplate.convertAndSend(
                    SmsRabbitMqConfig.SMS_TASK_EXCHANGE,
                    SmsRabbitMqConfig.SMS_TASK_ROUTING_KEY,
                    new SmsTaskMessage(taskId)
            );
        } catch (Exception ex) {
            dispatchNow(taskId);
        }
    }

    @Override
    @Transactional
    public void dispatchNow(Long taskId) {
        SmsTaskEntity task = smsTaskMapper.selectById(taskId);
        if (task == null || !STATUS_PENDING.equals(task.getTaskStatus())) {
            return;
        }
        SmsTemplateEntity template = smsTemplateMapper.selectById(task.getTemplateId());
        if (template == null) {
            task.setTaskStatus(STATUS_FAIL);
            task.setFailReason("短信模板不存在");
            task.setRetryCount(task.getRetryCount() == null ? 1 : task.getRetryCount() + 1);
            task.setSentAt(LocalDateTime.now());
            smsTaskMapper.updateById(task);
            return;
        }

        task.setTaskStatus(STATUS_SENDING);
        smsTaskMapper.updateById(task);

        SmsSendResult result;
        try {
            result = smsService.send(SmsSendRequest.builder()
                    .phone(task.getMobile())
                    .signName(template.getSignName())
                    .templateCode(template.getTemplateCode())
                    .templateParams(parseParams(task.getTemplateParams()))
                    .build());
        } catch (Exception ex) {
            result = SmsSendResult.builder()
                    .success(false)
                    .requestId(null)
                    .message(ex.getMessage())
                    .build();
        }

        LocalDateTime now = LocalDateTime.now();
        task.setRetryCount(task.getRetryCount() == null ? 1 : task.getRetryCount() + 1);
        task.setSentAt(now);
        task.setTaskStatus(result.isSuccess() ? STATUS_SUCCESS : STATUS_FAIL);
        task.setFailReason(result.isSuccess() ? null : result.getMessage());
        smsTaskMapper.updateById(task);

        SmsSendRecordEntity record = new SmsSendRecordEntity();
        record.setSmsTaskId(task.getId());
        record.setProviderName("ALIYUN");
        record.setProviderMsgId(result.getRequestId());
        record.setSendStatus(result.isSuccess() ? "SUCCESS" : "FAIL");
        record.setResponseCode(result.isSuccess() ? "OK" : "FAIL");
        record.setResponseMsg(result.getMessage());
        record.setSentAt(now);
        smsSendRecordMapper.insert(record);
    }

    private Map<String, String> parseParams(String paramsJson) {
        if (paramsJson == null || paramsJson.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(paramsJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "短信任务参数解析失败");
        }
    }
}
