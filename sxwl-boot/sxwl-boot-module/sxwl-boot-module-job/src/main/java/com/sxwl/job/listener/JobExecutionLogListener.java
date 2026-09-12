package com.sxwl.job.listener;

import com.sxwl.job.mapper.SysJobLogMapper;
import com.sxwl.job.model.entity.SysJobLogInfo;
import com.sxwl.quartz.event.JobExecutionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时任务执行日志监听器
 *
 * <p>监听 {@link JobExecutionEvent}，将任务执行结果（成功/失败、耗时、错误信息）落库到
 * {@code sys_job_log_info}，使"任务日志"页面有数据可查。位于 module-job 是为了能直接使用
 * SysJobLogMapper，且本模块已依赖 config-quartz（可引用 JobExecutionEvent）。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Component
public class JobExecutionLogListener {

    private static final Logger log = LoggerFactory.getLogger(JobExecutionLogListener.class);

    private final SysJobLogMapper sysJobLogMapper;

    public JobExecutionLogListener(SysJobLogMapper sysJobLogMapper) {
        this.sysJobLogMapper = sysJobLogMapper;
    }

    @EventListener(JobExecutionEvent.class)
    public void onJobExecution(JobExecutionEvent event) {
        try {
            SysJobLogInfo logInfo = new SysJobLogInfo();
            logInfo.setClassName(event.getClassName());
            logInfo.setMethodName(event.getMethodName());
            logInfo.setMethodParams(event.getParams());
            logInfo.setStatus(event.isSuccess() ? 1 : 0);
            logInfo.setErrorMsg(event.getErrorMsg());
            logInfo.setExecuteTime(event.getDurationMs());
            logInfo.setFireTime(LocalDateTime.now());
            sysJobLogMapper.insertLog(logInfo);
        } catch (Exception e) {
            // 落库失败不影响定时任务本身
            log.warn("写入定时任务执行日志失败: {}.{}", event.getClassName(), event.getMethodName(), e);
        }
    }
}
