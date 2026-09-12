package com.sxwl.rustfs.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link UploadChunkDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("UploadChunkDTO 测试")
class UploadChunkDTOTest {
    @Test
    void getterSetter_shouldWork() {
        UploadChunkDTO dto = new UploadChunkDTO();
        dto.setUploadId(1L);
        dto.setChunkIndex(2);
        assertEquals(1L, dto.getUploadId());
        assertEquals(2, dto.getChunkIndex());
    }

    @Test
    void allArgsConstructor_shouldSetFields() {
        UploadChunkDTO dto = new UploadChunkDTO(1L, 3);
        assertEquals(1L, dto.getUploadId());
        assertEquals(3, dto.getChunkIndex());
    }
}
