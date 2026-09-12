package com.sxwl.rustfs.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link UploadInitDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("UploadInitDTO 测试")
class UploadInitDTOTest {
    @Test
    void getterSetter_shouldWork() {
        UploadInitDTO dto = new UploadInitDTO();
        dto.setFileMd5("abc123");
        dto.setOriginalName("test.png");
        dto.setFileSize(1024L);
        dto.setContentType("image/png");
        dto.setTotalChunks(4);
        dto.setChunkSize(256);
        assertEquals("abc123", dto.getFileMd5());
        assertEquals("test.png", dto.getOriginalName());
        assertEquals(1024L, dto.getFileSize());
        assertEquals("image/png", dto.getContentType());
        assertEquals(4, dto.getTotalChunks());
        assertEquals(256, dto.getChunkSize());
    }
}
