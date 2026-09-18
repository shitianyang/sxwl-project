package com.sxwl.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.captcha.SxwlCaptchaValidator;
import com.sxwl.security.filter.SxwlIpListFilter;
import com.sxwl.security.handler.SxwlAccessDeniedHandler;
import com.sxwl.security.handler.SxwlAuthenticationEntryPoint;
import com.sxwl.security.handler.SxwlAuthenticationHandler;
import com.sxwl.security.jwt.JwtAuthenticationFilter;
import com.sxwl.security.password.SxwlPasswordEncoder;
import com.sxwl.security.password.SxwlPasswordValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Spring Security 配置
 * <p>
 * 核心配置：
 * <ul>
 *   <li>无状态会话（STATELESS），不使用 Session</li>
 *   <li>JWT Filter 注册在 UsernamePasswordAuthenticationFilter 之前</li>
 *   <li>放行登录/验证码/公开接口，其余均需认证</li>
 *   <li>401/403 处理器返回统一 JSON 格式</li>
 *   <li>放行路径通过 {@code SxwlSecurityProperties.allowedPaths} 配置化管理</li>
 * </ul>
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SxwlSecurityConfig {

    /**
     * Security 过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   SxwlAuthenticationEntryPoint entryPoint,
                                                   SxwlAccessDeniedHandler accessDeniedHandler,
                                                   SxwlIpListFilter ipListFilter,
                                                   SxwlSecurityProperties properties) throws Exception {
        httpSecurity
                // 禁用 CSRF（前后端分离，Token 鉴权不需要 CSRF）
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用表单登录 / HTTP Basic（纯 JWT，不需要 UserDetailsService）
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                // 无状态会话
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 放行规则（从配置类读取）
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(properties.getAllowedPaths().toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                )
                // 401 未认证
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // JWT Filter：必须先把 JwtAuthenticationFilter 加入链，才能拿它作为
                // addFilterBefore 的锚点；否则 FilterComparator 中尚无它的 order，
                // 构建 SecurityFilterChain 时会抛 "does not have a registered order"。
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(ipListFilter, JwtAuthenticationFilter.class);

        return httpSecurity.build();
    }

    // ==================== Bean 注册 ====================

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(SxwlSecurityProperties properties,
                                                           SxwlRedisHelper redisHelper) {
        return new JwtAuthenticationFilter(properties, redisHelper);
    }

    @Bean
    public SxwlAuthenticationEntryPoint sxwlAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new SxwlAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public SxwlAccessDeniedHandler sxwlAccessDeniedHandler(ObjectMapper objectMapper) {
        return new SxwlAccessDeniedHandler(objectMapper);
    }

    @Bean
    public SxwlAuthenticationHandler sxwlAuthenticationHandler(SxwlSecurityProperties properties,
                                                               SxwlRedisHelper redisHelper) {
        return new SxwlAuthenticationHandler(properties, redisHelper);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder sxwlPasswordEncoder() {
        return new SxwlPasswordEncoder();
    }

    @Bean
    public SxwlPasswordValidator sxwlPasswordValidator(SxwlSecurityProperties properties) {
        return new SxwlPasswordValidator(properties);
    }

    @Bean
    public SxwlCaptchaValidator sxwlCaptchaValidator(SxwlRedisHelper redisHelper) {
        return new SxwlCaptchaValidator(redisHelper);
    }

    @Bean
    public SxwlIpListFilter sxwlIpListFilter(ObjectMapper objectMapper, SxwlRedisHelper redisHelper, SxwlSecurityProperties properties) {
        return new SxwlIpListFilter(objectMapper, redisHelper, properties);
    }
}
