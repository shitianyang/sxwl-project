package com.sxwl.redis.aspect;

import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.common.principal.SxwlPrincipal;
import com.sxwl.redis.helper.SxwlRedisHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlRepeatSubmitAspect} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SxwlRepeatSubmitAspectTest {

    @Mock
    private SxwlRedisHelper redisHelper;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private SxwlRepeatSubmit repeatSubmit;
    @Mock
    private ServletRequestAttributes requestAttributes;

    private SxwlRepeatSubmitAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new SxwlRepeatSubmitAspect(redisHelper);
        // 默认 mock 注解
        lenient().when(repeatSubmit.interval()).thenReturn(3);
        lenient().when(repeatSubmit.message()).thenReturn("请勿重复提交");
    }

    @Test
    @DisplayName("首次请求应放行")
    void around_shouldProceed_whenFirstRequest() throws Throwable {
        Long userId = 1L;
        setupSecurityContext(userId);
        setupRequestContext("/api/test");

        when(redisHelper.setIfAbsent(anyString(), eq("1"), any(Duration.class))).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("success");

        Object result = aspect.around(joinPoint, repeatSubmit);
        assertEquals("success", result);
    }

    @Test
    @DisplayName("重复请求应抛出 SxwlRepeatSubmitException")
    void around_shouldThrow_whenRepeatRequest() {
        Long userId = 1L;
        setupSecurityContext(userId);
        setupRequestContext("/api/test");

        when(redisHelper.setIfAbsent(anyString(), eq("1"), any(Duration.class))).thenReturn(false);

        assertThrows(com.sxwl.common.exception.SxwlRepeatSubmitException.class,
                () -> aspect.around(joinPoint, repeatSubmit));
    }

    @Test
    @DisplayName("未登录用户直接放行")
    void around_shouldProceed_whenNotLoggedIn() throws Throwable {
        SecurityContextHolder.clearContext();
        setupRequestContext("/api/test");

        when(joinPoint.proceed()).thenReturn("anonymous");

        Object result = aspect.around(joinPoint, repeatSubmit);
        assertEquals("anonymous", result);
        verify(redisHelper, never()).setIfAbsent(anyString(), anyString(), any());
    }

    private void setupSecurityContext(Long userId) {
        SxwlPrincipal principal = new SxwlPrincipal() {
            @Override public Long getUserId() { return userId; }
            @Override public Long getOrgId() { return 0L; }
        };
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null));
    }

    private void setupRequestContext(String uri) {
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        lenient().when(request.getRequestURI()).thenReturn(uri);
        lenient().when(requestAttributes.getRequest()).thenReturn(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }
}
