package com.sxwl.websocket.handler;

import com.sxwl.websocket.manager.SxwlWebSocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket 消息处理器
 *
 * <p>处理心跳检测。支持简单的消息协议：
 * <ul>
 *   <li>{@code {"type":"ping"}} — 心跳回复 pong</li>
 * </ul></p>
 *
 * <h3>架构定位</h3>
 * <p>属于<strong>基础设施层</strong>，提供最基础的心跳检测能力。具体业务逻辑（如通知推送、消息转发等）由 {@code module-*} 实现。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Component
public class SxwlWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SxwlWebSocketHandler.class);

    private final SxwlWebSocketSessionManager sessionManager;

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
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.removeSession(userId, session);
        }
    }
}
