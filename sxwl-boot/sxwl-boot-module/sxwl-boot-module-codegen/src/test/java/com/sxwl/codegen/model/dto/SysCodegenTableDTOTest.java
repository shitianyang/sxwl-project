package com.sxwl.codegen.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysCodegenTableDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysCodegenTableDTO 测试")
class SysCodegenTableDTOTest {

    @Test
    void getterSetter_shouldWork() {
        SysCodegenTableDTO dto = new SysCodegenTableDTO();
        dto.setId(1L);
        dto.setTableName("sys_role_info");
        dto.setModulePrefix("system");
        dto.setBizName("Role");
        dto.setBizNameCn("角色");
        dto.setBizNamePlural("Roles");
        dto.setTableComment("角色表");
        dto.setPackageName("com.sxwl.system");
        dto.setAuthor("admin");
        dto.setGenType("crud");
        dto.setStatus(1);
        dto.setCreateTime("2026-07-24 12:00:00");
        dto.setFields(List.of(new SysCodegenFieldDTO()));

        assertEquals(1L, dto.getId());
        assertEquals("sys_role_info", dto.getTableName());
        assertEquals("system", dto.getModulePrefix());
        assertEquals("Role", dto.getBizName());
        assertEquals("角色", dto.getBizNameCn());
        assertEquals("Roles", dto.getBizNamePlural());
        assertEquals("角色表", dto.getTableComment());
        assertEquals("com.sxwl.system", dto.getPackageName());
        assertEquals("admin", dto.getAuthor());
        assertEquals("crud", dto.getGenType());
        assertEquals(1, dto.getStatus());
        assertEquals("2026-07-24 12:00:00", dto.getCreateTime());
        assertNotNull(dto.getFields());
        assertEquals(1, dto.getFields().size());
    }

    @Test
    void defaults_shouldBeNull() {
        SysCodegenTableDTO dto = new SysCodegenTableDTO();
        assertNull(dto.getId());
        assertNull(dto.getTableName());
        assertNull(dto.getFields());
    }
}
