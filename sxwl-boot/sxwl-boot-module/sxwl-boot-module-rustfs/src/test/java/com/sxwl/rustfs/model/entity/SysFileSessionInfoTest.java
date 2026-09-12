package com.sxwl.rustfs.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysFileSessionInfo} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysFileSessionInfo 测试")
class SysFileSessionInfoTest {
    @Test
    void getterSetter_shouldWork() {
        SysFileSessionInfo entity = new SysFileSessionInfo();
        entity.setFileMd5("abc123");
        entity.setOriginalName("test.png");
        entity.setFileSize(1024L);
        entity.setContentType("image/png");
        entity.setTotalChunks(4);
        entity.setChunkSize(256);
        entity.setStatus(0);
        assertEquals("abc123", entity.getFileMd5());
        assertEquals("test.png", entity.getOriginalName());
        assertEquals(1024L, entity.getFileSize());
        assertEquals("image/png", entity.getContentType());
        assertEquals(4, entity.getTotalChunks());
        assertEquals(256, entity.getChunkSize());
        assertEquals(0, entity.getStatus());
    }
}
