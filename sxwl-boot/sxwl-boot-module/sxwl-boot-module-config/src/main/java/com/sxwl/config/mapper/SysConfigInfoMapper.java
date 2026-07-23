package com.sxwl.config.mapper;

import com.sxwl.config.model.dto.SysConfigDTO;
import com.sxwl.config.model.entity.SysConfigInfo;
import com.sxwl.config.model.params.SysConfigPageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统参数配置 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysConfigInfoMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Mapper
public interface SysConfigInfoMapper {

    /**
     * 根据 ID 查询配置
     */
    SysConfigDTO getConfigById(@Param("id") Long id);

    /**
     * 根据键名查询配置
     */
    SysConfigDTO getConfigByKey(@Param("configKey") String configKey);

    /**
     * 分页查询配置列表
     */
    List<SysConfigDTO> getConfigPageByParams(SysConfigPageParams params);

    /**
     * 查询所有启用的配置（用于启动时预加载缓存）
     */
    List<SysConfigDTO> selectAllEnabledConfigs();

    /**
     * 校验键名是否唯一（排除指定 ID）
     */
    int checkConfigKeyUnique(@Param("configKey") String configKey,
                             @Param("excludeId") Long excludeId);

    /**
     * 新增配置
     */
    int insertConfig(SysConfigInfo entity);

    /**
     * 修改配置
     */
    int updateConfig(SysConfigInfo entity);

    /**
     * 逻辑删除配置
     */
    int deleteConfigById(@Param("id") Long id);
}
