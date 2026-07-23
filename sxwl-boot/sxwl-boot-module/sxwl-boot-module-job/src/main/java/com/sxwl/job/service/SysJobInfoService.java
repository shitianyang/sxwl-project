package com.sxwl.job.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.job.model.dto.SysJobDTO;
import com.sxwl.job.model.params.SysJobPageParams;

/**
 * 定时任务 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface SysJobInfoService {

    SysJobDTO getJobById(Long id);

    PageInfo<SysJobDTO> getJobPageByParams(SysJobPageParams params);

    int createJob(SysJobDTO dto);

    int updateJob(SysJobDTO dto);

    int deleteJobById(Long id);

    /**
     * 暂停任务
     */
    void pauseJob(Long id);

    /**
     * 恢复任务
     */
    void resumeJob(Long id);

    /**
     * 立即执行一次
     */
    void runOnce(Long id);

    /**
     * 启动时同步所有正常任务到 Quartz
     */
    void syncActiveJobsToQuartz();
}
