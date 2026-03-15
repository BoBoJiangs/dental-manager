package com.company.dental.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class RedisBizNoGenerator implements BizNoGenerator {

    private final StringRedisTemplate stringRedisTemplate;
    private final BizNoProperties bizNoProperties;
    private final Map<String, AtomicLong> localSequences = new ConcurrentHashMap<>();

    public RedisBizNoGenerator(StringRedisTemplate stringRedisTemplate, BizNoProperties bizNoProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.bizNoProperties = bizNoProperties;
    }

    @Override
    public String next(String bizType) {
        String prefix = bizNoProperties.resolvePrefix(bizType);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern(bizNoProperties.getDatePattern()));
        String key = bizNoProperties.getKeyPrefix() + ":" + prefix + ":" + date;
        try {
            Long seq = stringRedisTemplate.opsForValue().increment(key);
            long value = seq == null ? 1L : seq;
            if (value == 1L) {
                stringRedisTemplate.expire(key, bizNoProperties.getExpireDays(), TimeUnit.DAYS);
            }
            return formatBizNo(prefix, date, value);
        } catch (Exception ex) {
            log.warn("Redis biz no generation failed, fallback to local sequence for bizType={}", prefix, ex);
            AtomicLong localSequence = localSequences.computeIfAbsent(key, ignored -> new AtomicLong(0));
            return formatBizNo(prefix, date, localSequence.incrementAndGet());
        }
    }

    private String formatBizNo(String prefix, String date, long sequence) {
        return prefix + date + String.format("%0" + bizNoProperties.getSequenceLength() + "d", sequence);
    }
}
