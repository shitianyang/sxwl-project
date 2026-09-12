package com.sxwl.codegen.model.params;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysCodegenTablePageParams} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysCodegenTablePageParams 测试")
class SysCodegenTablePageParamsTest {

    @Test
    void defaults_shouldBe1And10() {
        SysCodegenTablePageParams params = new SysCodegenTablePageParams();
        assertEquals(1, params.getCurrent());
        assertEquals(10, params.getPageSize());
        assertNull(params.getTableName());
        assertNull(params.getBizNameCn());
        assertNull(params.getStatus());
    }

    @Test
    void getterSetter_shouldWork() {
        SysCodegenTablePageParams params = new SysCodegenTablePageParams();
        params.setTableName("sys_role");
        params.setBizNameCn("角色");
        params.setStatus(1);

        assertEquals("sys_role", params.getTableName());
        assertEquals("角色", params.getBizNameCn());
        assertEquals(1, params.getStatus());
    }
}
