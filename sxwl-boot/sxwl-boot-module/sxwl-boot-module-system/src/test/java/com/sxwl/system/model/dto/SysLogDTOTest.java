/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysLogDTO 测试")
class SysLogDTOTest {
    @Test
    void testGettersAndSetters() {
        SysLogDTO dto = new SysLogDTO();
        dto.setId(1L); dto.setLogType(1); dto.setTitle("用户管理"); dto.setDescription("删除用户");
        dto.setMethod("SysUserController.delete()"); dto.setRequestUrl("/sys/user/1"); dto.setRequestMethod("DELETE");
        dto.setRequestParam("{\"id\":1}"); dto.setResponseResult("{\"code\":200}");
        dto.setOperateIp("127.0.0.1"); dto.setOperateLocation("内网IP");
        dto.setUserId(100L); dto.setUserName("admin"); dto.setExecuteTime(50L);
        dto.setErrorMsg(null); dto.setStatus(1); dto.setTraceId("trace-001");
        dto.setUserAgent("Chrome"); dto.setBrowser("Chrome"); dto.setOs("Windows"); dto.setDiff("diff-json");
        dto.setCreateTime("2026-01-01");
        assertEquals(1L, dto.getId()); assertEquals(1, dto.getLogType());
        assertEquals("用户管理", dto.getTitle()); assertEquals("删除用户", dto.getDescription());
        assertEquals("SysUserController.delete()", dto.getMethod());
        assertEquals("/sys/user/1", dto.getRequestUrl()); assertEquals("DELETE", dto.getRequestMethod());
        assertEquals("{\"id\":1}", dto.getRequestParam()); assertEquals("{\"code\":200}", dto.getResponseResult());
        assertEquals("127.0.0.1", dto.getOperateIp()); assertEquals("内网IP", dto.getOperateLocation());
        assertEquals(100L, dto.getUserId()); assertEquals("admin", dto.getUserName());
        assertEquals(50L, dto.getExecuteTime()); assertNull(dto.getErrorMsg());
        assertEquals(1, dto.getStatus()); assertEquals("trace-001", dto.getTraceId());
        assertEquals("Chrome", dto.getUserAgent()); assertEquals("Chrome", dto.getBrowser());
        assertEquals("Windows", dto.getOs()); assertEquals("diff-json", dto.getDiff());
        assertEquals("2026-01-01", dto.getCreateTime());
    }
}
