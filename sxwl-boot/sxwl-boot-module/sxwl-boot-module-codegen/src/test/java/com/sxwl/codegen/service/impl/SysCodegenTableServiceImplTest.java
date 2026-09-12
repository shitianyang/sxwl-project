package com.sxwl.codegen.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.codegen.mapper.SysCodegenFieldMapper;
import com.sxwl.codegen.mapper.SysCodegenTableMapper;
import com.sxwl.codegen.model.dto.CodegenConfigDTO;
import com.sxwl.codegen.model.dto.SysCodegenFieldDTO;
import com.sxwl.codegen.model.dto.SysCodegenTableDTO;
import com.sxwl.codegen.model.entity.SysCodegenField;
import com.sxwl.codegen.model.entity.SysCodegenTable;
import com.sxwl.codegen.model.params.SysCodegenTablePageParams;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysCodegenTableServiceImpl} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysCodegenTableServiceImpl 测试")
class SysCodegenTableServiceImplTest {

    @Mock
    private SysCodegenTableMapper sysCodegenTableMapper;

    @Mock
    private SysCodegenFieldMapper sysCodegenFieldMapper;

    private SysCodegenTableServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SysCodegenTableServiceImpl(sysCodegenTableMapper, sysCodegenFieldMapper);
    }

    // ===== page =====

    @Test
    @DisplayName("page 应返回分页结果")
    void page_shouldReturn() {
        SysCodegenTablePageParams params = new SysCodegenTablePageParams();
        SysCodegenTableDTO dto = new SysCodegenTableDTO();
        when(sysCodegenTableMapper.getTablePageByParams(params)).thenReturn(List.of(dto));

        PageInfo<SysCodegenTableDTO> result = service.page(params);

        assertEquals(1, result.getList().size());
        assertSame(dto, result.getList().get(0));
    }

    @Test
    @DisplayName("page 无数据应返回空分页")
    void page_empty_shouldReturnEmpty() {
        SysCodegenTablePageParams params = new SysCodegenTablePageParams();
        when(sysCodegenTableMapper.getTablePageByParams(params)).thenReturn(List.of());

        PageInfo<SysCodegenTableDTO> result = service.page(params);

        assertTrue(result.getList().isEmpty());
    }

    // ===== getDetail =====

    @Test
    @DisplayName("getDetail 存在时应返回详情含字段列表")
    void getDetail_found_shouldReturnWithFields() {
        SysCodegenTableDTO dto = new SysCodegenTableDTO();
        dto.setId(1L);
        SysCodegenFieldDTO field = new SysCodegenFieldDTO();
        when(sysCodegenTableMapper.getTableById(1L)).thenReturn(dto);
        when(sysCodegenFieldMapper.getFieldsByTableId(1L)).thenReturn(List.of(field));

        SysCodegenTableDTO result = service.getDetail(1L);

        assertSame(dto, result);
        assertEquals(1, result.getFields().size());
    }

    @Test
    @DisplayName("getDetail 不存在时应抛出异常")
    void getDetail_notFound_shouldThrow() {
        when(sysCodegenTableMapper.getTableById(999L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.getDetail(999L));
        assertEquals(10004, ex.getCode());
    }

    // ===== create =====

    @Test
    @DisplayName("create 表名已存在时应抛出异常")
    void create_duplicateName_shouldThrow() {
        CodegenConfigDTO config = new CodegenConfigDTO();
        config.setTableName("sys_role_info");
        when(sysCodegenTableMapper.checkTableNameUnique("sys_role_info", null)).thenReturn(1);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.create(config));
        assertEquals(10001, ex.getCode());
    }

    @Test
    @DisplayName("create 成功应插入并返回新记录")
    void create_success_shouldInsertAndReturn() {
        CodegenConfigDTO config = new CodegenConfigDTO();
        config.setTableName("sys_role_info");
        config.setModulePrefix("system");
        config.setBizName("Role");
        config.setBizNameCn("角色");
        config.setBizNamePlural("Roles");
        config.setTableComment("角色表");
        config.setPackageName("com.sxwl.system");
        config.setAuthor("admin");
        config.setGenType("crud");

        when(sysCodegenTableMapper.checkTableNameUnique("sys_role_info", null)).thenReturn(0);

        SysCodegenTableDTO created = new SysCodegenTableDTO();
        created.setId(1L);
        when(sysCodegenTableMapper.getTableById(anyLong())).thenReturn(created);

        try (MockedStatic<SxwlSnowFlakeUtils> snowFlake = mockStatic(SxwlSnowFlakeUtils.class)) {
            snowFlake.when(SxwlSnowFlakeUtils::nextId).thenReturn(100L);

            ArgumentCaptor<SysCodegenTable> captor = ArgumentCaptor.forClass(SysCodegenTable.class);
            when(sysCodegenTableMapper.insertTable(captor.capture())).thenReturn(1);

            SysCodegenTableDTO result = service.create(config);

            SysCodegenTable entity = captor.getValue();
            assertEquals("sys_role_info", entity.getTableName());
            assertEquals("system", entity.getModulePrefix());
            assertEquals("Role", entity.getBizName());
            assertEquals("crud", entity.getGenType());
            assertEquals(1, entity.getStatus().intValue());
            assertEquals(100L, entity.getId().longValue());

            assertEquals(created, result);
            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("create genType 为 null 时应默认为 crud")
    void create_nullGenType_shouldDefaultCrud() {
        CodegenConfigDTO config = new CodegenConfigDTO();
        config.setTableName("sys_dept_info");
        config.setBizName("Dept");

        when(sysCodegenTableMapper.checkTableNameUnique("sys_dept_info", null)).thenReturn(0);

        SysCodegenTableDTO created = new SysCodegenTableDTO();
        when(sysCodegenTableMapper.getTableById(anyLong())).thenReturn(created);

        try (MockedStatic<SxwlSnowFlakeUtils> snowFlake = mockStatic(SxwlSnowFlakeUtils.class)) {
            snowFlake.when(SxwlSnowFlakeUtils::nextId).thenReturn(200L);

            ArgumentCaptor<SysCodegenTable> captor = ArgumentCaptor.forClass(SysCodegenTable.class);
            when(sysCodegenTableMapper.insertTable(captor.capture())).thenReturn(1);

            service.create(config);

            assertEquals("crud", captor.getValue().getGenType());
        }
    }

    // ===== update =====

    @Test
    @DisplayName("update 表配置不存在时应抛出异常")
    void update_notFound_shouldThrow() {
        when(sysCodegenTableMapper.getTableById(999L)).thenReturn(null);

        CodegenConfigDTO config = new CodegenConfigDTO();
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.update(999L, config));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("update 表名冲突时应抛出异常")
    void update_duplicateName_shouldThrow() {
        SysCodegenTableDTO existing = new SysCodegenTableDTO();
        existing.setTableName("old_name");
        when(sysCodegenTableMapper.getTableById(1L)).thenReturn(existing);

        CodegenConfigDTO config = new CodegenConfigDTO();
        config.setTableName("new_name");
        when(sysCodegenTableMapper.checkTableNameUnique("new_name", 1L)).thenReturn(1);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.update(1L, config));
        assertEquals(10001, ex.getCode());
    }

    @Test
    @DisplayName("update 同名更新应跳过唯一校验")
    void update_sameName_shouldSkipCheck() {
        SysCodegenTableDTO existing = new SysCodegenTableDTO();
        existing.setTableName("sys_role_info");
        when(sysCodegenTableMapper.getTableById(1L)).thenReturn(existing);

        CodegenConfigDTO config = new CodegenConfigDTO();
        config.setTableName("sys_role_info");
        config.setBizName("NewRole");

        when(sysCodegenTableMapper.updateTable(any())).thenReturn(1);

        service.update(1L, config);

        verify(sysCodegenTableMapper, never()).checkTableNameUnique(anyString(), anyLong());
        verify(sysCodegenTableMapper).updateTable(any());
    }

    @Test
    @DisplayName("update 成功应更新表配置")
    void update_success_shouldUpdate() {
        SysCodegenTableDTO existing = new SysCodegenTableDTO();
        existing.setTableName("old_name");
        when(sysCodegenTableMapper.getTableById(1L)).thenReturn(existing);

        CodegenConfigDTO config = new CodegenConfigDTO();
        config.setTableName("new_name");
        config.setBizName("NewRole");
        when(sysCodegenTableMapper.checkTableNameUnique("new_name", 1L)).thenReturn(0);

        ArgumentCaptor<SysCodegenTable> captor = ArgumentCaptor.forClass(SysCodegenTable.class);
        when(sysCodegenTableMapper.updateTable(captor.capture())).thenReturn(1);

        service.update(1L, config);

        assertEquals("new_name", captor.getValue().getTableName());
        assertEquals("NewRole", captor.getValue().getBizName());
        assertEquals(1L, captor.getValue().getId().longValue());
    }

    // ===== delete =====

    @Test
    @DisplayName("delete 表配置不存在时应抛出异常")
    void delete_notFound_shouldThrow() {
        when(sysCodegenTableMapper.getTableById(999L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.delete(999L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("delete 成功应级联删除字段和表配置")
    void delete_success_shouldCascadeDelete() {
        SysCodegenTableDTO existing = new SysCodegenTableDTO();
        existing.setTableName("sys_role_info");
        when(sysCodegenTableMapper.getTableById(1L)).thenReturn(existing);

        service.delete(1L);

        verify(sysCodegenFieldMapper).deleteFieldsByTableId(1L);
        verify(sysCodegenTableMapper).deleteTableById(1L);
    }

    // ===== saveFieldConfigs =====

    @Test
    @DisplayName("saveFieldConfigs 应删除旧字段并批量插入")
    void saveFieldConfigs_shouldDeleteAndInsert() {
        SysCodegenFieldDTO fieldDto = new SysCodegenFieldDTO();
        fieldDto.setColumnName("role_code");
        fieldDto.setJavaType("String");
        fieldDto.setJavaFieldName("roleCode");

        try (MockedStatic<SxwlSnowFlakeUtils> snowFlake = mockStatic(SxwlSnowFlakeUtils.class)) {
            snowFlake.when(SxwlSnowFlakeUtils::nextId).thenReturn(100L, 200L, 300L);

            ArgumentCaptor<List<SysCodegenField>> captor = ArgumentCaptor.forClass(List.class);
            when(sysCodegenFieldMapper.batchInsertFields(captor.capture())).thenReturn(3);

            List<SysCodegenFieldDTO> fields = List.of(fieldDto, fieldDto, fieldDto);
            service.saveFieldConfigs(1L, fields);

            verify(sysCodegenFieldMapper).deleteFieldsByTableId(1L);

            List<SysCodegenField> entities = captor.getValue();
            assertEquals(3, entities.size());
            assertEquals(100L, entities.get(0).getId().longValue());
            assertEquals(200L, entities.get(1).getId().longValue());
            assertEquals(300L, entities.get(2).getId().longValue());
            assertEquals("role_code", entities.get(0).getColumnName());
            assertEquals(1L, entities.get(0).getTableId().longValue());
        }
    }

    @Test
    @DisplayName("saveFieldConfigs 空字段列表应跳过批量插入")
    void saveFieldConfigs_empty_shouldSkipInsert() {
        service.saveFieldConfigs(1L, List.of());

        verify(sysCodegenFieldMapper).deleteFieldsByTableId(1L);
        verify(sysCodegenFieldMapper, never()).batchInsertFields(any());
    }
}
