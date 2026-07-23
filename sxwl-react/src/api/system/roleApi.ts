// ============================================
// 角色管理 API
// ============================================

import { http } from '@/api/http';

/** 角色列表项 */
export interface RoleItem {
  id: number;
  roleCode: string;
  roleName: string;
  dataScope: number;
  sort: number;
  status: number;
  description: string;
  createTime: string;
}

/** 角色表单 */
export interface RoleForm {
  id?: number;
  roleCode: string;
  roleName: string;
  dataScope: number;
  sort: number;
  status: number;
  description?: string;
}

/** 角色查询参数 */
export interface RoleQuery {
  roleCode?: string;
  roleName?: string;
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

// ==================== CRUD ====================

/** 查询角色列表 */
export function getRolePageByParams(params: RoleQuery) {
  return http.get<PageInfo<RoleItem>>('/system/role/page', params as unknown as Record<string, unknown>);
}

/** 查询角色详情 */
export function getRoleById(id: number) {
  return http.get<RoleItem>('/system/role/' + id);
}

/** 新增角色 */
export function createRole(data: RoleForm) {
  return http.post<null>('/system/role', data);
}

/** 修改角色 */
export function updateRole(data: RoleForm) {
  return http.put<null>('/system/role', data);
}

/** 删除角色 */
export function deleteRoleById(id: number) {
  return http.deleteReq<null>('/system/role/' + id);
}

// ==================== 菜单分配 ====================

/** 保存角色的菜单分配 */
export function saveRoleMenus(roleId: number, menuIds: number[]) {
  return http.post<null>('/system/role/' + roleId + '/menus', { menuIds });
}

/** 查询角色已分配的菜单 ID 列表 */
export function getMenuIdListByRoleId(roleId: number) {
  return http.get<number[]>('/system/role/' + roleId + '/menus');
}

// ==================== 数据权限 ====================

/** 保存角色的数据权限 */
export function saveRoleDataScope(roleId: number, orgIds: number[]) {
  return http.post<null>('/system/role/' + roleId + '/data-scope', { orgIds });
}

/** 查询角色已授权的组织 ID 列表 */
export function getDataScopeOrgIdListByRoleId(roleId: number) {
  return http.get<number[]>('/system/role/' + roleId + '/data-scope');
}
