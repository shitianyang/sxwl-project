package com.sxwl.websocket.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器
 *
 * <p>在握手阶段提取用户身份信息（从 URL query 参数获取 userId），用于基础连接管理。
 * 如需增强安全验证（如票据验证、Token 校验等），请在业务模块中扩展此类或创建新的拦截器。</p>
 *
 * <h3>架构定位</h3>
 * <p>属于<strong>基础设施层</strong>，提供最基础的连接身份绑定能力。具体的安全认证逻辑（如连接票据验证）由 {@code module-*} 实现。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SxwlWebSocketInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SxwlWebSocketInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            // 从 URL query 参数获取 userId（可选，具体实现由业务模块决定）
            String userIdStr = org.springframework.web.util.UriComponentsBuilder.fromUri(request.getURI())
                    .build()
                    .getQueryParams()
                    .getFirst("userId");
            
            if (userIdStr != null) {
                Long userId = Long.parseLong(userIdStr);
                attributes.put("userId", userId);
                log.debug("WebSocket 握手成功: userId={}", userId);
                return true;
            }
            
            // 没有 userId 参数也允许连接（兼容无状态场景）
            log.debug("WebSocket 连接未携带 userId");
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
