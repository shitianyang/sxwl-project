// ============================================
// 用户管理 API
// ============================================

import { http, PageResult } from '@/api/http';

/** 用户列表项 */
export interface UserItem {
  userId: number;
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
  page: number;
  pageSize: number;
}

/** 用户表单（新增/编辑） */
export interface UserForm {
  username: string;
  realName: string;
  phone: string;
  email: string;
  status: number;
  password?: string;
}

/** 查询用户列表 */
export function getUserList(params: UserQuery) {
  return http.get<PageResult<UserItem>>('/system/user/list', params as unknown as Record<string, unknown>);
}

/** 新增用户 */
export function createUser(data: UserForm) {
  return http.post<null>('/system/user/create', data);
}

/** 更新用户 */
export function updateUser(data: UserForm & { userId: number }) {
  return http.put<null>('/system/user/update', data);
}

/** 删除用户 */
export function deleteUser(userId: number) {
  return http.deleteReq<null>('/system/user/delete', { userId });
}
