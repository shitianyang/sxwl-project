package com.sxwl.codegen.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysCodegenTable} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysCodegenTable 测试")
class SysCodegenTableTest {

    @Test
    void getterSetter_shouldWork() {
        SysCodegenTable entity = new SysCodegenTable();
        entity.setId(1L);
        entity.setTableName("sys_role_info");
        entity.setModulePrefix("system");
        entity.setBizName("Role");
        entity.setBizNameCn("角色");
        entity.setBizNamePlural("Roles");
        entity.setTableComment("角色表");
        entity.setPackageName("com.sxwl.system");
        entity.setAuthor("admin");
        entity.setGenType("crud");
        entity.setStatus(1);

        assertEquals(1L, entity.getId());
        assertEquals("sys_role_info", entity.getTableName());
        assertEquals("system", entity.getModulePrefix());
        assertEquals("Role", entity.getBizName());
        assertEquals("角色", entity.getBizNameCn());
        assertEquals("Roles", entity.getBizNamePlural());
        assertEquals("角色表", entity.getTableComment());
        assertEquals("com.sxwl.system", entity.getPackageName());
        assertEquals("admin", entity.getAuthor());
        assertEquals("crud", entity.getGenType());
        assertEquals(1, entity.getStatus());
    }

    @Test
    void defaults_shouldBeNull() {
        SysCodegenTable entity = new SysCodegenTable();
        assertNull(entity.getId());
        assertNull(entity.getTableName());
        assertNull(entity.getBizName());
    }
}
