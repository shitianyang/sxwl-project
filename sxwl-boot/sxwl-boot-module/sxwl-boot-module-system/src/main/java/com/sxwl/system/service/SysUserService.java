package com.sxwl.system.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysUserDTO;
import com.sxwl.system.model.params.SysUserPageParams;

import java.util.List;

/**
 * 系统用户 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public interface SysUserService {

    /**
     * 根据 ID 查询用户（含密码，用于编辑回显）
     *
     * @param id 用户 ID
     * @return 用户 DTO（含密码字段），不存在返回 null
     */
    SysUserDTO getUserById(Long id);

    /**
     * 分页查询用户列表
     *
     * @param params 分页查询参数
     * @return 分页用户列表
     */
    PageInfo<SysUserDTO> getUserPageByParams(SysUserPageParams params);

    /**
     * 新增用户
     *
     * @param dto 用户信息
     * @return 影响行数
     */
    int createUser(SysUserDTO dto);

    /**
     * 修改用户
     *
     * @param dto 用户信息（含 id）
     * @return 影响行数
     */
    int updateUser(SysUserDTO dto);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户 ID
     * @return 影响行数
     */
    int deleteUserById(Long id);

    /**
     * 批量删除用户（逻辑删除）
     *
     * @param ids 用户 ID 列表
     * @return 影响行数
     */
    int batchDeleteByIds(List<Long> ids);
}
