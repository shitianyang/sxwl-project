package com.sxwl.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 短信验证码服务商配置
 * <p>
 * 支持阿里云 SMS、腾讯云 SMS、华为云 SMS 等多种服务商。
 * 当前为占位配置，实际部署时需要配置真实的 AccessKey 和模板 Code。
 * </p>
 *
 * <h3>安全提示</h3>
 * <p><b>严禁在 YAML 文件中硬编码密钥！</b> 以下敏感配置应通过环境变量注入：</p>
 * <ul>
 *   <li>阿里云 AccessKey Secret</li>
 *   <li>腾讯云 Secret ID / Secret Key</li>
 *   <li>华为云 AK / SK</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/9/12
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "sxwl.sms")
public class SxwlSmsProperties {

    // ==================== 通用配置 ====================

    /** 短信服务商标识：aliyun / tencent / huaweicloud */
    private String provider = "aliyun";

    /** 是否启用短信验证码（开发测试时可关闭，仅生成不发送） */
    private boolean enabled = false;

    // ==================== 阿里云 SMS ====================

    /** 阿里云 AccessKey ID */
    private String accessKeyId = "";

    /** 阿里云 AccessKey Secret */
    private String accessKeySecret = "";

    /** 阿里云签名名称（如："我的应用"） */
    private String signName = "";

    /** 短信模板 Code（如："SMS_123456789"） */
    private String templateCode = "";

    /** 模板参数名（通常为 "code"） */
    private String templateParamName = "code";

    // ==================== 腾讯云 SMS ====================

    /** 腾讯云 SDK App ID */
    private String sdkAppId = "";

    /** 腾讯云 Secret ID */
    private String tencentSecretId = "";

    /** 腾讯云 Secret Key */
    private String tencentSecretKey = "";

    /** 腾讯云签名名称 */
    private String tencentSignName = "";

    /** 腾讯云模板 ID */
    private String tencentTemplateId = "";

    // ==================== 华为云 SMS ====================

    /** 华为项目名 */
    private String regionName = "";

    /** 华为 AK */
    private String huaweiAccessKey = "";

    /** 华为 SK */
    private String huaweiSecretKey = "";

    /** 华为短号码接入地址 */
    private String endpoint = "";

    /** 华为模板 ID */
    private String huaweiTemplateId = "";

    /** 华为签名名称 */
    private String huaweiSignName = "";

    // ==================== getters/setters ====================

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateParamName() {
        return templateParamName;
    }

    public void setTemplateParamName(String templateParamName) {
        this.templateParamName = templateParamName;
    }

    public String getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(String sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public String getTencentSecretId() {
        return tencentSecretId;
    }

    public void setTencentSecretId(String tencentSecretId) {
        this.tencentSecretId = tencentSecretId;
    }

    public String getTencentSecretKey() {
        return tencentSecretKey;
    }

    public void setTencentSecretKey(String tencentSecretKey) {
        this.tencentSecretKey = tencentSecretKey;
    }

    public String getTencentSignName() {
        return tencentSignName;
    }

    public void setTencentSignName(String tencentSignName) {
        this.tencentSignName = tencentSignName;
    }

    public String getTencentTemplateId() {
        return tencentTemplateId;
    }

    public void setTencentTemplateId(String tencentTemplateId) {
        this.tencentTemplateId = tencentTemplateId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getHuaweiAccessKey() {
        return huaweiAccessKey;
    }

    public void setHuaweiAccessKey(String huaweiAccessKey) {
        this.huaweiAccessKey = huaweiAccessKey;
    }

    public String getHuaweiSecretKey() {
        return huaweiSecretKey;
    }

    public void setHuaweiSecretKey(String huaweiSecretKey) {
        this.huaweiSecretKey = huaweiSecretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHuaweiTemplateId() {
        return huaweiTemplateId;
    }

    public void setHuaweiTemplateId(String huaweiTemplateId) {
        this.huaweiTemplateId = huaweiTemplateId;
    }

    public String getHuaweiSignName() {
        return huaweiSignName;
    }

    public void setHuaweiSignName(String huaweiSignName) {
        this.huaweiSignName = huaweiSignName;
    }
}
