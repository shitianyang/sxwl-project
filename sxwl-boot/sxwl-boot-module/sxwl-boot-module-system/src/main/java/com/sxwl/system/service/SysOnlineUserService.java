package com.sxwl.system.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysOnlineUserDTO;

/**
 * 在线用户 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface SysOnlineUserService {

    /**
     * 分页查询在线用户列表
     * <p>数据完全来自 Redis，通过 SMEMBERS + HGETALL 实时组装。</p>
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页在线用户列表
     */
    PageInfo<SysOnlineUserDTO> list(int pageNum, int pageSize);

    /**
     * 在线用户总数
     *
     * @return 在线人数（SCARD online:users）
     */
    long count();

    /**
     * 强制踢人下线
     * <p>清除该用户所有设备的在线信息 + Token 白名单。</p>
     *
     * @param userId 用户 ID
     */
    void forceLogout(Long userId);
}
