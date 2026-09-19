package com.sxwl.system.service;

/**
 * 登录权限缓存失效 Service
 * <p>
 * 用户的权限/组织/数据范围在登录时以快照形式缓存于 Redis（token:info）。
 * 当管理员调整了用户的组织、角色或角色的权限配置时，调用本服务主动清除旧快照，
 * 使变更无需用户重新登录即可生效。
 * </p>
 *
 * @author shitianyang
 * @date 2026/9/19
 * @since 0.1.0
 */
public interface SxwlAuthCacheService {

    /**
     * 清除指定用户的登录权限快照（admin + front 双端）
     * <p>清除后用户下一次请求将 401，前端自动走 refreshToken 静默刷新，
     * /auth/refresh 发现快照缺失会从 DB 重建最新权限，用户无感知。</p>
     *
     * @param userId 用户 ID
     */
    void evictUserAuthCache(Long userId);

    /**
     * 清除拥有指定角色的所有用户的登录权限快照
     *
     * @param roleId 角色 ID
     */
    void evictUsersAuthCacheByRoleId(Long roleId);
}
