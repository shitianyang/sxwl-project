package com.sxwl.auth.service;

import com.sxwl.auth.mapper.SysAuthUserMapper;
import com.sxwl.common.constants.SxwlSystemConstants;
import com.sxwl.security.model.SxwlLoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 登录用户加载器
 * <p>
 * 统一负责 {@link SxwlLoginUser} 权限快照的构建与计算：
 * <ol>
 *   <li>各登录策略认证成功后调用 {@link #applyAuthorization} 填充 roles/perms/dataScope</li>
 *   <li>Token 刷新时若用户权限缓存已被主动失效（如管理员调整了组织/角色/数据权限），
 *       通过 {@link #loadByUserId} 从数据库重建最新快照，用户无需重新登录</li>
 * </ol>
 *
 * @author shitianyang
 * @date 2026/9/19
 * @since 0.1.0
 */
@Component
public class SxwlLoginUserLoader {

    private static final Logger log = LoggerFactory.getLogger(SxwlLoginUserLoader.class);

    /** 认证专用 Mapper（仅查 sys_user_info 及角色/权限关联表） */
    private final SysAuthUserMapper sysAuthUserMapper;

    public SxwlLoginUserLoader(SysAuthUserMapper sysAuthUserMapper) {
        this.sysAuthUserMapper = sysAuthUserMapper;
    }

    /**
     * 根据用户 ID 从数据库重建登录用户快照（含权限、数据范围）
     * <p>供 /auth/refresh 在用户信息缓存缺失时调用，实现权限变更后的无感刷新。</p>
     *
     * @param userId 用户 ID
     * @return 登录用户对象；用户不存在、已删除或被禁用时返回 null
     */
    public SxwlLoginUser loadByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        Map<String, Object> userRow = sysAuthUserMapper.selectById(userId);
        if (userRow == null || userRow.isEmpty()) {
            log.warn("重建登录用户失败，用户不存在或已删除: userId={}", userId);
            return null;
        }
        Number statusNum = (Number) userRow.get("status");
        Integer status = statusNum != null ? statusNum.intValue() : null;
        if (status == null || status == 0) {
            log.warn("重建登录用户失败，账号已被禁用: userId={}", userId);
            return null;
        }

        SxwlLoginUser loginUser = new SxwlLoginUser();
        loginUser.setUserId(userId);
        loginUser.setUsername((String) userRow.get("username"));
        String nickname = (String) userRow.get("nickname");
        loginUser.setNickname(nickname != null ? nickname : loginUser.getUsername());
        loginUser.setStatus(status);
        loginUser.setCreateOrg(userRow.get("create_org") != null
                ? ((Number) userRow.get("create_org")).longValue() : null);
        applyAuthorization(loginUser);
        return loginUser;
    }

    /**
     * 填充用户授权信息（roles、perms、dataScope、dataScopeOrgIds）
     * <p>
     * 数据范围语义（与 sys_role_info.data_scope 列注释、前端角色管理一致）：
     * 1=全部 2=本组织 3=本组织及下级 4=仅本人 5=自定义。
     * 多角色时取最强范围（数值最小），仅超级管理员账号的 admin 角色可生效"全部数据"。
     * </p>
     *
     * @param loginUser 登录用户对象（需已设置 userId/username/createOrg）
     */
    public void applyAuthorization(SxwlLoginUser loginUser) {
        List<Map<String, Object>> roleRows = sysAuthUserMapper.selectRolesByUserId(loginUser.getUserId());

        Set<String> roles = new HashSet<>();
        Set<Long> scopedOrgIds = new HashSet<>();
        boolean allData = false;
        Integer strongestScope = null;

        for (Map<String, Object> row : roleRows) {
            String roleCode = (String) row.get("role_code");
            if (roleCode != null && !roleCode.isBlank()) {
                roles.add(roleCode);
            }

            Long roleId = toLong(row.get("id"));
            Integer dataScope = toInteger(row.get("data_scope"));
            if (dataScope == null) {
                continue;
            }
            boolean isProtectedSuperAdmin = SxwlSystemConstants.ADMIN_USERNAME.equals(loginUser.getUsername())
                    && SxwlSystemConstants.ADMIN_ROLE_CODE.equals(roleCode);
            if (dataScope == 1 && !isProtectedSuperAdmin) {
                continue;
            }
            strongestScope = strongestScope == null ? dataScope : Math.min(strongestScope, dataScope);

            if (dataScope == 1) {
                allData = true;
                break;
            }
            if (dataScope == 2) {
                addIfNotNull(scopedOrgIds, loginUser.getCreateOrg());
            } else if (dataScope == 3) {
                addIfNotNull(scopedOrgIds, loginUser.getCreateOrg());
                if (loginUser.getCreateOrg() != null) {
                    scopedOrgIds.addAll(sysAuthUserMapper.selectSelfAndChildOrgIds(loginUser.getCreateOrg()));
                }
            } else if (dataScope == 4) {
                loginUser.setDataScopeSelf(true);
            } else if (dataScope == 5 && roleId != null) {
                scopedOrgIds.addAll(sysAuthUserMapper.selectCustomDataScopeOrgIds(roleId));
            }
        }

        Set<String> perms = new HashSet<>(sysAuthUserMapper.selectPermissionsByUserId(loginUser.getUserId()));
        if (allData) {
            perms.add("*:*:*");
        }

        loginUser.setRoles(roles);
        loginUser.setPerms(perms);
        loginUser.setDataScope(allData ? 1 : (strongestScope != null ? strongestScope : 0));
        loginUser.setDataScopeOrgIds(allData ? null : scopedOrgIds);
    }

    private void addIfNotNull(Set<Long> values, Long value) {
        if (value != null) {
            values.add(value);
        }
    }

    private Long toLong(Object value) {
        return value instanceof Number ? ((Number) value).longValue() : null;
    }

    private Integer toInteger(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : null;
    }
}

