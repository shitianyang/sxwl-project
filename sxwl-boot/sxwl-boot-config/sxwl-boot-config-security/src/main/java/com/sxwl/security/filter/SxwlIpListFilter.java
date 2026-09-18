package com.sxwl.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.config.SxwlSecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/**
 * IP 黑白名单拦截器
 * <p>
 * 在登录前检查客户端 IP 是否在白名单/黑名单中，防止恶意 IP 攻击。
 * </p>
 * <ul>
 *   <li><b>白名单模式</b>：仅允许白名单中的 IP 访问（需显式配置）</li>
 *   <li><b>黑名单模式</b>：自动拦截黑名单中的 IP（支持动态添加）</li>
 *   <li><b>内网 IP 忽略</b>：默认忽略 127.0.0.1、192.168.x.x 等私有 IP</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/9/12
 * @since 0.1.0
 */
public class SxwlIpListFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SxwlIpListFilter.class);

    /** 响应内容类型 */
    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    private final ObjectMapper objectMapper;
    private final SxwlRedisHelper redisHelper;
    private final SxwlSecurityProperties securityProperties;

    public SxwlIpListFilter(ObjectMapper objectMapper, SxwlRedisHelper redisHelper, SxwlSecurityProperties securityProperties) {
        this.objectMapper = objectMapper;
        this.redisHelper = redisHelper;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 获取客户端 IP
        String clientIp = getClientIp(request);
        
        // 2. 如果是内网 IP 且配置了忽略内网，直接放行
        if (securityProperties.isIgnorePrivateIp() && isPrivateIp(clientIp)) {
            log.debug("内网 IP 已忽略: ip={}", maskIp(clientIp));
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 检查白名单（如果启用）
        if (securityProperties.isIpWhitelistEnabled()) {
            if (!isInWhitelist(clientIp)) {
                log.warn("IP 不在白名单中被拦截: ip={}", maskIp(clientIp));
                sendForbiddenResponse(response, "您的 IP 未被授权访问");
                return;
            }
        }

        // 4. 检查黑名单（动态 + 静态配置）
        if (isInBlacklist(clientIp)) {
            log.warn("IP 在黑名单中被拦截: ip={}, reason={}", maskIp(clientIp), getBlacklistReason(clientIp));
            sendForbiddenResponse(response, "您的 IP 因安全原因被禁止访问");
            return;
        }

        // 5. 放行
        filterChain.doFilter(request, response);
    }

    /**
     * 获取客户端真实 IP（支持代理）
     */
    private String getClientIp(HttpServletRequest request) {
        String[] ipHeaders = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        
        for (String header : ipHeaders) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For 可能包含多个 IP，取第一个
                return ip.contains(",") ? ip.split(",")[0].trim() : ip.trim();
            }
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 判断是否为内网 IP
     */
    private boolean isPrivateIp(String ip) {
        if (ip == null) {
            return false;
        }
        
        // IPv4
        return ip.equals("127.0.0.1")                    // localhost
            || ip.startsWith("192.168.")                 // 192.168.x.x
            || ip.startsWith("10.")                      // 10.x.x.x
            || ip.startsWith("172.");                     // 172.16.x.x - 172.31.x.x (简化判断)
    }

    /**
     * 检查 IP 是否在白名单中
     */
    private boolean isInWhitelist(String ip) {
        java.util.List<String> whitelist = securityProperties.getIpWhitelist();
        
        // Redis 动态白名单
        String redisKey = SxwlRedisKeyUtils.ipWhitelistKey(ip);
        if (Boolean.TRUE.equals(redisHelper.exists(redisKey))) {
            log.info("IP 在白名单中（Redis）: ip={}", maskIp(ip));
            return true;
        }
        
        // 静态配置白名单
        boolean found = whitelist.stream().anyMatch(allowedIp -> allowedIp.equals(ip));
        if (found) {
            log.info("IP 在白名单中（配置）: ip={}", maskIp(ip));
        }
        
        return found;
    }

    /**
     * 检查 IP 是否在黑名单中
     */
    private boolean isInBlacklist(String ip) {
        // 1. 检查 Redis 动态黑名单
        String redisKey = SxwlRedisKeyUtils.ipBlacklistKey(ip);
        if (Boolean.TRUE.equals(redisHelper.exists(redisKey))) {
            log.warn("IP 在 Redis 黑名单中: ip={}, reason={}", maskIp(ip), getBlacklistReason(ip));
            return true;
        }
        
        // 2. 检查静态配置黑名单
        java.util.List<String> blacklist = securityProperties.getIpBlacklist();
        boolean found = blacklist.stream().anyMatch(blockedIp -> blockedIp.equals(ip));
        if (found) {
            log.warn("IP 在静态黑名单中: ip={}", maskIp(ip));
        }
        
        return found;
    }

    /**
     * 获取黑名单原因
     */
    private String getBlacklistReason(String ip) {
        String redisKey = SxwlRedisKeyUtils.ipBlacklistKey(ip);
        return redisHelper.get(redisKey).orElse("静态配置");
    }

    /**
     * 发送 403  Forbidden 响应
     */
    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(APPLICATION_JSON);
        
        Map<String, String> errorBody = Map.of(
            "code", "403",
            "message", message,
            "data", "您的 IP 已被拦截"
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }

    /**
     * 脱敏处理 IP（保护隐私）
     */
    private String maskIp(String ip) {
        if (ip == null || !ip.contains(".")) {
            return "***";
        }
        // IPv4: 192.168.1.100 → 192.168.1.***
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + "." + parts[2] + ".***";
        }
        return ip;
    }
}
