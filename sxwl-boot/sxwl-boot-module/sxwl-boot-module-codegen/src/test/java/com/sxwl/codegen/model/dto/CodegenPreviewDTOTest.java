package com.sxwl.codegen.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link CodegenPreviewDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("CodegenPreviewDTO 测试")
class CodegenPreviewDTOTest {

    @Test
    void getterSetter_shouldWork() {
        CodegenPreviewDTO dto = new CodegenPreviewDTO();
        dto.setFilePath("main/java/com/sxwl/system/controller/SysRoleController.java");
        dto.setContent("package com.sxwl.system;");

        assertEquals("main/java/com/sxwl/system/controller/SysRoleController.java", dto.getFilePath());
        assertEquals("package com.sxwl.system;", dto.getContent());
    }

    @Test
    void defaults_shouldBeNull() {
        CodegenPreviewDTO dto = new CodegenPreviewDTO();
        assertNull(dto.getFilePath());
        assertNull(dto.getContent());
    }
}
