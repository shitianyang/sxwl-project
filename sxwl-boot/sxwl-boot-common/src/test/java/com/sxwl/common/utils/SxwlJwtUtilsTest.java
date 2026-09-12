package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT 工具类单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SxwlJwtUtils 工具类测试")
class SxwlJwtUtilsTest {

    /** 测试用密钥（32 位十六进制字符串，满足 HS256 要求） */
    private static final String TEST_SECRET = "aabbccddee00112233445566778899ff00112233445566778899aabbccddee00";

    @BeforeAll
    static void setUp() {
        // 确保密钥长度 ≥ 32 字节
        assertTrue(TEST_SECRET.length() >= 32, "测试密钥长度应满足要求");
    }

    @Test
    @DisplayName("创建并解析 Token：生成后解析 claim 一致")
    void testCreateAndParse() {
        String token = SxwlJwtUtils.builder()
                .userId(1L)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                .deviceId("test-device-001")
                .clientType("admin")
                .jti("test-jti-unique-001")
                .issuer("sxwl-test")
                .expireSeconds(3600L)
                .build(TEST_SECRET);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"), "JWT 应以 'eyJ' 开头（Base64 编码的 Header）");

        // 解析
        Claims claims = SxwlJwtUtils.parseClaims(token, TEST_SECRET);
        assertNotNull(claims);
        assertEquals("1", claims.getSubject(), "subject 应为 userId=1");
    }

    @Test
    @DisplayName("创建并解析 Token：校验 issuer")
    void testParseWithIssuer() {
        String token = SxwlJwtUtils.builder()
                .userId(2L)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_REFRESH)
                .deviceId("test-device-002")
                .clientType("admin")
                .issuer("sxwl-test")
                .build(TEST_SECRET);

        // 正确 issuer -> 解析成功
        Claims claims = SxwlJwtUtils.parseClaims(token, TEST_SECRET, "sxwl-test");
        assertNotNull(claims);
        assertEquals("2", claims.getSubject());

        // 错误 issuer -> 抛出异常
        assertThrows(SxwlBusinessException.class,
                () -> SxwlJwtUtils.parseClaims(token, TEST_SECRET, "wrong-issuer"),
                "错误 issuer 应抛出异常");
    }

    @Test
    @DisplayName("Token 校验：有效 Token 返回 true")
    void testValidateValidToken() {
        String token = SxwlJwtUtils.builder()
                .userId(1L)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                .deviceId("test-device")
                .build(TEST_SECRET);
        assertTrue(SxwlJwtUtils.validateToken(token, TEST_SECRET));
    }

    @Test
    @DisplayName("Token 校验：无效 Token 返回 false")
    void testValidateInvalidToken() {
        assertFalse(SxwlJwtUtils.validateToken("invalid.token.string", TEST_SECRET));
    }

    @Test
    @DisplayName("Token 校验：错误密钥返回 false")
    void testValidateWrongSecret() {
        String token = SxwlJwtUtils.builder()
                .userId(1L)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                .deviceId("test-device")
                .build(TEST_SECRET);
        assertFalse(SxwlJwtUtils.validateToken(token, "wrong_secret_that_is_not_32_bytes_long_enough"));
    }

    @Test
    @DisplayName("解析 Claims：提取 userId 正确")
    void testResolveUserId() {
        String token = SxwlJwtUtils.builder()
                .userId(42L)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                .deviceId("device-42")
                .expireSeconds(3600L)
                .build(TEST_SECRET);
        Claims claims = SxwlJwtUtils.parseClaims(token, TEST_SECRET);
        Long userId = SxwlJwtUtils.resolveUserId(claims);
        assertEquals(42L, userId);
    }

    @Test
    @DisplayName("解析 Claims：提取 Token 类型正确")
    void testResolveTokenType() {
        String token = SxwlJwtUtils.builder()
                .userId(1L)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_REFRESH)
                .deviceId("device-refresh")
                .build(TEST_SECRET);
        Claims claims = SxwlJwtUtils.parseClaims(token, TEST_SECRET);
        assertEquals(SxwlJwtUtils.TOKEN_TYPE_REFRESH, SxwlJwtUtils.resolveTokenType(claims));
    }

    @Test
    @DisplayName("解析 Claims：提取 deviceId 正确")
    void testResolveDeviceId() {
        String token = SxwlJwtUtils.builder()
                .userId(1L)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                .deviceId("web-chrome-win10")
                .build(TEST_SECRET);
        Claims claims = SxwlJwtUtils.parseClaims(token, TEST_SECRET);
        assertEquals("web-chrome-win10", SxwlJwtUtils.resolveDeviceId(claims));
    }

    @Test
    @DisplayName("解析 Claims：提取 clientType 正确")
    void testResolveClientType() {
        String token = SxwlJwtUtils.builder()
                .userId(1L)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                .deviceId("device")
                .clientType("admin")
                .build(TEST_SECRET);
        Claims claims = SxwlJwtUtils.parseClaims(token, TEST_SECRET);
        assertEquals("admin", SxwlJwtUtils.resolveClientType(claims));
    }

    @Test
    @DisplayName("Builder 校验：缺少必填字段应抛出异常")
    void testBuilderValidation() {
        // 缺少 userId
        assertThrows(SxwlBusinessException.class,
                () -> SxwlJwtUtils.builder()
                        .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                        .deviceId("device")
                        .build(TEST_SECRET));
        // 缺少 tokenType
        assertThrows(SxwlBusinessException.class,
                () -> SxwlJwtUtils.builder()
                        .userId(1L)
                        .deviceId("device")
                        .build(TEST_SECRET));
        // 缺少 deviceId
        assertThrows(SxwlBusinessException.class,
                () -> SxwlJwtUtils.builder()
                        .userId(1L)
                        .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                        .build(TEST_SECRET));
    }

    @Test
    @DisplayName("解析 Claims：userId 为 null 时返回 null")
    void testResolveUserIdNullClaims() {
        assertNull(SxwlJwtUtils.resolveUserId(null));
    }

    @Test
    @DisplayName("解析 Claims：jti 提取正确")
    void testResolveJwtId() {
        String token = SxwlJwtUtils.builder()
                .userId(1L)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                .deviceId("device")
                .jti("explicit-jti-abc")
                .build(TEST_SECRET);
        Claims claims = SxwlJwtUtils.parseClaims(token, TEST_SECRET);
        assertEquals("explicit-jti-abc", SxwlJwtUtils.resolveJwtId(claims));
    }
}
