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
 * SysJobPageParams POJO 测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@DisplayName("SysJobPageParams 测试")
class SysJobPageParamsTest {

    @Test
    @DisplayName("getter/setter 应正确存取所有字段")
    void testGettersAndSetters() {
        SysJobPageParams params = new SysJobPageParams();

        params.setCurrent(1);
        params.setPageSize(20);
        params.setJobName("testJob");
        params.setJobGroup("DEFAULT");
        params.setStatus(1);

        assertEquals(1, params.getCurrent());
        assertEquals(20, params.getPageSize());
        assertEquals("testJob", params.getJobName());
        assertEquals("DEFAULT", params.getJobGroup());
        assertEquals(1, params.getStatus());
    }

    @Test
    @DisplayName("无参构造应设置默认分页值")
    void testNoArgsConstructor() {
        SysJobPageParams params = new SysJobPageParams();
        assertEquals(1, params.getCurrent());
        assertEquals(10, params.getPageSize());
        assertNull(params.getJobName());
        assertNull(params.getJobGroup());
        assertNull(params.getStatus());
    }
}
