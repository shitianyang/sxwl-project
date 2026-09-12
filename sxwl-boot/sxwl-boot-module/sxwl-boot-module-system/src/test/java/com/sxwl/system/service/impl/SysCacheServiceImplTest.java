/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.sxwl.system.model.dto.SysCacheCategoryDTO;
import com.sxwl.system.model.dto.SysCacheKeyDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysCacheServiceImpl 测试")
class SysCacheServiceImplTest {
    private SysCacheServiceImpl service;
    @Mock private StringRedisTemplate stringRedisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() { service = new SysCacheServiceImpl(stringRedisTemplate); }

    @Test
    void testListCategories() {
        List<SysCacheCategoryDTO> categories = service.listCategories();
        assertFalse(categories.isEmpty());
        assertEquals("Token 白名单", categories.get(0).getName());
    }
    @Test
    void testClearByName() {
        Set<String> keys = Set.of("config:key1", "config:key2");
        when(stringRedisTemplate.keys("config:*")).thenReturn(keys);
        service.clearByName("config:*");
        verify(stringRedisTemplate).unlink(keys);
    }
    @Test
    void testClearByKey() {
        service.clearByKey("config:key1");
        verify(stringRedisTemplate).unlink("config:key1");
    }
    @Test
    void testGetKeyDetail() {
        when(stringRedisTemplate.type("key")).thenReturn(DataType.STRING);
        when(stringRedisTemplate.getExpire("key", TimeUnit.SECONDS)).thenReturn(3600L);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("key")).thenReturn("value");

        SysCacheKeyDetailDTO dto = service.getKeyDetail("key");
        assertEquals("key", dto.getKey());
        assertEquals("string", dto.getType());
        assertEquals("value", dto.getValue());
        assertEquals(3600L, dto.getTtl());
    }
}
