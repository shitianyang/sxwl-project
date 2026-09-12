package com.sxwl.security.password;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.security.config.SxwlSecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlPasswordValidator} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlPasswordValidatorTest {

    private SxwlSecurityProperties properties;
    private SxwlPasswordValidator validator;

    @BeforeEach
    void setUp() {
        properties = mock(SxwlSecurityProperties.class);
        when(properties.getPasswordMinLength()).thenReturn(8);
        when(properties.getPasswordMaxLength()).thenReturn(32);
        validator = new SxwlPasswordValidator(properties);
    }

    @Test
    @DisplayName("合法密码应校验通过")
    void validate_shouldPass_whenPasswordIsValid() {
        assertDoesNotThrow(() -> validator.validate("Abc123!@xyz"));
    }

    @Test
    @DisplayName("密码为 null 应抛出异常")
    void validate_shouldThrow_whenPasswordIsNull() {
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validate(null));
        assertEquals("密码不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("密码为空字符串应抛出异常")
    void validate_shouldThrow_whenPasswordIsEmpty() {
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validate(""));
        assertEquals("密码不能为空", ex.getMessage());
    }

    @Test
    @DisplayName("密码太短应抛出异常")
    void validate_shouldThrow_whenPasswordTooShort() {
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validate("Ab1!cd"));
        assertEquals("密码长度不能少于 8 位", ex.getMessage());
    }

    @Test
    @DisplayName("密码太长应抛出异常")
    void validate_shouldThrow_whenPasswordTooLong() {
        String longPwd = "A1!" + "a".repeat(35); // 38 chars > 32
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validate(longPwd));
        assertEquals("密码长度不能超过 32 位", ex.getMessage());
    }

    @Test
    @DisplayName("复杂度不足（仅 1 种字符）应抛出异常")
    void validate_shouldThrow_whenComplexityTooLow_oneType() {
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validate("abcdefghi"));
        assertEquals("密码必须包含大写字母、小写字母、数字、特殊字符中的至少 3 种", ex.getMessage());
    }

    @Test
    @DisplayName("复杂度不足（仅 2 种字符）应抛出异常")
    void validate_shouldThrow_whenComplexityTooLow_twoTypes() {
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validate("abcdef1234"));
        assertEquals("密码必须包含大写字母、小写字母、数字、特殊字符中的至少 3 种", ex.getMessage());
    }

    @Test
    @DisplayName("恰好包含 3 种字符应校验通过")
    void validate_shouldPass_whenExactlyThreeTypes() {
        // 小写 + 大写 + 数字
        assertDoesNotThrow(() -> validator.validate("Abcdef1234"));
        // 小写 + 大写 + 特殊字符
        assertDoesNotThrow(() -> validator.validate("Abcdef!@#"));
        // 小写 + 数字 + 特殊字符
        assertDoesNotThrow(() -> validator.validate("abc123!@#"));
        // 大写 + 数字 + 特殊字符
        assertDoesNotThrow(() -> validator.validate("ABC123!@#"));
    }

    @Test
    @DisplayName("包含全部 4 种字符应校验通过")
    void validate_shouldPass_whenAllFourTypes() {
        assertDoesNotThrow(() -> validator.validate("Abcd123!@#"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abc123!@", "A1b2C3d4", "Test@123", "Hello#World1"})
    @DisplayName("多种合法密码组合应校验通过")
    void validate_shouldPass_forMultipleValidPasswords(String password) {
        assertDoesNotThrow(() -> validator.validate(password));
    }

    @Test
    @DisplayName("边界长度密码应校验通过")
    void validate_shouldPass_atBoundaryLength() {
        // 最小长度 8
        assertDoesNotThrow(() -> validator.validate("Abc12!@#"));
        // 最大长度 32
        String maxLenPwd = "A1!" + "b".repeat(28) + "9"; // 32 chars
        assertDoesNotThrow(() -> validator.validate(maxLenPwd));
    }

    @Test
    @DisplayName("可配置最小/最大长度应生效")
    void validate_shouldUseConfigurableLength() {
        // 覆盖配置
        when(properties.getPasswordMinLength()).thenReturn(4);
        when(properties.getPasswordMaxLength()).thenReturn(10);
        validator = new SxwlPasswordValidator(properties);

        assertDoesNotThrow(() -> validator.validate("Ab1!"));
        assertDoesNotThrow(() -> validator.validate("Ab1!xyz"));

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> validator.validate("Abc123!@#xyz")); // 11 chars > 10
        assertEquals("密码长度不能超过 10 位", ex.getMessage());
    }
}
