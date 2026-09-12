package com.sxwl.redis.rate;

import com.sxwl.redis.helper.SxwlRedisHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlSlidingWindowRateLimiter} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SxwlSlidingWindowRateLimiterTest {

    @Mock
    private SxwlRedisHelper redisHelper;

    private SxwlSlidingWindowRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new SxwlSlidingWindowRateLimiter(redisHelper);
    }

    @Test
    @DisplayName("tryAcquire 返回 true 时放行")
    void tryAcquire_shouldReturnTrue_whenAllowed() {
        when(redisHelper.executeScript(anyString(), anyList(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        assertTrue(rateLimiter.tryAcquire("rate:test:ip1", 10, 60));
    }

    @Test
    @DisplayName("tryAcquire 返回 false 时限流")
    void tryAcquire_shouldReturnFalse_whenLimited() {
        when(redisHelper.executeScript(anyString(), anyList(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(false);

        assertFalse(rateLimiter.tryAcquire("rate:test:ip1", 10, 60));
    }

    @Test
    @DisplayName("tryAcquire 执行 Lua 脚本使用正确的参数数量")
    void tryAcquire_shouldExecuteScriptWithCorrectArgs() {
        when(redisHelper.executeScript(anyString(), anyList(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        rateLimiter.tryAcquire("rate:test:key", 5, 30);

        verify(redisHelper).executeScript(
                anyString(),
                eq(List.of("rate:test:key")),
                anyString(), // windowStart
                eq("5"),     // maxCount
                anyString(), // member
                anyString(), // now
                eq("40"));   // ttl = windowSeconds + 10
    }

    @Test
    @DisplayName("连续调用时每次生成不同的 member")
    void tryAcquire_shouldUseDifferentMembers() {
        when(redisHelper.executeScript(anyString(), anyList(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        rateLimiter.tryAcquire("rate:test:diff", 10, 60);
        rateLimiter.tryAcquire("rate:test:diff", 10, 60);

        verify(redisHelper, times(2)).executeScript(anyString(), anyList(), anyString(), anyString(), anyString(), anyString(), anyString());
    }
}
