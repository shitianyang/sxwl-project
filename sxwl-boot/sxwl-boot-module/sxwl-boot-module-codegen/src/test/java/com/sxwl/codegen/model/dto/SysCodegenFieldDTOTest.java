package com.sxwl.codegen.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysCodegenFieldDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysCodegenFieldDTO 测试")
class SysCodegenFieldDTOTest {

    @Test
    void getterSetter_shouldWork() {
        SysCodegenFieldDTO dto = new SysCodegenFieldDTO();
        dto.setId(1L);
        dto.setTableId(1L);
        dto.setColumnName("role_code");
        dto.setColumnType("varchar");
        dto.setColumnComment("角色编码");
        dto.setJavaType("String");
        dto.setJavaFieldName("roleCode");
        dto.setIsPk(0);
        dto.setIsInsert(1);
        dto.setIsEdit(1);
        dto.setIsList(1);
        dto.setIsQuery(1);
        dto.setQueryType("eq");
        dto.setQueryFormType("Input");
        dto.setFormType("Input");
        dto.setFormDictCode("sys_role_type");
        dto.setIsRequired(1);
        dto.setIsUnique(1);
        dto.setMaxLength(50);
        dto.setSort(1);
        dto.setCreateTime("2026-07-24 12:00:00");

        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getTableId());
        assertEquals("role_code", dto.getColumnName());
        assertEquals("varchar", dto.getColumnType());
        assertEquals("角色编码", dto.getColumnComment());
        assertEquals("String", dto.getJavaType());
        assertEquals("roleCode", dto.getJavaFieldName());
        assertEquals(0, dto.getIsPk());
        assertEquals(1, dto.getIsInsert());
        assertEquals(1, dto.getIsEdit());
        assertEquals(1, dto.getIsList());
        assertEquals(1, dto.getIsQuery());
        assertEquals("eq", dto.getQueryType());
        assertEquals("Input", dto.getQueryFormType());
        assertEquals("Input", dto.getFormType());
        assertEquals("sys_role_type", dto.getFormDictCode());
        assertEquals(1, dto.getIsRequired());
        assertEquals(1, dto.getIsUnique());
        assertEquals(50, dto.getMaxLength());
        assertEquals(1, dto.getSort());
        assertEquals("2026-07-24 12:00:00", dto.getCreateTime());
    }

    @Test
    void defaults_shouldBeNull() {
        SysCodegenFieldDTO dto = new SysCodegenFieldDTO();
        assertNull(dto.getId());
        assertNull(dto.getColumnName());
    }
}
