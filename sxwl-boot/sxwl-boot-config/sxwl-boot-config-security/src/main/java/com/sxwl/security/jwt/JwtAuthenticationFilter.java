package com.sxwl.security.jwt;

import com.sxwl.common.utils.SxwlJwtUtils;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.config.SxwlSecurityProperties;
import com.sxwl.security.model.SxwlLoginUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * <p>
 * 每个请求经过的验证链路：
 * <ol>
 *   <li>从 Authorization Header 提取 Bearer Token</li>
 *   <li>SxwlJwtUtils.parseClaims() 验签 + 解析 Claims</li>
 *   <li>Redis EXISTS 白名单校验</li>
 *   <li>Redis HGETALL token:info 读取 username/roles/perms</li>
 *   <li>组装 SxwlLoginUser → 写入 SecurityContext</li>
 *   <li>检查剩余有效期 < 阈值 → 自动续期（X-New-Token 响应头）</li>
 * </ol>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final SxwlSecurityProperties securityProperties;
    private final SxwlRedisHelper redisHelper;

    public JwtAuthenticationFilter(SxwlSecurityProperties securityProperties, SxwlRedisHelper redisHelper) {
        this.securityProperties = securityProperties;
        this.redisHelper = redisHelper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String secret = securityProperties.getJwtSecret();
            Claims claims = SxwlJwtUtils.parseClaims(token, secret);
            Long userId = SxwlJwtUtils.resolveUserId(claims);
            String jti = SxwlJwtUtils.resolveJwtId(claims);
            String tokenType = SxwlJwtUtils.resolveTokenType(claims);
            String deviceId = SxwlJwtUtils.resolveDeviceId(claims);

            if (userId == null || jti == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 白名单校验
            String clientType = "admin"; // TODO: 从请求上下文获取
            String whitelistKey = SxwlRedisKeyUtils.tokenJwtKey(clientType, userId, deviceId, jti);
            if (!Boolean.TRUE.equals(redisHelper.exists(whitelistKey))) {
                log.debug("Token 不在白名单中: userId={}, jti={}", userId, jti);
                filterChain.doFilter(request, response);
                return;
            }

            // 从 Redis 读取用户信息缓存
            String infoKey = SxwlRedisKeyUtils.tokenInfoKey(userId);
            Map<String, String> userInfo = redisHelper.hgetAll(infoKey);
            if (userInfo.isEmpty()) {
                log.debug("用户信息缓存不存在: userId={}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            // 组装 SxwlLoginUser
            SxwlLoginUser loginUser = buildLoginUser(userId, userInfo);

            // 写入 SecurityContext
            Set<SimpleGrantedAuthority> authorities = loginUser.getPerms().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 自动续期检查
            checkAndRenew(token, claims, secret, userId, deviceId, clientType, response);

        } catch (Exception e) {
            log.debug("Token 验证失败: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从 Authorization Header 提取 Bearer Token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }

    /**
     * 从 Redis Hash 构建 SxwlLoginUser
     */
    private SxwlLoginUser buildLoginUser(Long userId, Map<String, String> userInfo) {
        SxwlLoginUser user = new SxwlLoginUser();
        user.setUserId(userId);
        user.setUsername(userInfo.get("username"));
        user.setNickname(userInfo.get("nickname"));
        user.setStatus(Integer.parseInt(userInfo.getOrDefault("status", "1")));
        user.setCreateOrg(parseLong(userInfo.get("createOrg")));

        // 角色集合
        String rolesStr = userInfo.get("roles");
        if (rolesStr != null && !rolesStr.isEmpty()) {
            user.setRoles(new HashSet<>(Arrays.asList(rolesStr.split(","))));
        } else {
            user.setRoles(Set.of());
        }

        // 权限集合
        String permsStr = userInfo.get("permissions");
        if (permsStr != null && !permsStr.isEmpty()) {
            user.setPerms(new HashSet<>(Arrays.asList(permsStr.split(","))));
        } else {
            user.setPerms(Set.of());
        }

        // 数据范围
        user.setDataScope(parseInt(userInfo.get("dataScope")));
        String orgIdsStr = userInfo.get("dataScopeOrgIds");
        if (orgIdsStr != null && !orgIdsStr.isEmpty()) {
            user.setDataScopeOrgIds(Arrays.stream(orgIdsStr.split(","))
                    .map(Long::parseLong).collect(Collectors.toSet()));
        }

        return user;
    }

    /**
     * 检查 Token 剩余有效期，低于阈值时自动续期
     */
    private void checkAndRenew(String token, Claims claims, String secret,
                               Long userId, String deviceId, String clientType,
                               HttpServletResponse response) {
        Long exp = claims.getExpiration().getTime() / 1000;
        long now = System.currentTimeMillis() / 1000;
        long remaining = exp - now;

        if (remaining > 0 && remaining < securityProperties.getTokenRenewThreshold()) {
            // 签发新 Token（旧 jti 保留至自然过期）
            String newJti = UUID.randomUUID().toString();
            String newToken = SxwlJwtUtils.builder()
                    .userId(userId)
                    .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                    .deviceId(deviceId)
                    .jti(newJti)
                    .expireSeconds(securityProperties.getAccessTokenExpire())
                    .build(secret);

            // 写新白名单
            String newWhitelistKey = SxwlRedisKeyUtils.tokenJwtKey(clientType, userId, deviceId, newJti);
            redisHelper.set(newWhitelistKey, "access",
                    java.time.Duration.ofSeconds(securityProperties.getAccessTokenExpire()));

            // 辅助索引
            String userSetKey = SxwlRedisKeyUtils.tokenUserSetKey(clientType, userId);
            redisHelper.sadd(userSetKey, newJti);

            response.setHeader("X-New-Token", newToken);
            log.debug("Token 自动续期: userId={}, newJti={}", userId, newJti);
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isEmpty()) return null;
        try { return Long.parseLong(value); } catch (NumberFormatException e) { return null; }
    }

    private Integer parseInt(String value) {
        if (value == null || value.isEmpty()) return null;
        try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
    }
}
