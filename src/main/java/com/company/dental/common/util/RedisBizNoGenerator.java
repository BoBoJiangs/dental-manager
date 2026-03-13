package com.company.dental.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class RedisBizNoGenerator implements BizNoGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final StringRedisTemplate stringRedisTemplate;

    public RedisBizNoGenerator(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String next(String bizType) {
        String prefix = bizType.toUpperCase();
        String date = LocalDate.now().format(DATE_FORMATTER);
        String key = "biz:no:" + prefix + ":" + date;
        try {
            Long seq = stringRedisTemplate.opsForValue().increment(key);
            long value = seq == null ? 1L : seq;
            return prefix + date + String.format("%05d", value);
        } catch (Exception ex) {
            log.warn("Redis biz no generation failed, fallback to local sequence for bizType={}", prefix, ex);
            return prefix + date + String.format("%05d", ThreadLocalRandom.current().nextInt(10000, 99999));
        }
    }
}
