package com.sxwl.websocket.interceptor;

import com.sxwl.security.ticket.SxwlConnectionTicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器
 *
 * <p>在握手阶段消费一次性短期票据并绑定身份。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Component
public class SxwlWebSocketInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SxwlWebSocketInterceptor.class);

    private final SxwlConnectionTicketService connectionTicketService;

    public SxwlWebSocketInterceptor(SxwlConnectionTicketService connectionTicketService) {
        this.connectionTicketService = connectionTicketService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            String ticket = org.springframework.web.util.UriComponentsBuilder.fromUri(request.getURI())
                    .build()
                    .getQueryParams()
                    .getFirst("ticket");
            Long userId = connectionTicketService.consume(ticket).orElse(null);
            if (userId == null) {
                log.warn("WebSocket 握手失败: 票据无效或已过期");
                return false;
            }
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
