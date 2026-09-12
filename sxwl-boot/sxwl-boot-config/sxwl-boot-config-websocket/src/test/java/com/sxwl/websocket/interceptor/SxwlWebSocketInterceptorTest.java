package com.sxwl.websocket.interceptor;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlJwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.WebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlWebSocketInterceptor} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlWebSocketInterceptor 测试")
class SxwlWebSocketInterceptorTest {

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebSocketHandler wsHandler;

    @Mock
    private Claims claims;

    private SxwlWebSocketInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new SxwlWebSocketInterceptor();
        ReflectionTestUtils.setField(interceptor, "jwtSecret", "test-jwt-secret");
    }

    @Test
    @DisplayName("beforeHandshake 缺少 token 参数应返回 false")
    void beforeHandshake_noToken_shouldReturnFalse() {
        when(request.getURI()).thenReturn(URI.create("ws://localhost/ws/connect"));

        Map<String, Object> attributes = new HashMap<>();
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertFalse(result);
        assertTrue(attributes.isEmpty());
    }

    @Test
    @DisplayName("beforeHandshake token 参数为空应返回 false")
    void beforeHandshake_emptyToken_shouldReturnFalse() {
        when(request.getURI()).thenReturn(URI.create("ws://localhost/ws/connect?token="));

        Map<String, Object> attributes = new HashMap<>();
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertFalse(result);
        assertTrue(attributes.isEmpty());
    }

    @Test
    @DisplayName("beforeHandshake 有效 token 应解析 userId 并返回 true")
    void beforeHandshake_validToken_shouldResolveUserId() {
        when(request.getURI()).thenReturn(URI.create("ws://localhost/ws/connect?token=valid.jwt.token"));

        try (MockedStatic<SxwlJwtUtils> jwtUtils = mockStatic(SxwlJwtUtils.class)) {
            jwtUtils.when(() -> SxwlJwtUtils.parseClaims("valid.jwt.token", "test-jwt-secret"))
                    .thenReturn(claims);
            jwtUtils.when(() -> SxwlJwtUtils.resolveUserId(claims)).thenReturn(1L);

            Map<String, Object> attributes = new HashMap<>();
            boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

            assertTrue(result);
            assertEquals(1L, attributes.get("userId"));
        }
    }

    @Test
    @DisplayName("beforeHandshake token 解析异常应返回 false")
    void beforeHandshake_parseException_shouldReturnFalse() {
        when(request.getURI()).thenReturn(URI.create("ws://localhost/ws/connect?token=invalid-token"));

        try (MockedStatic<SxwlJwtUtils> jwtUtils = mockStatic(SxwlJwtUtils.class)) {
            jwtUtils.when(() -> SxwlJwtUtils.parseClaims("invalid-token", "test-jwt-secret"))
                    .thenThrow(new SxwlBusinessException(400, "token 非法或已过期"));

            Map<String, Object> attributes = new HashMap<>();
            boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

            assertFalse(result);
            assertTrue(attributes.isEmpty());
        }
    }

    @Test
    @DisplayName("beforeHandshake 查询字符串中无 token= 应返回 false")
    void beforeHandshake_queryWithoutToken_shouldReturnFalse() {
        when(request.getURI()).thenReturn(URI.create("ws://localhost/ws/connect?foo=bar"));

        Map<String, Object> attributes = new HashMap<>();
        boolean result = interceptor.beforeHandshake(request, response, wsHandler, attributes);

        assertFalse(result);
        assertTrue(attributes.isEmpty());
    }

    @Test
    @DisplayName("afterHandshake 不应抛出异常")
    void afterHandshake_shouldNotThrow() {
        assertDoesNotThrow(() -> interceptor.afterHandshake(request, response, wsHandler, null));
    }
}
