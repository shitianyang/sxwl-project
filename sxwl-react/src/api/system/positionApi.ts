// ============================================
// 岗位管理 API
// ============================================

import { http } from '@/api/http';

/** 岗位列表项 */
export interface PositionItem {
  id: number;
  positionCode: string;
  positionName: string;
  sort: number;
  status: number;
  description: string;
  createTime: string;
}

/** 岗位表单（新增/编辑） */
export interface PositionForm {
  id?: number;
  positionCode: string;
  positionName: string;
  sort: number;
  status: number;
  description?: string;
}

/** 岗位查询参数 */
export interface PositionQuery {
  positionCode?: string;
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

/** 查询岗位列表 */
export function getPositionPageByParams(params: PositionQuery) {
  return http.get<PageInfo<PositionItem>>('/system/position/page', params as unknown as Record<string, unknown>);
}

/** 查询岗位详情 */
export function getPositionById(id: number) {
  return http.get<PositionItem>('/system/position/' + id);
}

/** 新增岗位 */
export function createPosition(data: PositionForm) {
  return http.post<null>('/system/position', data);
}

/** 修改岗位 */
export function updatePosition(data: PositionForm) {
  return http.put<null>('/system/position', data);
}

/** 删除岗位 */
export function deletePositionById(id: number) {
  return http.deleteReq<null>('/system/position/' + id);
}

/** 批量删除岗位 */
export function batchDeletePositionByIds(ids: number[]) {
  return http.deleteReq<null>('/system/position/batch', undefined, ids);
}
