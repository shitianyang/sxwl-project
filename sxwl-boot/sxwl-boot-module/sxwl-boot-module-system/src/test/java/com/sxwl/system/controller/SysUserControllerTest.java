/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysUserDTO;
import com.sxwl.system.model.params.SysUserPageParams;
import com.sxwl.system.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysUserController 测试")
class SysUserControllerTest {
    private SysUserController controller;
    @Mock private SysUserService sysUserService;

    @BeforeEach
    void setUp() { controller = new SysUserController(sysUserService); }

    @Test void testGetUserById() {
        SysUserDTO dto = new SysUserDTO(); dto.setId(1L);
        when(sysUserService.getUserById(1L)).thenReturn(dto);
        assertSame(dto, controller.getUserById(1L));
        verify(sysUserService).getUserById(1L);
    }
    @Test void testGetUserPageByParams() {
        SysUserPageParams params = new SysUserPageParams();
        PageInfo<SysUserDTO> page = new PageInfo<>();
        when(sysUserService.getUserPageByParams(params)).thenReturn(page);
        assertSame(page, controller.getUserPageByParams(params));
        verify(sysUserService).getUserPageByParams(params);
    }
    @Test void testCreateUser() {
        SysUserDTO dto = new SysUserDTO();
        controller.createUser(dto);
        verify(sysUserService).createUser(dto);
    }
    @Test void testUpdateUser() {
        SysUserDTO dto = new SysUserDTO();
        controller.updateUser(dto);
        verify(sysUserService).updateUser(dto);
    }
    @Test void testDeleteUserById() {
        controller.deleteUserById(1L);
        verify(sysUserService).deleteUserById(1L);
    }
    @Test void testBatchDeleteByIds() {
        List<Long> ids = List.of(1L, 2L);
        controller.batchDeleteByIds(ids);
        verify(sysUserService).batchDeleteByIds(ids);
    }
}
