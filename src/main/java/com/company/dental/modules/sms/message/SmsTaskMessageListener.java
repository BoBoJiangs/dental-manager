package com.company.dental.modules.sms.message;

import com.company.dental.config.SmsRabbitMqConfig;
import com.company.dental.modules.sms.service.SmsTaskDispatchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "dental.messaging", name = "sms-enabled", havingValue = "true")
public class SmsTaskMessageListener {

    private final SmsTaskDispatchService smsTaskDispatchService;

    public SmsTaskMessageListener(SmsTaskDispatchService smsTaskDispatchService) {
        this.smsTaskDispatchService = smsTaskDispatchService;
    }

    @RabbitListener(queues = SmsRabbitMqConfig.SMS_TASK_QUEUE)
    public void onMessage(SmsTaskMessage message) {
        if (message == null || message.getTaskId() == null) {
            return;
        }
        smsTaskDispatchService.dispatchNow(message.getTaskId());
    }
}
