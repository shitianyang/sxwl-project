/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sxwl.job.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysJobDTO POJO 测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@DisplayName("SysJobDTO 测试")
class SysJobDTOTest {

    @Test
    @DisplayName("getter/setter 应正确存取所有字段")
    void testGettersAndSetters() {
        SysJobDTO dto = new SysJobDTO();

        dto.setId(1L);
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");
        dto.setClassName("com.sxwl.TestService");
        dto.setMethodName("execute");
        dto.setMethodParams("param1");
        dto.setCronExpression("0 0/5 * * * ?");
        dto.setDescription("测试任务");
        dto.setStatus(1);
        dto.setCreateTime("2026-07-05 10:00:00");

        assertEquals(1L, dto.getId());
        assertEquals("testJob", dto.getJobName());
        assertEquals("DEFAULT", dto.getJobGroup());
        assertEquals("com.sxwl.TestService", dto.getClassName());
        assertEquals("execute", dto.getMethodName());
        assertEquals("param1", dto.getMethodParams());
        assertEquals("0 0/5 * * * ?", dto.getCronExpression());
        assertEquals("测试任务", dto.getDescription());
        assertEquals(1, dto.getStatus());
        assertEquals("2026-07-05 10:00:00", dto.getCreateTime());
    }

    @Test
    @DisplayName("无参构造应创建空对象")
    void testNoArgsConstructor() {
        SysJobDTO dto = new SysJobDTO();
        assertNull(dto.getId());
        assertNull(dto.getJobName());
        assertNull(dto.getStatus());
    }
}
