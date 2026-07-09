package com.sxwl.security.event;

import java.time.LocalDateTime;

/**
 * 登出事件
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlLogoutEvent {

    private Long userId;
    private String username;
    private LocalDateTime time;

    public SxwlLogoutEvent() {
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
}
