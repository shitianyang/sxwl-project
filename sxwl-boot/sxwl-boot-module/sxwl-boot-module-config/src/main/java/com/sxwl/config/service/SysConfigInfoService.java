package com.sxwl.config.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.config.model.dto.SysConfigDTO;
import com.sxwl.config.model.params.SysConfigPageParams;

/**
 * 系统参数配置 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface SysConfigInfoService {

    /**
     * 根据 ID 查询配置
     */
    SysConfigDTO getConfigById(Long id);

    /**
     * 根据键名查询配置（供 SxwlConfigHelper 使用）
     */
    SysConfigDTO getConfigByKey(String configKey);

    /**
     * 分页查询配置列表
     */
    PageInfo<SysConfigDTO> getConfigPageByParams(SysConfigPageParams params);

    /**
     * 新增配置
     */
    int createConfig(SysConfigDTO dto);

    /**
     * 修改配置
     */
    int updateConfig(SysConfigDTO dto);

    /**
     * 删除配置（逻辑删除）
     */
    int deleteConfigById(Long id);
}
