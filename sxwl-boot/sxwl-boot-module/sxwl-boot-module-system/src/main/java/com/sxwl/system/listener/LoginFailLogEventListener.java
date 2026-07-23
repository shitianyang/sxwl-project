package com.sxwl.system.listener;

import com.sxwl.security.event.SxwlLoginFailureEvent;
import com.sxwl.system.mapper.SysLogMapper;
import com.sxwl.system.model.entity.SysLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

/**
 * 登录失败日志事件监听器
 *
 * <p>消费 {@link SxwlLoginFailureEvent} 事件，将登录失败记录写入 {@code sys_log_info} 表（log_type=1, status=0）。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Component
public class LoginFailLogEventListener {

    private static final Logger log = LoggerFactory.getLogger(LoginFailLogEventListener.class);

    private final SysLogMapper sysLogMapper;

    public LoginFailLogEventListener(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    /**
     * 监听到登录失败事件后写入数据库
     *
     * @param event 登录失败事件
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleLoginFailure(SxwlLoginFailureEvent event) {
        try {
            SysLog entity = new SysLog();
            entity.setLogType(1);
            entity.setTitle("登录日志");
            entity.setDescription("用户登录失败");
            entity.setOperateIp(event.getIp());
            entity.setUserName(event.getTargetAccount());
            entity.setErrorMsg(event.getFailReason());
            entity.setStatus(0);
            entity.setExecuteTime(0L);
            entity.setUserAgent(event.getUserAgent());
            entity.setBrowser(event.getBrowser());
            entity.setOs(event.getOs());
            entity.setOperateLocation(event.getOperateLocation());

            LocalDateTime now = LocalDateTime.now();
            entity.setCreateTime(now);
            entity.setDeleteFlag(0);

            sysLogMapper.insertLog(entity);
            log.debug("登录失败日志写入成功: targetAccount={}, reason={}", event.getTargetAccount(), event.getFailReason());
        } catch (Exception e) {
            log.error("登录失败日志写入失败: targetAccount={}, error={}", event.getTargetAccount(), e.getMessage(), e);
        }
    }
}
