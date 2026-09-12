package com.sxwl.sse.controller;

import com.sxwl.common.annotation.SxwlNoWrap;
import com.sxwl.common.entity.SxwlResult;
import com.sxwl.common.utils.SxwlPrincipalUtils;
import com.sxwl.security.ticket.SxwlConnectionTicketService;
import com.sxwl.sse.manager.SxwlSseEmitterManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final SxwlConnectionTicketService connectionTicketService;

    public SxwlSseController(SxwlSseEmitterManager sseEmitterManager,
                             SxwlConnectionTicketService connectionTicketService) {
        this.sseEmitterManager = sseEmitterManager;
        this.connectionTicketService = connectionTicketService;
    }

    /** 签发 60 秒内有效的一次性 SSE/WebSocket 连接票据。 */
    @PostMapping("/sse/ticket")
    public SxwlResult<String> createTicket() {
        Long userId = SxwlPrincipalUtils.getCurrentPrincipal()
                .map(p -> p.getUserId())
                .orElseThrow(() -> new IllegalStateException("未登录"));
        return SxwlResult.success(connectionTicketService.issue(userId));
    }

    /**
     * 建立 SSE 连接
     * <p>前端使用一次性 ticket 建立连接，避免将 access token 写入 URL。</p>
     *
     * @return SseEmitter
     */
    @SxwlNoWrap
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@RequestParam String ticket) {
        // 失效/过期的一次性票据返回 401，前端据此判定为致命错误并停止重连（避免重连风暴）。
        return connectionTicketService.consume(ticket)
                .map(userId -> ResponseEntity.ok(sseEmitterManager.connect(userId)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
