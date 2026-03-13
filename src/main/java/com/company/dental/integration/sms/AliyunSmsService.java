package com.company.dental.integration.sms;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AliyunSmsService implements SmsService {

    @Override
    public SmsSendResult send(SmsSendRequest request) {
        return SmsSendResult.builder()
                .success(true)
                .requestId(UUID.randomUUID().toString())
                .message("短信服务骨架已接通，当前为开发占位实现")
                .build();
    }
}
