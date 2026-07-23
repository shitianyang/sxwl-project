// ============================================
// 组织管理 API
// ============================================

import { http } from '@/api/http';

/** 组织树节点 */
export interface OrganizationTreeItem {
  id: number;
  orgCode: string;
  orgName: string;
  parentId: number;
  ancestors: string;
  orgLevel: number;
  orgType?: string;
  leaderId?: number;
  phone?: string;
  sort: number;
  status: number;
  description?: string;
  children?: OrganizationTreeItem[];
}

/** 组织表单（新增/编辑） */
export interface OrganizationForm {
  id?: number;
  orgCode: string;
  orgName: string;
  parentId?: number;
  ancestors?: string;
  orgLevel: number;
  orgType?: string;
  leaderId?: number;
  phone?: string;
  sort?: number;
  status?: number;
  description?: string;
}

/** 查询组织树 */
export function getOrganizationTree() {
  return http.get<OrganizationTreeItem[]>('/system/organization/tree');
}

/** 查询所有组织（平铺） */
export function getAllOrganizationList() {
  return http.get<OrganizationTreeItem[]>('/system/organization/all');
}

/** 查询组织详情 */
export function getOrganizationById(id: number) {
  return http.get<OrganizationTreeItem>('/system/organization/' + id);
}

/** 新增组织 */
export function createOrganization(data: OrganizationForm) {
  return http.post<null>('/system/organization', data);
}

/** 修改组织 */
export function updateOrganization(data: OrganizationForm) {
  return http.put<null>('/system/organization', data);
}

/** 删除组织 */
export function deleteOrganizationById(id: number) {
  return http.deleteReq<null>('/system/organization/' + id);
}
