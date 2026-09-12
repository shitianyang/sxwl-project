package com.sxwl.security.utils;

import com.sxwl.common.principal.SxwlPrincipal;
import com.sxwl.common.utils.SxwlPrincipalUtils;
import com.sxwl.security.model.SxwlLoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlSecurityUtils} 的单元测试
 * <p>通过设置 SecurityContext 验证获取当前用户功能。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlSecurityUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("已登录时 getCurrentUser 返回用户")
    void getCurrentUser_shouldReturnUser_whenAuthenticated() {
        SxwlLoginUser loginUser = createLoginUser(1L, "admin");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, Set.of()));

        Optional<SxwlLoginUser> result = SxwlSecurityUtils.getCurrentUser();
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
        assertEquals("admin", result.get().getUsername());
    }

    @Test
    @DisplayName("未登录时 getCurrentUser 返回 empty")
    void getCurrentUser_shouldReturnEmpty_whenNotAuthenticated() {
        SecurityContextHolder.clearContext();
        assertTrue(SxwlSecurityUtils.getCurrentUser().isEmpty());
    }

    @Test
    @DisplayName("Principal 非 SxwlLoginUser 类型时返回 empty")
    void getCurrentUser_shouldReturnEmpty_whenPrincipalIsNotSxwlLoginUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("not-a-login-user", null, Set.of()));
        assertTrue(SxwlSecurityUtils.getCurrentUser().isEmpty());
    }

    @Test
    @DisplayName("getCurrentUserId 返回正确的用户 ID")
    void getCurrentUserId_shouldReturnUserId() {
        SxwlLoginUser loginUser = createLoginUser(42L, "test");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, Set.of()));

        assertEquals(42L, SxwlSecurityUtils.getCurrentUserId());
    }

    @Test
    @DisplayName("未登录时 getCurrentUserId 返回 null")
    void getCurrentUserId_shouldReturnNull_whenNotAuthenticated() {
        assertNull(SxwlSecurityUtils.getCurrentUserId());
    }

    @Test
    @DisplayName("getCurrentUsername 返回正确的用户名")
    void getCurrentUsername_shouldReturnUsername() {
        SxwlLoginUser loginUser = createLoginUser(1L, "testuser");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, Set.of()));

        assertEquals("testuser", SxwlSecurityUtils.getCurrentUsername());
    }

    @Test
    @DisplayName("未登录时 getCurrentUsername 返回 null")
    void getCurrentUsername_shouldReturnNull_whenNotAuthenticated() {
        assertNull(SxwlSecurityUtils.getCurrentUsername());
    }

    private SxwlLoginUser createLoginUser(Long userId, String username) {
        SxwlLoginUser user = new SxwlLoginUser();
        user.setUserId(userId);
        user.setUsername(username);
        return user;
    }
}
