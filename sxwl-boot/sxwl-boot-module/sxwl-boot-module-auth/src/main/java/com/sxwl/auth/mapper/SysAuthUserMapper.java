package com.sxwl.auth.mapper;

import org.apache.ibatis.annotations.Mapper;

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
     * <p>
     * SQL 实现：resources/mappers/SysAuthUserMapper.xml#selectByUsername
     * </p>
     */
    Map<String, Object> selectByUsername(String username);

    /**
     * 查询用户角色列表（含数据权限）
     * <p>
     * SQL 实现：resources/mappers/SysAuthUserMapper.xml#selectRolesByUserId
     * </p>
     */
    List<Map<String, Object>> selectRolesByUserId(Long userId);

    /**
     * 查询用户权限编码集合（按钮级权限）
     * <p>
     * SQL 实现：resources/mappers/SysAuthUserMapper.xml#selectPermissionsByUserId
     * </p>
     */
    List<String> selectPermissionsByUserId(Long userId);

    /**
     * 查询组织和子组织 ID 列表（用于数据范围过滤）
     * <p>
     * SQL 实现：resources/mappers/SysAuthUserMapper.xml#selectSelfAndChildOrgIds
     * </p>
     */
    List<Long> selectSelfAndChildOrgIds(Long orgId);

    /**
     * 查询自定义数据权限关联的组织 ID 列表
     * <p>
     * SQL 实现：resources/mappers/SysAuthUserMapper.xml#selectCustomDataScopeOrgIds
     * </p>
     */
    List<Long> selectCustomDataScopeOrgIds(Long roleId);

    /**
     * 根据手机号查询用户（仅查认证必需字段）
     * <p>
     * SQL 实现：resources/mappers/SysAuthUserMapper.xml#findByPhone
     * </p>
     */
    Map<String, Object> findByPhone(String phone);
}
