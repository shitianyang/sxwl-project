package com.sxwl.system.service.impl;

import com.sxwl.system.mapper.SysDashboardMapper;
import com.sxwl.system.model.dto.SysDashboardVO;
import com.sxwl.system.service.SysDashboardService;
import org.springframework.stereotype.Service;

/**
 * 仪表盘 Service 实现
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Service
public class SysDashboardServiceImpl implements SysDashboardService {

    private final SysDashboardMapper dashboardMapper;

    public SysDashboardServiceImpl(SysDashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public SysDashboardVO getStatistics() {
        SysDashboardVO vo = new SysDashboardVO();
        vo.setUserCount(dashboardMapper.countUsers());
        vo.setRoleCount(dashboardMapper.countRoles());
        vo.setMenuCount(dashboardMapper.countMenus());
        vo.setTodayLogCount(dashboardMapper.countTodayLogs());
        return vo;
    }
}
