package com.sxwl.websocket.config;

import com.sxwl.websocket.handler.SxwlWebSocketHandler;
import com.sxwl.websocket.interceptor.SxwlWebSocketInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 自动配置
 *
 * <p>注册 WebSocket 端点，配置拦截器和允许的来源。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Configuration
@EnableWebSocket
public class SxwlWebSocketAutoConfiguration implements WebSocketConfigurer {

    private final SxwlWebSocketHandler webSocketHandler;
    private final SxwlWebSocketInterceptor webSocketInterceptor;

    public SxwlWebSocketAutoConfiguration(SxwlWebSocketHandler webSocketHandler,
                                          SxwlWebSocketInterceptor webSocketInterceptor) {
        this.webSocketHandler = webSocketHandler;
        this.webSocketInterceptor = webSocketInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/connect")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
