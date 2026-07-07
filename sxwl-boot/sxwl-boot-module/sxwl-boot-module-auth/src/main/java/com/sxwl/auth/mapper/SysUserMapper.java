package com.sxwl.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * 认证专用 Mapper（仅查 sys_user_info，不做 CRUD）
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@Mapper
public interface SysUserMapper {

    /**
     * 根据用户名查询用户（仅查认证必需字段）
     */
    @Select("SELECT id, username, password, nickname, status, create_org " +
            "FROM sys_user_info WHERE username = #{username} AND delete_flag = 0")
    Map<String, Object> selectByUsername(String username);
}
