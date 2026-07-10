package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.system.model.dto.SysUserDTO;
import com.sxwl.system.model.params.SysUserPageParams;
import com.sxwl.system.service.SysUserService;
import jakarta.validation.Valid;
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
    @GetMapping("/getUserById")
    @SxwlLog(title = "用户管理", description = "查询用户详情[id=#{#id}]")
    public SysUserDTO getUserById(@RequestParam("id") Long id) {
        return sysUserService.getUserById(id);
    }

    /**
     * 分页查询用户列表
     *
     * @param params 分页查询参数（用户名模糊匹配、状态筛选、页码、每页条数）
     * @return 分页用户列表
     */
    @GetMapping("/getUserPageByParams")
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
    @PostMapping("/createUser")
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
    @PutMapping("/updateUser")
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
    @DeleteMapping("/deleteUserById")
    @SxwlLog(title = "用户管理", description = "删除用户[id=#{#id}]")
    public void deleteUserById(@RequestParam("id") Long id) {
        sysUserService.deleteUserById(id);
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户 ID 列表
     * @return 无数据成功响应
     */
    @DeleteMapping("/batchDeleteByIds")
    @SxwlLog(title = "用户管理", description = "批量删除用户[ids=#{#ids}]")
    public void batchDeleteByIds(@RequestBody List<Long> ids) {
        sysUserService.batchDeleteByIds(ids);
    }
}
