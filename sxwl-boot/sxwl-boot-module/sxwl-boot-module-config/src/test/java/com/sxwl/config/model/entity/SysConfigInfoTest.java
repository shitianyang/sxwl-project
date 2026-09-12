package com.sxwl.config.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysConfigInfo} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysConfigInfo 测试")
class SysConfigInfoTest {

    @Test
    @DisplayName("getter/setter 应正常读写")
    void getterSetter_shouldWork() {
        SysConfigInfo entity = new SysConfigInfo();
        entity.setConfigKey("sys.siteName");
        entity.setConfigName("站点名称");
        entity.setConfigValue("Sxwl");
        entity.setConfigType("system");
        entity.setDescription("系统站点名称");
        entity.setStatus(1);

        assertEquals("sys.siteName", entity.getConfigKey());
        assertEquals("站点名称", entity.getConfigName());
        assertEquals("Sxwl", entity.getConfigValue());
        assertEquals("system", entity.getConfigType());
        assertEquals("系统站点名称", entity.getDescription());
        assertEquals(1, entity.getStatus());
    }

    @Test
    @DisplayName("默认值应为 null")
    void defaults_shouldBeNull() {
        SysConfigInfo entity = new SysConfigInfo();
        assertNull(entity.getConfigKey());
        assertNull(entity.getConfigName());
        assertNull(entity.getConfigValue());
        assertNull(entity.getConfigType());
        assertNull(entity.getDescription());
        assertNull(entity.getStatus());
    }
}
