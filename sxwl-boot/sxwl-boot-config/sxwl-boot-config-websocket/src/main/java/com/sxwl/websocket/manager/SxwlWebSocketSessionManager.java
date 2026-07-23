package com.sxwl.websocket.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket Session 管理器
 *
 * <p>管理所有活跃的 WebSocket 连接，支持按用户推送和全局广播。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Component
public class SxwlWebSocketSessionManager {

    private static final Logger log = LoggerFactory.getLogger(SxwlWebSocketSessionManager.class);

    /** userId → Set<Session>（一个用户可能在多个标签页打开连接） */
    private final ConcurrentMap<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    /**
     * 注册 Session
     *
     * @param userId  用户 ID
     * @param session WebSocket Session
     */
    public void addSession(Long userId, WebSocketSession session) {
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
        log.debug("WebSocket 连接注册: userId={}, sessionId={}", userId, session.getId());
    }

    /**
     * 移除 Session
     *
     * @param userId  用户 ID
     * @param session WebSocket Session
     */
    public void removeSession(Long userId, WebSocketSession session) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
        log.debug("WebSocket 连接移除: userId={}, sessionId={}", userId, session.getId());
    }

    /**
     * 推送给指定用户的所有会话
     *
     * @param userId  用户 ID
     * @param message 消息内容
     */
    public void sendToUser(Long userId, String message) {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new org.springframework.web.socket.TextMessage(message));
                } catch (Exception e) {
                    log.warn("WebSocket 推送失败: userId={}, sessionId={}", userId, session.getId());
                }
            }
        }
    }

    /**
     * 推送给所有在线用户
     *
     * @param message 消息内容
     */
    public void broadcast(String message) {
        userSessions.forEach((userId, sessions) -> {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new org.springframework.web.socket.TextMessage(message));
                    } catch (Exception e) {
                        log.warn("WebSocket 广播失败: userId={}, sessionId={}", userId, session.getId());
                    }
                }
            }
        });
    }

    /**
     * 获取在线连接数
     *
     * @return 总连接数
     */
    public int getOnlineCount() {
        return (int) userSessions.values().stream().mapToLong(Set::size).sum();
    }
}
