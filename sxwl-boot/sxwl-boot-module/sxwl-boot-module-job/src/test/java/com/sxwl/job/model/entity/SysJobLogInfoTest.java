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
 * SysJobLogInfo 实体 POJO 测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@DisplayName("SysJobLogInfo 实体测试")
class SysJobLogInfoTest {

    @Test
    @DisplayName("getter/setter 应正确存取所有字段")
    void testGettersAndSetters() {
        SysJobLogInfo entity = new SysJobLogInfo();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fireTime = LocalDateTime.of(2026, 7, 5, 10, 30, 0);

        entity.setId(1L);
        entity.setJobId(10L);
        entity.setJobName("testJob");
        entity.setJobGroup("DEFAULT");
        entity.setClassName("com.sxwl.TestService");
        entity.setMethodName("execute");
        entity.setMethodParams("param1");
        entity.setCronExpression("0 0/5 * * * ?");
        entity.setStatus(1);
        entity.setExecuteTime(500L);
        entity.setErrorMsg("错误信息");
        entity.setFireTime(fireTime);
        entity.setCreateBy(100L);
        entity.setCreateOrg(200L);
        entity.setCreateTime(now);
        entity.setUpdateBy(101L);
        entity.setUpdateTime(now);
        entity.setDeleteFlag(0);

        assertEquals(1L, entity.getId());
        assertEquals(10L, entity.getJobId());
        assertEquals("testJob", entity.getJobName());
        assertEquals("DEFAULT", entity.getJobGroup());
        assertEquals("com.sxwl.TestService", entity.getClassName());
        assertEquals("execute", entity.getMethodName());
        assertEquals("param1", entity.getMethodParams());
        assertEquals("0 0/5 * * * ?", entity.getCronExpression());
        assertEquals(1, entity.getStatus());
        assertEquals(500L, entity.getExecuteTime());
        assertEquals("错误信息", entity.getErrorMsg());
        assertEquals(fireTime, entity.getFireTime());
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
        SysJobLogInfo entity = new SysJobLogInfo();
        assertNull(entity.getId());
        assertNull(entity.getJobName());
        assertNull(entity.getStatus());
    }
}
