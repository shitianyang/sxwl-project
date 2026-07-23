package com.sxwl.codegen.model.dto;

/**
 * 代码生成-预览 DTO
 *
 * <p>描述待生成的一个文件，供前端预览。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class CodegenPreviewDTO {

    /** 文件路径，如 main/java/com/sxwl/system/controller/SysRoleController.java */
    private String filePath;

    /** 文件内容预览（截取前 500 字符） */
    private String content;

    public String getFilePath() { return filePath; }

    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }
}
