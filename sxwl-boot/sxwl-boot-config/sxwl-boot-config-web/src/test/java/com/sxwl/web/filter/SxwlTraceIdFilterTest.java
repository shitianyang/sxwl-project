package com.sxwl.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlTraceIdFilter} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SxwlTraceIdFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    private SxwlTraceIdFilter filter;

    @BeforeEach
    void setUp() {
        filter = new SxwlTraceIdFilter();
    }

    @Test
    @DisplayName("无传入 TraceId 时自动生成并设置响应头和 MDC")
    void doFilter_shouldGenerateTraceId_whenNotProvided() throws Exception {
        when(request.getHeader("X-Request-Id")).thenReturn(null);

        // 在 FilterChain 执行时验证 MDC 已设置（filter 的 finally 块会清理 MDC）
        final String[] capturedTraceId = new String[1];
        doAnswer(invocation -> {
            capturedTraceId[0] = MDC.get("traceId");
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(request, response, chain);

        ArgumentCaptor<String> traceIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("X-Request-Id"), traceIdCaptor.capture());
        String traceId = traceIdCaptor.getValue();
        assertNotNull(traceId);
        assertFalse(traceId.isEmpty());
        assertFalse(traceId.contains("-")); // UUID 已去横线
        assertNotNull(capturedTraceId[0]);
        assertEquals(traceId, capturedTraceId[0]);

        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("有传入 TraceId 时透传")
    void doFilter_shouldUseProvidedTraceId() throws Exception {
        when(request.getHeader("X-Request-Id")).thenReturn("client-trace-id");

        // 在 FilterChain 执行时验证 MDC 已设置
        final String[] capturedTraceId = new String[1];
        doAnswer(invocation -> {
            capturedTraceId[0] = MDC.get("traceId");
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilter(request, response, chain);

        verify(response).setHeader("X-Request-Id", "client-trace-id");
        assertEquals("client-trace-id", capturedTraceId[0]);
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("FilterChain 执行后 MDC 应被清理")
    void doFilter_shouldCleanMDCAfterChain() throws Exception {
        when(request.getHeader("X-Request-Id")).thenReturn("test-trace");

        filter.doFilter(request, response, chain);

        assertNull(MDC.get("traceId"));
    }
}
