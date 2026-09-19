package com.sxwl.sse.config;

import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.ticket.SxwlConnectionTicketService;
import com.sxwl.sse.manager.SxwlSseEmitterManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * SSE 自动装配配置
 *
 * <p>提供 SSE 基础设施 Bean（连接管理器、票据服务）。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@AutoConfiguration
public class SxwlSseAutoConfiguration {

    /**
     * SSE 连接管理器（线程安全，全局单例）
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlSseEmitterManager sxwlSseEmitterManager() {
        return new SxwlSseEmitterManager();
    }

    /**
     * 连接票据服务（生成一次性 ticket，用于前端建立 SSE 连接时的身份验证）
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlConnectionTicketService sxwlConnectionTicketManager(SxwlRedisHelper redisHelper) {
        return new SxwlConnectionTicketService(redisHelper);
    }
}
