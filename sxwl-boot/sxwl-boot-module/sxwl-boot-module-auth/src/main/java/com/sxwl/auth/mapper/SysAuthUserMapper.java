package com.sxwl.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 认证专用 Mapper（仅查 sys_user_info，不做 CRUD）
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@Mapper
public interface SysAuthUserMapper {

    /**
     * 根据用户名查询用户（仅查认证必需字段）
     */
    @Select("SELECT id, username, password, nickname, status, create_org " +
            "FROM sys_user_info WHERE username = #{username} AND delete_flag = 0")
    Map<String, Object> selectByUsername(String username);

    @Select("""
            SELECT r.id, r.role_code, r.data_scope
            FROM sys_role_info r
            INNER JOIN sys_user_role_info ur ON ur.role_id = r.id AND ur.delete_flag = 0
            WHERE ur.user_id = #{userId}
              AND r.status = 1
              AND r.delete_flag = 0
            """)
    List<Map<String, Object>> selectRolesByUserId(Long userId);

    @Select("""
            SELECT DISTINCT m.perms
            FROM sys_menu_info m
            INNER JOIN sys_role_menu_info rm ON rm.menu_id = m.id AND rm.delete_flag = 0
            INNER JOIN sys_user_role_info ur ON ur.role_id = rm.role_id AND ur.delete_flag = 0
            INNER JOIN sys_role_info r ON r.id = ur.role_id AND r.status = 1 AND r.delete_flag = 0
            WHERE ur.user_id = #{userId}
              AND m.status = 1
              AND m.delete_flag = 0
              AND m.perms IS NOT NULL
              AND m.perms <> ''
            """)
    List<String> selectPermissionsByUserId(Long userId);

    @Select("""
            SELECT id
            FROM sys_organization_info
            WHERE delete_flag = 0
              AND (id = #{orgId} OR CONCAT(',', ancestors, ',') LIKE CONCAT('%,', #{orgId}, ',%'))
            """)
    List<Long> selectSelfAndChildOrgIds(Long orgId);

    @Select("""
            SELECT DISTINCT ds.org_id
            FROM sys_role_data_scope_info ds
            WHERE ds.role_id = #{roleId}
              AND ds.delete_flag = 0
            """)
    List<Long> selectCustomDataScopeOrgIds(Long roleId);
}
