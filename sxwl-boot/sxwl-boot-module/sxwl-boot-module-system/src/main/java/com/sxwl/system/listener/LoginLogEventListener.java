package com.sxwl.system.listener;

import com.sxwl.security.event.SxwlLoginSuccessEvent;
import com.sxwl.system.mapper.SysLogMapper;
import com.sxwl.system.model.entity.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 登录日志事件监听器
 *
 * <p>消费 {@link SxwlLoginSuccessEvent} 事件，将登录日志写入 {@code sys_log_info} 表（log_type=1）。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Component
public class LoginLogEventListener {

    private static final Logger log = LoggerFactory.getLogger(LoginLogEventListener.class);

    private final SysLogMapper sysLogMapper;

    public LoginLogEventListener(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    /**
     * 监听到登录成功事件后写入数据库
     *
     * @param event 登录成功事件
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleLoginSuccess(SxwlLoginSuccessEvent event) {
        try {
            SysLog entity = new SysLog();
            entity.setLogType(1);
            entity.setTitle("登录日志");
            entity.setDescription("用户登录成功");
            entity.setOperateIp(event.getIp());
            entity.setUserId(event.getUserId());
            entity.setUserName(event.getUsername());
            entity.setStatus(1);
            entity.setExecuteTime(0L);

            LocalDateTime now = LocalDateTime.now();
            entity.setCreateBy(event.getUserId());
            entity.setCreateOrg(0L);
            entity.setCreateTime(now);
            entity.setDeleteFlag(0);

            sysLogMapper.insertLog(entity);
            log.debug("登录日志写入成功: userId={}, username={}", event.getUserId(), event.getUsername());
        } catch (Exception e) {
            log.error("登录日志写入失败: userId={}, error={}", event.getUserId(), e.getMessage(), e);
        }
    }
}
