package com.sxwl.security.key;

import com.sxwl.common.entity.SxwlPublicKeyVO;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SM2Utils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.config.SxwlSecurityProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * SM2 密钥轮换管理器
 * <p>
 * 职责：
 * <ul>
 *   <li>启动时生成随机初始密钥对</li>
 *   <li>定时生成新的随机 SM2 密钥对（默认 24 小时轮换一次）</li>
 *   <li>保留上个密钥对（宽限期 2 小时），确保旧公钥加密的密码仍可解密</li>
 *   <li>提供公钥查询和密码解密接口</li>
 * </ul>
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Component
public class SxwlSM2KeyManager {

    private static final Logger log = LoggerFactory.getLogger(SxwlSM2KeyManager.class);

    private static final Random RANDOM = new Random();
    private static final HexFormat HEX = HexFormat.of();

    /** Redis 操作封装（持久化 SM2 密钥对，确保重启不丢失） */
    private static final String REDIS_KEY = "sxwl:sm2:key-store";

    /** 读写锁保护内部状态 */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /** 当前活跃密钥 */
    private SM2ActiveKey currentKey;

    /** 历史密钥列表（最近的在头部） */
    private final LinkedList<SM2ActiveKey> previousKeys = new LinkedList<>();

    /** 轮换间隔（分钟） */
    private final long rotationIntervalMinutes;

    /** 宽限期（分钟） */
    private final long gracePeriodMinutes;

