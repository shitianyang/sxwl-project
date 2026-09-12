package com.sxwl.quartz.event;

import org.springframework.context.ApplicationEvent;

/**
 * 定时任务执行事件
 *
 * <p>由 {@code QuartzJobDelegate} 在任务执行结束后发布，携带执行结果（成功/失败、耗时、错误信息），
 * 供 module-job 的监听器异步落库到 {@code sys_job_log_info}。放在 config-quartz 是为了让
 * 发布方（delegate）与监听方（module-job，已依赖 config-quartz）都能引用，避免反向依赖。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class JobExecutionEvent extends ApplicationEvent {

    private final String className;
    private final String methodName;
    private final String params;
    private final boolean success;
    private final String errorMsg;
    private final long durationMs;

    public JobExecutionEvent(Object source, String className, String methodName,
                             String params, boolean success, String errorMsg, long durationMs) {
        super(source);
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.success = success;
        this.errorMsg = errorMsg;
        this.durationMs = durationMs;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getParams() {
        return params;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public long getDurationMs() {
        return durationMs;
    }
}
