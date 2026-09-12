package com.sxwl.websocket.handler;

import com.sxwl.websocket.manager.SxwlWebSocketSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlWebSocketHandler} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlWebSocketHandler 测试")
class SxwlWebSocketHandlerTest {

    @Mock
    private SxwlWebSocketSessionManager sessionManager;

    @Mock
    private WebSocketSession session;

    @Captor
    private ArgumentCaptor<TextMessage> messageCaptor;

    private SxwlWebSocketHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SxwlWebSocketHandler(sessionManager);
        Map<String, Object> attributes = Map.of("userId", 1L);
        lenient().when(session.getAttributes()).thenReturn(attributes);
        lenient().when(session.getId()).thenReturn("session-1");
    }

    @Test
    @DisplayName("afterConnectionEstablished 应注册 Session")
    void afterConnectionEstablished_shouldRegisterSession() {
        handler.afterConnectionEstablished(session);
        verify(sessionManager).addSession(1L, session);
    }

    @Test
    @DisplayName("afterConnectionEstablished 无 userId 时不注册")
    void afterConnectionEstablished_noUserId_shouldNotRegister() {
        when(session.getAttributes()).thenReturn(Map.of());
        handler.afterConnectionEstablished(session);
        verify(sessionManager, never()).addSession(any(), any());
    }

    @Test
    @DisplayName("handleTextMessage ping 应回复 pong")
    void handleTextMessage_ping_shouldReplyPong() throws Exception {
        handler.handleTextMessage(session, new TextMessage("{\"type\":\"ping\"}"));
        verify(session).sendMessage(messageCaptor.capture());
        assertEquals("{\"type\":\"pong\"}", messageCaptor.getValue().getPayload());
    }

    @Test
    @DisplayName("handleTextMessage subscribe 应订阅通道")
    void handleTextMessage_subscribe_shouldSubscribeChannel() throws Exception {
        handler.handleTextMessage(session, new TextMessage("{\"type\":\"subscribe\",\"channel\":\"notifications\"}"));
        // 订阅后不应发送消息（订阅逻辑只维护内部 subscriptions 映射）
        verify(session, never()).sendMessage(any());
    }

    @Test
    @DisplayName("handleTextMessage unsubscribe 应取消订阅")
    void handleTextMessage_unsubscribe_shouldUnsubscribeChannel() throws Exception {
        // 先订阅
        handler.handleTextMessage(session, new TextMessage("{\"type\":\"subscribe\",\"channel\":\"notifications\"}"));
        // 再取消订阅
        handler.handleTextMessage(session, new TextMessage("{\"type\":\"unsubscribe\",\"channel\":\"notifications\"}"));
        verify(session, never()).sendMessage(any());
    }

    @Test
    @DisplayName("handleTextMessage 未知消息类型不应处理")
    void handleTextMessage_unknownType_shouldIgnore() throws Exception {
        handler.handleTextMessage(session, new TextMessage("{\"type\":\"unknown\"}"));
        verify(session, never()).sendMessage(any());
    }

    @Test
    @DisplayName("afterConnectionClosed 应移除 Session 和订阅")
    void afterConnectionClosed_shouldRemoveSession() {
        handler.afterConnectionEstablished(session);
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(sessionManager).addSession(1L, session);
        verify(sessionManager).removeSession(1L, session);
    }

    @Test
    @DisplayName("afterConnectionClosed 无 userId 时不应调用 removeSession")
    void afterConnectionClosed_noUserId_shouldNotRemove() {
        when(session.getAttributes()).thenReturn(Map.of());
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);
        verify(sessionManager, never()).removeSession(any(), any());
    }
}
