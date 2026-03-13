package com.company.dental.integration.sms;

public interface SmsService {

    SmsSendResult send(SmsSendRequest request);
}
