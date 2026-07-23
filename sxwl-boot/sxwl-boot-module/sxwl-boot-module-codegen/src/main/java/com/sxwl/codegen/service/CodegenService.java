package com.sxwl.codegen.service;

import com.sxwl.codegen.model.dto.CodegenPreviewDTO;
import com.sxwl.codegen.model.dto.SysCodegenTableDTO;

import java.util.List;

/**
 * 代码生成引擎 Service
 *
 * <p>读取表配置 + 字段配置 → FreeMarker 渲染 → ZIP 打包。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public interface CodegenService {

    /**
     * 生成代码 → 返回 ZIP 字节数组
     */
    byte[] generateCode(Long tableId);

    /**
     * 预览将生成的文件列表
     */
    List<CodegenPreviewDTO> preview(Long tableId);
}
