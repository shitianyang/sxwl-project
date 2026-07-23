package com.sxwl.websocket.interceptor;

import com.sxwl.common.utils.SxwlJwtUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * WebSocket 握手拦截器
 *
 * <p>在握手阶段从 URL Query 中提取 Token 并验证身份。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Component
public class SxwlWebSocketInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SxwlWebSocketInterceptor.class);

    @Value("${sxwl.security.jwt-secret}")
    private String jwtSecret;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            // 从 URL Query 中提取 token
            URI uri = request.getURI();
            String query = uri.getQuery();
            if (query == null || !query.contains("token=")) {
                log.warn("WebSocket 握手失败: 缺少 token 参数");
                return false;
            }

            // 简单解析 token 参数
            String token = null;
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.substring(6);
                    break;
                }
            }

            if (token == null || token.isEmpty()) {
                log.warn("WebSocket 握手失败: token 为空");
                return false;
            }

            // 验证 Token 并提取 userId
            Claims claims = SxwlJwtUtils.parseClaims(token, jwtSecret);
            Long userId = SxwlJwtUtils.resolveUserId(claims);
            attributes.put("userId", userId);
            log.debug("WebSocket 握手成功: userId={}", userId);
            return true;
        } catch (Exception e) {
            log.warn("WebSocket 握手异常: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 无需额外处理
    }
}
