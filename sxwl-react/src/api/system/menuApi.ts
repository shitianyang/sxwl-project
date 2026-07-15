// ============================================
// 菜单管理 API
// ============================================

import { http } from '@/api/http';

/** 菜单树节点 */
export interface MenuTreeItem {
  id: number;
  menuName: string;
  parentId: number;
  ancestors: string;
  menuType: number;
  path?: string;
  component?: string;
  perms?: string;
  icon?: string;
  isFrame: number;
  isCache: number;
  sort: number;
  visible: number;
  status: number;
  description?: string;
  children?: MenuTreeItem[];
}

/** 菜单表单（新增/编辑） */
export interface MenuForm {
  id?: number;
  menuName: string;
  parentId?: number;
  ancestors?: string;
  menuType: number;
  path?: string;
  component?: string;
  perms?: string;
  icon?: string;
  isFrame?: number;
  isCache?: number;
  sort?: number;
  visible?: number;
  status?: number;
  description?: string;
}

/** 查询菜单树 */
export function getMenuTree() {
  return http.get<MenuTreeItem[]>('/system/menu/tree');
}

/** 查询所有菜单（平铺） */
export function getAllMenuList() {
  return http.get<MenuTreeItem[]>('/system/menu/all');
}

/** 查询菜单详情 */
export function getMenuById(id: number) {
  return http.get<MenuTreeItem>('/system/menu/' + id);
}

/** 新增菜单 */
export function createMenu(data: MenuForm) {
  return http.post<null>('/system/menu', data);
}

/** 修改菜单 */
export function updateMenu(data: MenuForm) {
  return http.put<null>('/system/menu', data);
}

/** 删除菜单 */
export function deleteMenuById(id: number) {
  return http.deleteReq<null>('/system/menu/' + id);
}
