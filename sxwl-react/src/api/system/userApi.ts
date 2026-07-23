// ============================================
// 用户管理 API
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

/** 用户列表项 */
export interface UserItem {
  id: number;
  username: string;
  realName: string;
  phone: string;
  email: string;
  status: number;
  createTime: string;
}

/** 用户查询参数 */
export interface UserQuery {
  username?: string;
  status?: number;
  current: number;
  pageSize: number;
}

/** 用户表单（新增/编辑） */
export interface UserForm {
  id?: number;
  username: string;
  realName: string;
  phone: string;
  email: string;
  status: number;
  password?: string;
}

/** 查询用户列表 */
export function getUserPageByParams(params: UserQuery) {
  return http.get<PageInfo<UserItem>>('/system/user/page', params as unknown as Record<string, unknown>);
}

/** 查询用户详情 */
export function getUserById(id: number) {
  return http.get<UserItem>('/system/user/' + id);
}

/** 新增用户 */
export function createUser(data: UserForm) {
  return http.post<null>('/system/user', data);
}

/** 更新用户 */
export function updateUser(data: UserForm) {
  return http.put<null>('/system/user', data);
}

/** 删除用户 */
export function deleteUserById(id: number) {
  return http.deleteReq<null>('/system/user/' + id);
}

/** 批量删除用户 */
export function batchDeleteByIds(ids: number[]) {
  return http.deleteReq<null>('/system/user/batch', undefined, ids);
}
