package com.sxwl.security.event;

import java.time.LocalDateTime;

/**
 * 登录失败事件
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlLoginFailureEvent {

    private String targetAccount;
    private String ip;
    private String failReason;
    private int failCount;
    private LocalDateTime time;

    public SxwlLoginFailureEvent() {
    }

    public String getTargetAccount() { return targetAccount; }
    public void setTargetAccount(String targetAccount) { this.targetAccount = targetAccount; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }
    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
}
