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
 * SSE 连接业务控制器
 *
 * <p>提供 SSE 连接端点，前端通过 EventSource 连接。</p>
 *
 * <h3>设计原则</h3>
 * <ul>
 *   <li><b>业务逻辑层</b>：负责连接票据签发和连接建立的业务流程</li>
 *   <li><b>基础设施层</b>：config-sse 只提供 SxwlSseEmitterManager 工具类</li>
 *   <li><b>安全认证</b>：使用 security 模块的连接票据服务，避免 Token 暴露在 URL 中</li>
 * </ul>
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

    /**
     * 签发一次性 SSE/WebSocket 连接票据
     *
     * <p>票据有效期 60 秒，只能使用一次，用于前端建立连接时的身份验证。</p>
     *
     * @return 连接票据字符串
     */
    @PostMapping("/sse/ticket")
    public SxwlResult<String> createTicket() {
        Long userId = SxwlPrincipalUtils.getCurrentPrincipal()
                .map(p -> p.getUserId())
                .orElseThrow(() -> new IllegalStateException("未登录"));
        return SxwlResult.success(connectionTicketService.issue(userId));
    }

    /**
     * 建立 SSE 连接
     *
     * <p>前端使用一次性 ticket 建立连接，避免将 access token 写入 URL。</p>
     *
     * @param ticket 一次性连接票据（60 秒有效，只能使用一次）
     * @return SSE 连接发射器
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
