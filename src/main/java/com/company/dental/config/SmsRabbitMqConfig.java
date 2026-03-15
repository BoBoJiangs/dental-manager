package com.company.dental.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsRabbitMqConfig {

    public static final String SMS_TASK_EXCHANGE = "dental.sms.exchange";
    public static final String SMS_TASK_QUEUE = "dental.sms.task.queue";
    public static final String SMS_TASK_ROUTING_KEY = "dental.sms.task.dispatch";

    @Bean
    public DirectExchange smsTaskExchange() {
        return new DirectExchange(SMS_TASK_EXCHANGE, true, false);
    }

    @Bean
    public Queue smsTaskQueue() {
        return new Queue(SMS_TASK_QUEUE, true);
    }

    @Bean
    public Binding smsTaskBinding(Queue smsTaskQueue, DirectExchange smsTaskExchange) {
        return BindingBuilder.bind(smsTaskQueue).to(smsTaskExchange).with(SMS_TASK_ROUTING_KEY);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
