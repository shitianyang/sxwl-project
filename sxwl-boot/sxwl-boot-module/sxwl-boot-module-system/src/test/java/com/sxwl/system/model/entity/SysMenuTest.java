/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysMenu 实体测试")
class SysMenuTest {
    @Test
    void testGettersAndSetters() {
        SysMenu entity = new SysMenu();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setMenuName("用户管理"); entity.setParentId(0L); entity.setAncestors("0");
        entity.setMenuType(1); entity.setPath("/user"); entity.setComponent("user/index"); entity.setPerms("system:user:list");
        entity.setIcon("user"); entity.setIsFrame(0); entity.setIsCache(1); entity.setSort(1);
        entity.setVisible(1); entity.setStatus(1); entity.setDescription("描述");
        entity.setCreateBy(100L); entity.setCreateOrg(200L); entity.setCreateTime(now);
        entity.setUpdateBy(101L); entity.setUpdateTime(now); entity.setDeleteFlag(0);
        assertEquals(1L, entity.getId()); assertEquals("用户管理", entity.getMenuName());
        assertEquals(0L, entity.getParentId()); assertEquals("0", entity.getAncestors());
        assertEquals(1, entity.getMenuType()); assertEquals("/user", entity.getPath());
        assertEquals("user/index", entity.getComponent()); assertEquals("system:user:list", entity.getPerms());
        assertEquals("user", entity.getIcon()); assertEquals(0, entity.getIsFrame());
        assertEquals(1, entity.getIsCache()); assertEquals(1, entity.getSort());
        assertEquals(1, entity.getVisible()); assertEquals(1, entity.getStatus());
        assertEquals("描述", entity.getDescription());
    }
    @Test void testNoArgsConstructor() { SysMenu e = new SysMenu(); assertNull(e.getId()); assertNull(e.getMenuName()); }
}
