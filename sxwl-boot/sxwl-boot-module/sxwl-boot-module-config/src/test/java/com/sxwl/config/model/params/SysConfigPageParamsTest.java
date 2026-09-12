package com.sxwl.config.model.params;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysConfigPageParams} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysConfigPageParams 测试")
class SysConfigPageParamsTest {

    @Test
    @DisplayName("默认分页参数应为 1/10")
    void defaults_shouldBe1And10() {
        SysConfigPageParams params = new SysConfigPageParams();
        assertEquals(1, params.getCurrent());
        assertEquals(10, params.getPageSize());
        assertNull(params.getConfigKey());
        assertNull(params.getConfigName());
        assertNull(params.getConfigType());
        assertNull(params.getStatus());
    }

    @Test
    @DisplayName("getter/setter 应正常读写")
    void getterSetter_shouldWork() {
        SysConfigPageParams params = new SysConfigPageParams();
        params.setCurrent(2);
        params.setPageSize(20);
        params.setConfigKey("sys.site");
        params.setConfigName("站点");
        params.setConfigType("system");
        params.setStatus(1);

        assertEquals(2, params.getCurrent());
        assertEquals(20, params.getPageSize());
        assertEquals("sys.site", params.getConfigKey());
        assertEquals("站点", params.getConfigName());
        assertEquals("system", params.getConfigType());
        assertEquals(1, params.getStatus());
    }
}
