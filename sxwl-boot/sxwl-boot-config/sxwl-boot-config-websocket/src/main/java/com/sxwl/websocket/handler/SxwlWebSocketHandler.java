package com.sxwl.websocket.handler;

import com.sxwl.websocket.manager.SxwlWebSocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 消息处理器
 *
 * <p>处理心跳、通道订阅、消息推送等。支持简单消息协议：
 * <ul>
 *   <li>{@code {"type":"ping"}} — 心跳回复 pong</li>
 *   <li>{@code {"type":"subscribe", "channel":"..."}} — 订阅通道</li>
 *   <li>{@code {"type":"unsubscribe", "channel":"..."}} — 取消订阅</li>
 * </ul></p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Component
public class SxwlWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SxwlWebSocketHandler.class);

    private final SxwlWebSocketSessionManager sessionManager;

    /** sessionId → 已订阅的通道集合 */
    private final Map<String, Set<String>> subscriptions = new ConcurrentHashMap<>();

    public SxwlWebSocketHandler(SxwlWebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.addSession(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        // 简单 JSON 解析（不使用 Jackson 避免依赖）
        if (payload.contains("\"ping\"")) {
            session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
        } else if (payload.contains("\"subscribe\"")) {
            handleSubscribe(session, payload);
        } else if (payload.contains("\"unsubscribe\"")) {
            handleUnsubscribe(session, payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.removeSession(userId, session);
        }
        subscriptions.remove(session.getId());
    }

    // ==================== 私有方法 ====================

    private void handleSubscribe(WebSocketSession session, String payload) {
        String channel = extractChannel(payload);
        if (channel != null) {
            subscriptions.computeIfAbsent(session.getId(), k -> ConcurrentHashMap.newKeySet()).add(channel);
            log.debug("WebSocket 订阅通道: sessionId={}, channel={}", session.getId(), channel);
        }
    }

    private void handleUnsubscribe(WebSocketSession session, String payload) {
        String channel = extractChannel(payload);
        if (channel != null) {
            Set<String> channels = subscriptions.get(session.getId());
            if (channels != null) {
                channels.remove(channel);
            }
        }
    }

    /**
     * 从 JSON payload 中提取 channel 字段值（简易解析）
     */
    private String extractChannel(String payload) {
        int channelIdx = payload.indexOf("\"channel\"");
        if (channelIdx < 0) return null;
        int colonIdx = payload.indexOf(':', channelIdx);
        if (colonIdx < 0) return null;
        // 跳过冒号和引号
        int start = payload.indexOf('"', colonIdx + 1);
        if (start < 0) return null;
        int end = payload.indexOf('"', start + 1);
        if (end < 0) return null;
        return payload.substring(start + 1, end);
    }
}
