package com.company.dental.common.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "dental.biz-no")
public class BizNoProperties {

    private String keyPrefix = "biz:no";
    private String datePattern = "yyyyMMdd";
    private int sequenceLength = 5;
    private long expireDays = 2;
    private Map<String, String> prefixes = new LinkedHashMap<>();

    public String resolvePrefix(String bizType) {
        if (bizType == null || bizType.isBlank()) {
            return "BIZ";
        }
        String normalized = bizType.trim().toUpperCase();
        return prefixes.getOrDefault(normalized, normalized);
    }
}
