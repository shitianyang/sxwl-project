package com.sxwl.sse.controller;

import com.sxwl.common.principal.SxwlPrincipal;
import com.sxwl.common.utils.SxwlPrincipalUtils;
import com.sxwl.sse.manager.SxwlSseEmitterManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlSseController} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlSseController 测试")
class SxwlSseControllerTest {

    @Mock
    private SxwlSseEmitterManager sseEmitterManager;

    private SxwlSseController controller;

    @BeforeEach
    void setUp() {
        controller = new SxwlSseController(sseEmitterManager);
    }

    @Test
    @DisplayName("connect 应创建 SSE 连接并返回 Emitter")
    void connect_shouldCreateSseConnection() {
        SseEmitter mockEmitter = mock(SseEmitter.class);
        when(sseEmitterManager.connect(1L)).thenReturn(mockEmitter);

        try (MockedStatic<SxwlPrincipalUtils> principalUtils = mockStatic(SxwlPrincipalUtils.class)) {
            SxwlPrincipal principal = mock(SxwlPrincipal.class);
            when(principal.getUserId()).thenReturn(1L);
            principalUtils.when(SxwlPrincipalUtils::getCurrentPrincipal)
                    .thenReturn(Optional.of(principal));

            SseEmitter result = controller.connect();
            assertNotNull(result);
            verify(sseEmitterManager).connect(1L);
        }
    }

    @Test
    @DisplayName("connect 未登录时应抛出异常")
    void connect_notLoggedIn_shouldThrow() {
        try (MockedStatic<SxwlPrincipalUtils> principalUtils = mockStatic(SxwlPrincipalUtils.class)) {
            principalUtils.when(SxwlPrincipalUtils::getCurrentPrincipal)
                    .thenReturn(Optional.empty());

            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> controller.connect());
            assertEquals("未登录", exception.getMessage());
            verifyNoInteractions(sseEmitterManager);
        }
    }
}
