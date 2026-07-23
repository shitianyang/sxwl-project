package com.sxwl.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 仪表盘统计 Mapper
 *
 * <p>提供首页仪表盘所需的统计数据查询。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Mapper
public interface SysDashboardMapper {

    /** 查询用户总数 */
    @Select("SELECT COUNT(*) FROM sys_user_info WHERE delete_flag = 0")
    long countUsers();

    /** 查询角色总数 */
    @Select("SELECT COUNT(*) FROM sys_role_info WHERE delete_flag = 0")
    long countRoles();

    /** 查询菜单总数 */
    @Select("SELECT COUNT(*) FROM sys_menu_info WHERE delete_flag = 0")
    long countMenus();

    /** 查询今日日志数 */
    @Select("SELECT COUNT(*) FROM sys_log_info WHERE delete_flag = 0 AND create_time >= CURRENT_DATE")
    long countTodayLogs();
}
