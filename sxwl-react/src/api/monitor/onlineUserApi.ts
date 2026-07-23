// ============================================
// 在线用户管理 API
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

/** 在线用户 DTO */
export interface SysOnlineUserItem {
  userId: number;
  username: string;
  ip: string;
  browser: string;
  os: string;
  deviceId: string;
  loginTime: string;
}

/** 分页查询在线用户列表 */
export function getOnlineUserList(pageNum: number, pageSize: number) {
  return http.get<PageInfo<SysOnlineUserItem>>('/sys/online-user/list', { pageNum, pageSize } as unknown as Record<string, unknown>);
}

/** 获取在线用户总数 */
export function getOnlineUserCount() {
  return http.get<number>('/sys/online-user/count');
}

/** 强制踢人下线 */
export function forceLogout(userId: number) {
  return http.deleteReq<null>('/sys/online-user/forceLogout/' + userId);
}
