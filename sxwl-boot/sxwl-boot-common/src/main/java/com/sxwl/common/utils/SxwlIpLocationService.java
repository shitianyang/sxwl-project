package com.sxwl.common.utils;

/**
 * IP 属地查询接口
 *
 * <p>默认无实现（返回 null），用户可引入 GeoLite2、ipapi 等实现。
 * 实现类需注册为 Spring Bean，框架自动注入。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@FunctionalInterface
public interface SxwlIpLocationService {

    /**
     * 根据 IP 查询属地
     *
     * @param ip 客户端 IP 地址
     * @return 属地描述，如 "北京市"、"上海市"；null 表示无法识别
     */
    String getLocation(String ip);
}
