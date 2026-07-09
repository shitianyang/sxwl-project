package com.sxwl.security.event;

/**
 * Token 被吊销事件
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlTokenRevokedEvent {

    private Long userId;
    private String reason;
    private Long operatorId;

    public SxwlTokenRevokedEvent() {
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
}
