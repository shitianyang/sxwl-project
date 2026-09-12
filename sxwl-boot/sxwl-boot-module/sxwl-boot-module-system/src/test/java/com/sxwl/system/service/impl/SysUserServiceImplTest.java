/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.security.key.SxwlSM2KeyManager;
import com.sxwl.system.mapper.SysUserMapper;
import com.sxwl.system.model.dto.SysUserDTO;
import com.sxwl.system.model.params.SysUserPageParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysUserServiceImpl 测试")
class SysUserServiceImplTest {
    private SysUserServiceImpl service;
    @Mock private SysUserMapper sysUserMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SxwlSM2KeyManager keyManager;

    @BeforeEach
    void setUp() { service = new SysUserServiceImpl(sysUserMapper, passwordEncoder, keyManager); }

    @Test
    void testGetUserById_found() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);
        dto.setPassword("secret");
        when(sysUserMapper.getUserById(1L)).thenReturn(dto);

        SysUserDTO result = service.getUserById(1L);
        assertEquals(1L, result.getId());
        assertNull(result.getPassword());
    }

    @Test
    void testGetUserById_notFound() {
        when(sysUserMapper.getUserById(1L)).thenReturn(null);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.getUserById(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testGetUserPageByParams() {
        SysUserPageParams params = new SysUserPageParams();
        List<SysUserDTO> rows = List.of(new SysUserDTO(), new SysUserDTO());
        when(sysUserMapper.getUserPageByParams(params)).thenReturn(rows);

        PageInfo<SysUserDTO> result = service.getUserPageByParams(params);
        assertEquals(2, result.getList().size());
    }

    @Test
    void testCreateUser_usernameDuplicate() {
        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("test");
        dto.setPhone("123");
        dto.setPassword("encPass");
        when(sysUserMapper.checkUsernameUnique("test", null)).thenReturn(1);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createUser(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testCreateUser_phoneDuplicate() {
        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("test");
        dto.setPhone("123");
        dto.setPassword("encPass");
        when(sysUserMapper.checkUsernameUnique("test", null)).thenReturn(0);
        when(sysUserMapper.checkPhoneUnique("123", null)).thenReturn(1);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createUser(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testCreateUser_insertFails() {
        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("test");
        dto.setPhone("123");
        dto.setPassword("encPass");
        when(sysUserMapper.checkUsernameUnique("test", null)).thenReturn(0);
        when(sysUserMapper.checkPhoneUnique("123", null)).thenReturn(0);
        when(keyManager.decrypt("encPass")).thenReturn("plainPass");
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(sysUserMapper.insertUser(any())).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createUser(dto));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testCreateUser_success() {
        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("test");
        dto.setPhone("123");
        dto.setPassword("encPass");
        when(sysUserMapper.checkUsernameUnique("test", null)).thenReturn(0);
        when(sysUserMapper.checkPhoneUnique("123", null)).thenReturn(0);
        when(keyManager.decrypt("encPass")).thenReturn("plainPass");
        when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
        when(sysUserMapper.insertUser(any())).thenReturn(1);

        int result = service.createUser(dto);
        assertEquals(1, result);
    }

    @Test
    void testUpdateUser_usernameDuplicate() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);
        dto.setUsername("test");
        dto.setPhone("123");
        when(sysUserMapper.checkUsernameUnique("test", 1L)).thenReturn(1);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateUser(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testUpdateUser_withPassword_success() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);
        dto.setUsername("test");
        dto.setPhone("123");
        dto.setPassword("encPass");
        when(sysUserMapper.checkUsernameUnique("test", 1L)).thenReturn(0);
        when(sysUserMapper.checkPhoneUnique("123", 1L)).thenReturn(0);

        SysUserDTO oldDto = new SysUserDTO();
        oldDto.setUsername("old");
        when(sysUserMapper.getUserById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn("diff");
            diffUtils.when(() -> SxwlDiffUtils.setContextDiff("diff")).then(invocation -> null);

            when(keyManager.decrypt("encPass")).thenReturn("plainPass");
            when(passwordEncoder.encode("plainPass")).thenReturn("encodedPass");
            when(sysUserMapper.updateUser(any())).thenReturn(1);

            int result = service.updateUser(dto);
            assertEquals(1, result);
        }
    }

    @Test
    void testUpdateUser_withoutPassword_success() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);
        dto.setUsername("test");
        dto.setPhone("123");
        when(sysUserMapper.checkUsernameUnique("test", 1L)).thenReturn(0);
        when(sysUserMapper.checkPhoneUnique("123", 1L)).thenReturn(0);

        SysUserDTO oldDto = new SysUserDTO();
        oldDto.setUsername("old");
        when(sysUserMapper.getUserById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn("diff");
            diffUtils.when(() -> SxwlDiffUtils.setContextDiff("diff")).then(invocation -> null);
            when(sysUserMapper.updateUser(any())).thenReturn(1);

            int result = service.updateUser(dto);
            assertEquals(1, result);
            verify(keyManager, never()).decrypt(any());
            verify(passwordEncoder, never()).encode(any());
        }
    }

    @Test
    void testUpdateUser_notFound() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);
        dto.setUsername("test");
        dto.setPhone("123");
        when(sysUserMapper.checkUsernameUnique("test", 1L)).thenReturn(0);
        when(sysUserMapper.checkPhoneUnique("123", 1L)).thenReturn(0);
        SysUserDTO oldDto = new SysUserDTO();
        when(sysUserMapper.getUserById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysUserMapper.updateUser(any())).thenReturn(0);

            SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateUser(dto));
            assertEquals(10004, ex.getCode());
        }
    }

    @Test
    void testDeleteUserById_success() {
        when(sysUserMapper.deleteUserById(1L)).thenReturn(1);
        assertEquals(1, service.deleteUserById(1L));
    }

    @Test
    void testDeleteUserById_notFound() {
        when(sysUserMapper.deleteUserById(1L)).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.deleteUserById(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testBatchDeleteByIds_empty() {
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.batchDeleteByIds(List.of()));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testBatchDeleteByIds_success() {
        List<Long> ids = List.of(1L, 2L);
        when(sysUserMapper.batchDeleteByIds(ids)).thenReturn(2);
        assertEquals(2, service.batchDeleteByIds(ids));
    }

    @Test
    void testBatchDeleteByIds_notFound() {
        List<Long> ids = List.of(1L, 2L);
        when(sysUserMapper.batchDeleteByIds(ids)).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.batchDeleteByIds(ids));
        assertEquals(10004, ex.getCode());
    }
}
