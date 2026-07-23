package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.system.model.dto.SysOnlineUserDTO;
import com.sxwl.system.service.SysOnlineUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在线用户 Controller
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
@RequestMapping("/sys/online-user")
public class SysOnlineUserController {

    private final SysOnlineUserService sysOnlineUserService;

    public SysOnlineUserController(SysOnlineUserService sysOnlineUserService) {
        this.sysOnlineUserService = sysOnlineUserService;
    }

    /**
     * 分页查询在线用户列表
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 在线用户分页列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:onlineuser:list')")
    @SxwlLog(title = "在线用户管理", description = "查询在线用户列表")
    public PageInfo<SysOnlineUserDTO> list(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return sysOnlineUserService.list(pageNum, pageSize);
    }

    /**
     * 在线用户总数
     *
     * @return 在线人数
     */
    @GetMapping("/count")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:onlineuser:list')")
    public long count() {
        return sysOnlineUserService.count();
    }

    /**
     * 强制踢人下线
     *
     * @param userId 用户 ID
     */
    @DeleteMapping("/forceLogout/{userId}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:onlineuser:forceLogout')")
    @SxwlLog(title = "在线用户管理", description = "强制踢人下线[userId=#{#userId}]")
    public void forceLogout(@PathVariable("userId") Long userId) {
        sysOnlineUserService.forceLogout(userId);
    }
}
