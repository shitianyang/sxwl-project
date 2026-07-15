package com.sxwl.system.service;

import com.sxwl.system.model.dto.SysDashboardVO;

/**
 * 仪表盘 Service 接口
 *
 * @author shitianyang
 * @since 0.1.0
 */
public interface SysDashboardService {

    /**
     * 获取首页统计数据
     *
     * @return 统计 VO（用户数、角色数、菜单数、今日日志数）
     */
    SysDashboardVO getStatistics();
}
