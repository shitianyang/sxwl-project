package com.sxwl.sse.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SSE 连接管理器
 *
 * <p>管理所有活跃的 SSE 连接，支持多标签页连接，为所有连接提供心跳检测。</p>
 *
 * <ul>
 *   <li>多标签：同一用户允许建立多个 SSE 连接（{@link CopyOnWriteArrayList}）</li>
 *   <li>心跳：每 30 秒向所有连接发送 heartbeat 事件，自动清理已断开的连接</li>
 *   <li>超时：每个 emitter 最长存活 30 分钟</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Component
public class SxwlSseEmitterManager {

    private static final Logger log = LoggerFactory.getLogger(SxwlSseEmitterManager.class);

    /** 默认 SSE 超时时间：30 分钟 */
    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000L;

    /** 默认心跳间隔：30 秒 */
    private static final long HEARTBEAT_INTERVAL_MS = 30_000L;

    /** userId → [SseEmitter, ...]（支持多标签页） */
    private final Map<Long, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>();

    /** 心跳定时任务 */
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "sse-heartbeat");
        t.setDaemon(true);
        return t;
    });

    public SxwlSseEmitterManager() {
        heartbeatScheduler.scheduleAtFixedRate(this::sendHeartbeat,
                HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * 建立 SSE 连接
     *
     * @param userId 用户 ID
     * @return SseEmitter 实例
     */
    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        // 注册回调：完成后/超时/出错时自动清理
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        // 支持多标签：加到已有列表的尾部
        emitterMap.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        log.debug("SSE 连接建立: userId={}", userId);
        return emitter;
    }

    /**
     * 断开用户所有连接
     *
     * @param userId 用户 ID
     */
    public void disconnect(Long userId) {
        List<SseEmitter> emitters = emitterMap.remove(userId);
        if (emitters != null) {
            emitters.forEach(SseEmitter::complete);
            log.debug("SSE 连接断开: userId={}", userId);
        }
    }

    /**
     * 推送给指定用户（所有标签页）
     *
     * @param userId    用户 ID
     * @param eventName 事件名称
     * @param data      推送数据
     */
    public void sendToUser(Long userId, String eventName, Object data) {
        List<SseEmitter> emitters = emitterMap.get(userId);
        if (emitters == null || emitters.isEmpty()) {
            log.trace("用户 SSE 连接不存在: userId={}", userId);
            return;
        }
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                log.debug("SSE 推送失败，移除连接: userId={}", userId);
                removeEmitter(userId, emitter);
            }
        }
    }

    /**
     * 推送给所有在线用户（所有标签页）
     *
     * @param eventName 事件名称
     * @param data      推送数据
     */
    public void sendToAll(String eventName, Object data) {
        emitterMap.forEach((userId, emitters) -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(eventName)
                            .data(data));
                } catch (IOException e) {
                    log.debug("SSE 广播失败，移除连接: userId={}", userId);
                    removeEmitter(userId, emitter);
                }
            }
        });
    }

    /**
     * 获取在线用户数（不去重，一个用户多标签算一个）
     *
     * @return 当前活跃用户数
     */
    public int getOnlineCount() {
        return emitterMap.size();
    }

    /**
     * 向所有连接发送心跳事件，同时清理已断开的连接
     */
    private void sendHeartbeat() {
        if (emitterMap.isEmpty()) {
            return;
        }
        log.trace("SSE 心跳: 在线用户数={}", emitterMap.size());

        emitterMap.forEach((userId, emitters) -> {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("heartbeat")
                            .data(""));
                } catch (IOException e) {
                    log.debug("SSE 心跳失败，移除连接: userId={}", userId);
                    removeEmitter(userId, emitter);
                }
            }
        });
    }

    /**
     * 移除指定用户的指定 emitter
     */
    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> emitters = emitterMap.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emitterMap.remove(userId);
            }
        }
    }
}
