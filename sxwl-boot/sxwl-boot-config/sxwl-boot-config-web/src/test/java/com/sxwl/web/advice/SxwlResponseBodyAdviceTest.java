package com.sxwl.web.advice;

import com.sxwl.common.annotation.SxwlNoWrap;
import com.sxwl.common.entity.SxwlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 统一 Result 包装切面单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SxwlResponseBodyAdvice 测试")
class SxwlResponseBodyAdviceTest {

    private final SxwlResponseBodyAdvice advice = new SxwlResponseBodyAdvice();
    private final ServerHttpRequest request = mock(ServerHttpRequest.class);
    private final ServerHttpResponse response = mock(ServerHttpResponse.class);

    /** 带 @SxwlNoWrap 的方法 */
    @SxwlNoWrap
    String noWrapMethod() { return "no-wrap"; }

    /** 不带 @SxwlNoWrap 的方法 */
    String normalMethod() { return "normal"; }

    @Test
    @DisplayName("supports：非 @SxwlNoWrap 方法返回 true")
    void testSupportsNormal() throws Exception {
        Method method = getClass().getDeclaredMethod("normalMethod");
        MethodParameter parameter = methodParameter(method);
        assertTrue(advice.supports(parameter, null));
    }

    @Test
    @DisplayName("supports：@SxwlNoWrap 方法返回 false")
    void testSupportsNoWrap() throws Exception {
        Method method = getClass().getDeclaredMethod("noWrapMethod");
        MethodParameter parameter = methodParameter(method);
        assertFalse(advice.supports(parameter, null));
    }

    @Test
    @DisplayName("beforeBodyWrite：非 SxwlResult 包装为 success")
    void testWrapNonResult() {
        Object result = advice.beforeBodyWrite(
                "hello", null, MediaType.APPLICATION_JSON, null,
                request, response);
        assertInstanceOf(SxwlResult.class, result);
        SxwlResult<?> sxwlResult = (SxwlResult<?>) result;
        assertEquals(200, sxwlResult.getCode());
        assertEquals("hello", sxwlResult.getData());
    }

    @Test
    @DisplayName("beforeBodyWrite：SxwlResult 不重复包装")
    void testSkipWrappingResult() {
        SxwlResult<String> original = SxwlResult.error("已有错误");
        Object result = advice.beforeBodyWrite(
                original, null, MediaType.APPLICATION_JSON, null,
                request, response);
        assertSame(original, result, "SxwlResult 不应重复包装");
    }

    @Test
    @DisplayName("beforeBodyWrite：null body 包装为 success(null)")
    void testWrapNullBody() {
        Object result = advice.beforeBodyWrite(
                null, null, MediaType.APPLICATION_JSON, null,
                request, response);
        assertInstanceOf(SxwlResult.class, result);
        assertNull(((SxwlResult<?>) result).getData());
    }

    /** 构造 MethodParameter */
    private MethodParameter methodParameter(Method method) {
        return new MethodParameter(method, -1);
    }
}
