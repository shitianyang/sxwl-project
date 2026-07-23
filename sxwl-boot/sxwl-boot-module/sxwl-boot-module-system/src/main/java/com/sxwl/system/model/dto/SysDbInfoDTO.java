package com.sxwl.system.model.dto;

/**
 * 数据库连接信息 DTO
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysDbInfoDTO {

    /** 数据库活跃连接数 */
    private int activeConnections;

    public int getActiveConnections() {
        return activeConnections;
    }

    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
    }
}
