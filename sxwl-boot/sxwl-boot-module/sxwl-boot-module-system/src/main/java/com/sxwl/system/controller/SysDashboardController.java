package com.sxwl.system.controller;

import com.sxwl.common.entity.SxwlResult;
import com.sxwl.system.model.dto.SysDashboardVO;
import com.sxwl.system.service.SysDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页仪表盘 Controller
 *
 * <p>提供仪表盘统计数据接口。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/dashboard")
public class SysDashboardController {

    private final SysDashboardService dashboardService;

    public SysDashboardController(SysDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 获取首页统计数据
     *
     * @return 统计 VO（用户总数、角色总数、菜单总数、今日日志数）
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dashboard:query')")
    public SxwlResult<SysDashboardVO> getStatistics() {
        SysDashboardVO vo = dashboardService.getStatistics();
        return SxwlResult.success(vo);
    }
}
