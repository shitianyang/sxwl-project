package com.sxwl.system.model.dto;

/**
 * 仪表盘统计数据 VO
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysDashboardVO {

    /** 用户总数 */
    private long userCount;
    /** 角色总数 */
    private long roleCount;
    /** 菜单总数 */
    private long menuCount;
    /** 今日日志数 */
    private long todayLogCount;

    public long getUserCount() { return userCount; }
    public void setUserCount(long userCount) { this.userCount = userCount; }
    public long getRoleCount() { return roleCount; }
    public void setRoleCount(long roleCount) { this.roleCount = roleCount; }
    public long getMenuCount() { return menuCount; }
    public void setMenuCount(long menuCount) { this.menuCount = menuCount; }
    public long getTodayLogCount() { return todayLogCount; }
    public void setTodayLogCount(long todayLogCount) { this.todayLogCount = todayLogCount; }
}
