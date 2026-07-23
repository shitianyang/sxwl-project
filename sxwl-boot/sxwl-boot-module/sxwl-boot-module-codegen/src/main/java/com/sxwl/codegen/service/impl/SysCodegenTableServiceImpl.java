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
import com.sxwl.codegen.service.SysCodegenTableService;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码生成-表配置管理 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Service
public class SysCodegenTableServiceImpl implements SysCodegenTableService {

    private static final Logger log = LoggerFactory.getLogger(SysCodegenTableServiceImpl.class);

    private final SysCodegenTableMapper sysCodegenTableMapper;
    private final SysCodegenFieldMapper sysCodegenFieldMapper;

    public SysCodegenTableServiceImpl(SysCodegenTableMapper sysCodegenTableMapper,
                                       SysCodegenFieldMapper sysCodegenFieldMapper) {
        this.sysCodegenTableMapper = sysCodegenTableMapper;
        this.sysCodegenFieldMapper = sysCodegenFieldMapper;
    }

    @Override
    public PageInfo<SysCodegenTableDTO> page(SysCodegenTablePageParams params) {
        List<SysCodegenTableDTO> rows = sysCodegenTableMapper.getTablePageByParams(params);
        return new PageInfo<>(rows);
    }

    @Override
    public SysCodegenTableDTO getDetail(Long tableId) {
        SysCodegenTableDTO dto = sysCodegenTableMapper.getTableById(tableId);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "表配置不存在或已被删除");
        }
        List<SysCodegenFieldDTO> fields = sysCodegenFieldMapper.getFieldsByTableId(tableId);
        dto.setFields(fields);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysCodegenTableDTO create(CodegenConfigDTO config) {
        // 校验表名唯一
        if (sysCodegenTableMapper.checkTableNameUnique(config.getTableName(), null) > 0) {
            throw new SxwlBusinessException(10001, "表名 '" + config.getTableName() + "' 已存在");
        }

        LocalDateTime now = LocalDateTime.now();
        SysCodegenTable entity = new SysCodegenTable();
        entity.setId(SxwlSnowFlakeUtils.nextId());
        entity.setTableName(config.getTableName());
        entity.setModulePrefix(config.getModulePrefix());
        entity.setBizName(config.getBizName());
        entity.setBizNameCn(config.getBizNameCn());
        entity.setBizNamePlural(config.getBizNamePlural());
        entity.setTableComment(config.getTableComment());
        entity.setPackageName(config.getPackageName());
        entity.setAuthor(config.getAuthor());
        entity.setGenType(config.getGenType() != null ? config.getGenType() : "crud");
        entity.setStatus(1);
        entity.setCreateBy(0L);
        entity.setCreateOrg(0L);
        entity.setCreateTime(now);
        entity.setDeleteFlag(0);

        sysCodegenTableMapper.insertTable(entity);

        log.info("新增表配置：{} ({})", config.getTableName(), entity.getId());
        return sysCodegenTableMapper.getTableById(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long tableId, CodegenConfigDTO config) {
        SysCodegenTableDTO existing = sysCodegenTableMapper.getTableById(tableId);
        if (existing == null) {
            throw new SxwlBusinessException(10004, "表配置不存在或已被删除");
        }

        // 校验表名唯一
        if (!config.getTableName().equals(existing.getTableName())
                && sysCodegenTableMapper.checkTableNameUnique(config.getTableName(), tableId) > 0) {
            throw new SxwlBusinessException(10001, "表名 '" + config.getTableName() + "' 已存在");
        }

        SysCodegenTable entity = new SysCodegenTable();
        entity.setId(tableId);
        entity.setTableName(config.getTableName());
        entity.setModulePrefix(config.getModulePrefix());
        entity.setBizName(config.getBizName());
        entity.setBizNameCn(config.getBizNameCn());
        entity.setBizNamePlural(config.getBizNamePlural());
        entity.setTableComment(config.getTableComment());
        entity.setPackageName(config.getPackageName());
        entity.setAuthor(config.getAuthor());
        entity.setGenType(config.getGenType());
        entity.setStatus(1);
        entity.setUpdateBy(0L);
        entity.setUpdateTime(LocalDateTime.now());

        sysCodegenTableMapper.updateTable(entity);
        log.info("更新表配置：{} ({})", config.getTableName(), tableId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long tableId) {
        SysCodegenTableDTO existing = sysCodegenTableMapper.getTableById(tableId);
        if (existing == null) {
            throw new SxwlBusinessException(10004, "表配置不存在或已被删除");
        }

        // 级联删除字段配置
        sysCodegenFieldMapper.deleteFieldsByTableId(tableId);
        // 删除表配置
        sysCodegenTableMapper.deleteTableById(tableId);
        log.info("删除表配置：{} ({})", existing.getTableName(), tableId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFieldConfigs(Long tableId, List<SysCodegenFieldDTO> fields) {
        // 先删除旧字段配置
        sysCodegenFieldMapper.deleteFieldsByTableId(tableId);

        // 批量插入新字段配置
        LocalDateTime now = LocalDateTime.now();
        List<SysCodegenField> entities = fields.stream().map(dto -> {
            SysCodegenField entity = new SysCodegenField();
            entity.setId(SxwlSnowFlakeUtils.nextId());
            entity.setTableId(tableId);
            entity.setColumnName(dto.getColumnName());
            entity.setColumnType(dto.getColumnType());
            entity.setColumnComment(dto.getColumnComment());
            entity.setJavaType(dto.getJavaType());
            entity.setJavaFieldName(dto.getJavaFieldName());
            entity.setIsPk(dto.getIsPk() != null ? dto.getIsPk() : 0);
            entity.setIsInsert(dto.getIsInsert() != null ? dto.getIsInsert() : 1);
            entity.setIsEdit(dto.getIsEdit() != null ? dto.getIsEdit() : 1);
            entity.setIsList(dto.getIsList() != null ? dto.getIsList() : 1);
            entity.setIsQuery(dto.getIsQuery() != null ? dto.getIsQuery() : 0);
            entity.setQueryType(dto.getQueryType());
            entity.setQueryFormType(dto.getQueryFormType());
            entity.setFormType(dto.getFormType());
            entity.setFormDictCode(dto.getFormDictCode());
            entity.setIsRequired(dto.getIsRequired() != null ? dto.getIsRequired() : 0);
            entity.setIsUnique(dto.getIsUnique() != null ? dto.getIsUnique() : 0);
            entity.setMaxLength(dto.getMaxLength());
            entity.setSort(dto.getSort() != null ? dto.getSort() : 0);
            entity.setCreateTime(now.toString());
            return entity;
        }).collect(Collectors.toList());

        if (!entities.isEmpty()) {
            sysCodegenFieldMapper.batchInsertFields(entities);
        }

        log.info("保存字段配置：tableId={}, count={}", tableId, entities.size());
    }
}
