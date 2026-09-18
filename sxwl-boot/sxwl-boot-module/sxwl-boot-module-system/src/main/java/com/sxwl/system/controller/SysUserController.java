package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.constant.SxwlPermConstant;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.system.model.dto.SysUserDTO;
import com.sxwl.system.model.params.SysUserPageParams;
import com.sxwl.system.service.SysUserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户 Controller
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 根据 ID 查询用户（编辑回显，密码置空返回）
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.USER_QUERY + ")")
    @SxwlLog(title = "用户管理", description = "查询用户详情[id=#{#id}]")
    public SysUserDTO getUserById(@PathVariable("id") Long id) {
        return sysUserService.getUserById(id);
    }

    /**
     * 分页查询用户列表
     *
     * @param params 分页查询参数（用户名模糊匹配、状态筛选、页码、每页条数）
     * @return 分页用户列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.USER_LIST + ")")
    @SxwlLog(title = "用户管理", description = "查询用户列表")
    public PageInfo<SysUserDTO> getUserPageByParams(@Valid SysUserPageParams params) {
        return sysUserService.getUserPageByParams(params);
    }

    /**
     * 新增用户
     *
     * @param dto 用户信息（用户名、密码、姓名、手机号、邮箱、状态）
     * @return 无数据成功响应
     */
    @PostMapping
    @SxwlRepeatSubmit(interval = 3, message = "用户创建中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.USER_ADD + ")")
    @SxwlLog(title = "用户管理", description = "新增用户[#{#dto.username}]")
    public void createUser(@Valid @RequestBody SysUserDTO dto) {
        sysUserService.createUser(dto);
    }

    /**
     * 修改用户
     *
     * @param dto 用户信息（含 id，密码可选）
     * @return 无数据成功响应
     */
    @PutMapping
    @SxwlRepeatSubmit(interval = 3, message = "用户修改中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.USER_EDIT + ")")
    @SxwlLog(title = "用户管理", description = "修改用户[#{#dto.username}]")
    public void updateUser(@Valid @RequestBody SysUserDTO dto) {
        sysUserService.updateUser(dto);
    }

    /**
     * 删除用户
     *
     * @param id 用户 ID
     * @return 无数据成功响应
     */
    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit(interval = 3, message = "用户删除中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.USER_DELETE + ")")
    @SxwlLog(title = "用户管理", description = "删除用户[id=#{#id}]")
    public void deleteUserById(@PathVariable("id") Long id) {
        sysUserService.deleteUserById(id);
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户 ID 列表
     * @return 无数据成功响应
     */
    @DeleteMapping("/batch")
    @SxwlRepeatSubmit(interval = 60, message = "批量删除用户中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.USER_DELETE + ")")
    @SxwlLog(title = "用户管理", description = "批量删除用户[ids=#{#ids}]")
    public void batchDeleteByIds(@RequestBody List<Long> ids) {
        sysUserService.batchDeleteByIds(ids);
    }
}
