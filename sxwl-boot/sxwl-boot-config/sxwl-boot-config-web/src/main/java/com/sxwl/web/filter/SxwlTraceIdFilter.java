package com.sxwl.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * TraceId 过滤器
 *
 * <p>为每个 HTTP 请求注入唯一 TraceId，方便跨服务排查问题：</p>
 * <ul>
 *   <li>写入 {@code X-Request-Id} 响应头</li>
 *   <li>写入 SLF4J {@link MDC}，日志中可通过 {@code %X{traceId}} 输出</li>
 *   <li>客户端传入的 {@code X-Request-Id} 优先使用（透传），否则自动生成</li>
 * </ul>
 *
 * <p><b>日志配置示例（logback-spring.xml）：</b></p>
 * <pre>{@code
 * <pattern>%d{HH:mm:ss.SSS} [%thread] [%X{traceId}] %-5level %logger{36} - %msg%n</pattern>
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlTraceIdFilter implements Filter {

    public static final String TRACE_ID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "traceId";
    private static final Pattern TRACE_ID_PATTERN = Pattern.compile("^[A-Za-z0-9._\\-]{1,64}$");

    private static boolean isValidTraceId(String traceId) {
        return traceId != null && !traceId.isEmpty() && TRACE_ID_PATTERN.matcher(traceId).matches();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 优先使用客户端传入的 TraceId（透传），否则自动生成
        // 清洗：仅允许安全字符且长度受限，防止日志注入（换行/控制字符）与超长污染（L36）
        String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
        if (!isValidTraceId(traceId)) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        // 写入 MDC，日志中可通过 %X{traceId} 输出
        MDC.put(MDC_KEY, traceId);
        // 写入响应头
        httpResponse.setHeader(TRACE_ID_HEADER, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
