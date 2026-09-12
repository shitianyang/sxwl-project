package com.sxwl.redis.lock;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.redis.helper.SxwlRedisHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlRedisLockManager} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SxwlRedisLockManagerTest {

    @Mock
    private SxwlRedisHelper redisHelper;

    private SxwlRedisLockManager lockManager;

    @BeforeEach
    void setUp() {
        lockManager = new SxwlRedisLockManager(redisHelper);
    }

    @Test
    @DisplayName("tryLock 成功时返回非空锁实例")
    void tryLock_shouldReturnLock_whenAcquired() {
        when(redisHelper.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);

        SxwlRedisLockManager.SxwlRedisLock lock = lockManager.tryLock("lock:test", 10);
        assertNotNull(lock);
    }

    @Test
    @DisplayName("tryLock 失败时返回 null")
    void tryLock_shouldReturnNull_whenNotAcquired() {
        when(redisHelper.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false);

        SxwlRedisLockManager.SxwlRedisLock lock = lockManager.tryLock("lock:test", 10);
        assertNull(lock);
    }

    @Test
    @DisplayName("lock 阻塞获取成功时返回锁实例")
    void lock_shouldReturnLock_whenAcquired() {
        when(redisHelper.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);

        SxwlRedisLockManager.SxwlRedisLock lock = lockManager.lock("lock:test", 10, 5);
        assertNotNull(lock);
    }

    @Test
    @DisplayName("lock 阻塞等待超时应抛出异常")
    void lock_shouldThrow_whenTimeout() {
        when(redisHelper.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> lockManager.lock("lock:test", 10, 1));
        assertTrue(ex.getMessage().contains("超时"));
    }

    @Test
    @DisplayName("锁释放时执行 Lua 脚本")
    void close_shouldExecuteScript() throws Exception {
        when(redisHelper.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(redisHelper.executeScript(anyString(), anyList(), anyString())).thenReturn(true);

        SxwlRedisLockManager.SxwlRedisLock lock = lockManager.tryLock("lock:close-test", 10);
        assertNotNull(lock);
        lock.close();

        verify(redisHelper).executeScript(anyString(), eq(List.of("lock:close-test")), anyString());
    }

    @Test
    @DisplayName("tryLock 使用的锁值每次不同")
    void tryLock_shouldUseDifferentValues() {
        when(redisHelper.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);

        lockManager.tryLock("lock:val-test", 10);
        lockManager.tryLock("lock:val-test", 10);

        // 验证每次使用不同的 lockValue
        verify(redisHelper, times(2)).setIfAbsent(eq("lock:val-test"), anyString(), eq(Duration.ofSeconds(10)));
    }
}
