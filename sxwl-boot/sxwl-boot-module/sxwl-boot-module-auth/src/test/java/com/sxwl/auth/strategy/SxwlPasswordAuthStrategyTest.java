package com.sxwl.auth.strategy;

import com.sxwl.auth.crypto.SxwlPasswordDecryptor;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlPasswordAuthStrategy} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlPasswordAuthStrategy 测试")
class SxwlPasswordAuthStrategyTest {

    @Mock
    private com.sxwl.auth.mapper.SysAuthUserMapper sysUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SxwlPasswordDecryptor passwordDecryptor;

    private SxwlPasswordAuthStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new SxwlPasswordAuthStrategy(sysUserMapper, passwordEncoder, passwordDecryptor);
    }

    private SxwlLoginRequest createRequest(String username, String password) {
        SxwlLoginRequest request = new SxwlLoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private Map<String, Object> createUserRow(Long id, String username, String password, Integer status, Long createOrg) {
        return Map.of(
                "id", id,
                "username", username,
                "password", password,
                "nickname", "测试用户",
                "status", status,
                "create_org", createOrg
        );
    }

    @Test
    @DisplayName("authenticate 用户名空时应抛出异常")
    void authenticate_emptyUsername_shouldThrow() {
        SxwlLoginRequest request = createRequest(null, "pass");
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> strategy.authenticate(request));
        assertEquals(400, ex.getCode());
    }

    @Test
    @DisplayName("authenticate 密码空时应抛出异常")
    void authenticate_emptyPassword_shouldThrow() {
        SxwlLoginRequest request = createRequest("user", null);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> strategy.authenticate(request));
        assertEquals(400, ex.getCode());
    }

    @Test
    @DisplayName("authenticate 解密后密码为空时应抛出异常")
    void authenticate_decryptedPasswordEmpty_shouldThrow() {
        when(passwordDecryptor.decrypt("enc-pass")).thenReturn("");
        SxwlLoginRequest request = createRequest("user", "enc-pass");

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> strategy.authenticate(request));
        assertEquals(400, ex.getCode());
    }

    @Test
    @DisplayName("authenticate 用户不存在时应抛出异常")
    void authenticate_userNotFound_shouldThrow() {
        when(passwordDecryptor.decrypt("enc-pass")).thenReturn("plain-pass");
        when(sysUserMapper.selectByUsername("nonexist")).thenReturn(null);

        SxwlLoginRequest request = createRequest("nonexist", "enc-pass");
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> strategy.authenticate(request));
        assertEquals(401, ex.getCode());
    }

    @Test
    @DisplayName("authenticate 账号已禁用时应抛出异常")
    void authenticate_disabledAccount_shouldThrow() {
        when(passwordDecryptor.decrypt("enc-pass")).thenReturn("plain-pass");
        when(sysUserMapper.selectByUsername("disabled")).thenReturn(createUserRow(1L, "disabled", "hash", 0, 1L));

        SxwlLoginRequest request = createRequest("disabled", "enc-pass");
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> strategy.authenticate(request));
        assertEquals(403, ex.getCode());
    }

    @Test
    @DisplayName("authenticate 密码错误时应抛出异常")
    void authenticate_wrongPassword_shouldThrow() {
        when(passwordDecryptor.decrypt("enc-pass")).thenReturn("plain-pass");
        when(sysUserMapper.selectByUsername("user")).thenReturn(createUserRow(1L, "user", "hash", 1, 1L));
        when(passwordEncoder.matches("plain-pass", "hash")).thenReturn(false);

        SxwlLoginRequest request = createRequest("user", "enc-pass");
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> strategy.authenticate(request));
        assertEquals(401, ex.getCode());
    }

    @Test
    @DisplayName("authenticate 认证成功应返回 SxwlLoginUser")
    void authenticate_success_shouldReturnLoginUser() {
        when(passwordDecryptor.decrypt("enc-pass")).thenReturn("plain-pass");
        when(sysUserMapper.selectByUsername("user")).thenReturn(createUserRow(1L, "user", "hash", 1, 1L));
        when(passwordEncoder.matches("plain-pass", "hash")).thenReturn(true);
        when(sysUserMapper.selectRolesByUserId(1L)).thenReturn(List.of());
        when(sysUserMapper.selectPermissionsByUserId(1L)).thenReturn(List.of());

        SxwlLoginRequest request = createRequest("user", "enc-pass");
        SxwlLoginUser loginUser = strategy.authenticate(request);

        assertNotNull(loginUser);
        assertEquals(1L, loginUser.getUserId());
        assertEquals("user", loginUser.getUsername());
        assertEquals("测试用户", loginUser.getNickname());
    }

    @Test
    @DisplayName("authenticate 包含角色和数据权限应正确填充")
    void authenticate_withRoles_shouldFillAuthorization() {
        when(passwordDecryptor.decrypt("enc-pass")).thenReturn("plain-pass");
        when(sysUserMapper.selectByUsername("admin")).thenReturn(createUserRow(1L, "admin", "hash", 1, 1L));
        when(passwordEncoder.matches("plain-pass", "hash")).thenReturn(true);

        Map<String, Object> roleRow = Map.of("id", 10L, "role_code", "admin", "data_scope", 1);
        when(sysUserMapper.selectRolesByUserId(1L)).thenReturn(List.of(roleRow));
        when(sysUserMapper.selectPermissionsByUserId(1L)).thenReturn(List.of("system:user:list"));

        SxwlLoginRequest request = createRequest("admin", "enc-pass");
        SxwlLoginUser loginUser = strategy.authenticate(request);

        assertEquals(1, loginUser.getDataScope());
        assertNull(loginUser.getDataScopeOrgIds());
        assertTrue(loginUser.getPerms().contains("*:*:*"));
    }
}
