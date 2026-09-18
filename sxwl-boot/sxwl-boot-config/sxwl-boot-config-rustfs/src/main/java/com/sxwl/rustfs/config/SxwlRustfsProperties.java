package com.sxwl.rustfs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RustFS 配置属性
 *
 * <p>绑定前缀 {@code sxwl.rustfs}，仅暴露必需的连接参数。</p>
 *
 * <h3>必需配置</h3>
 * <ul>
 *   <li>{@code endpoint}：RustFS 服务地址（如 {@code http://localhost:9000}）</li>
 *   <li>{@code access-key}：访问密钥（Access Key）</li>
 *   <li>{@code secret-key}：秘密密钥（Secret Key）</li>
 * </ul>
 *
 * <h3>硬编码默认值</h3>
 * <ul>
 *   <li>区域：{@code us-east-1}</li>
 *   <li>默认桶名：{@code sxwl-files}</li>
 *   <li>预签名 URL 有效期：{@code 3600 秒 (1 小时)}</li>
 *   <li>分片临时对象前缀：{@code tmp/}</li>
 * </ul>
 *
 * <h3>配置示例</h3>
 * <pre>{@code
 * sxwl:
 *   rustfs:
 *     endpoint: http://localhost:9000
 *     access-key: minioadmin
 *     secret-key: minioadmin-secret
 * }</pre>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "sxwl.rustfs")
public class SxwlRustfsProperties {

    /**
     * RustFS 服务地址
     *
     * <p>必须配置，如 {@code http://localhost:9000} 或 {@code https://rustfs.example.com}。</p>
     */
    private String endpoint;

    /**
     * 访问密钥（Access Key）
     *
     * <p>必须配置，与 Secret Key 配对使用。</p>
     */
    private String accessKey;

    /**
     * 秘密密钥（Secret Key）
     *
     * <p>必须配置，与 Access Key 配对使用。</p>
     */
    private String secretKey;

    /**
     * AWS 区域（默认 us-east-1）
     */
    private String region = "us-east-1";

    /**
     * 默认 Bucket 名称（默认 sxwl-files）
     */
    private String defaultBucket = "sxwl-files";

    /**
     * 分片临时对象前缀（默认 tmp/）
     */
    private String tmpPrefix = "tmp/";

    /**
     * 预签名 URL 有效期（默认 3600 秒 = 1 小时）
     */
    private int presignedUrlExpire = 3600;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDefaultBucket() {
        return defaultBucket;
    }

    public void setDefaultBucket(String defaultBucket) {
        this.defaultBucket = defaultBucket;
    }

    public String getTmpPrefix() {
        return tmpPrefix;
    }

    public void setTmpPrefix(String tmpPrefix) {
        this.tmpPrefix = tmpPrefix;
    }

    public int getPresignedUrlExpire() {
        return presignedUrlExpire;
    }

    public void setPresignedUrlExpire(int presignedUrlExpire) {
        this.presignedUrlExpire = presignedUrlExpire;
    }
}
