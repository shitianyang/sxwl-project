/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysOnlineUserDTO 测试")
class SysOnlineUserDTOTest {
    @Test
    void testGettersAndSetters() {
        SysOnlineUserDTO dto = new SysOnlineUserDTO();
        LocalDateTime now = LocalDateTime.now();
        dto.setUserId(1L); dto.setUsername("admin"); dto.setIp("127.0.0.1");
        dto.setBrowser("Chrome"); dto.setOs("Windows"); dto.setDeviceId("device-001"); dto.setLoginTime(now);
        assertEquals(1L, dto.getUserId()); assertEquals("admin", dto.getUsername());
        assertEquals("127.0.0.1", dto.getIp()); assertEquals("Chrome", dto.getBrowser());
        assertEquals("Windows", dto.getOs()); assertEquals("device-001", dto.getDeviceId());
        assertEquals(now, dto.getLoginTime());
    }
}
