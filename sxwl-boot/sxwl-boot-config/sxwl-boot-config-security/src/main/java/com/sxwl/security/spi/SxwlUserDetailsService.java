package com.sxwl.security.spi;

import com.sxwl.security.model.SxwlAuthCredential;

/**
 * 用户认证详情 SPI 接口
 * <p>
 * 由 module-system 实现（SysUserDetailsServiceImpl），负责根据用户名从数据库加载认证凭据。
 * security 模块不碰数据库，通过本接口获取用户数据。
 * </p>
 *
 * <h3>调用链</h3>
 * <pre>
 * SxwlPasswordAuthStrategy（auth 模块）
 *     └── 调用 .loadUserByUsername(username)
 *     └── 拿到 SxwlAuthCredential → 比对密码
 *
 * SysUserDetailsServiceImpl（module-system 模块）
 *     └── 实现本接口
 *     └── SELECT * FROM sys_user_info WHERE username = ?
 * </pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public interface SxwlUserDetailsService {

    /**
     * 根据用户名加载认证凭据
     *
     * @param username 登录用户名
     * @return 认证凭据（含脱敏用户信息 + 编码后密码）
     * @throws com.sxwl.common.exception.SxwlBusinessException 用户不存在或已禁用时抛出
     */
    SxwlAuthCredential loadUserByUsername(String username);
}
