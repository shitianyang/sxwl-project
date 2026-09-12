/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysLog 实体测试")
class SysLogTest {
    @Test
    void testGettersAndSetters() {
        SysLog entity = new SysLog();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setLogType(1); entity.setTitle("用户管理"); entity.setDescription("删除用户");
        entity.setMethod("SysUserController.delete()"); entity.setRequestUrl("/sys/user/1"); entity.setRequestMethod("DELETE");
        entity.setRequestParam("{\"id\":1}"); entity.setResponseResult("{\"code\":200}");
        entity.setOperateIp("127.0.0.1"); entity.setOperateLocation("内网IP");
        entity.setUserId(100L); entity.setUserName("admin"); entity.setExecuteTime(50L);
        entity.setErrorMsg(null); entity.setStatus(1); entity.setTraceId("trace-001");
        entity.setUserAgent("Chrome"); entity.setBrowser("Chrome"); entity.setOs("Windows"); entity.setDiff("diff-json");
        entity.setCreateBy(100L); entity.setCreateOrg(200L); entity.setCreateTime(now);
        entity.setUpdateBy(101L); entity.setUpdateTime(now); entity.setDeleteFlag(0);
        assertEquals(1L, entity.getId()); assertEquals(1, entity.getLogType());
        assertEquals("用户管理", entity.getTitle()); assertEquals("删除用户", entity.getDescription());
        assertEquals("SysUserController.delete()", entity.getMethod());
        assertEquals("/sys/user/1", entity.getRequestUrl()); assertEquals("DELETE", entity.getRequestMethod());
        assertEquals("{\"id\":1}", entity.getRequestParam()); assertEquals("{\"code\":200}", entity.getResponseResult());
        assertEquals("127.0.0.1", entity.getOperateIp()); assertEquals("内网IP", entity.getOperateLocation());
        assertEquals(100L, entity.getUserId()); assertEquals("admin", entity.getUserName());
        assertEquals(50L, entity.getExecuteTime()); assertNull(entity.getErrorMsg());
        assertEquals(1, entity.getStatus()); assertEquals("trace-001", entity.getTraceId());
        assertEquals("Chrome", entity.getUserAgent()); assertEquals("Chrome", entity.getBrowser());
        assertEquals("Windows", entity.getOs()); assertEquals("diff-json", entity.getDiff());
    }
    @Test void testNoArgsConstructor() { SysLog e = new SysLog(); assertNull(e.getId()); assertNull(e.getLogType()); }
}
