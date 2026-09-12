package com.sxwl.web.advice;

import com.sxwl.common.entity.SxwlResult;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.exception.SxwlForbiddenException;
import com.sxwl.common.exception.SxwlRepeatSubmitException;
import com.sxwl.common.exception.SxwlUnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 全局异常处理器单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SxwlGlobalExceptionHandler 测试")
class SxwlGlobalExceptionHandlerTest {

    private final SxwlGlobalExceptionHandler handler = new SxwlGlobalExceptionHandler();

    @Test
    @DisplayName("SxwlUnauthorizedException → 401")
    void testUnauthorizedException() {
        SxwlResult<Void> result = handler.handleUnauthorizedException(
                new SxwlUnauthorizedException("Token 已过期"));
        assertEquals(401, result.getCode());
        assertEquals("Token 已过期", result.getMessage());
    }

    @Test
    @DisplayName("SxwlForbiddenException → 403")
    void testForbiddenException() {
        SxwlResult<Void> result = handler.handleForbiddenException(
                new SxwlForbiddenException("无权访问"));
        assertEquals(403, result.getCode());
        assertEquals("无权访问", result.getMessage());
    }

    @Test
    @DisplayName("SxwlBusinessException → 自定义错误码")
    void testBusinessException() {
        SxwlResult<Void> result = handler.handleBusinessException(
                new SxwlBusinessException(10002, "密码强度不足"));
        assertEquals(10002, result.getCode());
        assertEquals("密码强度不足", result.getMessage());
    }

    @Test
    @DisplayName("SxwlRepeatSubmitException → 10001")
    void testRepeatSubmitException() {
        SxwlResult<Void> result = handler.handleRepeatSubmitException(
                new SxwlRepeatSubmitException("请勿重复提交"));
        assertEquals(10001, result.getCode());
        assertEquals("请勿重复提交", result.getMessage());
    }

    @Test
    @DisplayName("NoHandlerFoundException → 10001")
    void testNotFoundException() {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/api/not-found", null);
        SxwlResult<Void> result = handler.handleNotFoundException(ex);
        assertEquals(10001, result.getCode());
        assertEquals("请求的接口不存在", result.getMessage());
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException → 10001")
    void testMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("PUT", List.of("GET"));
        SxwlResult<Void> result = handler.handleMethodNotSupported(ex);
        assertEquals(10001, result.getCode());
    }

    @Test
    @DisplayName("HttpMediaTypeNotSupportedException → 10001")
    void testMediaTypeNotSupported() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("text/plain");
        SxwlResult<Void> result = handler.handleMediaTypeNotSupported(ex);
        assertEquals(10001, result.getCode());
    }

    @Test
    @DisplayName("Exception → 10001（默认业务错误码）")
    void testGenericException() {
        SxwlResult<Void> result = handler.handleException(new RuntimeException("DB error"));
        // handleException 调用 SxwlResult.error(message) 使用默认错误码 10001
        assertEquals(10001, result.getCode());
        assertEquals("服务器内部错误", result.getMessage());
    }
}
