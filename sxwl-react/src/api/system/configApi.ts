// ============================================
// 参数配置 API
// ============================================

import { http } from '@/api/http';

/** PageInfo 分页响应（与后端 com.github.pagehelper.PageInfo 对齐） */
export interface PageInfo<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

/** 参数配置 DTO */
export interface SysConfigItem {
  id: number;
  configKey: string;
  configName: string;
  configValue: string;
  configType: string;
  description?: string;
  status: number;
  createTime: string;
}

/** 参数配置查询参数 */
export interface SysConfigQuery {
  configKey?: string;
  configName?: string;
  configType?: string;
  status?: number;
  current: number;
  pageSize: number;
}

/** 参数配置表单 */
export interface SysConfigForm {
  id?: number;
  configKey: string;
  configName: string;
  configValue: string;
  configType: string;
  description?: string;
  status: number;
}

/** 查询配置详情 */
export function getConfigById(id: number) {
  return http.get<SysConfigItem>('/system/config/' + id);
}

/** 根据键名查询配置 */
export function getConfigByKey(configKey: string) {
  return http.get<SysConfigItem>('/system/config/key/' + configKey);
}

/** 分页查询配置列表 */
export function getConfigPageByParams(params: SysConfigQuery) {
  return http.get<PageInfo<SysConfigItem>>('/system/config/page', params as unknown as Record<string, unknown>);
}

/** 新增配置 */
export function createConfig(data: SysConfigForm) {
  return http.post<null>('/system/config', data);
}

/** 修改配置 */
export function updateConfig(data: SysConfigForm) {
  return http.put<null>('/system/config', data);
}

/** 删除配置 */
export function deleteConfigById(id: number) {
  return http.deleteReq<null>('/system/config/' + id);
}
