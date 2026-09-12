package com.sxwl.codegen.service.impl;

import com.sxwl.codegen.mapper.SysCodegenFieldMapper;
import com.sxwl.codegen.mapper.SysCodegenTableMapper;
import com.sxwl.codegen.model.dto.CodegenPreviewDTO;
import com.sxwl.codegen.model.dto.SysCodegenFieldDTO;
import com.sxwl.codegen.model.dto.SysCodegenTableDTO;
import com.sxwl.common.exception.SxwlBusinessException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Writer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link CodegenServiceImpl} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CodegenServiceImpl 测试")
class CodegenServiceImplTest {

    @Mock
    private SysCodegenTableMapper sysCodegenTableMapper;

    @Mock
    private SysCodegenFieldMapper sysCodegenFieldMapper;

    @Mock
    private Configuration freemarkerConfiguration;

    private CodegenServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CodegenServiceImpl(sysCodegenTableMapper, sysCodegenFieldMapper, freemarkerConfiguration);
    }

    private SysCodegenTableDTO createMockTable() {
        SysCodegenTableDTO table = new SysCodegenTableDTO();
        table.setId(1L);
        table.setTableName("sys_role_info");
        table.setBizName("Role");
        table.setBizNameCn("角色");
        table.setBizNamePlural("Roles");
        table.setModulePrefix("system");
        table.setPackageName("com.sxwl.system");
        table.setAuthor("admin");
        table.setTableComment("角色表");
        table.setGenType("crud");

        SysCodegenFieldDTO field = new SysCodegenFieldDTO();
        field.setColumnName("role_code");
        field.setJavaType("String");
        field.setJavaFieldName("roleCode");
        table.setFields(List.of(field));

        return table;
    }

    // ===== generateCode =====

    @Test
    @DisplayName("generateCode 表配置不存在时应抛出异常")
    void generateCode_tableNotFound_shouldThrow() {
        when(sysCodegenTableMapper.getTableById(999L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.generateCode(999L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("generateCode 成功应返回 ZIP 字节")
    void generateCode_success_shouldReturnZipBytes() throws Exception {
        SysCodegenTableDTO table = createMockTable();
        when(sysCodegenTableMapper.getTableById(1L)).thenReturn(table);
        when(sysCodegenFieldMapper.getFieldsByTableId(1L)).thenReturn(table.getFields());

        Template template = mock(Template.class);
        doAnswer(invocation -> {
            Writer writer = invocation.getArgument(1);
            writer.write("package com.sxwl.system;");
            return null;
        }).when(template).process(anyMap(), any(Writer.class));

        when(freemarkerConfiguration.getTemplate(anyString())).thenReturn(template);

        byte[] result = service.generateCode(1L);

        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(freemarkerConfiguration, atLeastOnce()).getTemplate(anyString());
    }

    @Test
    @DisplayName("generateCode 部分模板渲染失败时应跳过继续")
    void generateCode_partialFailure_shouldSkip() throws Exception {
        SysCodegenTableDTO table = createMockTable();
        when(sysCodegenTableMapper.getTableById(1L)).thenReturn(table);
        when(sysCodegenFieldMapper.getFieldsByTableId(1L)).thenReturn(table.getFields());

        Template template = mock(Template.class);
        doThrow(new RuntimeException("Render error"))
                .when(template).process(anyMap(), any(Writer.class));

        when(freemarkerConfiguration.getTemplate(anyString())).thenReturn(template);

        byte[] result = service.generateCode(1L);

        assertNotNull(result);
        assertTrue(result.length >= 0);
    }

    // ===== preview =====

    @Test
    @DisplayName("preview 表配置不存在时应抛出异常")
    void preview_tableNotFound_shouldThrow() {
        when(sysCodegenTableMapper.getTableById(999L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.preview(999L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("preview 成功应返回预览列表")
    void preview_success_shouldReturnList() throws Exception {
        SysCodegenTableDTO table = createMockTable();
        when(sysCodegenTableMapper.getTableById(1L)).thenReturn(table);
        when(sysCodegenFieldMapper.getFieldsByTableId(1L)).thenReturn(table.getFields());

        Template template = mock(Template.class);
        doAnswer(invocation -> {
            Writer writer = invocation.getArgument(1);
            writer.write("package com.sxwl.system;\npublic class Role {");
            return null;
        }).when(template).process(anyMap(), any(Writer.class));

        when(freemarkerConfiguration.getTemplate(anyString())).thenReturn(template);

        List<CodegenPreviewDTO> previews = service.preview(1L);

        assertNotNull(previews);
        assertFalse(previews.isEmpty());
        assertEquals(10, previews.size());

        CodegenPreviewDTO first = previews.get(0);
        assertNotNull(first.getFilePath());
        assertTrue(first.getFilePath().contains("Role"));

        String content = first.getContent();
        assertTrue(content.length() <= 500);
        assertTrue(content.contains("package"));
    }

    @Test
    @DisplayName("preview 部分模板渲染失败时应跳过")
    void preview_partialFailure_shouldSkip() throws Exception {
        SysCodegenTableDTO table = createMockTable();
        when(sysCodegenTableMapper.getTableById(1L)).thenReturn(table);
        when(sysCodegenFieldMapper.getFieldsByTableId(1L)).thenReturn(table.getFields());

        Template template = mock(Template.class);
        doThrow(new RuntimeException("Template error"))
                .when(template).process(anyMap(), any(Writer.class));

        when(freemarkerConfiguration.getTemplate(anyString())).thenReturn(template);

        List<CodegenPreviewDTO> previews = service.preview(1L);

        assertNotNull(previews);
        assertTrue(previews.isEmpty()); // all 10 templates failed
    }
}
