package com.sxwl.codegen.service;

import com.sxwl.codegen.model.dto.CodegenConfigDTO;
import com.sxwl.codegen.model.dto.SysCodegenFieldDTO;
import com.sxwl.codegen.model.dto.SysCodegenTableDTO;
import com.sxwl.codegen.model.params.SysCodegenTablePageParams;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 代码生成-表配置管理 Service
 *
 * <p>负责表配置和字段配置的 CRUD 操作。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public interface SysCodegenTableService {

    /**
     * 分页查询已配置的表
     */
    PageInfo<SysCodegenTableDTO> page(SysCodegenTablePageParams params);

    /**
     * 获取表详情（含字段列表）
     */
    SysCodegenTableDTO getDetail(Long tableId);

    /**
     * 新增表配置
     */
    SysCodegenTableDTO create(CodegenConfigDTO config);

    /**
     * 更新表配置
     */
    void update(Long tableId, CodegenConfigDTO config);

    /**
     * 删除表配置（级联删除字段配置）
     */
    void delete(Long tableId);

    /**
     * 保存字段配置
     */
    void saveFieldConfigs(Long tableId, List<SysCodegenFieldDTO> fields);
}
