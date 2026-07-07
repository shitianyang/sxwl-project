package com.sxwl.security.handler;

import com.sxwl.common.utils.SxwlJwtUtils;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.config.SxwlSecurityProperties;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.model.SxwlTokenPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

/**
 * 认证处理器
 * <p>
 * 核心职责：
 * <ol>
 *   <li>Token 签发：{@link #createTokenPair} 生成 access + refresh Token 并写入 Redis 白名单</li>
 *   <li>登出吊销：{@link #logout} 批量删除用户所有 Token</li>
 * </ol>
 * <p>
 * 认证逻辑由 Controller 直接调用对应的 {@code SxwlAuthenticationStrategy} 实现，不经过本类路由。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlAuthenticationHandler {

    private static final Logger log = LoggerFactory.getLogger(SxwlAuthenticationHandler.class);

    private final SxwlSecurityProperties securityProperties;
    private final SxwlRedisHelper redisHelper;

    public SxwlAuthenticationHandler(SxwlSecurityProperties securityProperties,
                                     SxwlRedisHelper redisHelper) {
        this.securityProperties = securityProperties;
        this.redisHelper = redisHelper;
    }

    /**
     * 签发 Token 对（access + refresh）并写入 Redis
     *
     * @param loginUser 认证用户
     * @param deviceId  设备标识
     * @param clientType 客户端类型（admin/front）
     * @return Token 对
     */
    public SxwlTokenPair createTokenPair(SxwlLoginUser loginUser, String deviceId, String clientType) {
        Long userId = loginUser.getUserId();
        String secret = securityProperties.getJwtSecret();

        // 确定过期时间
        long accessExpire = "admin".equals(clientType)
                ? securityProperties.getAccessTokenExpire()
                : securityProperties.getFrontAccessTokenExpire();
        long refreshExpire = "admin".equals(clientType)
                ? securityProperties.getRefreshTokenExpire()
                : securityProperties.getFrontRefreshTokenExpire();

        // 生成 JWT
        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();

        String accessToken = SxwlJwtUtils.builder()
                .userId(userId)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                .deviceId(deviceId)
                .jti(accessJti)
                .expireSeconds(accessExpire)
                .build(secret);

        String refreshToken = SxwlJwtUtils.builder()
                .userId(userId)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_REFRESH)
                .deviceId(deviceId)
                .jti(refreshJti)
                .expireSeconds(refreshExpire)
                .build(secret);

        // 写 Redis 白名单（新版格式，含设备维度）
        String accessKey = SxwlRedisKeyUtils.tokenJwtKey(clientType, userId, deviceId, accessJti);
        redisHelper.set(accessKey, "access", Duration.ofSeconds(accessExpire));

        String refreshKey = SxwlRedisKeyUtils.tokenJwtKey(clientType, userId, deviceId, refreshJti);
        redisHelper.set(refreshKey, "refresh", Duration.ofSeconds(refreshExpire));

        // 辅助索引（用于批量吊销）
        String userSetKey = SxwlRedisKeyUtils.tokenUserSetKey(clientType, userId);
        redisHelper.sadd(userSetKey, accessJti, refreshJti);

        // 用户信息缓存（Hash）
        cacheUserInfo(loginUser);

        // 在线设备索引
        String devicesSetKey = SxwlRedisKeyUtils.onlineDevicesSetKey(userId);
        redisHelper.sadd(devicesSetKey, deviceId);

        log.info("Token 签发成功: userId={}, deviceId={}, clientType={}", userId, deviceId, clientType);
        return new SxwlTokenPair(accessToken, refreshToken);
    }

    /**
     * 登出：吊销用户所有 Token
     *
     * @param userId     用户 ID
     * @param clientType 客户端类型
     */
    public void logout(Long userId, String clientType) {
        // 辅助索引中拿到所有 jti
        String userSetKey = SxwlRedisKeyUtils.tokenUserSetKey(clientType, userId);
        Set<String> jtis = redisHelper.smembers(userSetKey);

        if (jtis != null && !jtis.isEmpty()) {
            // 批量删除白名单 Key（需要遍历设备维度，此处删除所有可能的 Key）
            // 由于白名单 Key 含 deviceId，而辅助 Set 不区分设备，
            // 这里通过 tokenUserSetKey 拿到 jti 后，无法直接拼出完整 Key，
            // 因此采用删除用户信息缓存 + 辅助索引的方式使 Token 失效
            log.debug("吊销 Token: userId={}, jtiCount={}", userId, jtis.size());
        }

        // 删除用户信息缓存（Filter 读不到用户信息 → 视为未认证）
        String infoKey = SxwlRedisKeyUtils.tokenInfoKey(userId);
        redisHelper.delete(infoKey);

        // 删除辅助索引
        redisHelper.delete(userSetKey);

        // 删除在线设备索引
        String devicesSetKey = SxwlRedisKeyUtils.onlineDevicesSetKey(userId);
        redisHelper.delete(devicesSetKey);

        log.info("用户登出: userId={}, clientType={}", userId, clientType);
    }

    /**
     * 缓存用户信息到 Redis Hash（供 JwtAuthenticationFilter 读取）
     */
    private void cacheUserInfo(SxwlLoginUser loginUser) {
        String infoKey = SxwlRedisKeyUtils.tokenInfoKey(loginUser.getUserId());

        Map<String, String> userInfo = new LinkedHashMap<>();
        userInfo.put("username", nullSafe(loginUser.getUsername()));
        userInfo.put("nickname", nullSafe(loginUser.getNickname()));
        userInfo.put("status", String.valueOf(loginUser.getStatus() != null ? loginUser.getStatus() : 1));
        userInfo.put("createOrg", loginUser.getCreateOrg() != null ? String.valueOf(loginUser.getCreateOrg()) : "");
        userInfo.put("roles", String.join(",", loginUser.getRoles() != null ? loginUser.getRoles() : Set.of()));
        userInfo.put("permissions", String.join(",", loginUser.getPerms() != null ? loginUser.getPerms() : Set.of()));
        userInfo.put("dataScope", loginUser.getDataScope() != null ? String.valueOf(loginUser.getDataScope()) : "");

        Set<Long> dataScopeOrgIds = loginUser.getDataScopeOrgIds();
        if (dataScopeOrgIds != null && !dataScopeOrgIds.isEmpty()) {
            userInfo.put("dataScopeOrgIds", dataScopeOrgIds.stream()
                    .map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""));
        } else {
            userInfo.put("dataScopeOrgIds", "");
        }

        // TTL 与 Refresh Token 一致
        long refreshExpire = securityProperties.getRefreshTokenExpire();
        redisHelper.hmset(infoKey, userInfo);
        redisHelper.expire(infoKey, Duration.ofSeconds(refreshExpire));
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
