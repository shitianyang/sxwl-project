package com.sxwl.security.captcha;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.redis.helper.SxwlRedisHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlCaptchaValidator} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SxwlCaptchaValidatorTest {

    @Mock
    private SxwlRedisHelper redisHelper;

    private SxwlCaptchaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SxwlCaptchaValidator(redisHelper);
    }

    // ==================== 图形验证码 ====================

    @Test
    @DisplayName("图形验证码校验通过")
    void validateImageCaptcha_shouldPass_whenCodeMatches() {
        String uuid = "uuid-123";
        String captchaCode = "A1B2";
        // Redis 返回验证码（不区分大小写）
        when(redisHelper.get(anyString())).thenReturn(Optional.of("a1b2"));
        doReturn(true).when(redisHelper).delete(anyString());

        assertDoesNotThrow(() -> validator.validateImageCaptcha(uuid, captchaCode));
        verify(redisHelper).delete(anyString());
    }

    @Test
    @DisplayName("图形验证码不匹配应抛出异常")
    void validateImageCaptcha_shouldThrow_whenCodeNotMatch() {
        String uuid = "uuid-123";
        when(redisHelper.get(anyString())).thenReturn(Optional.of("A1B2"));

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validateImageCaptcha(uuid, "WRONG"));
        assertEquals("验证码错误", ex.getMessage());
        verify(redisHelper, never()).delete(anyString());
    }

    @Test
    @DisplayName("图形验证码已过期应抛出异常")
    void validateImageCaptcha_shouldThrow_whenExpired() {
        when(redisHelper.get(anyString())).thenReturn(Optional.empty());

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validateImageCaptcha("uuid-123", "A1B2"));
        assertEquals("验证码已过期，请刷新", ex.getMessage());
    }

    @Test
    @DisplayName("图形验证码 uuid 或 code 为 null 应抛出异常")
    void validateImageCaptcha_shouldThrow_whenInputNull() {
        assertThrows(SxwlBusinessException.class,
                () -> validator.validateImageCaptcha(null, "code"));
        assertThrows(SxwlBusinessException.class,
                () -> validator.validateImageCaptcha("uuid", null));
    }

    // ==================== 短信验证码 ====================

    @Test
    @DisplayName("短信验证码校验通过")
    void validateSmsCaptcha_shouldPass_whenCodeMatches() {
        when(redisHelper.get(anyString())).thenReturn(Optional.of("123456"));
        doReturn(true).when(redisHelper).delete(anyString());

        assertDoesNotThrow(() -> validator.validateSmsCaptcha("13800138000", "123456"));
        verify(redisHelper).delete(anyString());
    }

    @Test
    @DisplayName("短信验证码不匹配应抛出异常")
    void validateSmsCaptcha_shouldThrow_whenCodeNotMatch() {
        when(redisHelper.get(anyString())).thenReturn(Optional.of("123456"));

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validateSmsCaptcha("13800138000", "654321"));
        assertEquals("验证码错误", ex.getMessage());
    }

    @Test
    @DisplayName("短信验证码已过期应抛出异常")
    void validateSmsCaptcha_shouldThrow_whenExpired() {
        when(redisHelper.get(anyString())).thenReturn(Optional.empty());

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validateSmsCaptcha("13800138000", "123456"));
        assertEquals("验证码已过期，请重新发送", ex.getMessage());
    }

    @Test
    @DisplayName("短信验证码手机号或 code 为 null 应抛出异常")
    void validateSmsCaptcha_shouldThrow_whenInputNull() {
        assertThrows(SxwlBusinessException.class,
                () -> validator.validateSmsCaptcha(null, "123456"));
        assertThrows(SxwlBusinessException.class,
                () -> validator.validateSmsCaptcha("13800138000", null));
    }

    // ==================== 邮箱验证码 ====================

    @Test
    @DisplayName("邮箱验证码校验通过")
    void validateEmailCaptcha_shouldPass_whenCodeMatches() {
        when(redisHelper.get(anyString())).thenReturn(Optional.of("abc123"));
        doReturn(true).when(redisHelper).delete(anyString());

        assertDoesNotThrow(() -> validator.validateEmailCaptcha("test@example.com", "abc123"));
        verify(redisHelper).delete(anyString());
    }

    @Test
    @DisplayName("邮箱验证码不匹配应抛出异常")
    void validateEmailCaptcha_shouldThrow_whenCodeNotMatch() {
        when(redisHelper.get(anyString())).thenReturn(Optional.of("abc123"));

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validateEmailCaptcha("test@example.com", "wrong"));
        assertEquals("验证码错误", ex.getMessage());
    }

    @Test
    @DisplayName("邮箱验证码已过期应抛出异常")
    void validateEmailCaptcha_shouldThrow_whenExpired() {
        when(redisHelper.get(anyString())).thenReturn(Optional.empty());

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validateEmailCaptcha("test@example.com", "abc123"));
        assertEquals("验证码已过期，请重新发送", ex.getMessage());
    }

    @Test
    @DisplayName("邮箱或 code 为 null 应抛出异常")
    void validateEmailCaptcha_shouldThrow_whenInputNull() {
        assertThrows(SxwlBusinessException.class,
                () -> validator.validateEmailCaptcha(null, "code"));
        assertThrows(SxwlBusinessException.class,
                () -> validator.validateEmailCaptcha("email@example.com", null));
    }
}
