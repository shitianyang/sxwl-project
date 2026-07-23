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

import java.time.LocalDateTime;
import java.util.List;

/**
 * ${tableComment} Service 实现
 *
 * <p>代码生成器自动创建</p>
 *
 * @author ${author}
 * @since 0.1.0
 */
@Service
public class ${bizName}ServiceImpl implements ${bizName}Service {

    private final ${bizName}Mapper ${bizNameLower}Mapper;

    public ${bizName}ServiceImpl(${bizName}Mapper ${bizNameLower}Mapper) {
        this.${bizNameLower}Mapper = ${bizNameLower}Mapper;
    }

    @Override
    public PageInfo<${bizName}DTO> get${bizNamePlural}PageByParams(${bizName}PageParams params) {
        List<${bizName}DTO> rows = ${bizNameLower}Mapper.get${bizNamePlural}PageByParams(params);
        return new PageInfo<>(rows);
    }

    @Override
    public ${bizName}DTO get${bizName}ById(Long id) {
        return ${bizNameLower}Mapper.get${bizName}ById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create${bizName}(${bizName}DTO dto) {
        // 校验必填
        // TODO: 根据字段配置校验必填项
        // 保存
        ${bizName} entity = new ${bizName}();
        // TODO: DTO → Entity 转换
        ${bizNameLower}Mapper.insert${bizName}(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update${bizName}(${bizName}DTO dto) {
        ${bizName} entity = new ${bizName}();
        // TODO: DTO → Entity 转换
        ${bizNameLower}Mapper.update${bizName}(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete${bizName}ById(Long id) {
        ${bizNameLower}Mapper.delete${bizName}ById(id);
    }
}
