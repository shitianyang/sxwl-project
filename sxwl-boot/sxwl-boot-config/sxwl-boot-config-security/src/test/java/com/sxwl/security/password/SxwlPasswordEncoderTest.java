package com.sxwl.security.password;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlPasswordEncoder} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlPasswordEncoderTest {

    private final SxwlPasswordEncoder encoder = new SxwlPasswordEncoder();

    @Test
    @DisplayName("encode 返回格式：{sm3}base64Salt$hexHash")
    void encode_shouldReturnCorrectFormat() {
        String encoded = encoder.encode("MyP@ssw0rd");
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("{sm3}"), "应以 {sm3} 开头");
        assertTrue(encoded.contains("$"), "应包含 $ 分隔符");

        String[] parts = encoded.substring(5).split("\\$");
        assertEquals(2, parts.length, "前缀之后应有两部分：salt 和 hash");
        assertFalse(parts[0].isEmpty(), "salt 不应为空");
        assertFalse(parts[1].isEmpty(), "hash 不应为空");
        assertEquals(64, parts[1].length(), "SM3 hash 应为 64 位十六进制");
    }

    @Test
    @DisplayName("同明文每次 encode 结果不同（因随机盐）")
    void encode_shouldProduceDifferentResultsForSamePassword() {
        String encoded1 = encoder.encode("SameP@ss1");
        String encoded2 = encoder.encode("SameP@ss1");
        assertNotEquals(encoded1, encoded2, "随机盐应导致每次编码结果不同");
    }

    @Test
    @DisplayName("matches 返回 true：明文与编码匹配")
    void matches_shouldReturnTrue_whenPasswordMatches() {
        String encoded = encoder.encode("MyP@ssw0rd");
        assertTrue(encoder.matches("MyP@ssw0rd", encoded));
    }

    @Test
    @DisplayName("matches 返回 false：明文与编码不匹配")
    void matches_shouldReturnFalse_whenPasswordDoesNotMatch() {
        String encoded = encoder.encode("CorrectP@ss1");
        assertFalse(encoder.matches("WrongP@ss1", encoded));
    }

    @Test
    @DisplayName("matches 返回 false：rawPassword 为 null")
    void matches_shouldReturnFalse_whenRawPasswordIsNull() {
        String encoded = encoder.encode("SomeP@ss1");
        assertFalse(encoder.matches(null, encoded));
    }

    @Test
    @DisplayName("matches 返回 false：encodedPassword 为 null")
    void matches_shouldReturnFalse_whenEncodedPasswordIsNull() {
        assertFalse(encoder.matches("SomeP@ss1", null));
    }

    @Test
    @DisplayName("matches 返回 false：非 {sm3} 前缀的编码")
    void matches_shouldReturnFalse_whenPrefixInvalid() {
        assertFalse(encoder.matches("P@ss1234", "{md5}somehash"));
    }

    @Test
    @DisplayName("matches 返回 false：编码格式异常（无 $ 分隔符）")
    void matches_shouldReturnFalse_whenFormatInvalid() {
        // 有前缀但无 $
        assertFalse(encoder.matches("P@ss1234", "{sm3}abcdef"));
    }

    @Test
    @DisplayName("encode 支持特殊字符和中文")
    void encode_shouldSupportSpecialCharsAndChinese() {
        String encoded = encoder.encode("密码@2024!你好");
        assertTrue(encoder.matches("密码@2024!你好", encoded));
    }

    @Test
    @DisplayName("encode 支持空字符串")
    void encode_shouldSupportEmptyString() {
        String encoded = encoder.encode("");
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("{sm3}"));
        assertTrue(encoder.matches("", encoded));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "abc", "aB1", "abcdefgh", "aB3$xyz"})
    @DisplayName("matches 多种密码都能正确匹配")
    void matches_shouldWorkForVariousPasswords(String raw) {
        String encoded = encoder.encode(raw);
        assertTrue(encoder.matches(raw, encoded));
    }
}
