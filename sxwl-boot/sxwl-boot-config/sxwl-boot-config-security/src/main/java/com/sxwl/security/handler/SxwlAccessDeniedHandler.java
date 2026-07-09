package com.sxwl.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sxwl.common.entity.SxwlResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 403 无权限处理器
 * <p>
 * 当已认证用户访问无权限资源时，返回 403 + SxwlResult JSON。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlAccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(SxwlAccessDeniedHandler.class);
    private final ObjectMapper objectMapper;

    public SxwlAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.debug("无权限访问: {} {} - {}", request.getMethod(), request.getRequestURI(),
                accessDeniedException.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        SxwlResult<Void> result = SxwlResult.forbidden("权限不足，无法访问该资源");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
