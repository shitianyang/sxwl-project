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
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlAccessDeniedHandler} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SxwlAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;

    @Captor
    private ArgumentCaptor<String> responseBody;

    private SxwlAccessDeniedHandler handler;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        handler = new SxwlAccessDeniedHandler(objectMapper);
        lenient().when(request.getMethod()).thenReturn("GET");
        lenient().when(request.getRequestURI()).thenReturn("/api/admin/users");
    }

    @Test
    @DisplayName("handle 应设置 403 状态并返回 SxwlResult.forbidden")
    void handle_shouldReturn403() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        handler.handle(request, response, new AccessDeniedException("权限不足"));

        verify(response).setStatus(403);
        verify(response).setContentType("application/json");
        verify(writer).write(responseBody.capture());

        String json = responseBody.getValue();
        assertTrue(json.contains("\"code\":403") || json.contains("\"code\":403"));
    }

    @Test
    @DisplayName("handle 返回 JSON 包含权限不足消息")
    void handle_shouldContainForbiddenMessage() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        handler.handle(request, response, new AccessDeniedException("权限不足"));

        verify(writer).write(responseBody.capture());
        String json = responseBody.getValue();
        assertTrue(json.contains("权限不足"));
    }

    @Test
    @DisplayName("handle 返回的 JSON 应能反序列化为 SxwlResult")
    void handle_shouldReturnValidSxwlResult() throws Exception {
        when(response.getWriter()).thenReturn(writer);

        handler.handle(request, response, new AccessDeniedException("权限不足"));

        verify(writer).write(responseBody.capture());
        ObjectMapper objectMapper = new ObjectMapper();
        SxwlResult result = objectMapper.readValue(responseBody.getValue(), SxwlResult.class);
        assertEquals(403, result.getCode());
    }
}
