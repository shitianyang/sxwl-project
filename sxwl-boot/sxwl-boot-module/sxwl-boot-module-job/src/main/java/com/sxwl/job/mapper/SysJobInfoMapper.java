package com.sxwl.job.mapper;

import com.sxwl.job.model.dto.SysJobDTO;
import com.sxwl.job.model.entity.SysJobInfo;
import com.sxwl.job.model.params.SysJobPageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务 Mapper
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Mapper
public interface SysJobInfoMapper {

    SysJobDTO getJobById(@Param("id") Long id);

    List<SysJobDTO> getJobPageByParams(SysJobPageParams params);

    int insertJob(SysJobInfo entity);

    int updateJob(SysJobInfo entity);

    int deleteJobById(@Param("id") Long id);

    /**
     * 校验任务名称+分组是否唯一（排除指定 ID）
     */
    int checkJobUnique(@Param("jobName") String jobName,
                       @Param("jobGroup") String jobGroup,
                       @Param("excludeId") Long excludeId);

    /**
     * 获取所有正常状态的任务（用于启动时同步到 Quartz）
     */
    List<SysJobDTO> getAllActiveJobs();
}
