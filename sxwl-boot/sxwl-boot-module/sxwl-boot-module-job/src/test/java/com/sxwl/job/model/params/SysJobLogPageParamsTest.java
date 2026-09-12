/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sxwl.job.model.params;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysJobLogPageParams POJO 测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@DisplayName("SysJobLogPageParams 测试")
class SysJobLogPageParamsTest {

    @Test
    @DisplayName("getter/setter 应正确存取所有字段")
    void testGettersAndSetters() {
        SysJobLogPageParams params = new SysJobLogPageParams();

        params.setCurrent(2);
        params.setPageSize(15);
        params.setJobId(10L);
        params.setJobName("testJob");
        params.setStatus(1);

        assertEquals(2, params.getCurrent());
        assertEquals(15, params.getPageSize());
        assertEquals(10L, params.getJobId());
        assertEquals("testJob", params.getJobName());
        assertEquals(1, params.getStatus());
    }

    @Test
    @DisplayName("无参构造应设置默认分页值")
    void testNoArgsConstructor() {
        SysJobLogPageParams params = new SysJobLogPageParams();
        assertEquals(1, params.getCurrent());
        assertEquals(10, params.getPageSize());
        assertNull(params.getJobId());
        assertNull(params.getJobName());
        assertNull(params.getStatus());
    }
}
