package com.sxwl.job.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.job.model.dto.SysJobLogDTO;
import com.sxwl.job.model.params.SysJobLogPageParams;

/**
 * 定时任务日志 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface SysJobLogService {

    SysJobLogDTO getLogById(Long id);

    PageInfo<SysJobLogDTO> getLogPageByParams(SysJobLogPageParams params);

    int deleteLogById(Long id);

    /**
     * 清理指定天数前的日志
     */
    int cleanLogBefore(int days);
}
