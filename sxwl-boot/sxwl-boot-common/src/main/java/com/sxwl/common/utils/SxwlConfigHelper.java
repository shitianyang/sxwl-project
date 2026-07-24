package com.sxwl.common.utils;

/**
 * 系统参数配置工具类（跨模块访问）
 *
 * <p>通过 Provider 模式实现跨模块访问：定义 {@link ConfigProvider} 接口，
 * 由上层模块（如 sxwl-boot-module-config）在启动时调用 {@link #setProvider(ConfigProvider)}
 * 注册真实实现，本工具类提供静态便捷方法供任意模块调用。</p>
 *
 * <p>Redis 缓存逻辑由 Provider 实现层负责，SxwlConfigHelper 本身不感知缓存。</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * // 获取字符串值
 * String siteName = SxwlConfigHelper.getConfigValue("sys.siteName");
 * // 获取整数值（null safe）
 * Integer maxLoginAttempts = SxwlConfigHelper.getConfigInt("sys.maxLoginAttempts");
 * // 获取布尔值（null safe）
 * Boolean captchaEnabled = SxwlConfigHelper.getConfigBool("sys.captchaEnabled");
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public final class SxwlConfigHelper {

    private SxwlConfigHelper() {
        throw new UnsupportedOperationException("SxwlConfigHelper 工具类，不允许实例化");
    }

    /** 默认空实现：未注册 Provider 时返回 null */
    private static volatile ConfigProvider provider = configKey -> null;

    /**
     * 注册配置提供者（由上层模块在启动时调用）
     *
     * @param configProvider 配置提供者实现，为 null 时重置为空实现
     */
    public static void setProvider(ConfigProvider configProvider) {
        provider = configProvider != null ? configProvider : configKey -> null;
    }

    /**
     * 获取配置值（字符串）
     *
     * @param configKey 参数键名
     * @return 参数值，未配置返回 null
     */
    public static String getConfigValue(String configKey) {
        return provider.getConfigValue(configKey);
    }

    /**
     * 获取配置值（整型）
     *
     * @param configKey 参数键名
     * @return 整数值，未配置或非数字时返回 null
     */
    public static Integer getConfigInt(String configKey) {
        String value = getConfigValue(configKey);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取配置值（布尔型）
     *
     * <p>以下值视为 {@code true}（不区分大小写）：
     * {@code "true"}、{@code "yes"}、{@code "1"}</p>
     *
     * @param configKey 参数键名
     * @return 布尔值，未配置返回 null
     */
    public static Boolean getConfigBool(String configKey) {
        String value = getConfigValue(configKey);
        if (value == null) {
            return null;
        }
        return "true".equalsIgnoreCase(value)
                || "yes".equalsIgnoreCase(value)
                || "1".equals(value);
    }

    /**
     * 配置提供者接口
     *
     * <p>由上层模块实现，负责查询系统参数配置（Redis 缓存 + DB 兜底）。</p>
     */
    @FunctionalInterface
    public interface ConfigProvider {

        /**
         * 根据键名获取配置值
         *
         * @param configKey 参数键名
         * @return 参数值，不存在返回 null
         */
        String getConfigValue(String configKey);
    }
}
