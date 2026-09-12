package com.sxwl.config.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.config.mapper.SysConfigInfoMapper;
import com.sxwl.config.model.dto.SysConfigDTO;
import com.sxwl.config.model.entity.SysConfigInfo;
import com.sxwl.config.model.params.SysConfigPageParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysConfigInfoServiceImpl} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysConfigInfoServiceImpl 测试")
class SysConfigInfoServiceImplTest {

    @Mock
    private SysConfigInfoMapper sysConfigInfoMapper;

    private SysConfigInfoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SysConfigInfoServiceImpl(sysConfigInfoMapper);
    }

    @Test
    @DisplayName("getConfigById 存在时应返回 DTO")
    void getConfigById_found_shouldReturn() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setId(1L);
        dto.setConfigKey("sys.siteName");
        when(sysConfigInfoMapper.getConfigById(1L)).thenReturn(dto);

        SysConfigDTO result = service.getConfigById(1L);
        assertSame(dto, result);
    }

    @Test
    @DisplayName("getConfigById 不存在时应抛出异常")
    void getConfigById_notFound_shouldThrow() {
        when(sysConfigInfoMapper.getConfigById(999L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.getConfigById(999L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("getConfigByKey 应返回 DTO")
    void getConfigByKey_shouldReturn() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setConfigKey("sys.siteName");
        when(sysConfigInfoMapper.getConfigByKey("sys.siteName")).thenReturn(dto);

        SysConfigDTO result = service.getConfigByKey("sys.siteName");
        assertSame(dto, result);
    }

    @Test
    @DisplayName("getConfigPageByParams 应返回分页结果")
    void getConfigPageByParams_shouldReturnPage() {
        SysConfigDTO dto1 = new SysConfigDTO();
        dto1.setId(1L);
        SysConfigDTO dto2 = new SysConfigDTO();
        dto2.setId(2L);

        SysConfigPageParams params = new SysConfigPageParams();
        when(sysConfigInfoMapper.getConfigPageByParams(params)).thenReturn(List.of(dto1, dto2));

        PageInfo<SysConfigDTO> result = service.getConfigPageByParams(params);
        assertEquals(2, result.getList().size());
    }

    @Test
    @DisplayName("createConfig 键名重复时应抛出异常")
    void createConfig_duplicateKey_shouldThrow() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setConfigKey("sys.siteName");
        dto.setConfigValue("Sxwl");

        when(sysConfigInfoMapper.checkConfigKeyUnique("sys.siteName", null)).thenReturn(1);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.createConfig(dto));
        assertEquals(10002, ex.getCode());
        verify(sysConfigInfoMapper, never()).insertConfig(any());
    }

    @Test
    @DisplayName("createConfig 应成功创建")
    void createConfig_shouldSucceed() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setConfigKey("sys.siteName");
        dto.setConfigName("站点名称");
        dto.setConfigValue("Sxwl");
        dto.setConfigType("system");
        dto.setDescription("test");
        dto.setStatus(1);

        when(sysConfigInfoMapper.checkConfigKeyUnique("sys.siteName", null)).thenReturn(0);
        when(sysConfigInfoMapper.insertConfig(any(SysConfigInfo.class))).thenReturn(1);

        int result = service.createConfig(dto);
        assertEquals(1, result);
        verify(sysConfigInfoMapper).insertConfig(any(SysConfigInfo.class));
    }

    @Test
    @DisplayName("createConfig 插入失败时应抛出异常")
    void createConfig_insertFailed_shouldThrow() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setConfigKey("sys.siteName");
        dto.setConfigValue("Sxwl");

        when(sysConfigInfoMapper.checkConfigKeyUnique("sys.siteName", null)).thenReturn(0);
        when(sysConfigInfoMapper.insertConfig(any(SysConfigInfo.class))).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.createConfig(dto));
        assertEquals(10001, ex.getCode());
    }

    @Test
    @DisplayName("updateConfig 键名重复时应抛出异常")
    void updateConfig_duplicateKey_shouldThrow() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setId(1L);
        dto.setConfigKey("sys.siteName");

        when(sysConfigInfoMapper.checkConfigKeyUnique("sys.siteName", 1L)).thenReturn(1);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.updateConfig(dto));
        assertEquals(10002, ex.getCode());
        verify(sysConfigInfoMapper, never()).updateConfig(any());
    }

    @Test
    @DisplayName("updateConfig 应成功更新并记录变更")
    void updateConfig_shouldSucceedWithDiff() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setId(1L);
        dto.setConfigKey("sys.siteName");
        dto.setConfigName("站点名称");
        dto.setConfigValue("Sxwl-new");
        dto.setConfigType("system");
        dto.setStatus(1);

        SysConfigDTO oldDto = new SysConfigDTO();
        oldDto.setId(1L);
        oldDto.setConfigKey("sys.siteName");
        oldDto.setConfigName("站点名称");
        oldDto.setConfigValue("Sxwl-old");
        oldDto.setConfigType("system");
        oldDto.setStatus(1);

        when(sysConfigInfoMapper.checkConfigKeyUnique("sys.siteName", 1L)).thenReturn(0);
        when(sysConfigInfoMapper.getConfigById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(SysConfigInfo.class), any(SysConfigInfo.class)))
                    .thenReturn("[{\"field\":\"参数值\",\"oldValue\":\"Sxwl-old\",\"newValue\":\"Sxwl-new\"}]");
            when(sysConfigInfoMapper.updateConfig(any(SysConfigInfo.class))).thenReturn(1);

            int result = service.updateConfig(dto);
            assertEquals(1, result);
            diffUtils.verify(() -> SxwlDiffUtils.setContextDiff(anyString()));
        }
    }

    @Test
    @DisplayName("updateConfig 更新影响行数为 0 时应抛出异常")
    void updateConfig_zeroAffected_shouldThrow() {
        SysConfigDTO dto = new SysConfigDTO();
        dto.setId(999L);
        dto.setConfigKey("sys.test");

        when(sysConfigInfoMapper.checkConfigKeyUnique("sys.test", 999L)).thenReturn(0);
        when(sysConfigInfoMapper.getConfigById(999L)).thenReturn(null);
        when(sysConfigInfoMapper.updateConfig(any(SysConfigInfo.class))).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.updateConfig(dto));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("deleteConfigById 应成功删除")
    void deleteConfigById_shouldSucceed() {
        when(sysConfigInfoMapper.deleteConfigById(1L)).thenReturn(1);

        int result = service.deleteConfigById(1L);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("deleteConfigById 不存在时应抛出异常")
    void deleteConfigById_notFound_shouldThrow() {
        when(sysConfigInfoMapper.deleteConfigById(999L)).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.deleteConfigById(999L));
        assertEquals(10004, ex.getCode());
    }
}
