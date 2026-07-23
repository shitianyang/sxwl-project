package com.sxwl.sse.controller;

import com.sxwl.common.annotation.SxwlNoWrap;
import com.sxwl.common.utils.SxwlPrincipalUtils;
import com.sxwl.sse.manager.SxwlSseEmitterManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 连接 Controller
 *
 * <p>提供 SSE 连接端点，前端通过 EventSource 连接。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
public class SxwlSseController {

    private final SxwlSseEmitterManager sseEmitterManager;

    public SxwlSseController(SxwlSseEmitterManager sseEmitterManager) {
        this.sseEmitterManager = sseEmitterManager;
    }

    /**
     * 建立 SSE 连接
     * <p>前端通过 {@code new EventSource('/sxwl-api/sse/connect')} 连接。
     * <br>需要携带 Token，后端通过 {@link SxwlPrincipalUtils} 获取当前用户 ID。</p>
     *
     * @return SseEmitter
     */
    @SxwlNoWrap
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        Long userId = SxwlPrincipalUtils.getCurrentPrincipal()
                .map(p -> p.getUserId())
                .orElseThrow(() -> new IllegalStateException("未登录"));
        return sseEmitterManager.connect(userId);
    }
}
