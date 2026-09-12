package com.sxwl.monitor.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ServerInfoVO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("ServerInfoVO 测试")
class ServerInfoVOTest {

    @Test
    @DisplayName("getter/setter 应正常读写")
    void testGetterSetter() {
        ServerInfoVO vo = new ServerInfoVO();
        vo.setCpuCores(8);
        vo.setCpuLoad(45.5);
        vo.setMemTotal(16L * 1024 * 1024 * 1024);
        vo.setMemUsed(8L * 1024 * 1024 * 1024);
        vo.setDiskTotal(500L * 1024 * 1024 * 1024);
        vo.setDiskUsed(200L * 1024 * 1024 * 1024);

        assertEquals(8, vo.getCpuCores());
        assertEquals(45.5, vo.getCpuLoad(), 0.001);
        assertEquals(16L * 1024 * 1024 * 1024, vo.getMemTotal());
        assertEquals(8L * 1024 * 1024 * 1024, vo.getMemUsed());
        assertEquals(500L * 1024 * 1024 * 1024, vo.getDiskTotal());
        assertEquals(200L * 1024 * 1024 * 1024, vo.getDiskUsed());
    }
}
