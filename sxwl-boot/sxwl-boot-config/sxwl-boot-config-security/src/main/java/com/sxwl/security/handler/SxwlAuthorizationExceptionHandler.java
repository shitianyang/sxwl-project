package com.sxwl.security.handler;

import com.sxwl.common.entity.SxwlResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 方法级鉴权拒绝异常处理器（403）
 *
 * <p>{@code @PreAuthorize} 鉴权拒绝时 Spring Security 抛出
 * {@link AccessDeniedException}（6.x 实际为子类 AuthorizationDeniedException）。
 * 若不显式映射，会落入 {@code SxwlGlobalExceptionHandler} 的
 * {@code Exception} 兜底分支返回 500，把"缺权限"伪装成"服务器内部错误"，
 * 极难排查（如权限码与菜单种子数据不一致的场景）。</p>
 *
 * <p>{@code @Order(-100)} 确保本 advice 优先于未指定顺序的通用 advice 被咨询。</p>
 *
 * @author shitianyang
 * @date 2026/9/19
 * @since 0.1.0
 */
@RestControllerAdvice
@Order(-100)
public class SxwlAuthorizationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SxwlAuthorizationExceptionHandler.class);

    /**
     * 处理方法级鉴权拒绝（403）
     *
     * <p>响应契约与过滤器层的 {@link SxwlAccessDeniedHandler} 保持一致。</p>
     *
     * @param e 鉴权拒绝异常
     * @return 403 + 统一响应体
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public SxwlResult<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Authorization denied: {}", e.getMessage());
        return SxwlResult.forbidden("权限不足，无法访问该资源");
    }
}
