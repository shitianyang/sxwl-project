package com.sxwl.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * {@link SxwlRequestLogFilter} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SxwlRequestLogFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    @Test
    @DisplayName("enabled=false 时直接放行，不包装 request/response")
    void doFilter_shouldSkip_whenDisabled() throws Exception {
        SxwlRequestLogFilter filter = new SxwlRequestLogFilter(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("enabled=true 时正常执行 FilterChain")
    void doFilter_shouldProceed_whenEnabled() throws Exception {
        SxwlRequestLogFilter filter = new SxwlRequestLogFilter(true);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    @DisplayName("SSE 请求应直接放行（不包装 response）")
    void doFilter_shouldNotWrapResponse_forSseRequest() throws Exception {
        SxwlRequestLogFilter filter = new SxwlRequestLogFilter(true);
        when(request.getHeader("Accept")).thenReturn("text/event-stream");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/sse");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        filter.doFilter(request, response, chain);

        // SSE 请求直接调 chain.doFilter 传原始 request/response
        verify(chain).doFilter(request, response);
    }
}
