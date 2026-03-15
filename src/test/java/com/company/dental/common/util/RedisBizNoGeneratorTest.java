package com.company.dental.common.util;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisBizNoGeneratorTest {

    @Test
    void shouldGenerateBizNoWithRedisSequence() {
        StringRedisTemplate redisTemplate = Mockito.mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(redisTemplate.expire(anyString(), anyLong(), any())).thenReturn(true);

        BizNoProperties properties = new BizNoProperties();
        properties.getPrefixes().put("PATIENT", "P");
        RedisBizNoGenerator generator = new RedisBizNoGenerator(redisTemplate, properties);

        String bizNo = generator.next("PATIENT");
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern(properties.getDatePattern()));
        assertTrue(bizNo.startsWith("P" + date));
        assertTrue(bizNo.endsWith("00001"));
        verify(redisTemplate, times(1)).expire(anyString(), anyLong(), any());
    }

    @Test
    void shouldFallbackToLocalSequenceWhenRedisFails() {
        StringRedisTemplate redisTemplate = Mockito.mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("redis down"));

        BizNoProperties properties = new BizNoProperties();
        RedisBizNoGenerator generator = new RedisBizNoGenerator(redisTemplate, properties);

        String bizNo = generator.next("A");
        assertTrue(bizNo.endsWith("00001"));
    }
}
