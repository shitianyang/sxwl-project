package com.sxwl.common.utils;

import com.sxwl.common.principal.SxwlPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SxwlPrincipalUtils 工具类单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SxwlPrincipalUtils 工具类测试")
class SxwlPrincipalUtilsTest {

    /** 测试用 Principal 实现 */
    private record TestPrincipal(Long userId, Long orgId, Set<Long> dataScopeOrgIds) implements SxwlPrincipal {
        @Override
        public Long getUserId() { return userId; }
        @Override
        public Long getOrgId() { return orgId; }
        @Override
        public Set<Long> getDataScopeOrgIds() { return dataScopeOrgIds; }
    }

    @AfterEach
    void tearDown() {
        // 清理 SecurityContext，避免影响其他测试
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("无 SecurityContext 时返回 empty")
    void testNoSecurityContext() {
        SecurityContextHolder.clearContext();
        Optional<SxwlPrincipal> principal = SxwlPrincipalUtils.getCurrentPrincipal();
        assertTrue(principal.isEmpty(), "无 SecurityContext 时应返回 empty");
    }

    @Test
    @DisplayName("有 SxwlPrincipal 时返回正确 userId")
    void testWithPrincipal() {
        TestPrincipal testPrincipal = new TestPrincipal(1L, 10L, Set.of(10L, 20L));
        Authentication auth = new Authentication() {
            private final Object principal = testPrincipal;

            @Override
            public String getName() { return "test"; }

            @Override
            public Object getCredentials() { return null; }

            @Override
            public Object getDetails() { return null; }

            @Override
            public Object getPrincipal() { return principal; }

            @Override
            public boolean isAuthenticated() { return true; }

            @Override
            public void setAuthenticated(boolean isAuthenticated) { }

            @Override
            public Set<org.springframework.security.core.GrantedAuthority> getAuthorities() {
                return Set.of();
            }
        };

        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<SxwlPrincipal> result = SxwlPrincipalUtils.getCurrentPrincipal();
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
        assertEquals(10L, result.get().getOrgId());
        assertEquals(Set.of(10L, 20L), result.get().getDataScopeOrgIds());
    }

    @Test
    @DisplayName("Authentication 的 Principal 不是 SxwlPrincipal 时返回 empty")
    void testNonSxwlPrincipal() {
        Authentication auth = new Authentication() {
            @Override public String getName() { return "test"; }
            @Override public Object getCredentials() { return null; }
            @Override public Object getDetails() { return null; }
            @Override public Object getPrincipal() { return "string_principal"; }
            @Override public boolean isAuthenticated() { return true; }
            @Override public void setAuthenticated(boolean isAuthenticated) { }
            @Override public java.util.Set<org.springframework.security.core.GrantedAuthority> getAuthorities() { return java.util.Set.of(); }
        };
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<SxwlPrincipal> result = SxwlPrincipalUtils.getCurrentPrincipal();
        assertTrue(result.isEmpty(), "非 SxwlPrincipal 应返回 empty");
    }

    @Test
    @DisplayName("Authentication 为 null 时返回 empty")
    void testNullAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
        Optional<SxwlPrincipal> result = SxwlPrincipalUtils.getCurrentPrincipal();
        assertTrue(result.isEmpty());
    }
}