    /** 最大历史密钥数 */
    private final int maxHistory;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "sm2-key-rotation");
        t.setDaemon(true);
        return t;
    });

    private final SxwlRedisHelper redisHelper;

    public SxwlSM2KeyManager(SxwlSecurityProperties properties, SxwlRedisHelper redisHelper) {
        this.rotationIntervalMinutes = properties.getSm2KeyRotationIntervalMinutes();
        this.gracePeriodMinutes = properties.getSm2KeyGracePeriodMinutes();
        this.maxHistory = properties.getSm2KeyMaxHistory();
        this.redisHelper = redisHelper;
    }

    @PostConstruct
    public void init() {
        // 1. 优先从 Redis 恢复密钥对（确保重启不丢失）
        boolean loaded = loadFromRedis();
        if (!loaded) {
            // 2. Redis 无数据，生成新密钥对
            currentKey = generateNewKey();
            saveToRedis(currentKey);
            log.info("SM2 初始密钥已生成并持久化到 Redis: keyId={}", currentKey.keyId);
        } else {
            log.info("SM2 密钥已从 Redis 恢复: keyId={}", currentKey.keyId);
        }

        // 3. 启动定时轮换
        scheduler.scheduleWithFixedDelay(
                this::rotate,
                rotationIntervalMinutes,
                rotationIntervalMinutes,
                TimeUnit.MINUTES
        );
        log.info("SM2 密钥轮换管理器启动完成，轮换间隔={}分钟，宽限期={}分钟，最大历史={}",
                rotationIntervalMinutes, gracePeriodMinutes, maxHistory);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    /**
     * 获取当前公钥信息（供 /auth/public-key 端点使用）
     *
     * @return 包含公钥、keyId、过期时间的 VO
     */
    public SxwlPublicKeyVO getCurrentPublicKey() {
        lock.readLock().lock();
        try {
            SM2ActiveKey key = currentKey;
            if (key == null) {
                throw new SxwlBusinessException(500, "SM2 密钥尚未初始化");
            }
            return new SxwlPublicKeyVO(key.publicKeyRaw, key.keyId, key.expiresAt);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 使用当前或历史私钥解密 SM2 密文
     * <p>
     * 尝试顺序：当前密钥 → 历史密钥（最近优先）。
     * </p>
     *
     * @param cipherTextBase64 Base64 密文
     * @return 明文字符串
     * @throws SxwlBusinessException 所有私钥均无法解密时抛出
     */
    public String decrypt(String cipherTextBase64) {
        if (cipherTextBase64 == null || cipherTextBase64.isBlank()) {
            return "";
        }

        lock.readLock().lock();
        try {
            // 1. 尝试当前密钥
            try {
                return SM2Utils.decryptFromBase64(cipherTextBase64, currentKey.privateKeyHex);
            } catch (Exception e) {
                log.warn("SM2 解密失败（当前密钥），keyId={}: {}", currentKey.keyId, e.getMessage());
            }

            // 2. 尝试历史密钥（最近在前）
            for (SM2ActiveKey prev : previousKeys) {
                try {
                    return SM2Utils.decryptFromBase64(cipherTextBase64, prev.privateKeyHex);
                } catch (Exception e) {
                    log.warn("SM2 解密失败（历史密钥），keyId={}: {}", prev.keyId, e.getMessage());
                }
            }

            log.error("SM2 解密失败，当前密钥及 {} 个历史密钥均无法解密", previousKeys.size());
            throw new SxwlBusinessException(500, "SM2 解密失败，当前密钥及历史密钥均无法解密");
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 执行一次密钥轮换
     * <p>
     * 由定时任务调用，也可手动触发。生成新随机密钥对，历史密钥按宽限期和最大数量管理。
     * 新密钥会持久化到 Redis，确保重启后仍可解密用此公钥加密的密码。
     * </p>
     */
    public void rotate() {
        lock.writeLock().lock();
        try {
            // 1. 移动当前密钥到历史列表头部
            if (currentKey != null) {
                previousKeys.addFirst(currentKey);
            }

            // 2. 生成新的随机密钥对
            currentKey = generateNewKey();
            saveToRedis(currentKey);

            // 3. 清理超过宽限期的历史密钥
            Instant cutoff = Instant.now().minus(gracePeriodMinutes, ChronoUnit.MINUTES);
            previousKeys.removeIf(k -> k.createdAt.isBefore(cutoff));

            // 4. 限制最大历史数量
            while (previousKeys.size() > maxHistory) {
                previousKeys.removeLast();
            }

            log.info("SM2 密钥轮换完成: keyId={}, 剩余历史密钥数={}",
                    currentKey.keyId, previousKeys.size());
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 生成新的随机密钥对
     */
    private SM2ActiveKey generateNewKey() {
        SM2Utils.SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        String keyId = "sm2-" + Instant.now().toEpochMilli() + "-" + HEX.formatHex(randomBytes(4));
        String publicKeyRaw = SM2Utils.toRawPublicKeyHex(keyPair.getPublicKeyHex());
        Instant now = Instant.now();
        long expiresAt = now.plus(rotationIntervalMinutes, ChronoUnit.MINUTES).getEpochSecond();
        return new SM2ActiveKey(keyId, keyPair.getPrivateKeyHex(), publicKeyRaw, now, expiresAt);
    }

    private static byte[] randomBytes(int len) {
        byte[] buf = new byte[len];
        RANDOM.nextBytes(buf);
        return buf;
    }

    /**
     * 将当前密钥持久化到 Redis Hash
     * <p>
     * 存储字段：keyId, privateKeyHex, publicKeyRaw, createdAt, expiresAt
     * </p>
     */
    private void saveToRedis(SM2ActiveKey key) {
        try {
            redisHelper.hmset(REDIS_KEY, Map.of(
                    "keyId", key.keyId,
                    "privateKeyHex", key.privateKeyHex,
                    "publicKeyRaw", key.publicKeyRaw,
                    "createdAt", String.valueOf(key.createdAt.toEpochMilli()),
                    "expiresAt", String.valueOf(key.expiresAt)
            ));
        } catch (Exception e) {
            log.warn("SM2 密钥持久化到 Redis 失败（不影响运行）: {}", e.getMessage());
        }
    }

    /**
     * 从 Redis 恢复当前密钥对
     *
     * @return true 表示成功恢复
     */
    private boolean loadFromRedis() {
        try {
            Map<String, String> data = redisHelper.hgetAll(REDIS_KEY);
            if (data == null || data.isEmpty()
                    || data.get("privateKeyHex") == null
                    || data.get("publicKeyRaw") == null) {
                return false;
            }

            String keyId = data.get("keyId");
            String privateKeyHex = data.get("privateKeyHex");
            String publicKeyRaw = data.get("publicKeyRaw");
            long createdAtEpoch = Long.parseLong(data.getOrDefault("createdAt", "0"));
            long expiresAt = Long.parseLong(data.getOrDefault("expiresAt", "0"));

            Instant createdAt = createdAtEpoch > 0
                    ? Instant.ofEpochMilli(createdAtEpoch)
                    : Instant.now();

            // 验证私钥和公钥是否匹配
            String derivedPublicKey = SM2Utils.deriveRawPublicKeyHex(privateKeyHex);
            if (!derivedPublicKey.equals(publicKeyRaw)) {
                log.warn("Redis 中存储的 SM2 公私钥不匹配，将重新生成");
                return false;
            }

            currentKey = new SM2ActiveKey(keyId, privateKeyHex, publicKeyRaw, createdAt, expiresAt);
            return true;
        } catch (Exception e) {
            log.warn("从 Redis 恢复 SM2 密钥失败，将重新生成: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 活跃密钥内部模型
     */
    private static class SM2ActiveKey {

        /** 密钥 ID（如 sm2-1720156800000-a1b2） */
        final String keyId;

        /** 私钥（PKCS#8 编码十六进制） */
        final String privateKeyHex;

        /** 裸公钥（04||x||y 格式） */
        final String publicKeyRaw;

        /** 创建时间 */
        final Instant createdAt;

        /** 过期时间戳（Unix 秒），用于前端缓存到期自动刷新 */
        final long expiresAt;

        SM2ActiveKey(String keyId, String privateKeyHex, String publicKeyRaw,
                     Instant createdAt, long expiresAt) {
            this.keyId = keyId;
            this.privateKeyHex = privateKeyHex;
            this.publicKeyRaw = publicKeyRaw;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
        }
    }
}
