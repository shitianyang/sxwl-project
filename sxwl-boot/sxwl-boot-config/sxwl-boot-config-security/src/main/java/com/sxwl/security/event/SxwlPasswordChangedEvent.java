package com.sxwl.security.event;

import java.time.LocalDateTime;

/**
 * 密码修改事件
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlPasswordChangedEvent {

    private Long userId;
    private String oldEncodedPassword;
    private Long operatorId;
    private LocalDateTime time;

    public SxwlPasswordChangedEvent() {
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOldEncodedPassword() { return oldEncodedPassword; }
    public void setOldEncodedPassword(String oldEncodedPassword) { this.oldEncodedPassword = oldEncodedPassword; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
}
