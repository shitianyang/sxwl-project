package com.sxwl.monitor.service;

import com.sxwl.monitor.model.VO.ServerInfoVO;

/**
 * 服务器信息采集服务接口
 *
 * <p>定义操作系统级别硬件信息采集的服务契约。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface ServerInfoService {

    /**
     * 获取服务器硬件信息
     *
     * @return 包含 CPU、内存、磁盘的服务器信息 VO
     */
    ServerInfoVO getServerInfo();
}
