package com.sxwl.rustfs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RustFS 配置属性
 *
 * <p>绑定前缀 {@code sxwl.rustfs}，配置 S3 兼容对象存储的连接参数和行为。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "sxwl.rustfs")
public class SxwlRustfsProperties {

    /** S3 兼容服务地址，如 http://localhost:9000 */
    private String endpoint;

    /** 区域，如 us-east-1 */
    private String region = "us-east-1";

    /** Access Key */
    private String accessKey;

    /** Secret Key */
    private String secretKey;

    /** 默认桶名 */
    private String defaultBucket = "sxwl-files";

    /** 预签名 URL 有效期（秒），默认 1h */
    private long presignedUrlExpire = 3600;

    /** 分片临时对象前缀 */
    private String tempPathPrefix = "tmp/";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getDefaultBucket() {
        return defaultBucket;
    }

    public void setDefaultBucket(String defaultBucket) {
        this.defaultBucket = defaultBucket;
    }

    public long getPresignedUrlExpire() {
        return presignedUrlExpire;
    }

    public void setPresignedUrlExpire(long presignedUrlExpire) {
        this.presignedUrlExpire = presignedUrlExpire;
    }

    public String getTempPathPrefix() {
        return tempPathPrefix;
    }

    public void setTempPathPrefix(String tempPathPrefix) {
        this.tempPathPrefix = tempPathPrefix;
    }
}
