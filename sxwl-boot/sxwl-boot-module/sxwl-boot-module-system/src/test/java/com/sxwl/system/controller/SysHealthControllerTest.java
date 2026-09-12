/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysHealthController 测试")
class SysHealthControllerTest {
    private final SysHealthController controller = new SysHealthController();

    @Test
    void testHealth() {
        ResponseEntity<Void> result = controller.health();
        assertNotNull(result);
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }
}
