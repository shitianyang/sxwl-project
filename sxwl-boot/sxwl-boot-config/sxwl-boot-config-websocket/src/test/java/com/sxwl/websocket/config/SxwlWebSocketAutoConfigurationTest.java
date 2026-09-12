package com.sxwl.websocket.config;

import com.sxwl.websocket.handler.SxwlWebSocketHandler;
import com.sxwl.websocket.interceptor.SxwlWebSocketInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link SxwlWebSocketAutoConfiguration} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlWebSocketAutoConfiguration 测试")
class SxwlWebSocketAutoConfigurationTest {

    @Mock
    private SxwlWebSocketHandler webSocketHandler;

    @Mock
    private SxwlWebSocketInterceptor webSocketInterceptor;

    @Mock
    private WebSocketHandlerRegistry registry;

    @Mock
    private WebSocketHandlerRegistration registration;

    private SxwlWebSocketAutoConfiguration autoConfig;

    @BeforeEach
    void setUp() {
        autoConfig = new SxwlWebSocketAutoConfiguration(webSocketHandler, webSocketInterceptor);
    }

    @Test
    @DisplayName("registerWebSocketHandlers 应注册 Handler 和 Interceptor")
    void registerWebSocketHandlers_shouldRegisterHandlerAndInterceptor() {
        when(registry.addHandler(webSocketHandler, "/ws/connect")).thenReturn(registration);
        when(registration.addInterceptors(webSocketInterceptor)).thenReturn(registration);
        when(registration.setAllowedOriginPatterns("*")).thenReturn(registration);

        autoConfig.registerWebSocketHandlers(registry);

        verify(registry).addHandler(webSocketHandler, "/ws/connect");
        verify(registration).addInterceptors(webSocketInterceptor);
        verify(registration).setAllowedOriginPatterns("*");
    }
}
