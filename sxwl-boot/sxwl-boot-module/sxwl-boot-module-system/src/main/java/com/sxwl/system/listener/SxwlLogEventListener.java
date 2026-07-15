package com.sxwl.system.listener;

import com.sxwl.common.event.SxwlOperationLogEvent;
import com.sxwl.system.mapper.SysLogMapper;
import com.sxwl.system.model.entity.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 操作日志事件监听器
 *
 * <p>消费 {@link SxwlOperationLogEvent} 事件，将操作日志异步写入 {@code sys_log_info} 表。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Component
public class SxwlLogEventListener {

    private static final Logger log = LoggerFactory.getLogger(SxwlLogEventListener.class);

    private final SysLogMapper sysLogMapper;

    public SxwlLogEventListener(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    /**
     * 监听到操作日志事件后写入数据库
     *
     * @param event 操作日志事件
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleOperationLog(SxwlOperationLogEvent event) {
        try {
            SysLog entity = new SysLog();
            entity.setLogType(event.getLogType() > 0 ? event.getLogType() : 2);
            entity.setTitle(event.getTitle());
            entity.setDescription(event.getDescription());
            entity.setMethod(event.getMethod());
            entity.setRequestUrl(event.getRequestUrl());
            entity.setRequestMethod(event.getRequestMethod());
            entity.setRequestParam(truncate(event.getRequestParam(), 2000));
            entity.setResponseResult(truncate(event.getResponseResult(), 2000));
            entity.setOperateIp(event.getOperateIp());
            entity.setUserId(event.getUserId());
            entity.setUserName(event.getUserName());
            entity.setExecuteTime(event.getExecuteTime());
            entity.setErrorMsg(event.getErrorMsg());
            entity.setStatus(event.getStatus());
            entity.setTraceId(event.getTraceId());

            sysLogMapper.insertLog(entity);
            log.debug("操作日志写入成功: type={}, title={}", entity.getLogType(), entity.getTitle());
        } catch (Exception e) {
            log.error("操作日志写入失败: title={}, error={}", event.getTitle(), e.getMessage(), e);
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
