package com.company.dental.modules.sms.service;

public interface SmsTaskDispatchService {

    void enqueue(Long taskId);

    void dispatchNow(Long taskId);
}
