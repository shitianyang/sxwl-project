package com.sxwl.codegen.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link CodegenConfigDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("CodegenConfigDTO 测试")
class CodegenConfigDTOTest {

    @Test
    void getterSetter_shouldWork() {
        CodegenConfigDTO dto = new CodegenConfigDTO();
        dto.setTableName("sys_role_info");
        dto.setModulePrefix("system");
        dto.setBizName("Role");
        dto.setBizNameCn("角色");
        dto.setBizNamePlural("Roles");
        dto.setTableComment("角色表");
        dto.setPackageName("com.sxwl.system");
        dto.setAuthor("admin");
        dto.setGenType("crud");

        assertEquals("sys_role_info", dto.getTableName());
        assertEquals("system", dto.getModulePrefix());
        assertEquals("Role", dto.getBizName());
        assertEquals("角色", dto.getBizNameCn());
        assertEquals("Roles", dto.getBizNamePlural());
        assertEquals("角色表", dto.getTableComment());
        assertEquals("com.sxwl.system", dto.getPackageName());
        assertEquals("admin", dto.getAuthor());
        assertEquals("crud", dto.getGenType());
    }

    @Test
    void defaults_shouldBeNull() {
        CodegenConfigDTO dto = new CodegenConfigDTO();
        assertNull(dto.getTableName());
        assertNull(dto.getBizName());
    }
}
