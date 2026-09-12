package com.sxwl.monitor.service;

import com.sxwl.monitor.model.ServerInfoVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ServerInfoService} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("ServerInfoService 测试")
class ServerInfoServiceTest {

    private final ServerInfoService service = new ServerInfoService();

    @Test
    @DisplayName("getServerInfo 应返回合法的服务器信息")
    void getServerInfo_shouldReturnValidInfo() {
        ServerInfoVO info = service.getServerInfo();
        assertNotNull(info);
        // CPU 核心数应大于 0
        assertTrue(info.getCpuCores() > 0);
        // 内存总量应大于 0
        assertTrue(info.getMemTotal() > 0);
        // 磁盘总量应大于 0
        assertTrue(info.getDiskTotal() > 0);
    }
}
