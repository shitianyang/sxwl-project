package com.sxwl.job.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.job.mapper.SysJobInfoMapper;
import com.sxwl.job.model.dto.SysJobDTO;
import com.sxwl.job.model.entity.SysJobInfo;
import com.sxwl.job.model.params.SysJobPageParams;
import com.sxwl.job.service.SysJobInfoService;
import com.sxwl.quartz.manager.SysJobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Service
public class SysJobInfoServiceImpl implements SysJobInfoService {

    private static final Logger log = LoggerFactory.getLogger(SysJobInfoServiceImpl.class);

    private final SysJobInfoMapper sysJobInfoMapper;
    private final SysJobManager sysJobManager;

    public SysJobInfoServiceImpl(SysJobInfoMapper sysJobInfoMapper,
                                 SysJobManager sysJobManager) {
        this.sysJobInfoMapper = sysJobInfoMapper;
        this.sysJobManager = sysJobManager;
    }

    @Override
    public SysJobDTO getJobById(Long id) {
        SysJobDTO dto = sysJobInfoMapper.getJobById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "定时任务不存在或已被删除");
        }
        return dto;
    }

    @Override
    public PageInfo<SysJobDTO> getJobPageByParams(SysJobPageParams params) {
        List<SysJobDTO> rows = sysJobInfoMapper.getJobPageByParams(params);
        return new PageInfo<>(rows);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createJob(SysJobDTO dto) {
        // 唯一性校验
        if (sysJobInfoMapper.checkJobUnique(dto.getJobName(), dto.getJobGroup(), null) > 0) {
            throw new SxwlBusinessException(10002, "任务名称+分组已存在");
        }

        SysJobInfo entity = toEntity(dto);
        int result = sysJobInfoMapper.insertJob(entity);
        if (result != 1) {
            log.error("新增定时任务失败: jobName={}, result={}", dto.getJobName(), result);
            throw new SxwlBusinessException(10001, "新增定时任务失败");
        }

        // 如果状态为正常，同步创建到 Quartz
        if (dto.getStatus() != null && dto.getStatus() == 1) {
            try {
                sysJobManager.createJob(dto.getJobName(), dto.getJobGroup(),
                        dto.getClassName(), dto.getMethodName(),
                        dto.getCronExpression(), dto.getMethodParams());
            } catch (SchedulerException e) {
                log.error("新增定时任务后同步 Quartz 失败: jobName={}", dto.getJobName(), e);
            }
        }

        log.info("新增定时任务成功: jobName={}", dto.getJobName());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(SysJobDTO dto) {
        // 唯一性校验（排除自身）
        if (sysJobInfoMapper.checkJobUnique(dto.getJobName(), dto.getJobGroup(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "任务名称+分组已存在");
        }

        SysJobInfo entity = toEntity(dto);
        entity.setId(dto.getId());

        int result = sysJobInfoMapper.updateJob(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "定时任务不存在或已被删除");
        }

        // 同步 Quartz：先删除旧任务，再根据新状态决定是否创建
        try {
            sysJobManager.deleteJob(dto.getJobName(), dto.getJobGroup());
            if (dto.getStatus() != null && dto.getStatus() == 1) {
                sysJobManager.createJob(dto.getJobName(), dto.getJobGroup(),
                        dto.getClassName(), dto.getMethodName(),
                        dto.getCronExpression(), dto.getMethodParams());
            }
        } catch (SchedulerException e) {
            log.error("修改定时任务后同步 Quartz 失败: id={}", dto.getId(), e);
        }

        log.info("修改定时任务成功: id={}", dto.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJobById(Long id) {
        SysJobDTO dto = getJobById(id);

        int affected = sysJobInfoMapper.deleteJobById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "定时任务不存在或已被删除");
        }

        // 同步删除 Quartz 任务
        try {
            sysJobManager.deleteJob(dto.getJobName(), dto.getJobGroup());
        } catch (SchedulerException e) {
            log.error("删除定时任务后同步 Quartz 失败: id={}", id, e);
        }

        log.info("删除定时任务成功: id={}", id);
        return affected;
    }

    @Override
    public void pauseJob(Long id) {
        SysJobDTO dto = getJobById(id);
        try {
            sysJobManager.pauseJob(dto.getJobName(), dto.getJobGroup());
        } catch (SchedulerException e) {
            log.error("暂停定时任务失败: id={}", id, e);
            throw new SxwlBusinessException(10001, "暂停定时任务失败");
        }
        log.info("暂停定时任务成功: id={}", id);
    }

    @Override
    public void resumeJob(Long id) {
        SysJobDTO dto = getJobById(id);
        try {
            sysJobManager.resumeJob(dto.getJobName(), dto.getJobGroup());
        } catch (SchedulerException e) {
            log.error("恢复定时任务失败: id={}", id, e);
            throw new SxwlBusinessException(10001, "恢复定时任务失败");
        }
        log.info("恢复定时任务成功: id={}", id);
    }

    @Override
    public void runOnce(Long id) {
        SysJobDTO dto = getJobById(id);
        try {
            sysJobManager.runOnce(dto.getJobName(), dto.getJobGroup());
        } catch (SchedulerException e) {
            log.error("立即执行定时任务失败: id={}", id, e);
            throw new SxwlBusinessException(10001, "立即执行定时任务失败");
        }
        log.info("立即执行定时任务成功: id={}", id);
    }

    @Override
    public void syncActiveJobsToQuartz() {
        List<SysJobDTO> activeJobs = sysJobInfoMapper.getAllActiveJobs();
        int success = 0;
        for (SysJobDTO job : activeJobs) {
            try {
                sysJobManager.createJob(job.getJobName(), job.getJobGroup(),
                        job.getClassName(), job.getMethodName(),
                        job.getCronExpression(), job.getMethodParams());
                success++;
            } catch (SchedulerException e) {
                log.warn("同步任务到 Quartz 失败: jobName={}, error={}", job.getJobName(), e.getMessage());
            }
        }
        log.info("启动时同步定时任务完成: 共 {} 个, 成功 {} 个", activeJobs.size(), success);
    }

    private SysJobInfo toEntity(SysJobDTO dto) {
        SysJobInfo entity = new SysJobInfo();
        entity.setJobName(dto.getJobName());
        entity.setJobGroup(dto.getJobGroup());
        entity.setClassName(dto.getClassName());
        entity.setMethodName(dto.getMethodName());
        entity.setMethodParams(dto.getMethodParams());
        entity.setCronExpression(dto.getCronExpression());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
