package com.sxwl.codegen.controller;

import com.sxwl.codegen.model.dto.CodegenPreviewDTO;
import com.sxwl.codegen.service.CodegenService;
import com.sxwl.common.entity.SxwlResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码生成控制器
 *
 * <p>提供代码生成和预览 API，输出 ZIP 下载。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@RestController
@RequestMapping("/codegen")
public class CodegenController {

    private final CodegenService codegenService;

    public CodegenController(CodegenService codegenService) {
        this.codegenService = codegenService;
    }

    /**
     * 生成代码 → 返回 ZIP 下载
     */
    @PostMapping("/generate/{tableId}")
    public ResponseEntity<byte[]> generate(@PathVariable Long tableId) {
        byte[] zipBytes = codegenService.generateCode(tableId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "generated-code.zip");
        headers.setContentLength(zipBytes.length);

        return ResponseEntity.ok().headers(headers).body(zipBytes);
    }

    /**
     * 预览将生成的文件列表
     */
    @GetMapping("/preview/{tableId}")
    public SxwlResult<List<CodegenPreviewDTO>> preview(@PathVariable Long tableId) {
        return SxwlResult.success(codegenService.preview(tableId));
    }
}
