package com.company.dental.integration.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "dental.sms.aliyun")
public class AliyunSmsProperties {

    private boolean enabled = false;
    private boolean mockEnabled = true;
    private String endpoint = "https://dysmsapi.aliyuncs.com";
    private String accessKeyId;
    private String accessKeySecret;
    private String regionId = "cn-hangzhou";
    private String version = "2017-05-25";
    private String action = "SendSms";
    private String defaultSignName;
}
