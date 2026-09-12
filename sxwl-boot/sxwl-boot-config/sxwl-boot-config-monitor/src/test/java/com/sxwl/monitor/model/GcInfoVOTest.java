package com.sxwl.monitor.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link GcInfoVO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("GcInfoVO 测试")
class GcInfoVOTest {

    @Test
    @DisplayName("getter/setter 应正常读写")
    void testGetterSetter() {
        GcInfoVO vo = new GcInfoVO();
        vo.setName("G1 Young Generation");
        vo.setCount(100L);
        vo.setTotalTimeMs(5000L);

        assertEquals("G1 Young Generation", vo.getName());
        assertEquals(100L, vo.getCount());
        assertEquals(5000L, vo.getTotalTimeMs());
    }
}
