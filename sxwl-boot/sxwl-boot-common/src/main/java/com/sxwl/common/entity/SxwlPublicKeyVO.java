package com.sxwl.common.entity;

/**
 * SM2 公钥响应 VO
 * <p>
 * 包含裸公钥、密钥 ID、过期时间，支持前端缓存和密钥轮换。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SxwlPublicKeyVO {

    /** 裸公钥（04||x||y 格式，130 位十六进制） */
    private String publicKey;

    /** 密钥 ID（用于区分轮换版本，格式如 sm2-1720156800000-a1b2） */
    private String keyId;

    /** 该公钥的过期时间戳（Unix 秒） */
    private long expiresAt;

    public SxwlPublicKeyVO() {
    }

    public SxwlPublicKeyVO(String publicKey, String keyId, long expiresAt) {
        this.publicKey = publicKey;
        this.keyId = keyId;
        this.expiresAt = expiresAt;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
