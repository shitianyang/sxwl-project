package com.sxwl.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlClientTypeUtils} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlClientTypeUtilsTest {

    @Test
    @DisplayName("resolve 请求头为 admin 时返回 admin")
    void resolve_shouldReturnAdmin_whenHeaderIsAdmin() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Client-Type")).thenReturn("admin");
        assertEquals("admin", SxwlClientTypeUtils.resolve(request));
    }

    @Test
    @DisplayName("resolve 请求头为 front 时返回 front")
    void resolve_shouldReturnFront_whenHeaderIsFront() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Client-Type")).thenReturn("front");
        assertEquals("front", SxwlClientTypeUtils.resolve(request));
    }

    @Test
    @DisplayName("resolve 请求头为空时返回 admin")
    void resolve_shouldReturnAdmin_whenHeaderIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Client-Type")).thenReturn(null);
        assertEquals("admin", SxwlClientTypeUtils.resolve(request));
    }

    @Test
    @DisplayName("resolve 请求为 null 时返回 admin")
    void resolve_shouldReturnAdmin_whenRequestIsNull() {
        assertEquals("admin", SxwlClientTypeUtils.resolve(null));
    }

    @Test
    @DisplayName("normalize 不合法值返回 admin")
    void normalize_shouldReturnAdmin_forInvalidValues() {
        assertEquals("admin", SxwlClientTypeUtils.normalize("unknown"));
        assertEquals("admin", SxwlClientTypeUtils.normalize("app"));
        assertEquals("admin", SxwlClientTypeUtils.normalize(""));
        assertEquals("admin", SxwlClientTypeUtils.normalize(null));
    }

    @Test
    @DisplayName("normalize 忽略大小写和前后空格")
    void normalize_shouldIgnoreCaseAndTrim() {
        assertEquals("admin", SxwlClientTypeUtils.normalize("  Admin  "));
        assertEquals("front", SxwlClientTypeUtils.normalize("  Front  "));
        assertEquals("admin", SxwlClientTypeUtils.normalize("ADMIN"));
        assertEquals("front", SxwlClientTypeUtils.normalize("FRONT"));
    }

    @Test
    @DisplayName("normalize 合法值正常返回")
    void normalize_shouldReturnForValidValues() {
        assertEquals("admin", SxwlClientTypeUtils.normalize("admin"));
        assertEquals("front", SxwlClientTypeUtils.normalize("front"));
    }
}
