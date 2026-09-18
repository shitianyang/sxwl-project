package com.sxwl.common.utils;

/**
 * IP 属地查询接口
 *
 * <p>默认无实现（返回 null），如需启用 IP 属地识别功能，可在业务模块提供该接口的 Spring Bean 实现。
 * 常用实现方案：GeoLite2（MaxMind）、ipapi、阿里云 IP 地址查询等。</p>
 *
 * <p><b>框架自动注入：</b>若项目中存在 {@code SxwlIpLocationService} 的实现类 Bean，
 * 框架将通过 {@code Optional<SxwlIpLocationService>} 自动注入到以下组件：</p>
 * <ul>
 *   <li>{@code SxwlLogAspect} - 操作日志切面，记录登录 IP 属地</li>
 *   <li>{@code AuthController} - 认证控制器，记录用户登录 IP 属地</li>
 *   <li>{@code WxAuthController} - 微信认证控制器，记录微信用户 IP 属地</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 1. 创建实现类
 * @Component
 * public class GeoLite2IpLocationService implements SxwlIpLocationService {
 *     private final DatabaseReader reader;
 *     
 *     public GeoLite2IpLocationService() {
 *         this.reader = new DatabaseReader.Builder(
 *             Paths.get("/path/to/GeoLite2-City.mmdb")).build();
 *     }
 *     
 *     @Override
 *     public String getLocation(String ip) {
 *         try {
 *             CityResponse response = reader.city(ip);
 *             Location loc = response.getLocation().get();
 *             String country = response.getCountry().getName();
 *             String city = response.getCity().getNames().get("zh-CN");
 *             return country + (city != null ? " - " + city : "");
 *         } catch (Exception e) {
 *             return null;
 *         }
 *     }
 * }
 * 
 * // 2. 框架自动注入，无需手动配置
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public interface SxwlIpLocationService {

    /**
     * 根据 IP 查询属地
     *
     * @param ip 客户端 IP 地址
     * @return 属地描述，如 "北京市"、"上海市"；null 表示无法识别
     */
    String getLocation(String ip);
}
