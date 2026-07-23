import request from '@/utils/request';
import type { SxwlPageResult } from '@/types/sxwl';

export interface ${bizName}DTO {
<#list fields as field>
    /** ${field.columnComment} */
    ${field.javaFieldName}?: ${field.javaType?replace('String', 'string')?replace('Long', 'number')?replace('Integer', 'number')?replace('BigDecimal', 'number')?replace('Boolean', 'boolean')?replace('LocalDateTime', 'string')};
</#list>
    createTime?: string;
}

export interface ${bizName}PageParams {
    current: number;
    pageSize: number;
<#list fields as field>
<#if field.isQuery?? && field.isQuery == 1>
    ${field.javaFieldName}?: ${field.javaType?replace('String', 'string')?replace('Long', 'number')?replace('Integer', 'number')?replace('BigDecimal', 'number')?replace('Boolean', 'boolean')?replace('LocalDateTime', 'string')};
</#if>
</#list>
}

/**
 * 分页查询${tableComment}
 */
export function get${bizNamePlural}Page(params: ${bizName}PageParams) {
    return request.get<SxwlPageResult<${bizName}DTO>>('/${modulePrefix}/${bizNameLower}/page', { params });
}

/**
 * 根据 ID 查询${tableComment}
 */
export function get${bizName}ById(id: number) {
    return request.get<${bizName}DTO>('/${modulePrefix}/${bizNameLower}/' + id);
}

/**
 * 新增${tableComment}
 */
export function create${bizName}(data: ${bizName}DTO) {
    return request.post('/${modulePrefix}/${bizNameLower}', data);
}

/**
 * 修改${tableComment}
 */
export function update${bizName}(id: number, data: ${bizName}DTO) {
    return request.put('/${modulePrefix}/${bizNameLower}/' + id, data);
}

/**
 * 删除${tableComment}
 */
export function delete${bizName}(id: number) {
    return request.delete('/${modulePrefix}/${bizNameLower}/' + id);
}
