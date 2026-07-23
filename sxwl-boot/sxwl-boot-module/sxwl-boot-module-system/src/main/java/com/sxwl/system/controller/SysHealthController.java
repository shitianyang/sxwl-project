package com.sxwl.system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查 Controller
 *
 * <p>简单的存活检查，只返回 200，不暴露任何系统细节。
 * 在安全配置中放行此路径，无需认证。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
public class SysHealthController {

    /**
     * 健康检查
     *
     * @return 200 OK，无响应体
     */
    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }
}
