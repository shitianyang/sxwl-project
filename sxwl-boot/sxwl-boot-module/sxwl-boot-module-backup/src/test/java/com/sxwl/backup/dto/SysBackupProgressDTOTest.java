package com.sxwl.backup.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysBackupProgressDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysBackupProgressDTO 测试")
class SysBackupProgressDTOTest {

    @Test
    @DisplayName("全参构造应正确设置字段")
    void allArgsConstructor_shouldSetFields() {
        SysBackupProgressDTO dto = new SysBackupProgressDTO(1L, 50, "备份中...");
        assertEquals(1L, dto.getFileId());
        assertEquals(50, dto.getProgress());
        assertEquals("备份中...", dto.getMessage());
    }

    @Test
    @DisplayName("无参构造应创建空对象")
    void noArgsConstructor_shouldCreateEmpty() {
        SysBackupProgressDTO dto = new SysBackupProgressDTO();
        assertNull(dto.getFileId());
        assertEquals(0, dto.getProgress());
        assertNull(dto.getMessage());
    }

    @Test
    @DisplayName("getter/setter 应正常读写")
    void getterSetter_shouldWork() {
        SysBackupProgressDTO dto = new SysBackupProgressDTO();
        dto.setFileId(2L);
        dto.setProgress(100);
        dto.setMessage("备份完成");

        assertEquals(2L, dto.getFileId());
        assertEquals(100, dto.getProgress());
        assertEquals("备份完成", dto.getMessage());
    }
}
