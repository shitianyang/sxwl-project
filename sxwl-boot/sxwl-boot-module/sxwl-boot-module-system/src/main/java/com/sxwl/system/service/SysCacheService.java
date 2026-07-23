package com.sxwl.system.service;

import com.sxwl.system.model.dto.SysCacheCategoryDTO;
import com.sxwl.system.model.dto.SysCacheKeyDetailDTO;

import java.util.List;

/**
 * 缓存管理 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface SysCacheService {

    /**
     * 获取所有预定义的缓存分类列表
     *
     * @return 缓存分类列表（名称 + Key 前缀模式）
     */
    List<SysCacheCategoryDTO> listCategories();

    /**
     * 根据分类前缀获取匹配的 Key 列表
     * <p>生产环境使用 SCAN 分批迭代，避免阻塞。</p>
     *
     * @param categoryKeyPrefix Key 前缀模式，如 "dict:*"
     * @return 匹配的 Key 详情列表
     */
    List<SysCacheKeyDetailDTO> listKeys(String categoryKeyPrefix);

    /**
     * 获取单个 Key 的详细信息
     *
     * @param key 完整 Key
     * @return Key 的详细信息（类型 + Value + TTL）
     */
    SysCacheKeyDetailDTO getKeyDetail(String key);

    /**
     * 清空指定分类下的所有缓存
     *
     * @param categoryKeyPrefix Key 前缀模式
     */
    void clearByName(String categoryKeyPrefix);

    /**
     * 删除单个缓存 Key
     *
     * @param key 完整 Key
     */
    void clearByKey(String key);
}
