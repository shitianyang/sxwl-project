package com.sxwl.sse.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlSseEmitterManager} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SxwlSseEmitterManager 测试")
class SxwlSseEmitterManagerTest {

    private final SxwlSseEmitterManager manager = new SxwlSseEmitterManager();

    @Test
    @DisplayName("connect 应返回 SseEmitter 并增加在线用户数")
    void connect_shouldReturnEmitterAndIncreaseCount() {
        assertEquals(0, manager.getOnlineCount());

        SseEmitter emitter = manager.connect(1L);
        assertNotNull(emitter);
        assertEquals(1, manager.getOnlineCount());
    }

    @Test
    @DisplayName("connect 同一用户多次调用，在线用户数不重复计算")
    void connect_sameUserMultiple_shouldNotDuplicateCount() {
        manager.connect(1L);
        manager.connect(1L);
        assertEquals(1, manager.getOnlineCount());
    }

    @Test
    @DisplayName("disconnect 应移除用户并减少在线数")
    void disconnect_shouldRemoveUser() {
        manager.connect(1L);
        manager.disconnect(1L);
        assertEquals(0, manager.getOnlineCount());
    }

    @Test
    @DisplayName("disconnect 对不存在的用户不应抛出异常")
    void disconnect_unknownUser_shouldNotThrow() {
        assertDoesNotThrow(() -> manager.disconnect(999L));
    }

    @Test
    @DisplayName("sendToUser 应推送给指定用户")
    void sendToUser_shouldSendToTargetUser() {
        manager.connect(1L);
        assertDoesNotThrow(() -> manager.sendToUser(1L, "event", "test-data"));
    }

    @Test
    @DisplayName("sendToUser 对不存在的用户不应抛出异常")
    void sendToUser_unknownUser_shouldNotThrow() {
        assertDoesNotThrow(() -> manager.sendToUser(999L, "event", "data"));
    }

    @Test
    @DisplayName("sendToAll 应广播给所有用户")
    void sendToAll_shouldSendToAllUsers() {
        manager.connect(1L);
        manager.connect(2L);
        assertDoesNotThrow(() -> manager.sendToAll("event", "data"));
    }

    @Test
    @DisplayName("sendToAll 无在线用户时不应抛出异常")
    void sendToAll_noUsers_shouldNotThrow() {
        assertDoesNotThrow(() -> manager.sendToAll("event", "data"));
    }

    @Test
    @DisplayName("getOnlineCount 应返回正确在线用户数")
    void getOnlineCount_shouldReturnCorrectCount() {
        assertEquals(0, manager.getOnlineCount());
        manager.connect(1L);
        assertEquals(1, manager.getOnlineCount());
        manager.connect(2L);
        assertEquals(2, manager.getOnlineCount());
        manager.disconnect(1L);
        assertEquals(1, manager.getOnlineCount());
    }
}
