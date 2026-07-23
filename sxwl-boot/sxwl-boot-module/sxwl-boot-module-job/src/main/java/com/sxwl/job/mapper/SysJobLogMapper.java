package com.sxwl.job.mapper;

import com.sxwl.job.model.dto.SysJobLogDTO;
import com.sxwl.job.model.entity.SysJobLogInfo;
import com.sxwl.job.model.params.SysJobLogPageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务日志 Mapper
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Mapper
public interface SysJobLogMapper {

    SysJobLogDTO getLogById(@Param("id") Long id);

    List<SysJobLogDTO> getLogPageByParams(SysJobLogPageParams params);

    int insertLog(SysJobLogInfo entity);

    int deleteLogById(@Param("id") Long id);

    /**
     * 清理指定天数前的日志（物理删除）
     */
    int cleanLogBefore(@Param("days") int days);
}
