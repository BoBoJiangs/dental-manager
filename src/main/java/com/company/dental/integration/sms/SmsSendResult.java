package com.company.dental.integration.sms;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsSendResult {

    private boolean success;
    private String requestId;
    private String message;
}
