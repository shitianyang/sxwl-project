package com.sxwl.codegen.service.impl;

import com.sxwl.codegen.mapper.SysCodegenFieldMapper;
import com.sxwl.codegen.mapper.SysCodegenTableMapper;
import com.sxwl.codegen.model.dto.CodegenPreviewDTO;
import com.sxwl.codegen.model.dto.SysCodegenFieldDTO;
import com.sxwl.codegen.model.dto.SysCodegenTableDTO;
import com.sxwl.codegen.service.CodegenService;
import com.sxwl.common.exception.SxwlBusinessException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成引擎 Service 实现
 *
 * <p>读取表配置 → 构建 FreeMarker 数据模型 → 遍历模板渲染 → 打包 ZIP。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Service
public class CodegenServiceImpl implements CodegenService {

    private static final Logger log = LoggerFactory.getLogger(CodegenServiceImpl.class);

    private static final List<String> TEMPLATES = Arrays.asList(
            "Entity.java.ftl", "DTO.java.ftl", "PageParams.java.ftl",
            "Mapper.java.ftl", "Mapper.xml.ftl", "Service.java.ftl",
            "ServiceImpl.java.ftl", "Controller.java.ftl",
            "Api.ts.ftl", "Page.tsx.ftl"
    );

    private final SysCodegenTableMapper sysCodegenTableMapper;
    private final SysCodegenFieldMapper sysCodegenFieldMapper;
    private final Configuration freemarkerConfiguration;

    public CodegenServiceImpl(SysCodegenTableMapper sysCodegenTableMapper,
                               SysCodegenFieldMapper sysCodegenFieldMapper,
                               Configuration freemarkerConfiguration) {
        this.sysCodegenTableMapper = sysCodegenTableMapper;
        this.sysCodegenFieldMapper = sysCodegenFieldMapper;
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    @Override
    public byte[] generateCode(Long tableId) {
        SysCodegenTableDTO table = buildDataModel(tableId);
        return renderToZip(table);
    }

    @Override
    public List<CodegenPreviewDTO> preview(Long tableId) {
        SysCodegenTableDTO table = buildDataModel(tableId);
        List<CodegenPreviewDTO> previews = new ArrayList<>();

        for (String templateName : TEMPLATES) {
            try {
                Template template = freemarkerConfiguration.getTemplate(templateName);
                StringWriter writer = new StringWriter();
                template.process(buildTemplateData(table), writer);
                String content = writer.toString();

                CodegenPreviewDTO dto = new CodegenPreviewDTO();
                dto.setFilePath(resolveFilePath(templateName, table));
                dto.setContent(content.substring(0, Math.min(content.length(), 500)));
                previews.add(dto);
            } catch (Exception e) {
                log.warn("预览模板失败：{}", templateName, e);
            }
        }

        return previews;
    }

    /**
     * 构建数据模型：加载表配置 + 字段列表
     */
    private SysCodegenTableDTO buildDataModel(Long tableId) {
        SysCodegenTableDTO table = sysCodegenTableMapper.getTableById(tableId);
        if (table == null) {
            throw new SxwlBusinessException(10004, "表配置不存在或已被删除");
        }
        List<SysCodegenFieldDTO> fields = sysCodegenFieldMapper.getFieldsByTableId(tableId);
        table.setFields(fields);
        return table;
    }

    /**
     * 构建 FreeMarker 数据模型
     */
    private Map<String, Object> buildTemplateData(SysCodegenTableDTO table) {
        Map<String, Object> data = new HashMap<>();
        data.put("tableName", table.getTableName());
        data.put("bizName", table.getBizName());
        data.put("bizNameLower", table.getBizName().substring(0, 1).toLowerCase() + table.getBizName().substring(1));
        data.put("bizNameCn", table.getBizNameCn());
        data.put("bizNamePlural", table.getBizNamePlural());
        data.put("modulePrefix", table.getModulePrefix());
        data.put("packageName", table.getPackageName());
        data.put("author", table.getAuthor());
        data.put("tableComment", table.getTableComment());
        data.put("genType", table.getGenType());
        data.put("fields", table.getFields());
        return data;
    }

    /**
     * 渲染所有模板并打包 ZIP
     */
    private byte[] renderToZip(SysCodegenTableDTO table) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8)) {
            Map<String, Object> data = buildTemplateData(table);

            for (String templateName : TEMPLATES) {
                try {
                    Template template = freemarkerConfiguration.getTemplate(templateName);
                    StringWriter writer = new StringWriter();
                    template.process(data, writer);

                    String filePath = resolveFilePath(templateName, table);
                    ZipEntry entry = new ZipEntry(filePath);
                    zos.putNextEntry(entry);
                    zos.write(writer.toString().getBytes(StandardCharsets.UTF_8));
                    zos.closeEntry();

                    log.debug("生成文件：{}", filePath);
                } catch (Exception e) {
                    log.error("渲染模板失败：{}", templateName, e);
                }
            }
        } catch (IOException e) {
            throw new SxwlBusinessException(10001, "ZIP 打包失败", e);
        }

        return baos.toByteArray();
    }

    /**
     * 根据模板名解析目标文件路径
     */
    private String resolveFilePath(String templateName, SysCodegenTableDTO table) {
        String basePkg = table.getPackageName().replace('.', '/');
        String name = table.getBizName();
        String nameLower = name.substring(0, 1).toLowerCase() + name.substring(1);
        String basePath = "生成的代码/main/java/" + basePkg;
        String frontendPath = "生成的代码/frontend/src";

        return switch (templateName) {
            case "Entity.java.ftl" -> basePath + "/model/entity/" + name + ".java";
            case "DTO.java.ftl" -> basePath + "/model/dto/" + name + "DTO.java";
            case "PageParams.java.ftl" -> basePath + "/model/params/" + name + "PageParams.java";
            case "Mapper.java.ftl" -> basePath + "/mapper/" + name + "Mapper.java";
            case "Mapper.xml.ftl" -> basePath.replace("/java/", "/resources/") + "/mapper/" + name + "Mapper.xml";
            case "Service.java.ftl" -> basePath + "/service/" + name + "Service.java";
            case "ServiceImpl.java.ftl" -> basePath + "/service/impl/" + name + "ServiceImpl.java";
            case "Controller.java.ftl" -> basePath + "/controller/" + name + "Controller.java";
            case "Api.ts.ftl" -> frontendPath + "/api/" + table.getModulePrefix() + "/" + nameLower + "Api.ts";
            case "Page.tsx.ftl" -> frontendPath + "/pages/" + table.getModulePrefix() + "/" + name + "/index.tsx";
            default -> "unknown/" + templateName.replace(".ftl", "");
        };
    }
}
