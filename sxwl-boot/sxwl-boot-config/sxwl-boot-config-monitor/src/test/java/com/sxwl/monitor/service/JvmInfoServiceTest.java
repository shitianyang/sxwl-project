package com.sxwl.monitor.service;

import com.sxwl.monitor.model.JvmInfoVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link JvmInfoService} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("JvmInfoService 测试")
class JvmInfoServiceTest {

    private final JvmInfoService service = new JvmInfoService();

    @Test
    @DisplayName("getJvmInfo 应返回合法的 JVM 信息")
    void getJvmInfo_shouldReturnValidInfo() {
        JvmInfoVO info = service.getJvmInfo();
        assertNotNull(info);
        // 堆内存应该大于0
        assertTrue(info.getHeapMax() > 0);
        assertTrue(info.getHeapUsed() > 0);
        assertTrue(info.getHeapCommitted() > 0);
        // 线程数应该大于0
        assertTrue(info.getThreadCount() > 0);
        // 类加载数量应该大于0
        assertTrue(info.getClassLoadedCount() > 0);
        // GC 信息不应为空
        assertNotNull(info.getGcInfos());
    }
}
