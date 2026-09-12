package com.sxwl.rustfs.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysFileChunkInfo} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysFileChunkInfo 测试")
class SysFileChunkInfoTest {
    @Test
    void getterSetter_shouldWork() {
        SysFileChunkInfo entity = new SysFileChunkInfo();
        entity.setUploadId(1L);
        entity.setChunkIndex(0);
        entity.setChunkMd5("chunk-md5");
        entity.setObjectKey("tmp/1/0");
        entity.setChunkSize(256L);
        entity.setStatus(1);
        assertEquals(1L, entity.getUploadId());
        assertEquals(0, entity.getChunkIndex());
        assertEquals("chunk-md5", entity.getChunkMd5());
        assertEquals("tmp/1/0", entity.getObjectKey());
        assertEquals(256L, entity.getChunkSize());
        assertEquals(1, entity.getStatus());
    }
}
