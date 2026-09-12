package com.sxwl.rustfs.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link UploadCompleteDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("UploadCompleteDTO 测试")
class UploadCompleteDTOTest {
    @Test
    void getterSetter_shouldWork() {
        UploadCompleteDTO dto = new UploadCompleteDTO();
        dto.setUploadId(1L);
        dto.setFileMd5("abc123");
        assertEquals(1L, dto.getUploadId());
        assertEquals("abc123", dto.getFileMd5());
    }
}
