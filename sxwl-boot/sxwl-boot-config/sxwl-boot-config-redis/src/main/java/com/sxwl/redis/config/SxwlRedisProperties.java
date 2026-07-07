package com.sxwl.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 配置属性
 *
 * <p>仅暴露随环境变化的配置项。Redis 连接本身（host/port/password/pool）走
 * Spring Boot 标准属性 {@code spring.data.redis.*}，不在本类中定义。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "sxwl.redis")
public class SxwlRedisProperties {

    /**
     * 分布式锁默认超时（秒），默认 10
     */
    private long lockDefaultTimeout = 10;

    /**
     * 限流默认窗口（秒），默认 60
     */
    private long rateLimitDefaultWindow = 60;

    /**
     * 防重复提交默认间隔（秒），默认 3
     */
    private long repeatSubmitInterval = 3;

    public long getLockDefaultTimeout() {
        return lockDefaultTimeout;
    }

    public void setLockDefaultTimeout(long lockDefaultTimeout) {
        this.lockDefaultTimeout = lockDefaultTimeout;
    }

    public long getRateLimitDefaultWindow() {
        return rateLimitDefaultWindow;
    }

    public void setRateLimitDefaultWindow(long rateLimitDefaultWindow) {
        this.rateLimitDefaultWindow = rateLimitDefaultWindow;
    }

    public long getRepeatSubmitInterval() {
        return repeatSubmitInterval;
    }

    public void setRepeatSubmitInterval(long repeatSubmitInterval) {
        this.repeatSubmitInterval = repeatSubmitInterval;
    }
}
