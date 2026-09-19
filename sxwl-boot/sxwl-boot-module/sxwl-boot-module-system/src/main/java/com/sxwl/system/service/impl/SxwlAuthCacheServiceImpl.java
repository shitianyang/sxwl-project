package com.sxwl.system.service.impl;

import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.system.mapper.SysRoleMapper;
import com.sxwl.system.service.SxwlAuthCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录权限缓存失效 Service 实现
 *
 * @author shitianyang
 * @date 2026/9/19
 * @since 0.1.0
 */
@Service
public class SxwlAuthCacheServiceImpl implements SxwlAuthCacheService {

    private static final Logger log = LoggerFactory.getLogger(SxwlAuthCacheServiceImpl.class);

    /** Redis 操作助手 */
    private final SxwlRedisHelper redisHelper;

    /** SysRole Mapper（查询角色关联用户） */
    private final SysRoleMapper sysRoleMapper;

    public SxwlAuthCacheServiceImpl(SxwlRedisHelper redisHelper, SysRoleMapper sysRoleMapper) {
        this.redisHelper = redisHelper;
        this.sysRoleMapper = sysRoleMapper;
    }

    /**
     * 清除指定用户的登录权限快照（token:info，admin + front 双端）
     *
     * @param userId 用户 ID
     */
    @Override
    public void evictUserAuthCache(Long userId) {
        if (userId == null) {
            return;
        }
        redisHelper.delete(SxwlRedisKeyUtils.tokenInfoKey("admin", userId));
        redisHelper.delete(SxwlRedisKeyUtils.tokenInfoKey("front", userId));
        log.debug("已清除用户登录权限快照: userId={}", userId);
    }

    /**
     * 清除拥有指定角色的所有用户的登录权限快照
     *
     * @param roleId 角色 ID
     */
    @Override
    public void evictUsersAuthCacheByRoleId(Long roleId) {
        if (roleId == null) {
            return;
        }
        List<Long> userIds = sysRoleMapper.selectUserIdsByRoleId(roleId);
        for (Long userId : userIds) {
            evictUserAuthCache(userId);
        }
        log.debug("已按角色清除用户登录权限快照: roleId={}, userCount={}", roleId, userIds.size());
    }
}
