package com.sxwl.web.advice;

import com.sxwl.common.entity.SxwlResult;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.exception.SxwlForbiddenException;
import com.sxwl.common.exception.SxwlUnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * <p>统一拦截所有未捕获异常，转换为 {@link SxwlResult} 格式返回。</p>
 *
 * <p><b>处理的异常类型：</b></p>
 * <ul>
 *   <li>{@link SxwlBusinessException} → 取异常的 code 和 message</li>
 *   <li>{@link MethodArgumentNotValidException} → code=10001，拼接字段校验错误</li>
 *   <li>{@link HttpMessageNotReadableException} → code=10001，"请求参数格式错误"</li>
 *   <li>{@link Exception} → code=500，日志记录完整堆栈，前端只返回"服务器内部错误"</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
public class SxwlGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SxwlGlobalExceptionHandler.class);

    /**
     * 处理未认证异常（401）
     */
    @ExceptionHandler(SxwlUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public SxwlResult<Void> handleUnauthorizedException(SxwlUnauthorizedException e) {
        log.warn("Unauthorized: {}", e.getMessage());
        return SxwlResult.unauthorized(e.getMessage());
    }

    /**
     * 处理无权限异常（403）
     */
    @ExceptionHandler(SxwlForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public SxwlResult<Void> handleForbiddenException(SxwlForbiddenException e) {
        log.warn("Forbidden: {}", e.getMessage());
        return SxwlResult.forbidden(e.getMessage());
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(SxwlBusinessException.class)
    public SxwlResult<Void> handleBusinessException(SxwlBusinessException e) {
        log.warn("Business exception: code={}, msg={}", e.getCode(), e.getMessage());
        return SxwlResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验失败（@Valid / @Validated）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SxwlResult<Void> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", msg);
        return SxwlResult.error("参数校验失败: " + msg);
    }

    /**
     * 处理请求体格式错误（如 JSON 格式错误）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public SxwlResult<Void> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Request body not readable: {}", e.getMessage());
        return SxwlResult.error("请求参数格式错误");
    }

    /**
     * 处理未知异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SxwlResult<Void> handleException(Exception e) {
        log.error("Unexpected exception", e);
        return SxwlResult.error("服务器内部错误");
    }
}
