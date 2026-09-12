package com.sxwl.monitor.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link JvmInfoVO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("JvmInfoVO 测试")
class JvmInfoVOTest {

    @Test
    @DisplayName("getter/setter 应正常读写")
    void testGetterSetter() {
        JvmInfoVO vo = new JvmInfoVO();
        vo.setHeapMax(1024L * 1024 * 1024);
        vo.setHeapUsed(512L * 1024 * 1024);
        vo.setHeapCommitted(768L * 1024 * 1024);
        vo.setNonHeapUsed(128L * 1024 * 1024);
        vo.setThreadCount(20);
        vo.setPeakThreadCount(35);
        vo.setClassLoadedCount(10000L);

        GcInfoVO gc = new GcInfoVO();
        gc.setName("G1 GC");
        vo.setGcInfos(List.of(gc));

        assertEquals(1024L * 1024 * 1024, vo.getHeapMax());
        assertEquals(512L * 1024 * 1024, vo.getHeapUsed());
        assertEquals(768L * 1024 * 1024, vo.getHeapCommitted());
        assertEquals(128L * 1024 * 1024, vo.getNonHeapUsed());
        assertEquals(20, vo.getThreadCount());
        assertEquals(35, vo.getPeakThreadCount());
        assertEquals(10000L, vo.getClassLoadedCount());
        assertNotNull(vo.getGcInfos());
        assertEquals(1, vo.getGcInfos().size());
    }
}
