package ${packageName}.controller;

import ${packageName}.model.dto.${bizName}DTO;
import ${packageName}.model.params.${bizName}PageParams;
import ${packageName}.service.${bizName}Service;
import com.github.pagehelper.PageInfo;
import com.sxwl.common.entity.SxwlResult;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.common.constant.SxwlPermConstant;
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
     *
     * @param params 分页查询参数（模糊匹配、状态筛选、页码、每页条数）
     * @return 分页列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.${modulePrefix?upper_case}.${bizNameLower}_LIST + ")")
    @SxwlLog(title = "${tableComment}", description = "查询${bizNameCn}列表")
    public SxwlResult<PageInfo<${bizName}DTO>> page(${bizName}PageParams params) {
        return SxwlResult.success(${bizNameLower}Service.get${bizNamePlural}PageByParams(params));
    }

    /**
     * 根据 ID 查询（编辑回显）
     *
     * @param id ${bizNameCn} ID
     * @return ${bizNameCn}信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.${modulePrefix?upper_case}.${bizNameLower}_QUERY + ")")
    @SxwlLog(title = "${tableComment}", description = "查询${bizNameCn}详情[id=#{#id}]")
    public SxwlResult<${bizName}DTO> getById(@PathVariable("id") Long id) {
        return SxwlResult.success(${bizNameLower}Service.get${bizName}ById(id));
    }

    /**
     * 新增${bizNameCn}
     *
     * @param dto ${bizNameCn}信息（含所有必填字段）
     * @return 无数据成功响应
     */
    @PostMapping
    @SxwlRepeatSubmit(interval = 3, message = "${bizNameCn}创建中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.${modulePrefix?upper_case}.${bizNameLower}_ADD + ")")
    @SxwlLog(title = "${tableComment}", description = "新增${bizNameCn}[#{#dto.username}]")
    public SxwlResult<Void> create(@RequestBody ${bizName}DTO dto) {
        ${bizNameLower}Service.create${bizName}(dto);
        return SxwlResult.success();
    }

    /**
     * 修改${bizNameCn}
     *
     * @param id ${bizNameCn} ID
     * @param dto ${bizNameCn}信息（含 id，部分字段可选）
     * @return 无数据成功响应
     */
    @PutMapping
    @SxwlRepeatSubmit(interval = 3, message = "${bizNameCn}修改中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.${modulePrefix?upper_case}.${bizNameLower}_EDIT + ")")
    @SxwlLog(title = "${tableComment}", description = "修改${bizNameCn}[#{#dto.id}]")
    public SxwlResult<Void> update(@PathVariable("id") Long id, @RequestBody ${bizName}DTO dto) {
        dto.setId(id);
        ${bizNameLower}Service.update${bizName}(dto);
        return SxwlResult.success();
    }

    /**
     * 删除${bizNameCn}
     *
     * @param id ${bizNameCn} ID
     * @return 无数据成功响应
     */
    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit(interval = 3, message = "${bizNameCn}删除中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.${modulePrefix?upper_case}.${bizNameLower}_DELETE + ")")
    @SxwlLog(title = "${tableComment}", description = "删除${bizNameCn}[id=#{#id}]")
    public SxwlResult<Void> delete(@PathVariable("id") Long id) {
        ${bizNameLower}Service.delete${bizName}ById(id);
        return SxwlResult.success();
    }

    /**
     * 批量删除${bizNameCn}
     *
     * @param ids ${bizNameCn} ID 列表
     * @return 无数据成功响应
     */
    @DeleteMapping("/batch")
    @SxwlRepeatSubmit(interval = 60, message = "批量删除${bizNameCn}中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.${modulePrefix?upper_case}.${bizNameLower}_DELETE + ")")
    @SxwlLog(title = "${tableComment}", description = "批量删除${bizNameCn}[ids=#{#ids}]")
    public SxwlResult<Void> batchDelete(@RequestBody List<Long> ids) {
        ${bizNameLower}Service.batchDeleteByIds(ids);
        return SxwlResult.success();
    }
}
