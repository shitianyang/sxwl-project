package com.sxwl.codegen.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysCodegenField} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysCodegenField 测试")
class SysCodegenFieldTest {

    @Test
    void getterSetter_shouldWork() {
        SysCodegenField entity = new SysCodegenField();
        entity.setId(1L);
        entity.setTableId(1L);
        entity.setColumnName("role_code");
        entity.setColumnType("varchar");
        entity.setColumnComment("角色编码");
        entity.setJavaType("String");
        entity.setJavaFieldName("roleCode");
        entity.setIsPk(0);
        entity.setIsInsert(1);
        entity.setIsEdit(1);
        entity.setIsList(1);
        entity.setIsQuery(1);
        entity.setQueryType("eq");
        entity.setQueryFormType("Input");
        entity.setFormType("Input");
        entity.setFormDictCode(null);
        entity.setIsRequired(1);
        entity.setIsUnique(1);
        entity.setMaxLength(50);
        entity.setSort(1);
        entity.setCreateTime("2026-07-24 12:00:00");

        assertEquals(1L, entity.getId());
        assertEquals(1L, entity.getTableId());
        assertEquals("role_code", entity.getColumnName());
        assertEquals("varchar", entity.getColumnType());
        assertEquals("角色编码", entity.getColumnComment());
        assertEquals("String", entity.getJavaType());
        assertEquals("roleCode", entity.getJavaFieldName());
        assertEquals(0, entity.getIsPk());
        assertEquals(1, entity.getIsInsert());
        assertEquals(1, entity.getIsEdit());
        assertEquals(1, entity.getIsList());
        assertEquals(1, entity.getIsQuery());
        assertEquals("eq", entity.getQueryType());
        assertEquals("Input", entity.getQueryFormType());
        assertEquals("Input", entity.getFormType());
        assertNull(entity.getFormDictCode());
        assertEquals(1, entity.getIsRequired());
        assertEquals(1, entity.getIsUnique());
        assertEquals(50, entity.getMaxLength());
        assertEquals(1, entity.getSort());
        assertEquals("2026-07-24 12:00:00", entity.getCreateTime());
    }

    @Test
    void defaults_shouldBeNull() {
        SysCodegenField entity = new SysCodegenField();
        assertNull(entity.getId());
        assertNull(entity.getColumnName());
        assertNull(entity.getJavaType());
    }
}
