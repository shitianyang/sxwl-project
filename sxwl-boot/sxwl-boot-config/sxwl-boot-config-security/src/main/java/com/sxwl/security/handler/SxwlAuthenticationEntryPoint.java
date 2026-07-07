package com.sxwl.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sxwl.common.entity.SxwlResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 401 未认证处理器
 * <p>
 * 当未认证用户访问受保护资源时，返回 401 + SxwlResult JSON。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(SxwlAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper;

    public SxwlAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.debug("未认证访问: {} {}", request.getMethod(), request.getRequestURI());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        SxwlResult<Void> result = SxwlResult.unauthorized("未登录或登录已过期，请重新登录");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
