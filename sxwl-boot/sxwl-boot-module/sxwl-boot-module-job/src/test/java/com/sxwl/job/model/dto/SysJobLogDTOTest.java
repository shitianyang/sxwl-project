/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sxwl.job.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysJobLogDTO POJO 测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@DisplayName("SysJobLogDTO 测试")
class SysJobLogDTOTest {

    @Test
    @DisplayName("getter/setter 应正确存取所有字段")
    void testGettersAndSetters() {
        SysJobLogDTO dto = new SysJobLogDTO();
        LocalDateTime fireTime = LocalDateTime.of(2026, 7, 5, 10, 30, 0);

        dto.setId(1L);
        dto.setJobId(10L);
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");
        dto.setClassName("com.sxwl.TestService");
        dto.setMethodName("execute");
        dto.setMethodParams("param1");
        dto.setCronExpression("0 0/5 * * * ?");
        dto.setStatus(1);
        dto.setExecuteTime(500L);
        dto.setErrorMsg("错误信息");
        dto.setFireTime(fireTime);
        dto.setCreateTime("2026-07-05 10:30:00");

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getJobId());
        assertEquals("testJob", dto.getJobName());
        assertEquals("DEFAULT", dto.getJobGroup());
        assertEquals("com.sxwl.TestService", dto.getClassName());
        assertEquals("execute", dto.getMethodName());
        assertEquals("param1", dto.getMethodParams());
        assertEquals("0 0/5 * * * ?", dto.getCronExpression());
        assertEquals(1, dto.getStatus());
        assertEquals(500L, dto.getExecuteTime());
        assertEquals("错误信息", dto.getErrorMsg());
        assertEquals(fireTime, dto.getFireTime());
        assertEquals("2026-07-05 10:30:00", dto.getCreateTime());
    }

    @Test
    @DisplayName("无参构造应创建空对象")
    void testNoArgsConstructor() {
        SysJobLogDTO dto = new SysJobLogDTO();
        assertNull(dto.getId());
        assertNull(dto.getJobName());
        assertNull(dto.getStatus());
    }
}
