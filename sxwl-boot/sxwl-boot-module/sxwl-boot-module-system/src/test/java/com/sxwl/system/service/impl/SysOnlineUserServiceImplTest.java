/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.constants.SxwlRedisKeyConstants;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.system.model.dto.SysOnlineUserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysOnlineUserServiceImpl 测试")
class SysOnlineUserServiceImplTest {
    private SysOnlineUserServiceImpl service;
    @Mock private SxwlRedisHelper redisHelper;

    @BeforeEach
    void setUp() { service = new SysOnlineUserServiceImpl(redisHelper); }

    @Test
    void testList_whenNoOnlineUsers() {
        when(redisHelper.smembers(SxwlRedisKeyConstants.ONLINE_USERS_KEY)).thenReturn(null);
        PageInfo<SysOnlineUserDTO> result = service.list(1, 10);
        assertTrue(result.getList().isEmpty());
    }
    @Test
    void testCount() {
        when(redisHelper.smembers(SxwlRedisKeyConstants.ONLINE_USERS_KEY)).thenReturn(Set.of("1", "2"));
        assertEquals(2, service.count());
    }
    @Test
    void testCount_whenNull() {
        when(redisHelper.smembers(SxwlRedisKeyConstants.ONLINE_USERS_KEY)).thenReturn(null);
        assertEquals(0, service.count());
    }
    @Test
    void testForceLogout() {
        Set<String> deviceIds = Set.of("dev-1");
        when(redisHelper.smembers(SxwlRedisKeyUtils.onlineDevicesSetKey(1L))).thenReturn(deviceIds);

        service.forceLogout(1L);

        verify(redisHelper).delete(SxwlRedisKeyUtils.tokenInfoKey(1L));
        verify(redisHelper).delete(SxwlRedisKeyUtils.tokenUserSetKey("admin", 1L));
        verify(redisHelper).delete(SxwlRedisKeyUtils.tokenUserSetKey("front", 1L));
        verify(redisHelper).delete(SxwlRedisKeyUtils.onlineUserDeviceKey(1L, "dev-1"));
        verify(redisHelper).delete(SxwlRedisKeyUtils.onlineDevicesSetKey(1L));
        verify(redisHelper).srem(SxwlRedisKeyConstants.ONLINE_USERS_KEY, "1");
    }
    @Test
    void testList_withOnlineUsers() {
        when(redisHelper.smembers(SxwlRedisKeyConstants.ONLINE_USERS_KEY)).thenReturn(Set.of("1"));
        when(redisHelper.smembers(SxwlRedisKeyUtils.onlineDevicesSetKey(1L))).thenReturn(Set.of("dev-1"));
        when(redisHelper.hgetAll(SxwlRedisKeyUtils.onlineUserDeviceKey(1L, "dev-1")))
                .thenReturn(Map.of("username", "admin", "ip", "127.0.0.1", "browser", "Chrome", "os", "Windows", "loginTime", "2026-01-01 00:00:00"));

        PageInfo<SysOnlineUserDTO> result = service.list(1, 10);
        assertEquals(1, result.getList().size());
        SysOnlineUserDTO dto = result.getList().get(0);
        assertEquals(1L, dto.getUserId());
        assertEquals("admin", dto.getUsername());
        assertEquals("dev-1", dto.getDeviceId());
    }
}
