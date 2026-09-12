package com.sxwl.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sxwl.common.entity.SxwlResult;
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
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlAuthenticationEntryPoint} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SxwlAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;

    @Captor
    private ArgumentCaptor<String> responseBody;

    private SxwlAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        entryPoint = new SxwlAuthenticationEntryPoint(objectMapper);
        lenient().when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getRequestURI()).thenReturn("/api/admin/users");
    }

    @Test
    @DisplayName("commence 应设置 401 状态并返回 SxwlResult.unauthorized")
    void commence_shouldReturn401() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        entryPoint.commence(request, response, new AuthenticationException("未登录") {});

        verify(response).setStatus(401);
        verify(response).setContentType("application/json");
        verify(writer).write(responseBody.capture());

        String json = responseBody.getValue();
        assertTrue(json.contains("\"code\":401") || json.contains("\"code\":401"));
    }

    @Test
    @DisplayName("commence 返回 JSON 包含未登录消息")
    void commence_shouldContainUnauthorizedMessage() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        entryPoint.commence(request, response, new AuthenticationException("未登录") {});

        verify(writer).write(responseBody.capture());
        String json = responseBody.getValue();
        assertTrue(json.contains("未登录") || json.contains("登录已过期"));
    }

    @Test
    @DisplayName("commence 返回的 JSON 应能反序列化为 SxwlResult")
    void commence_shouldReturnValidSxwlResult() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        entryPoint.commence(request, response, new AuthenticationException("未登录") {});

        verify(writer).write(responseBody.capture());
        ObjectMapper objectMapper = new ObjectMapper();
        SxwlResult result = objectMapper.readValue(responseBody.getValue(), SxwlResult.class);
        assertEquals(401, result.getCode());
    }
}
