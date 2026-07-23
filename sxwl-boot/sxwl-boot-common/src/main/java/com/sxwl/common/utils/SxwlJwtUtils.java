package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * JWT 工具类
 * <p>
 * 使用 Builder 模式构建 Token，Payload 只存最小必要信息：
 * sub（userId）、jti（唯一标识）、type（access/refresh）、deviceId、iat、exp。
 * 角色/权限/用户名/数据范围均不存入 JWT，通过 Redis 读取（见设计文档 3.2 节）。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public final class SxwlJwtUtils {

    // ==================== Claim 键常量 ====================

    /** Token 类型声明键（access / refresh） */
    public static final String CLAIM_TOKEN_TYPE = "type";

    /** 设备标识声明键 */
    public static final String CLAIM_DEVICE_ID = "deviceId";

    public static final String CLAIM_CLIENT_TYPE = "clientType";

    /** Token 类型：Access Token */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /** Token 类型：Refresh Token */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    // ==================== 实例字段 ====================

    private static final long DEFAULT_EXPIRE_SECONDS = 7200L;

    private SxwlJwtUtils() {
        throw new UnsupportedOperationException("SxwlJwtUtils 工具类，不允许实例化");
    }

    // ==================== Builder 入口 ====================

    /**
     * 创建 Token Builder
     *
     * @return new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    // ==================== 解析方法 ====================

    /**
     * 解析 Token 并返回 Claims
     *
     * @param token  JWT 字符串
     * @param secret 签名密钥（十六进制字符串）
     * @return Claims
     */
    public static Claims parseClaims(String token, String secret) {
        return parseClaims(token, secret, null);
    }

    /**
     * 解析 Token 并返回 Claims（可选校验 issuer）
     *
     * @param token           JWT 字符串
     * @param secret          签名密钥（十六进制字符串）
     * @param expectedIssuer  期望的签发者（null 表示不校验）
     * @return Claims
     */
    public static Claims parseClaims(String token, String secret, String expectedIssuer) {
        if (token == null || token.trim().isEmpty()) {
            throw new SxwlBusinessException(400, "token 不能为空");
        }
        SecretKey key = buildSecretKey(secret);
        try {
            var parserBuilder = Jwts.parser().verifyWith(key);
            if (expectedIssuer != null && !expectedIssuer.trim().isEmpty()) {
                parserBuilder.requireIssuer(expectedIssuer.trim());
            }
            return parserBuilder.build().parseSignedClaims(token.trim()).getPayload();
        } catch (JwtException jwtException) {
            throw new SxwlBusinessException(400, "token 非法或已过期", jwtException);
        }
    }

    /**
     * 校验 Token 是否有效
     *
     * @param token  JWT 字符串
     * @param secret 签名密钥
     * @return true-有效，false-无效或已过期
     */
    public static boolean validateToken(String token, String secret) {
        try {
            parseClaims(token, secret);
            return true;
        } catch (SxwlBusinessException exception) {
            return false;
        }
    }

    // ==================== Claims 提取方法 ====================

    /**
     * 从 Claims 中提取 userId
     */
    public static Long resolveUserId(Claims claims) {
        if (claims == null) {
            return null;
        }
        String subject = claims.getSubject();
        if (subject == null || subject.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(subject.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * 从 Claims 中提取 JWT ID（jti）
     */
    public static String resolveJwtId(Claims claims) {
        if (claims == null) {
            return null;
        }
        String jwtId = claims.getId();
        if (jwtId == null || jwtId.trim().isEmpty()) {
            return null;
        }
        return jwtId.trim();
    }

    /**
     * 从 Claims 中提取 Token 类型（access / refresh）
     */
    public static String resolveTokenType(Claims claims) {
        if (claims == null) {
            return null;
        }
        Object typeObj = claims.get(CLAIM_TOKEN_TYPE);
        if (typeObj instanceof String type && !type.trim().isEmpty()) {
            return type.trim();
        }
        return null;
    }

    /**
     * 从 Claims 中提取设备标识
     */
    public static String resolveDeviceId(Claims claims) {
        if (claims == null) {
            return null;
        }
        Object deviceIdObj = claims.get(CLAIM_DEVICE_ID);
        if (deviceIdObj instanceof String deviceId && !deviceId.trim().isEmpty()) {
            return deviceId.trim();
        }
        return null;
    }

    public static String resolveClientType(Claims claims) {
        if (claims == null) {
            return null;
        }
        Object clientTypeObj = claims.get(CLAIM_CLIENT_TYPE);
        if (clientTypeObj instanceof String clientType && !clientType.trim().isEmpty()) {
            return clientType.trim();
        }
        return null;
    }

    // ==================== 内部方法 ====================

    /**
     * 构建 HMAC 密钥：
     * 当原始密钥长度不足 32 字节时，使用 SHA-256 扩展为 32 字节，满足 HS256 要求。
     */
    private static SecretKey buildSecretKey(String secret) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new SxwlBusinessException(400, "jwt secret 不能为空");
        }
        byte[] secretBytes = secret.trim().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            secretBytes = sha256(secretBytes);
        }
        return Keys.hmacShaKeyFor(secretBytes);
    }

    private static byte[] sha256(byte[] bytes) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return messageDigest.digest(bytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new SxwlBusinessException(500, "当前 JDK 不支持 SHA-256", exception);
        }
    }

    // ==================== Builder ====================

    /**
     * JWT Token Builder
     * <p>
     * 使用示例：
     * <pre>{@code
     * String token = SxwlJwtUtils.builder()
     *     .userId(1L)
     *     .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
     *     .deviceId("web-chrome-abc")
     *     .jti("uuid")                 // 可选，默认自动生成 UUID
     *     .issuer("sxwl")              // 可选
     *     .expireSeconds(1800L)        // 可选，默认 7200 秒
     *     .build(secret);
     * }</pre>
     */
    public static final class Builder {

        private Long userId;
        private String tokenType;
        private String deviceId;
        private String clientType;
        private String issuer;
        private Long expireSeconds;
        private String jti;

        private Builder() {
        }

        /**
         * 设置用户 ID（必填）
         */
        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        /**
         * 设置 Token 类型（必填），取值：
         * {@link #TOKEN_TYPE_ACCESS} 或 {@link #TOKEN_TYPE_REFRESH}
         */
        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        /**
         * 设置设备标识（必填）
         */
        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder clientType(String clientType) {
            this.clientType = clientType;
            return this;
        }

        /**
         * 设置签发者（可选）
         */
        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        /**
         * 设置过期时间（秒），默认 7200 秒（2 小时）
         */
        public Builder expireSeconds(Long expireSeconds) {
            this.expireSeconds = expireSeconds;
            return this;
        }

        /**
         * 设置 JWT ID（可选），不设则自动生成 UUID
         */
        public Builder jti(String jti) {
            this.jti = jti;
            return this;
        }

        /**
         * 构建 JWT Token
         *
         * @param secret 签名密钥（十六进制字符串）
         * @return JWT 字符串
         */
        public String build(String secret) {
            Objects.requireNonNull(secret, "secret 不能为空");

            // 校验必填字段
            if (userId == null || userId <= 0L) {
                throw new SxwlBusinessException(400, "userId 不能为空且必须大于 0");
            }
            if (tokenType == null || tokenType.trim().isEmpty()) {
                throw new SxwlBusinessException(400, "tokenType 不能为空");
            }
            if (deviceId == null || deviceId.trim().isEmpty()) {
                throw new SxwlBusinessException(400, "deviceId 不能为空");
            }

            SecretKey key = buildSecretKey(secret);

            long actualExpireSeconds = expireSeconds == null || expireSeconds <= 0L
                    ? DEFAULT_EXPIRE_SECONDS
                    : expireSeconds;
            String actualJti = (jti == null || jti.trim().isEmpty())
                    ? UUID.randomUUID().toString()
                    : jti.trim();
            String actualIssuer = issuer == null ? "" : issuer.trim();
            String actualClientType = clientType == null || clientType.trim().isEmpty()
                    ? "admin"
                    : clientType.trim();

            Instant now = Instant.now();
            Instant expiration = now.plusSeconds(actualExpireSeconds);

            return Jwts.builder()
                    .id(actualJti)
                    .subject(String.valueOf(userId))
                    .issuer(actualIssuer)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(expiration))
                    .claim(CLAIM_TOKEN_TYPE, tokenType.trim())
                    .claim(CLAIM_DEVICE_ID, deviceId.trim())
                    .claim(CLAIM_CLIENT_TYPE, actualClientType)
                    .signWith(key, Jwts.SIG.HS256)
                    .compact();
        }
    }
}
