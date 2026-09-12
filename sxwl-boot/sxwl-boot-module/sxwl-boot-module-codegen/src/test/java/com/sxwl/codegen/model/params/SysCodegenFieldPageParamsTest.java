package com.sxwl.codegen.model.params;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysCodegenFieldPageParams} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysCodegenFieldPageParams 测试")
class SysCodegenFieldPageParamsTest {

    @Test
    void defaults_shouldBe1And50() {
        SysCodegenFieldPageParams params = new SysCodegenFieldPageParams();
        assertEquals(1, params.getCurrent());
        assertEquals(50, params.getPageSize());
        assertNull(params.getTableId());
        assertNull(params.getColumnName());
    }

    @Test
    void getterSetter_shouldWork() {
        SysCodegenFieldPageParams params = new SysCodegenFieldPageParams();
        params.setTableId(1L);
        params.setColumnName("role_code");

        assertEquals(1L, params.getTableId());
        assertEquals("role_code", params.getColumnName());
    }
}
