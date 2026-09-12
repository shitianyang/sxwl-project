package com.sxwl.codegen.controller;

import com.sxwl.codegen.model.dto.CodegenPreviewDTO;
import com.sxwl.codegen.service.CodegenService;
import com.sxwl.common.entity.SxwlResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link CodegenController} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CodegenController 测试")
class CodegenControllerTest {

    @Mock
    private CodegenService codegenService;

    private CodegenController controller;

    @BeforeEach
    void setUp() {
        controller = new CodegenController(codegenService);
    }

    @Test
    @DisplayName("generate 应返回 ZIP 下载响应")
    void generate_shouldReturnZip() {
        byte[] zipBytes = "zip-content".getBytes();
        when(codegenService.generateCode(1L)).thenReturn(zipBytes);

        ResponseEntity<byte[]> response = controller.generate(1L);

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("generated-code.zip"));
        assertEquals(zipBytes.length, response.getHeaders().getContentLength());
        assertArrayEquals(zipBytes, response.getBody());
    }

    @Test
    @DisplayName("preview 应返回预览列表")
    void preview_shouldReturn() {
        CodegenPreviewDTO dto = new CodegenPreviewDTO();
        dto.setFilePath("test.java");
        when(codegenService.preview(1L)).thenReturn(List.of(dto));

        SxwlResult<List<CodegenPreviewDTO>> result = controller.preview(1L);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
        assertEquals("test.java", result.getData().get(0).getFilePath());
    }

    @Test
    @DisplayName("preview 空列表应返回成功")
    void preview_empty_shouldReturn() {
        when(codegenService.preview(2L)).thenReturn(List.of());

        SxwlResult<List<CodegenPreviewDTO>> result = controller.preview(2L);

        assertEquals(200, result.getCode());
        assertTrue(result.getData().isEmpty());
    }
}
