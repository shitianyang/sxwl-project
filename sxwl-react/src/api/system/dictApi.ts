// ============================================
// 字典管理 API
// ============================================

import { http } from '@/api/http';

/** 字典列表项 */
export interface DictItem {
  id: number;
  dictCode: string;
  dictName: string;
  description: string;
  status: number;
  createTime: string;
}

/** 字典表单 */
export interface DictForm {
  id?: number;
  dictCode: string;
  dictName: string;
  description?: string;
  status: number;
}

/** 字典明细项 */
export interface DictDetailItem {
  id: number;
  dictId: number;
  detailValue: string;
  detailLabel: string;
  description: string;
  sort: number;
  status: number;
  isDefault: number;
}

/** 字典明细表单 */
export interface DictDetailForm {
  id?: number;
  dictId: number;
  detailValue: string;
  detailLabel: string;
  description?: string;
  sort: number;
  status: number;
  isDefault: number;
}

/** 字典查询参数 */
export interface DictQuery {
  dictCode?: string;
  dictName?: string;
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

// ==================== 字典主表 ====================

/** 查询字典列表 */
export function getDictPageByParams(params: DictQuery) {
  return http.get<PageInfo<DictItem>>('/system/dict/page', params as unknown as Record<string, unknown>);
}

/** 查询字典详情 */
export function getDictById(id: number) {
  return http.get<DictItem>('/system/dict/' + id);
}

/** 新增字典 */
export function createDict(data: DictForm) {
  return http.post<null>('/system/dict', data);
}

/** 修改字典 */
export function updateDict(data: DictForm) {
  return http.put<null>('/system/dict', data);
}

/** 删除字典 */
export function deleteDictById(id: number) {
  return http.deleteReq<null>('/system/dict/' + id);
}

// ==================== 字典明细 ====================

/** 查询字典明细列表（不分页） */
export function getDetailListByDictId(dictId: number) {
  return http.get<DictDetailItem[]>('/system/dict/' + dictId + '/details');
}

/** 新增字典明细 */
export function createDetail(data: DictDetailForm) {
  return http.post<null>('/system/dict/details', data);
}

/** 修改字典明细 */
export function updateDetail(data: DictDetailForm) {
  return http.put<null>('/system/dict/details', data);
}

/** 删除字典明细 */
export function deleteDetailById(id: number) {
  return http.deleteReq<null>('/system/dict/details/' + id);
}
