package com.sxwl.security.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlLoginUser} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlLoginUserTest {

    @Test
    @DisplayName("getter/setter 应正常读写字段")
    void getterAndSetter_shouldWork() {
        SxwlLoginUser user = new SxwlLoginUser();
        user.setUserId(1L);
        user.setUsername("admin");
        user.setNickname("管理员");
        user.setStatus(1);
        user.setRoles(Set.of("admin", "system"));
        user.setPerms(Set.of("*:*:*"));
        user.setCreateOrg(100L);
        user.setDataScope(1);
        user.setDataScopeOrgIds(Set.of(100L, 200L));

        assertEquals(1L, user.getUserId());
        assertEquals("admin", user.getUsername());
        assertEquals("管理员", user.getNickname());
        assertEquals(Integer.valueOf(1), user.getStatus());
        assertEquals(Set.of("admin", "system"), user.getRoles());
        assertEquals(Set.of("*:*:*"), user.getPerms());
        assertEquals(Long.valueOf(100), user.getOrgId());
        assertEquals(Long.valueOf(100), user.getCreateOrg());
        assertEquals(Integer.valueOf(1), user.getDataScope());
        assertEquals(Set.of(100L, 200L), user.getDataScopeOrgIds());
    }

    @Test
    @DisplayName("默认字段值应为 null")
    void defaultValues_shouldBeNull() {
        SxwlLoginUser user = new SxwlLoginUser();
        assertNull(user.getUserId());
        assertNull(user.getUsername());
        assertNull(user.getNickname());
        assertNull(user.getStatus());
        assertNull(user.getRoles());
        assertNull(user.getPerms());
        assertNull(user.getCreateOrg());
        assertNull(user.getDataScope());
        assertNull(user.getDataScopeOrgIds());
    }

    @Test
    @DisplayName("SxwlLoginUser 实现 SxwlPrincipal 接口")
    void shouldImplementSxwlPrincipal() {
        SxwlLoginUser user = new SxwlLoginUser();
        user.setUserId(5L);
        user.setCreateOrg(10L);

        assertInstanceOf(com.sxwl.common.principal.SxwlPrincipal.class, user);
        assertEquals(5L, user.getUserId());
        assertEquals(Long.valueOf(10L), user.getOrgId());
    }
}
