package com.sxwl.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * 请求日志过滤器
 *
 * <p>记录每个 HTTP 请求的 method、URI、IP、耗时、状态码，
 * 用于开发排查和生产审计。</p>
 *
 * <p>日志格式：{@code [REQUEST] GET /api/user/list | IP=192.168.1.1 | 200 | 23ms}</p>
 *
 * <p>可通过 {@code sxwl.web.request-log-enabled=false} 在生产环境关闭。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlRequestLogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(SxwlRequestLogFilter.class);

    private final boolean enabled;

    public SxwlRequestLogFilter(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        long startTime = System.currentTimeMillis();

        // SSE (text/event-stream) 响应需要直接流式输出到客户端，
        // ContentCachingResponseWrapper 会缓冲所有响应体，
        // 导致前端 EventSource 永远收不到响应头，"open" 事件永不触发
        if (isSseRequest(httpRequest)) {
            try {
                chain.doFilter(httpRequest, httpResponse);
            } finally {
                logRequest(httpRequest, httpResponse, startTime);
            }
            return;
        }

        // 非 SSE 请求：包装 response 以便读取响应体用于日志
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            logRequest(httpRequest, responseWrapper, startTime);
            // 对于非 SSE 请求，将缓存的响应内容写回客户端
            responseWrapper.copyBodyToResponse();
        }
    }

    /**
     * 判断是否为 SSE 请求：EventSource 在浏览器中会自动设置 Accept: text/event-stream
     */
    private boolean isSseRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/event-stream");
    }

    /**
     * 记录请求日志（提取为方法避免 SSE/非 SSE 分支重复）
     */
    private void logRequest(HttpServletRequest request, HttpServletResponse response, long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        int status = response.getStatus();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String fullUri = query != null ? uri + "?" + query : uri;
        String ip = getClientIp(request);

        if (status >= 500) {
            log.error("[REQUEST] {} {} | IP={} | {} | {}ms", method, fullUri, ip, status, elapsed);
        } else if (status >= 400) {
            log.warn("[REQUEST] {} {} | IP={} | {} | {}ms", method, fullUri, ip, status, elapsed);
        } else {
            log.info("[REQUEST] {} {} | IP={} | {} | {}ms", method, fullUri, ip, status, elapsed);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
