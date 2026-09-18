package com.sxwl.websocket.config;

import com.sxwl.websocket.handler.SxwlWebSocketHandler;
import com.sxwl.websocket.interceptor.SxwlWebSocketInterceptor;
import com.sxwl.websocket.manager.SxwlWebSocketSessionManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * WebSocket 自动配置类
 *
 * <p>提供基础设施 Bean，注册 WebSocket Session 管理器、消息处理器和握手拦截器。
 * 所有 WebSocket 相关属性均已预设合理默认值，无需额外配置。</p>
 *
 * <h3>设计原则</h3>
 * <ul>
 *   <li><b>最小化配置</b>：仅暴露必需的基础配置（零配置即可运行）</li>
 *   <li><b>零业务逻辑</b>：只提供工具类 Bean，不包含任何 Controller 或业务处理</li>
 * </ul>
 *
 * <h3>提供的 Bean</h3>
 * <table border="1">
 *   <tr>
 *     <th>Bean 名称</th>
 *     <th>功能</th>
 *     <th>使用场景</th>
 *   </tr>
 *   <tr>
 *     <td>{@code sxwlWebSocketSessionManager}</td>
 *     <td>连接管理</td>
 *     <td>按用户推送、全量广播、获取在线数</td>
 *   </tr>
 *   <tr>
 *     <td>{@code sxwlWebSocketHandler}</td>
 *     <td>消息处理</td>
 *     <td>心跳检测、通道订阅/取消</td>
 *   </tr>
 *   <tr>
 *     <td>{@code sxwlWebSocketInterceptor}</td>
 *     <td>安全验证</td>
 *     <td>WebSocket 握手阶段验证连接票据</td>
 *   </tr>
 * </table>
 *
 * <h3>架构定位</h3>
 * <p>属于<strong>基础设施层</strong>，为业务模块提供通用的 WebSocket 通信能力。具体业务逻辑（如通知推送、消息转发等）由 {@code module-*} 实现。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@AutoConfiguration
public class SxwlWebSocketAutoConfiguration {

    /**
     * 注册 WebSocket Session 管理器（单例）
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlWebSocketSessionManager sxwlWebSocketSessionManager() {
        return new SxwlWebSocketSessionManager();
    }

    /**
     * 注册 WebSocket 消息处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlWebSocketHandler sxwlWebSocketHandler(SxwlWebSocketSessionManager sessionManager) {
        return new SxwlWebSocketHandler(sessionManager);
    }

    /**
     * 注册 WebSocket 握手拦截器（默认空实现，业务模块可根据需要自定义）
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlWebSocketInterceptor sxwlWebSocketInterceptor() {
        return new SxwlWebSocketInterceptor();
    }
}
