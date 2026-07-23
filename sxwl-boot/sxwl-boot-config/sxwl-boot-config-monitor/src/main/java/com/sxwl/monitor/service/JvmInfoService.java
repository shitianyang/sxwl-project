package com.sxwl.monitor.service;

import com.sxwl.monitor.model.GcInfoVO;
import com.sxwl.monitor.model.JvmInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * JVM 信息 Service
 *
 * <p>使用 JDK 内置的 {@link ManagementFactory} 获取 JVM 堆内存、线程、类加载、GC 等信息。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Component
public class JvmInfoService {

    private static final Logger log = LoggerFactory.getLogger(JvmInfoService.class);

    /**
     * 获取 JVM 信息
     *
     * @return JVM 信息 VO
     */
    public JvmInfoVO getJvmInfo() {
        JvmInfoVO vo = new JvmInfoVO();

        try {
            // 堆内存
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
            vo.setHeapMax(heap.getMax());
            vo.setHeapUsed(heap.getUsed());
            vo.setHeapCommitted(heap.getCommitted());

            // 非堆内存
            MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();
            vo.setNonHeapUsed(nonHeap.getUsed());

            // 线程
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            vo.setThreadCount(threadMXBean.getThreadCount());
            vo.setPeakThreadCount(threadMXBean.getPeakThreadCount());

            // 类加载
            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
            vo.setClassLoadedCount(classLoadingMXBean.getLoadedClassCount());

            // GC
            List<GcInfoVO> gcInfos = new ArrayList<>();
            for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
                GcInfoVO gcInfo = new GcInfoVO();
                gcInfo.setName(gc.getName());
                gcInfo.setCount(gc.getCollectionCount());
                gcInfo.setTotalTimeMs(gc.getCollectionTime());
                gcInfos.add(gcInfo);
            }
            vo.setGcInfos(gcInfos);
        } catch (Exception e) {
            log.warn("获取 JVM 信息失败: {}", e.getMessage());
        }

        return vo;
    }
}
