package com.sxwl.system.model.dto;

/**
 * 缓存分类 DTO
 *
 * <p>对应一个预定义的缓存分类，包含名称和 Key 前缀模式。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysCacheCategoryDTO {

    /** 分类名称，如 "字典缓存" */
    private String name;

    /** Key 前缀模式，如 "dict:*" */
    private String keyPrefix;

    public SysCacheCategoryDTO() {
    }

    public SysCacheCategoryDTO(String name, String keyPrefix) {
        this.name = name;
        this.keyPrefix = keyPrefix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
