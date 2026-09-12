/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.params;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysRolePageParams 测试")
class SysRolePageParamsTest {
    @Test
    void testGettersAndSetters() {
        SysRolePageParams params = new SysRolePageParams();
        params.setCurrent(1); params.setPageSize(10);
        assertEquals(1, params.getCurrent()); assertEquals(10, params.getPageSize());
    }
}
