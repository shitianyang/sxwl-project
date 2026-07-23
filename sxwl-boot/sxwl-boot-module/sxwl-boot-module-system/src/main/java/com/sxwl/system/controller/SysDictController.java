package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.system.model.dto.SysDictDTO;
import com.sxwl.system.model.dto.SysDictDetailDTO;
import com.sxwl.system.model.params.SysDictPageParams;
import com.sxwl.system.service.SysDictService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统字典 Controller
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/dict")
public class SysDictController {

    private final SysDictService sysDictService;

    public SysDictController(SysDictService sysDictService) {
        this.sysDictService = sysDictService;
    }

    // ==================== 字典主表 ====================

    /**
     * 根据 ID 查询字典（编辑回显）
     *
     * @param id 字典 ID
     * @return 字典信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dict:query')")
    @SxwlLog(title = "字典管理", description = "查询字典详情[id=#{#id}]")
    public SysDictDTO getDictById(@PathVariable("id") Long id) {
        return sysDictService.getDictById(id);
    }

    /**
     * 分页查询字典列表
     *
     * @param params 分页查询参数
     * @return 分页字典列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dict:list')")
    @SxwlLog(title = "字典管理", description = "查询字典列表")
    public PageInfo<SysDictDTO> getDictPageByParams(@Valid SysDictPageParams params) {
        return sysDictService.getDictPageByParams(params);
    }

    /**
     * 新增字典
     *
     * @param dto 字典信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dict:add')")
    @SxwlLog(title = "字典管理", description = "新增字典[#{#dto.dictCode}]")
    public void createDict(@Valid @RequestBody SysDictDTO dto) {
        sysDictService.createDict(dto);
    }

    /**
     * 修改字典
     *
     * @param dto 字典信息（含 id）
     */
    @PutMapping
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dict:edit')")
    @SxwlLog(title = "字典管理", description = "修改字典[#{#dto.dictCode}]")
    public void updateDict(@Valid @RequestBody SysDictDTO dto) {
        sysDictService.updateDict(dto);
    }

    /**
     * 删除字典
     *
     * @param id 字典 ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dict:delete')")
    @SxwlLog(title = "字典管理", description = "删除字典[id=#{#id}]")
    public void deleteDictById(@PathVariable("id") Long id) {
        sysDictService.deleteDictById(id);
    }

    // ==================== 字典明细 ====================

    /**
     * 根据字典 ID 查询所有明细
     *
     * @param dictId 字典 ID
     * @return 明细列表
     */
    @GetMapping("/{dictId}/details")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dict:query')")
    @SxwlLog(title = "字典管理", description = "查询字典明细[dictId=#{#dictId}]")
    public List<SysDictDetailDTO> getDetailListByDictId(@PathVariable("dictId") Long dictId) {
        return sysDictService.getDetailListByDictId(dictId);
    }

    /**
     * 新增字典明细
     *
     * @param dto 明细信息
     */
    @PostMapping("/details")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dict:add')")
    @SxwlLog(title = "字典管理", description = "新增字典明细[#{#dto.detailValue}]")
    public void createDetail(@Valid @RequestBody SysDictDetailDTO dto) {
        sysDictService.createDetail(dto);
    }

    /**
     * 修改字典明细
     *
     * @param dto 明细信息（含 id）
     */
    @PutMapping("/details")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dict:edit')")
    @SxwlLog(title = "字典管理", description = "修改字典明细[id=#{#dto.id}]")
    public void updateDetail(@Valid @RequestBody SysDictDetailDTO dto) {
        sysDictService.updateDetail(dto);
    }

    /**
     * 删除字典明细
     *
     * @param id 明细 ID
     */
    @DeleteMapping("/details/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:dict:delete')")
    @SxwlLog(title = "字典管理", description = "删除字典明细[id=#{#id}]")
    public void deleteDetailById(@PathVariable("id") Long id) {
        sysDictService.deleteDetailById(id);
    }
}
