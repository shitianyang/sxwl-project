package com.sxwl.monitor.service;

import com.sxwl.monitor.model.VO.JvmInfoVO;

/**
 * JVM 信息采集服务接口
 *
 * <p>定义 JVM 运行时数据采集的服务契约。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface JvmInfoService {

    /**
     * 获取 JVM 信息
     *
     * @return JVM 运行时信息（堆内存、非堆内存、线程数、类加载数、GC 统计）
     */
    JvmInfoVO getJvmInfo();
}
