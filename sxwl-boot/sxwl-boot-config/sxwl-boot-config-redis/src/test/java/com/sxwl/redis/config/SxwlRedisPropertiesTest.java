package com.sxwl.redis.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlRedisProperties} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlRedisPropertiesTest {

    @Test
    @DisplayName("默认值应符合预期")
    void defaultValues_shouldBeCorrect() {
        SxwlRedisProperties props = new SxwlRedisProperties();
        assertEquals(10, props.getLockDefaultTimeout());
        assertEquals(60, props.getRateLimitDefaultWindow());
        assertEquals(3, props.getRepeatSubmitInterval());
    }

    @Test
    @DisplayName("setter 应正确修改字段值")
    void setter_shouldWork() {
        SxwlRedisProperties props = new SxwlRedisProperties();
        props.setLockDefaultTimeout(30);
        props.setRateLimitDefaultWindow(120);
        props.setRepeatSubmitInterval(5);

        assertEquals(30, props.getLockDefaultTimeout());
        assertEquals(120, props.getRateLimitDefaultWindow());
        assertEquals(5, props.getRepeatSubmitInterval());
    }
}
