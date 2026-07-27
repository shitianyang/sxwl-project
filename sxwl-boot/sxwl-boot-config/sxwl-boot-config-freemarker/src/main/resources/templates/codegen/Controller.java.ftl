package ${packageName}.controller;

import ${packageName}.model.dto.${bizName}DTO;
import ${packageName}.model.params.${bizName}PageParams;
import ${packageName}.service.${bizName}Service;
import com.github.pagehelper.PageInfo;
import com.sxwl.common.entity.SxwlResult;
import com.sxwl.common.annotation.SxwlLog;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${tableComment} 控制器
 *
 * <p>代码生成器自动创建</p>
 *
 * @author ${author}
 * @since 0.1.0
 */
@RestController
@RequestMapping("/${modulePrefix}/${bizNameLower}")
public class ${bizName}Controller {

    private final ${bizName}Service ${bizNameLower}Service;

    public ${bizName}Controller(${bizName}Service ${bizNameLower}Service) {
        this.${bizNameLower}Service = ${bizNameLower}Service;
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('${modulePrefix}:${bizNameLower}:list')")
    public SxwlResult<PageInfo<${bizName}DTO>> page(${bizName}PageParams params) {
        return SxwlResult.success(${bizNameLower}Service.get${bizNamePlural}PageByParams(params));
    }

    /**
     * 根据 ID 查询
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('${modulePrefix}:${bizNameLower}:query')")
    public SxwlResult<${bizName}DTO> getById(@PathVariable Long id) {
        return SxwlResult.success(${bizNameLower}Service.get${bizName}ById(id));
    }

    /**
     * 新增
     */
    @PostMapping
    @SxwlLog(title = "${tableComment}", description = "新增${bizNameCn}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('${modulePrefix}:${bizNameLower}:add')")
    public SxwlResult<Void> create(@RequestBody ${bizName}DTO dto) {
        ${bizNameLower}Service.create${bizName}(dto);
        return SxwlResult.success();
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    @SxwlLog(title = "${tableComment}", description = "编辑${bizNameCn}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('${modulePrefix}:${bizNameLower}:edit')")
    public SxwlResult<Void> update(@PathVariable Long id, @RequestBody ${bizName}DTO dto) {
        dto.setId(id);
        ${bizNameLower}Service.update${bizName}(dto);
        return SxwlResult.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    @SxwlLog(title = "${tableComment}", description = "删除${bizNameCn}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('${modulePrefix}:${bizNameLower}:delete')")
    public SxwlResult<Void> delete(@PathVariable Long id) {
        ${bizNameLower}Service.delete${bizName}ById(id);
        return SxwlResult.success();
    }
}
