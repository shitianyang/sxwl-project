/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysMenuDTO 测试")
class SysMenuDTOTest {
    @Test
    void testGettersAndSetters() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setId(1L); dto.setMenuName("用户管理"); dto.setParentId(0L); dto.setAncestors("0");
        dto.setMenuType(1); dto.setPath("/user"); dto.setComponent("/system/user/index");
        dto.setPerms("system:user:list"); dto.setIcon("user"); dto.setIsFrame(0); dto.setIsCache(1);
        dto.setSort(1); dto.setVisible(1); dto.setStatus(1); dto.setDescription("描述");
        SysMenuDTO child = new SysMenuDTO();
        dto.setChildren(List.of(child));
        assertEquals(1L, dto.getId()); assertEquals("用户管理", dto.getMenuName());
        assertEquals(0L, dto.getParentId()); assertEquals("0", dto.getAncestors());
        assertEquals(1, dto.getMenuType()); assertEquals("/user", dto.getPath());
        assertEquals("/system/user/index", dto.getComponent()); assertEquals("system:user:list", dto.getPerms());
        assertEquals("user", dto.getIcon()); assertEquals(0, dto.getIsFrame()); assertEquals(1, dto.getIsCache());
        assertEquals(1, dto.getSort()); assertEquals(1, dto.getVisible()); assertEquals(1, dto.getStatus());
        assertEquals("描述", dto.getDescription()); assertEquals(1, dto.getChildren().size());
    }
    @Test
    void testSortValue() {
        SysMenuDTO dto = new SysMenuDTO();
        assertEquals(0, dto.getSortValue());
        dto.setSort(5);
        assertEquals(5, dto.getSortValue());
    }
}
