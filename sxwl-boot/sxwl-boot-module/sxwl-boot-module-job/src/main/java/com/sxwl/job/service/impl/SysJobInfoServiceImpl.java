package com.sxwl.job.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
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
import jakarta.annotation.PostConstruct;

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

        // 如果状态为正常，同步创建到 Quartz。
        // 调度同步失败必须抛出，使事务回滚（DB 不落库），避免 DB status=1 但 Quartz 无任务（假启用）。
        if (dto.getStatus() != null && dto.getStatus() == 1) {
            try {
                sysJobManager.createJob(dto.getJobName(), dto.getJobGroup(),
                        dto.getClassName(), dto.getMethodName(),
                        dto.getCronExpression(), dto.getMethodParams());
            } catch (SchedulerException e) {
                log.error("新增定时任务后同步 Quartz 失败: jobName={}", dto.getJobName(), e);
                throw new SxwlBusinessException(10001, "创建定时任务失败（调度器异常）: " + dto.getJobName());
            }
        }

        log.info("新增定时任务成功: jobName={}", dto.getJobName());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(SysJobDTO dto) {
        if (dto.getId() == null) {
            throw new SxwlBusinessException(10001, "定时任务 ID 不能为空");
        }
        // 唯一性校验（排除自身）
        if (sysJobInfoMapper.checkJobUnique(dto.getJobName(), dto.getJobGroup(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "任务名称+分组已存在");
        }

        // 查询旧数据并计算字段级变更差异
        SysJobDTO oldDto = sysJobInfoMapper.getJobById(dto.getId());
        if (oldDto != null) {
            SysJobInfo oldEntity = toEntity(oldDto);
            SysJobInfo newEntity = toEntity(dto);
            newEntity.setId(dto.getId());
            String diffJson = SxwlDiffUtils.diff(oldEntity, newEntity);
            if (diffJson != null) {
                SxwlDiffUtils.setContextDiff(diffJson);
            }
        }

        SysJobInfo entity = toEntity(dto);
        entity.setId(dto.getId());

        int result = sysJobInfoMapper.updateJob(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "定时任务不存在或已被删除");
        }

        // 同步 Quartz：先删除旧任务，再根据新状态决定是否创建。
        // 任何调度异常都抛出，使 @Transactional 回滚本次更新，保证 DB 与 Quartz 一致（不出现假启用/假停用）。
        try {
            sysJobManager.deleteJob(dto.getJobName(), dto.getJobGroup());
            if (dto.getStatus() != null && dto.getStatus() == 1) {
                sysJobManager.createJob(dto.getJobName(), dto.getJobGroup(),
                        dto.getClassName(), dto.getMethodName(),
                        dto.getCronExpression(), dto.getMethodParams());
            }
        } catch (SchedulerException e) {
            log.error("修改定时任务后同步 Quartz 失败: id={}", dto.getId(), e);
            throw new SxwlBusinessException(10001, "修改定时任务失败（调度器异常）: id=" + dto.getId());
        }

        log.info("修改定时任务成功: id={}", dto.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJobById(Long id) {
        SysJobDTO dto = getJobById(id);

        // 先删除 Quartz 任务：调度异常直接抛出，DB 记录保留，保证 DB 与 Quartz 一致（不会 DB 已删但任务仍在跑）。
        try {
            sysJobManager.deleteJob(dto.getJobName(), dto.getJobGroup());
        } catch (SchedulerException e) {
            log.error("删除定时任务时 Quartz 同步失败: id={}", id, e);
            throw new SxwlBusinessException(10001, "删除定时任务失败（调度器异常）: id=" + id);
        }

        int affected = sysJobInfoMapper.deleteJobById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "定时任务不存在或已被删除");
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

    /**
     * 应用启动后，将数据库中的激活任务同步到 Quartz Scheduler
     *
     * <p>使用 @PostConstruct 确保 Spring 容器完全初始化后再执行同步逻辑</p>
     * <p>采用幂等设计：重复调用不会产生副作用（Quartz.checkExists 会跳过已存在任务）</p>
     */
    @PostConstruct
    public void syncActiveJobsToQuartz() {
        List<SysJobDTO> activeJobs = sysJobInfoMapper.getAllActiveJobs();
        int success = 0;
        List<String> failures = new java.util.ArrayList<>();
        for (SysJobDTO job : activeJobs) {
            try {
                sysJobManager.createJob(job.getJobName(), job.getJobGroup(),
                        job.getClassName(), job.getMethodName(),
                        job.getCronExpression(), job.getMethodParams());
                success++;
            } catch (SchedulerException e) {
                failures.add(job.getJobName());
                log.error("同步任务到 Quartz 失败: jobName={}, error={}", job.getJobName(), e.getMessage());
            }
        }
        if (!failures.isEmpty()) {
            log.error("启动时同步定时任务存在失败: 共 {} 个, 成功 {} 个, 失败 {} 个: {}",
                    activeJobs.size(), success, failures.size(), failures);
        } else {
            log.info("启动时同步定时任务完成: 共 {} 个, 成功 {} 个", activeJobs.size(), success);
        }
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
