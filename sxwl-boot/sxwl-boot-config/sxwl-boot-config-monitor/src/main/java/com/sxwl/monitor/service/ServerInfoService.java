package com.sxwl.monitor.service;

import com.sxwl.monitor.model.ServerInfoVO;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 服务器信息 Service
 *
 * <p>使用 OSHI 获取操作系统级别的 CPU、内存、磁盘使用情况。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Component
public class ServerInfoService {

    private static final Logger log = LoggerFactory.getLogger(ServerInfoService.class);

    private final SystemInfo systemInfo;

    public ServerInfoService() {
        this.systemInfo = new SystemInfo();
    }

    /**
     * 获取服务器信息
     *
     * @return 包含 CPU、内存、磁盘的服务器信息 VO
     */
    public ServerInfoVO getServerInfo() {
        ServerInfoVO vo = new ServerInfoVO();

        try {
            HardwareAbstractionLayer hal = systemInfo.getHardware();

            // CPU 信息
            CentralProcessor processor = hal.getProcessor();
            vo.setCpuCores(processor.getLogicalProcessorCount());
            // 获取系统 CPU 负载（0.0 ~ 1.0，采样间隔 1000ms）
            vo.setCpuLoad(processor.getSystemCpuLoad(1000L) * 100);

            // 内存信息
            GlobalMemory memory = hal.getMemory();
            vo.setMemTotal(memory.getTotal());
            vo.setMemUsed(memory.getTotal() - memory.getAvailable());
        } catch (Exception e) {
            log.warn("获取服务器硬件信息失败: {}", e.getMessage());
        }

        // 磁盘信息（取项目所在磁盘）
        try {
            File root = new File(".");
            File disk = root.getAbsoluteFile().getParentFile();
            while (disk.getParentFile() != null) {
                disk = disk.getParentFile();
            }
            vo.setDiskTotal(disk.getTotalSpace());
            vo.setDiskUsed(disk.getTotalSpace() - disk.getFreeSpace());
        } catch (Exception e) {
            log.warn("获取磁盘信息失败: {}", e.getMessage());
        }

        return vo;
    }
}
