package com.sxwl.web.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlLogAspect} 的单元测试（仅测试可独立验证的方法）
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlLogAspectTest {

    private SxwlLogAspect aspect;

    @BeforeEach
    void setUp() {
        // 仅用于测试 maskSensitiveFields 等工具方法
        aspect = new SxwlLogAspect(null, new ObjectMapper(), java.util.Optional.empty());
    }

    @Test
    @DisplayName("maskSensitiveFields 应脱敏密码字段")
    void maskSensitiveFields_shouldMaskPassword() {
        String json = "{\"username\":\"admin\",\"password\":\"my-secret-pwd\"}";
        String result = aspect.maskSensitiveFields(json);
        assertTrue(result.contains("\"password\": \"***\""));
        assertFalse(result.contains("my-secret-pwd"));
    }

    @Test
    @DisplayName("maskSensitiveFields 应脱敏 token 字段")
    void maskSensitiveFields_shouldMaskToken() {
        String json = "{\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9.test\"}";
        String result = aspect.maskSensitiveFields(json);
        assertTrue(result.contains("\"accessToken\": \"***\""));
        assertFalse(result.contains("eyJhbGci"));
    }

    @Test
    @DisplayName("maskSensitiveFields 应脱敏 idCard 字段")
    void maskSensitiveFields_shouldMaskIdCard() {
        String json = "{\"idCard\":\"110101199001011234\"}";
        String result = aspect.maskSensitiveFields(json);
        assertTrue(result.contains("\"idCard\": \"***\""));
    }

    @Test
    @DisplayName("maskSensitiveFields 应处理多个敏感字段")
    void maskSensitiveFields_shouldHandleMultipleSensitiveFields() {
        String json = "{\"username\":\"admin\",\"password\":\"pwd123\",\"secret\":\"my-api-key\",\"token\":\"abc\"}";
        String result = aspect.maskSensitiveFields(json);
        assertTrue(result.contains("\"password\": \"***\""));
        assertTrue(result.contains("\"secret\": \"***\""));
        assertTrue(result.contains("\"token\": \"***\""));
        assertFalse(result.contains("pwd123"));
        assertFalse(result.contains("my-api-key"));
        assertFalse(result.contains("abc"));
        // username 不应被脱敏
        assertTrue(result.contains("\"username\":\"admin\""));
    }

    @Test
    @DisplayName("maskSensitiveFields 无敏感字段时不变")
    void maskSensitiveFields_shouldNotModify_whenNoSensitiveFields() {
        String json = "{\"username\":\"admin\",\"nickname\":\"管理员\"}";
        String result = aspect.maskSensitiveFields(json);
        assertEquals(json, result);
    }

    @Test
    @DisplayName("maskSensitiveFields 空字符串应返回空")
    void maskSensitiveFields_shouldReturnEmpty_whenEmpty() {
        assertEquals("", aspect.maskSensitiveFields(""));
    }

    @Test
    @DisplayName("maskSensitiveFields null 应返回 null")
    void maskSensitiveFields_shouldReturnNull_whenNull() {
        assertNull(aspect.maskSensitiveFields(null));
    }
}
