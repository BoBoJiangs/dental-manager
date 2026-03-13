package com.company.dental.integration.sms;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SmsSendRequest {

    private String phone;
    private String templateCode;
    private Map<String, String> templateParams;
}
