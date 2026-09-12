package com.sxwl.config.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.config.model.dto.SysConfigDTO;
import com.sxwl.config.model.params.SysConfigPageParams;
import com.sxwl.config.service.SysConfigInfoService;
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
 * {@link SysConfigController} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysConfigController 测试")
class SysConfigControllerTest {

    @Mock
    private SysConfigInfoService sysConfigInfoService;

    private SysConfigController controller;

    @BeforeEach
    void setUp() {
        controller = new SysConfigController(sysConfigInfoService);
    }

    @Test
    @DisplayName("getConfigById 应返回配置")
    void getConfigById_shouldReturn() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setId(1L);
        when(sysConfigInfoService.getConfigById(1L)).thenReturn(dto);

        SysConfigDTO result = controller.getConfigById(1L);
        assertSame(dto, result);
    }

    @Test
    @DisplayName("getConfigByKey 应返回配置")
    void getConfigByKey_shouldReturn() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setConfigKey("sys.siteName");
        when(sysConfigInfoService.getConfigByKey("sys.siteName")).thenReturn(dto);

        SysConfigDTO result = controller.getConfigByKey("sys.siteName");
        assertSame(dto, result);
    }

    @Test
    @DisplayName("getConfigPageByParams 应返回分页结果")
    void getConfigPageByParams_shouldReturn() {
        SysConfigPageParams params = new SysConfigPageParams();
        PageInfo<SysConfigDTO> page = new PageInfo<>(List.of(new SysConfigDTO()));
        when(sysConfigInfoService.getConfigPageByParams(params)).thenReturn(page);

        PageInfo<SysConfigDTO> result = controller.getConfigPageByParams(params);
        assertSame(page, result);
    }

    @Test
    @DisplayName("createConfig 应调用 service")
    void createConfig_shouldCallService() {
        SysConfigDTO dto = new SysConfigDTO();
        controller.createConfig(dto);
        verify(sysConfigInfoService).createConfig(dto);
    }

    @Test
    @DisplayName("updateConfig 应调用 service")
    void updateConfig_shouldCallService() {
        SysConfigDTO dto = new SysConfigDTO();
        controller.updateConfig(dto);
        verify(sysConfigInfoService).updateConfig(dto);
    }

    @Test
    @DisplayName("deleteConfigById 应调用 service")
    void deleteConfigById_shouldCallService() {
        controller.deleteConfigById(1L);
        verify(sysConfigInfoService).deleteConfigById(1L);
    }
}
