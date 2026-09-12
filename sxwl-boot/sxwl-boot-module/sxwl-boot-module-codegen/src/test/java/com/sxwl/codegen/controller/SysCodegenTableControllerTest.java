package com.sxwl.codegen.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.codegen.model.dto.CodegenConfigDTO;
import com.sxwl.codegen.model.dto.SysCodegenFieldDTO;
import com.sxwl.codegen.model.dto.SysCodegenTableDTO;
import com.sxwl.codegen.model.params.SysCodegenTablePageParams;
import com.sxwl.codegen.service.SysCodegenTableService;
import com.sxwl.common.entity.SxwlResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysCodegenTableController} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysCodegenTableController 测试")
class SysCodegenTableControllerTest {

    @Mock
    private SysCodegenTableService sysCodegenTableService;

    private SysCodegenTableController controller;

    @BeforeEach
    void setUp() {
        controller = new SysCodegenTableController(sysCodegenTableService);
    }

    @Test
    @DisplayName("page 应返回分页结果")
    void page_shouldReturn() {
        SysCodegenTablePageParams params = new SysCodegenTablePageParams();
        PageInfo<SysCodegenTableDTO> page = new PageInfo<>(List.of(new SysCodegenTableDTO()));
        when(sysCodegenTableService.page(params)).thenReturn(page);

        SxwlResult<PageInfo<SysCodegenTableDTO>> result = controller.page(params);

        assertEquals(200, result.getCode());
        assertSame(page, result.getData());
    }

    @Test
    @DisplayName("getDetail 应返回详情")
    void getDetail_shouldReturn() {
        SysCodegenTableDTO dto = new SysCodegenTableDTO();
        when(sysCodegenTableService.getDetail(1L)).thenReturn(dto);

        SxwlResult<SysCodegenTableDTO> result = controller.getDetail(1L);

        assertEquals(200, result.getCode());
        assertSame(dto, result.getData());
    }

    @Test
    @DisplayName("create 应返回新创建的表配置")
    void create_shouldReturn() {
        CodegenConfigDTO config = new CodegenConfigDTO();
        SysCodegenTableDTO dto = new SysCodegenTableDTO();
        when(sysCodegenTableService.create(config)).thenReturn(dto);

        SxwlResult<SysCodegenTableDTO> result = controller.create(config);

        assertEquals(200, result.getCode());
        assertSame(dto, result.getData());
    }

    @Test
    @DisplayName("update 应返回成功")
    void update_shouldReturnSuccess() {
        CodegenConfigDTO config = new CodegenConfigDTO();

        SxwlResult<Void> result = controller.update(1L, config);

        assertEquals(200, result.getCode());
        verify(sysCodegenTableService).update(1L, config);
    }

    @Test
    @DisplayName("delete 应返回成功")
    void delete_shouldReturnSuccess() {
        SxwlResult<Void> result = controller.delete(1L);

        assertEquals(200, result.getCode());
        verify(sysCodegenTableService).delete(1L);
    }

    @Test
    @DisplayName("saveFields 应返回成功")
    void saveFields_shouldReturnSuccess() {
        List<SysCodegenFieldDTO> fields = List.of(new SysCodegenFieldDTO());

        SxwlResult<Void> result = controller.saveFields(1L, fields);

        assertEquals(200, result.getCode());
        verify(sysCodegenTableService).saveFieldConfigs(1L, fields);
    }
}
