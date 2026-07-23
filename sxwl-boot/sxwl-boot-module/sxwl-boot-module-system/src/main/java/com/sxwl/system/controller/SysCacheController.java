package com.sxwl.system.controller;

import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.system.model.dto.SysCacheCategoryDTO;
import com.sxwl.system.model.dto.SysCacheKeyDetailDTO;
import com.sxwl.system.service.SysCacheService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 缓存管理 Controller
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
@RequestMapping("/sys/cache")
public class SysCacheController {

    private final SysCacheService sysCacheService;

    public SysCacheController(SysCacheService sysCacheService) {
        this.sysCacheService = sysCacheService;
    }

    /**
     * 获取所有缓存分类列表
     *
     * @return 缓存分类列表
     */
    @GetMapping("/names")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:cache:list')")
    public List<SysCacheCategoryDTO> listCategories() {
        return sysCacheService.listCategories();
    }

    /**
     * 根据缓存分类前缀获取 Key 列表
     *
     * @param keyPrefix 缓存分类前缀模式，如 "dict:*"
     * @return Key 详情列表
     */
    @GetMapping("/keys")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:cache:list')")
    public List<SysCacheKeyDetailDTO> listKeys(@RequestParam("keyPrefix") String keyPrefix) {
        return sysCacheService.listKeys(keyPrefix);
    }

    /**
     * 获取单个 Key 的详细信息
     *
     * @param key 完整 Key 名称
     * @return Key 详情（类型 + Value + TTL）
     */
    @GetMapping("/value")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:cache:list')")
    public SysCacheKeyDetailDTO getKeyDetail(@RequestParam("key") String key) {
        return sysCacheService.getKeyDetail(key);
    }

    /**
     * 清空指定分类下的所有缓存
     *
     * @param keyPrefix 缓存分类前缀模式
     */
    @DeleteMapping("/clearName")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:cache:clear')")
    @SxwlLog(title = "缓存管理", description = "清空缓存分类[prefix=#{#keyPrefix}]")
    public void clearByName(@RequestParam("keyPrefix") String keyPrefix) {
        sysCacheService.clearByName(keyPrefix);
    }

    /**
     * 删除单个缓存 Key
     *
     * @param key 完整 Key 名称
     */
    @DeleteMapping("/clearKey")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:cache:clear')")
    @SxwlLog(title = "缓存管理", description = "清除缓存 Key[#{#key}]")
    public void clearByKey(@RequestParam("key") String key) {
        sysCacheService.clearByKey(key);
    }
}
