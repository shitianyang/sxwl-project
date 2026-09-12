package com.sxwl.websocket.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlWebSocketSessionManager} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlWebSocketSessionManager 测试")
class SxwlWebSocketSessionManagerTest {

    @Mock
    private WebSocketSession session1;

    @Mock
    private WebSocketSession session2;

    private SxwlWebSocketSessionManager manager;

    @BeforeEach
    void setUp() {
        manager = new SxwlWebSocketSessionManager();
    }

    @Test
    @DisplayName("addSession 应注册用户会话")
    void addSession_shouldRegisterSession() {
        manager.addSession(1L, session1);
        assertEquals(1, manager.getOnlineCount());
    }

    @Test
    @DisplayName("addSession 同一用户多个会话应累加连接数")
    void addSession_multipleSessions_shouldAccumulateCount() {
        manager.addSession(1L, session1);
        manager.addSession(1L, session2);
        assertEquals(2, manager.getOnlineCount());
    }

    @Test
    @DisplayName("removeSession 应移除指定会话")
    void removeSession_shouldRemoveSession() {
        manager.addSession(1L, session1);
        manager.removeSession(1L, session1);
        assertEquals(0, manager.getOnlineCount());
    }

    @Test
    @DisplayName("removeSession 移除最后一个会话后应清理用户")
    void removeSession_lastSession_shouldCleanupUser() {
        manager.addSession(1L, session1);
        manager.addSession(1L, session2);
        manager.removeSession(1L, session1);
        assertEquals(1, manager.getOnlineCount());
        manager.removeSession(1L, session2);
        assertEquals(0, manager.getOnlineCount());
    }

    @Test
    @DisplayName("removeSession 对不存在的会话不应抛出异常")
    void removeSession_unknownSession_shouldNotThrow() {
        manager.addSession(1L, session1);
        assertDoesNotThrow(() -> manager.removeSession(1L, session2));
        assertEquals(1, manager.getOnlineCount());
    }

    @Test
    @DisplayName("sendToUser 应发送消息给指定用户")
    void sendToUser_shouldSendMessage() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        manager.addSession(1L, session1);

        manager.sendToUser(1L, "hello");

        verify(session1).sendMessage(new TextMessage("hello"));
    }

    @Test
    @DisplayName("sendToUser 对不存在的用户不应发送")
    void sendToUser_unknownUser_shouldNotSend() {
        manager.sendToUser(999L, "hello");
        verifyNoInteractions(session1);
    }

    @Test
    @DisplayName("sendToUser 对已关闭的会话不应发送")
    void sendToUser_closedSession_shouldNotSend() throws IOException {
        when(session1.isOpen()).thenReturn(false);
        manager.addSession(1L, session1);

        manager.sendToUser(1L, "hello");

        verify(session1, never()).sendMessage(any());
    }

    @Test
    @DisplayName("sendToUser 发送异常不应抛出")
    void sendToUser_sendException_shouldNotThrow() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        doThrow(new IOException("send failed")).when(session1).sendMessage(any());
        manager.addSession(1L, session1);

        assertDoesNotThrow(() -> manager.sendToUser(1L, "hello"));
    }

    @Test
    @DisplayName("broadcast 应发送消息给所有用户")
    void broadcast_shouldSendToAll() throws IOException {
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);
        manager.addSession(1L, session1);
        manager.addSession(2L, session2);

        manager.broadcast("broadcast-msg");

        verify(session1).sendMessage(new TextMessage("broadcast-msg"));
        verify(session2).sendMessage(new TextMessage("broadcast-msg"));
    }

    @Test
    @DisplayName("broadcast 无用户时不应抛出异常")
    void broadcast_noUsers_shouldNotThrow() {
        assertDoesNotThrow(() -> manager.broadcast("msg"));
    }

    @Test
    @DisplayName("getOnlineCount 应返回正确连接数")
    void getOnlineCount_shouldReturnCorrectCount() {
        assertEquals(0, manager.getOnlineCount());
        manager.addSession(1L, session1);
        assertEquals(1, manager.getOnlineCount());
        manager.addSession(1L, session2);
        assertEquals(2, manager.getOnlineCount());
        manager.removeSession(1L, session1);
        assertEquals(1, manager.getOnlineCount());
        manager.removeSession(1L, session2);
        assertEquals(0, manager.getOnlineCount());
    }
}
