package com.sxwl.config.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysConfigDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysConfigDTO 测试")
class SysConfigDTOTest {

    @Test
    @DisplayName("getter/setter 应正常读写")
    void getterSetter_shouldWork() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setId(1L);
        dto.setConfigKey("sys.siteName");
        dto.setConfigName("站点名称");
        dto.setConfigValue("Sxwl");
        dto.setConfigType("system");
        dto.setDescription("系统站点名称");
        dto.setStatus(1);
        dto.setCreateTime("2026-07-05 12:00:00");

        assertEquals(1L, dto.getId());
        assertEquals("sys.siteName", dto.getConfigKey());
        assertEquals("站点名称", dto.getConfigName());
        assertEquals("Sxwl", dto.getConfigValue());
        assertEquals("system", dto.getConfigType());
        assertEquals("系统站点名称", dto.getDescription());
        assertEquals(1, dto.getStatus());
        assertEquals("2026-07-05 12:00:00", dto.getCreateTime());
    }

    @Test
    @DisplayName("默认值应为 null")
    void defaults_shouldBeNull() {
        SysConfigDTO dto = new SysConfigDTO();
        assertNull(dto.getId());
        assertNull(dto.getConfigKey());
        assertNull(dto.getConfigName());
        assertNull(dto.getConfigValue());
        assertNull(dto.getConfigType());
        assertNull(dto.getDescription());
        assertNull(dto.getStatus());
        assertNull(dto.getCreateTime());
    }
}
