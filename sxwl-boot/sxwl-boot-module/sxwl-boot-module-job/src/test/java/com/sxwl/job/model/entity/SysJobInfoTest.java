/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sxwl.job.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysJobInfo 实体 POJO 测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@DisplayName("SysJobInfo 实体测试")
class SysJobInfoTest {

    @Test
    @DisplayName("getter/setter 应正确存取所有字段")
    void testGettersAndSetters() {
        SysJobInfo entity = new SysJobInfo();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(1L);
        entity.setJobName("testJob");
        entity.setJobGroup("DEFAULT");
        entity.setClassName("com.sxwl.TestService");
        entity.setMethodName("execute");
        entity.setMethodParams("param1");
        entity.setCronExpression("0 0/5 * * * ?");
        entity.setDescription("测试任务");
        entity.setStatus(1);
        entity.setCreateBy(100L);
        entity.setCreateOrg(200L);
        entity.setCreateTime(now);
        entity.setUpdateBy(101L);
        entity.setUpdateTime(now);
        entity.setDeleteFlag(0);

        assertEquals(1L, entity.getId());
        assertEquals("testJob", entity.getJobName());
        assertEquals("DEFAULT", entity.getJobGroup());
        assertEquals("com.sxwl.TestService", entity.getClassName());
        assertEquals("execute", entity.getMethodName());
        assertEquals("param1", entity.getMethodParams());
        assertEquals("0 0/5 * * * ?", entity.getCronExpression());
        assertEquals("测试任务", entity.getDescription());
        assertEquals(1, entity.getStatus());
        assertEquals(100L, entity.getCreateBy());
        assertEquals(200L, entity.getCreateOrg());
        assertEquals(now, entity.getCreateTime());
        assertEquals(101L, entity.getUpdateBy());
        assertEquals(now, entity.getUpdateTime());
        assertEquals(0, entity.getDeleteFlag());
    }

    @Test
    @DisplayName("无参构造应创建空对象")
    void testNoArgsConstructor() {
        SysJobInfo entity = new SysJobInfo();
        assertNull(entity.getId());
        assertNull(entity.getJobName());
        assertNull(entity.getStatus());
    }
}
