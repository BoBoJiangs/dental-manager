package com.company.dental.integration.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AliyunSmsServiceTest {

    @Test
    void shouldReturnMockResultWhenMockEnabled() {
        AliyunSmsProperties properties = new AliyunSmsProperties();
        properties.setEnabled(false);
        properties.setMockEnabled(true);

        AliyunSmsService smsService = new AliyunSmsService(properties, new ObjectMapper());
        SmsSendResult result = smsService.send(SmsSendRequest.builder()
                .phone("13800000000")
                .templateCode("SMS_TEST")
                .signName("牙科门诊")
                .build());

        assertTrue(result.isSuccess());
        assertNotNull(result.getRequestId());
    }
}
