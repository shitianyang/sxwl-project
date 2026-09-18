package com.sxwl.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.sxwl.redis.aspect.SxwlRepeatSubmitAspect;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.redis.lock.SxwlRedisLockManager;
import com.sxwl.redis.rate.SxwlSlidingWindowRateLimiter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 自动装配配置
 *
 * <p>提供开箱即用的 Redis 基础设施配置，无需在 {@code application.yaml} 中额外配置。</p>
 *
 * <h3>设计原则</h3>
 * <ul>
 *   <li><b>零配置启动</b>：所有默认值硬编码在 Java 类中，开箱即用</li>
 *   <li><b>序列化安全</b>：Key 使用 String 序列化（可读性好），Value 使用 GenericJackson2JsonRedisSerializer（支持泛型和继承）</li>
 *   <li><b>工具类 Bean 化</b>：注册常用工具类，业务模块直接注入使用</li>
 * </ul>
 *
 * <h3>默认值</h3>
 * <table border="1">
 *   <tr><th>功能</th><th>默认值</th><th>说明</th></tr>
 *   <tr><td>分布式锁超时</td><td>10 秒</td><td>{@link SxwlRedisLockManager#tryLock(String, long)}</td></tr>
 *   <tr><td>限流窗口</td><td>60 秒</td><td>{@link SxwlSlidingWindowRateLimiter#tryAcquire(String, long, long)}</td></tr>
 *   <tr><td>防重复提交</td><td>3 秒</td><td>@{@link com.sxwl.common.annotation.SxwlRepeatSubmit}</td></tr>
 * </table>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * @RestController
 * @RequestMapping("/sys/user")
 * public class SysUserCtrl {
 *     
 *     @Autowired private SxwlRedisHelper redisHelper;
 *     @Autowired private SxwlRedisLockManager lockManager;
 *     @Autowired private SxwlSlidingWindowRateLimiter rateLimiter;
 *     
 *     @PostMapping("/login")
 *     @SxwlRepeatSubmit(interval = 3)
 *     public Result login() {
 *         // 1. 限流检查
 *         if (!rateLimiter.tryAcquire("rate:login:" + ip, 10, 60)) {
 *             throw new SxwlBusinessException("请求过于频繁");
 *         }
 *         
 *         // 2. 分布式锁
 *         try (SxwlRedisLock lock = lockManager.tryLock("user:lock:123", 10)) {
 *             if (lock == null) {
 *                 throw new SxwlBusinessException("操作进行中");
 *             }
 *             // 执行业务逻辑
 *         }
 *         
 *         // 3. Redis 操作
 *         redisHelper.set("cache:key", "value", Duration.ofMinutes(5));
 *     }
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@AutoConfiguration
public class SxwlRedisAutoConfiguration {

    // ==================== 序列化配置 ====================

    /**
     * RedisTemplate：Key 用 String，Value 用 Jackson JSON
     *
     * <p>支持泛型和继承类型，自动处理对象/集合/基本类型序列化。</p>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key / HashKey：String 序列化（可读性好，Redis CLI 可直接查看）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value / HashValue：Jackson JSON 序列化（支持泛型和继承）
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.ALL, 
                com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY);
        // 允许序列化抽象类和接口（支持多态）
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * StringRedisTemplate：专门用于 String 类型的 Redis 操作
     *
     * <p>{@link SxwlRedisHelper} 基于此类实现，保证序列化一致性。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    // ==================== 工具类 Bean ====================

    /**
     * SxwlRedisHelper：Redis 操作封装
     *
     * <p>统一封装 {@link StringRedisTemplate} 的常用操作，简化上层模块的 Redis 调用。</p>
     *
     * <h3>核心能力</h3>
     * <ul>
     *   <li>String 操作：set/get/delete/increment/expiry</li>
     *   <li>Hash 操作：hset/hget/hdel/hscan</li>
     *   <li>Set 操作：sadd/smembers/srem</li>
     *   <li>ZSet 操作：zadd/zremrangeByScore/zcard（用于滑动窗口限流）</li>
     *   <li>Lua 脚本执行：原子操作保障</li>
     * </ul>
     *
     * <h3>使用场景</h3>
     * <ul>
     *   <li>验证码存储：{@code set("sms:code:" + phone, code, Duration.ofMinutes(5))}</li>
     *   <li>计数器：{@code increment("counter:api:" + userId)}
     *   <li>会话存储：{@code hset("session:" + token, "userId", String.valueOf(userId))}</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlRedisHelper sxwlRedisHelper(StringRedisTemplate stringRedisTemplate) {
        return new SxwlRedisHelper(stringRedisTemplate);
    }

    /**
     * SxwlRedisLockManager：分布式锁管理器
     *
     * <p>基于 Redis {@code SETNX} + Lua 解锁实现，用于并发控制。</p>
     *
     * <h3>核心能力</h3>
     * <ul>
     *   <li><b>非阻塞获取</b>：{@code tryLock(key, timeout)} - 立即返回成功/失败</li>
     *   <li><b>阻塞获取</b>：{@code lock(key, timeout, waitSeconds)} - 最多等待指定时间</li>
     *   <li><b>自动释放</b>：实现 {@link AutoCloseable}，推荐使用 try-with-resources</li>
     *   <li><b>UUID 防误删</b>：解锁时校验 lockValue，防止线程 A 的锁被线程 B 误删</li>
     * </ul>
     *
     * <h3>使用场景</h3>
     * <ul>
     *   <li>库存扣减：{@code lock("stock:lock:" + productId)}</li>
     *   <li>Token 刷新：{@code lock("refresh:lock:" + userId)}</li>
     *   <li>批量任务：{@code lock("batch:lock:" + taskId)}</li>
     * </ul>
     *
     * <h3>默认值</h3>
     * <ul>
     *   <li>锁持有超时：{@code 10 秒}</li>
     *   <li>建议：业务逻辑应在毫秒级完成，避免锁长时间持有</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlRedisLockManager sxwlRedisLockManager(SxwlRedisHelper redisHelper) {
        return new SxwlRedisLockManager(redisHelper);
    }

    /**
     * SxwlSlidingWindowRateLimiter：滑动窗口限流器
     *
     * <p>基于 Redis ZSET 实现，精确统计窗口内请求数，有效防止刷接口和暴力破解。</p>
     *
     * <h3>算法原理</h3>
     * <pre>
     * 1. 清理过期记录：ZREMRANGEBYSCORE key 0 (now - window)
     * 2. 统计窗口内请求数：ZCARD key
     * 3. 若 count < maxCount → ZADD 当前请求 → 放行
     * 4. 否则 → 限流拒绝
     * </pre>
     *
     * <h3>核心能力</h3>
     * <ul>
     *   <li><b>Lua 脚本原子执行</b>：清理 + 计数 + 添加，避免并发竞争</li>
     *   <li><b>滑动窗口</b>：比固定窗口更精准，过渡平滑</li>
     *   <li><b>自动过期</b>：Key 自动设置 TTL，不会内存泄漏</li>
     * </ul>
     *
     * <h3>使用场景</h3>
     * <ul>
     *   <li>登录限流：{@code tryAcquire("rate:login:" + ip, 10, 60)}</li>
     *   <li>短信验证码：{@code tryAcquire("rate:sms:" + phone, 1, 60)}</li>
     *   <li>API 调用：{@code tryAcquire("rate:api:" + userId, 100, 3600)}</li>
     * </ul>
     *
     * <h3>默认值</h3>
     * <ul>
     *   <li>限流窗口：{@code 60 秒}</li>
     *   <li>建议：根据业务场景调整 maxCount 和 windowSeconds</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlSlidingWindowRateLimiter sxwlSlidingWindowRateLimiter(SxwlRedisHelper redisHelper) {
        return new SxwlSlidingWindowRateLimiter(redisHelper);
    }

    /**
     * SxwlRepeatSubmitAspect：防重复提交 AOP 切面
     *
     * <p>拦截标注了 {@link com.sxwl.common.annotation.SxwlRepeatSubmit} 的方法，
     * 在指定间隔内同一用户对同一接口的重复请求将被拒绝。</p>
     *
     * <h3>实现原理</h3>
     * <pre>
     * 首次请求 → SETNX repeat:{userId}:{uri} → 成功 → 放行
     * 重复请求 → SETNX 返回 false（Key 已存在）→ 抛 SxwlRepeatSubmitException
     * </pre>
     *
     * <h3>核心能力</h3>
     * <ul>
     *   <li><b>用户隔离</b>：Key 包含 userId，不同用户互不影响</li>
     *   <li><b>URI 隔离</b>：Key 包含 URI，不同接口独立计数</li>
     *   <li><b>HTTP 方法区分</b>：{@code GET:/api} 和 {@code POST:/api} 独立计数</li>
     *   <li><b>未登录放行</b>：未获取到登录用户时直接放行，不影响正常流程</li>
     * </ul>
     *
     * <h3>使用场景</h3>
     * <ul>
     *   <li>表单提交：防止用户双击提交按钮重复提交</li>
     *   <li>支付请求：防止网络重试导致重复扣款</li>
     *   <li>数据导入：防止批量上传重复处理</li>
     * </ul>
     *
     * <h3>使用示例</h3>
     * <pre>{@code
     * @PostMapping("/submit")
     * @SxwlRepeatSubmit(interval = 3, message = "请勿重复提交")
     * public Result submit() {
     *     // 3 秒内同一用户不能重复调用此接口
     *     return Result.success();
     * }
     * }</pre>
     *
     * <h3>默认值</h3>
     * <ul>
     *   <li>防重复间隔：{@code 3 秒}</li>
     *   <li>建议：根据业务接口特点调整 interval 参数</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlRepeatSubmitAspect sxwlRepeatSubmitAspect(SxwlRedisHelper redisHelper) {
        return new SxwlRepeatSubmitAspect(redisHelper);
    }
}
