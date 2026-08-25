package com.sxwl.websocket.config;

import com.sxwl.websocket.handler.SxwlWebSocketHandler;
import com.sxwl.websocket.interceptor.SxwlWebSocketInterceptor;
import com.sxwl.web.config.SxwlWebProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;

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
    private final SxwlWebProperties webProperties;

    public SxwlWebSocketAutoConfiguration(SxwlWebSocketHandler webSocketHandler,
                                          SxwlWebSocketInterceptor webSocketInterceptor,
                                          SxwlWebProperties webProperties) {
        this.webSocketHandler = webSocketHandler;
        this.webSocketInterceptor = webSocketInterceptor;
        this.webProperties = webProperties;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/connect")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins(Arrays.stream(webProperties.getAllowedOrigins().split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isEmpty())
                        .toArray(String[]::new));
    }
}
