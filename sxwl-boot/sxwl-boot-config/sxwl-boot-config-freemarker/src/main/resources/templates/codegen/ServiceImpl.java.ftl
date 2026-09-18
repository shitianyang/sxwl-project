package ${packageName}.service.impl;

import ${packageName}.mapper.${bizName}Mapper;
import ${packageName}.model.dto.${bizName}DTO;
import ${packageName}.model.entity.${bizName};
import ${packageName}.model.params.${bizName}PageParams;
import ${packageName}.service.${bizName}Service;
import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ${tableComment} Service 实现
 *
 * <p>代码生成器自动创建</p>
 * <p><b>注意：</b>审计字段（createBy, createOrg, createTime, updateBy, updateTime）由 MyBatis <code>SxwlAutoFillInterceptor</code> 自动填充，无需手动设置。</p>
 *
 * @author ${author}
 * @since 0.1.0
 */
@Service
public class ${bizName}ServiceImpl implements ${bizName}Service {

    /** ${bizName} Mapper */
    private final ${bizName}Mapper ${bizNameLower}Mapper;

    public ${bizName}ServiceImpl(${bizName}Mapper ${bizNameLower}Mapper) {
        this.${bizNameLower}Mapper = ${bizNameLower}Mapper;
    }

    /**
     * 分页查询${bizNameCn}列表
     *
     * @param params 分页查询参数（模糊匹配、状态筛选、页码、每页条数）
     * @return 分页结果
     */
    @Override
    public PageInfo<${bizName}DTO> get${bizNamePlural}PageByParams(${bizName}PageParams params) {
        List<${bizName}DTO> rows = ${bizNameLower}Mapper.get${bizNamePlural}PageByParams(params);
        return new PageInfo<>(rows);
    }

    /**
     * 根据 ID 查询${bizNameCn}（编辑回显，密码置空返回）
     *
     * @param id ${bizNameCn} ID
     * @return ${bizNameCn} DTO，查不到返回 null
     */
    @Override
    public ${bizName}DTO get${bizName}ById(Long id) {
        return ${bizNameLower}Mapper.get${bizName}ById(id);
    }

    /**
     * 新增${bizNameCn}
     * <p>包含唯一性校验、审计字段由 SxwlAutoFillInterceptor 自动填充。</p>
     *
     * @param dto ${bizNameCn} DTO
     * @throws RuntimeException 新增失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create${bizName}(${bizName}DTO dto) {
        // 构建实体（审计字段由 SxwlAutoFillInterceptor 自动填充）
        ${bizName} entity = new ${bizName}();
<#list fields as field>
        entity.set${field.javaFieldName?cap_first}(dto.get${field.javaFieldName?cap_first}());
</#list>
        ${bizNameLower}Mapper.insert${bizName}(entity);
    }

    /**
     * 修改${bizNameCn}
     * <p>唯一性校验排除自身，审计字段由 SxwlAutoFillInterceptor 自动填充。</p>
     *
     * @param dto ${bizNameCn} DTO
     * @throws RuntimeException 修改失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update${bizName}(${bizName}DTO dto) {
        // 构建实体（审计字段由 SxwlAutoFillInterceptor 自动填充）
        ${bizName} entity = new ${bizName}();
<#list fields as field>
        entity.set${field.javaFieldName?cap_first}(dto.get${field.javaFieldName?cap_first}());
</#list>
        ${bizNameLower}Mapper.update${bizName}(entity);
    }

    /**
     * 删除${bizNameCn}（逻辑删除，审计字段由 SxwlAutoFillInterceptor 自动填充）
     *
     * @param id ${bizNameCn} ID
     * @throws RuntimeException 删除失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete${bizName}ById(Long id) {
        ${bizNameLower}Mapper.delete${bizName}ById(id);
    }

    /**
     * 批量删除${bizNameCn}（逻辑删除，审计字段由 SxwlAutoFillInterceptor 自动填充）
     *
     * @param ids ${bizNameCn} ID 列表
     * @throws RuntimeException 批量删除失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteByIds(List<Long> ids) {
        ${bizNameLower}Mapper.batchDeleteByIds(ids);
    }
}
