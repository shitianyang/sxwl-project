// ============================================
// 代码生成 API
// ============================================

import { http } from '@/api/http';

// ==================== 类型定义 ====================

/** 代码生成表配置项 */
export interface CodegenTableItem {
  id: number;
  tableName: string;
  modulePrefix: string;
  bizName: string;
  bizNameCn: string;
  bizNamePlural: string;
  tableComment: string;
  packageName: string;
  author: string;
  genType: string;
  status: number;
  createTime: string;
  fields?: CodegenFieldItem[];
}

/** 字段配置项 */
export interface CodegenFieldItem {
  id?: number;
  tableId?: number;
  columnName: string;
  columnType: string;
  columnComment: string;
  javaType: string;
  javaFieldName: string;
  isPk: number;
  isInsert: number;
  isEdit: number;
  isList: number;
  isQuery: number;
  queryType: string;
  queryFormType: string;
  formType: string;
  formDictCode: string;
  isRequired: number;
  isUnique: number;
  maxLength: number;
  sort: number;
  createTime?: string;
}

/** 表配置表单（新增/编辑时传入） */
export interface CodegenConfigForm {
  tableName: string;
  modulePrefix: string;
  bizName: string;
  bizNameCn: string;
  bizNamePlural?: string;
  tableComment?: string;
  packageName: string;
  author: string;
  genType: string;
}

/** 表配置查询参数 */
export interface CodegenTableQuery {
  tableName?: string;
  bizNameCn?: string;
  status?: number;
  current: number;
  pageSize: number;
}

/** 分页响应 */
export interface PageInfo<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

/** 预览项 */
export interface CodegenPreviewItem {
  filePath: string;
  content: string;
}

// ==================== CRUD ====================

/** 分页查询表配置 */
export function getCodegenTablePage(params: CodegenTableQuery) {
  return http.get<PageInfo<CodegenTableItem>>('/codegen/table/page', params as unknown as Record<string, unknown>);
}

/** 获取表配置详情（含字段列表） */
export function getCodegenTableById(id: number) {
  return http.get<CodegenTableItem>('/codegen/table/' + id);
}

/** 新增表配置 */
export function createCodegenTable(data: CodegenConfigForm) {
  return http.post<CodegenTableItem>('/codegen/table', data);
}

/** 更新表配置 */
export function updateCodegenTable(id: number, data: CodegenConfigForm) {
  return http.put<void>('/codegen/table/' + id, data);
}

/** 删除表配置（级联删除字段） */
export function deleteCodegenTable(id: number) {
  return http.deleteReq<void>('/codegen/table/' + id);
}

/** 保存字段配置 */
export function saveCodegenFields(id: number, fields: CodegenFieldItem[]) {
  return http.put<void>('/codegen/table/' + id + '/fields', fields);
}

/** 预览代码生成 */
export function previewCodegen(tableId: number) {
  return http.get<CodegenPreviewItem[]>('/codegen/preview/' + tableId);
}

/** 生成并下载代码 ZIP */
export function downloadCodegen(tableId: number) {
  return http.downloadPost('/codegen/generate/' + tableId);
}
