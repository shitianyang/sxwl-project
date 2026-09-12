package com.sxwl.rustfs.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysFileDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysFileDTO 测试")
class SysFileDTOTest {
    @Test
    void getterSetter_shouldWork() {
        SysFileDTO dto = new SysFileDTO();
        dto.setId(1L);
        dto.setFileName("test.png");
        dto.setFileSize(1024L);
        dto.setFileType("image/png");
        dto.setFileSuffix("png");
        dto.setMd5("abc123");
        dto.setBusinessType("avatar");
        dto.setPresignedUrl("http://minio/test.png");
        dto.setCreateTime("2026-07-05 12:00:00");
        assertEquals(1L, dto.getId());
        assertEquals("test.png", dto.getFileName());
        assertEquals(1024L, dto.getFileSize());
        assertEquals("image/png", dto.getFileType());
        assertEquals("png", dto.getFileSuffix());
        assertEquals("abc123", dto.getMd5());
        assertEquals("avatar", dto.getBusinessType());
        assertEquals("http://minio/test.png", dto.getPresignedUrl());
        assertEquals("2026-07-05 12:00:00", dto.getCreateTime());
    }

    @Test
    void defaults_shouldBeNull() {
        SysFileDTO dto = new SysFileDTO();
        assertNull(dto.getId());
        assertNull(dto.getFileName());
    }
}
