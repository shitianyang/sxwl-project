package com.sxwl.rustfs.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ChunkCheckDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("ChunkCheckDTO 测试")
class ChunkCheckDTOTest {
    @Test
    void getterSetter_shouldWork() {
        ChunkCheckDTO dto = new ChunkCheckDTO();
        dto.setUploadId(1L);
        dto.setUploadedChunks(List.of(0, 1, 2));
        assertEquals(1L, dto.getUploadId());
        assertEquals(3, dto.getUploadedChunks().size());
    }

    @Test
    void allArgsConstructor_shouldSetFields() {
        ChunkCheckDTO dto = new ChunkCheckDTO(1L, List.of(0, 1));
        assertEquals(1L, dto.getUploadId());
        assertEquals(2, dto.getUploadedChunks().size());
    }
}
