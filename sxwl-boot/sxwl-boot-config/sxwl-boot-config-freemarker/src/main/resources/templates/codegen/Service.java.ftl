package ${packageName}.service;

import ${packageName}.model.dto.${bizName}DTO;
import ${packageName}.model.params.${bizName}PageParams;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * ${tableComment} Service 接口
 *
 * <p>代码生成器自动创建</p>
 *
 * @author ${author}
 * @since 0.1.0
 */
public interface ${bizName}Service {

    /**
     * 分页查询列表
     */
    PageInfo<${bizName}DTO> get${bizNamePlural}PageByParams(${bizName}PageParams params);

    /**
     * 根据 ID 查询
     */
    ${bizName}DTO get${bizName}ById(Long id);

    /**
     * 新增
     */
    void create${bizName}(${bizName}DTO dto);

    /**
     * 修改
     */
    void update${bizName}(${bizName}DTO dto);

    /**
     * 删除
     */
    void delete${bizName}ById(Long id);
}
