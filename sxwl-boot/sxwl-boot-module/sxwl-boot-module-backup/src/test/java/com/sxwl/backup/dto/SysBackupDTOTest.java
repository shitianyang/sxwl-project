package com.sxwl.backup.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysBackupDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysBackupDTO 测试")
class SysBackupDTOTest {

    @Test
    @DisplayName("getter/setter 应正常读写")
    void getterSetter_shouldWork() {
        SysBackupDTO dto = new SysBackupDTO();
        dto.setId(1L);
        dto.setFileName("backup.sql.gz");
        dto.setFileSize(1024L);
        dto.setFileSizeDisplay("1.00 KB");
        dto.setFileUrl("http://minio/backup/1.sql.gz");
        dto.setStatus(1);
        dto.setCreateTime("2026-07-05 12:00:00");

        assertEquals(1L, dto.getId());
        assertEquals("backup.sql.gz", dto.getFileName());
        assertEquals(1024L, dto.getFileSize());
        assertEquals("1.00 KB", dto.getFileSizeDisplay());
        assertEquals("http://minio/backup/1.sql.gz", dto.getFileUrl());
        assertEquals(1, dto.getStatus());
        assertEquals("2026-07-05 12:00:00", dto.getCreateTime());
    }

    @Test
    @DisplayName("默认值应为 null")
    void defaults_shouldBeNull() {
        SysBackupDTO dto = new SysBackupDTO();
        assertNull(dto.getId());
        assertNull(dto.getFileName());
        assertNull(dto.getFileSize());
        assertNull(dto.getFileSizeDisplay());
        assertNull(dto.getFileUrl());
        assertNull(dto.getStatus());
        assertNull(dto.getCreateTime());
    }
}
