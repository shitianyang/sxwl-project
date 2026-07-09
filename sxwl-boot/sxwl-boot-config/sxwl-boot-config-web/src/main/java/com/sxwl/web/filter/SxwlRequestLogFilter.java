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
        long startTime = System.currentTimeMillis();

        // 包装 request/response 以便读取 body（按需使用）
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long elapsed = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();
            String method = httpRequest.getMethod();
            String uri = httpRequest.getRequestURI();
            String query = httpRequest.getQueryString();
            String fullUri = query != null ? uri + "?" + query : uri;
            String ip = getClientIp(httpRequest);

            if (status >= 500) {
                log.error("[REQUEST] {} {} | IP={} | {} | {}ms", method, fullUri, ip, status, elapsed);
            } else if (status >= 400) {
                log.warn("[REQUEST] {} {} | IP={} | {} | {}ms", method, fullUri, ip, status, elapsed);
            } else {
                log.info("[REQUEST] {} {} | IP={} | {} | {}ms", method, fullUri, ip, status, elapsed);
            }

            // 将缓存的响应内容写回客户端
            responseWrapper.copyBodyToResponse();
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
