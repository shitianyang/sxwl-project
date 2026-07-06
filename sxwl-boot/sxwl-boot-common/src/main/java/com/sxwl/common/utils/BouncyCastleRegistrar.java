package com.sxwl.common.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

/**
 * BouncyCastle 提供者统一注册器
 * <p>
 * 确保 BouncyCastle JCE Provider 在整个 JVM 生命周期中只注册一次，
 * 供 SM2/SM3/SM4 等需要国密算法的工具类复用。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public final class BouncyCastleRegistrar {

    /**
     * 注册状态标记（volatile + 双重检查，保证并发安全）
     */
    private static volatile boolean registered = false;

    /**
     * 工具类不允许实例化
     */
    private BouncyCastleRegistrar() {
        throw new UnsupportedOperationException("BouncyCastleRegistrar 工具类，不允许实例化");
    }

    /**
     * 确保 BouncyCastle Provider 已注册（幂等，多线程安全）
     * <p>
     * 采用双重检查锁定 + volatile 确保并发场景下只注册一次。
     * </p>
     */
    public static void ensureRegistered() {
        if (registered) {
            return;
        }
        synchronized (BouncyCastleRegistrar.class) {
            if (!registered) {
                if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                    Security.addProvider(new BouncyCastleProvider());
                }
                registered = true;
            }
        }
    }
}
