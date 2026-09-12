package com.sxwl.rustfs.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysFileInfo} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysFileInfo 测试")
class SysFileInfoTest {

    @Test
    void getterSetter_shouldWork() {
        SysFileInfo entity = new SysFileInfo();
        entity.setId(1L);
        entity.setFileName("test.png");
        entity.setObjectKey("2026/07/24/uuid.png");
        entity.setFileUrl("http://minio/test.png");
        entity.setFileSize(1024L);
        entity.setFileType("image/png");
        entity.setFileSuffix("png");
        entity.setBucketName("sxwl-files");
        entity.setMd5("abc123");
        entity.setBusinessType("avatar");
        entity.setStatus(1);
        entity.setDescription("test description");
        entity.setCreateBy(100L);
        entity.setCreateTime(LocalDateTime.of(2026, 7, 24, 12, 0, 0));

        assertEquals(1L, entity.getId());
        assertEquals("test.png", entity.getFileName());
        assertEquals("2026/07/24/uuid.png", entity.getObjectKey());
        assertEquals("http://minio/test.png", entity.getFileUrl());
        assertEquals(1024L, entity.getFileSize());
        assertEquals("image/png", entity.getFileType());
        assertEquals("png", entity.getFileSuffix());
        assertEquals("sxwl-files", entity.getBucketName());
        assertEquals("abc123", entity.getMd5());
        assertEquals("avatar", entity.getBusinessType());
        assertEquals(1, entity.getStatus());
        assertEquals("test description", entity.getDescription());
        assertEquals(100L, entity.getCreateBy());
        assertEquals(LocalDateTime.of(2026, 7, 24, 12, 0, 0), entity.getCreateTime());
    }

    @Test
    void defaults_shouldBeNull() {
        SysFileInfo entity = new SysFileInfo();
        assertNull(entity.getId());
        assertNull(entity.getFileName());
        assertNull(entity.getObjectKey());
        assertNull(entity.getFileSize());
        assertNull(entity.getStatus());
    }
}
