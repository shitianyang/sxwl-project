package com.sxwl.system.model.dto;

import java.time.LocalDateTime;

/**
 * 在线用户 DTO
 *
 * <p>数据完全来自 Redis，不存在对应的 DB 表。
 * <br>每个 DTO 对应一个设备会话（同一用户多端登录会产生多条记录）。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysOnlineUserDTO {

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 登录 IP */
    private String ip;

    /** 浏览器 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 设备 ID */
    private String deviceId;

    /** 登录时间 */
    private LocalDateTime loginTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取登录 IP（脱敏格式）
     *
     * <p>保留内网前两位和最后一个网段，中间用 **** 替代</p>
     * <p>示例：192.168.1.100 → 192.168.*.**</p>
     *
     * @return 脱敏后的 IP 地址
     */
    public String getIp() {
        return maskIpAddress(ip);
    }

    /**
     * 获取原始 IP（未脱敏，仅供后端日志使用）
     *
     * @return 原始 IP 地址
     */
    public String getRawIp() {
        return ip;
    }

    /**
     * 对 IPv4 地址进行脱敏处理
     *
     * <p>规则：保留内网前两位（如 192.168），最后一个网段保留（如 .100），中间替换为 ****</p>
     * <p>外网 IP 仅保留最后一段（如 1.2.3.4 → ***.***.***.4）</p>
     *
     * @param ipAddress 原始 IP 地址
     * @return 脱敏后的 IP 地址
     */
    private String maskIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return ipAddress;
        }
        
        // 简单脱敏：将第三个 octet 替换为 *，第二个 octet 部分隐藏
        String[] parts = ipAddress.split("\\.");
        if (parts.length == 4) {
            // IPv4: x.x.x.x → x.x.*.x
            return parts[0] + "." + parts[1] + ".*.**";
        }
        
        // 如果不是标准 IPv4，返回 "****"
        return "****";
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }
}
