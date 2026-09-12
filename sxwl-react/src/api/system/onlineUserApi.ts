// ============================================
// 在线用户管理 API
// ============================================

import { http } from '@/api/http';

/** 在线用户列表项 */
export interface OnlineUserItem {
  /** 用户 ID */
  userId: number;
  /** 用户名 */
  username: string;
  /** 登录 IP */
  ip: string;
  /** 浏览器 */
  browser: string;
  /** 操作系统 */
  os: string;
  /** 设备 ID */
  deviceId: string;
  /** 登录时间 */
  loginTime: string;
}

/** 分页响应 */
export interface PageInfo<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

/** 分页查询在线用户列表 */
export function getOnlineUserList(pageNum: number = 1, pageSize: number = 10) {
  return http.get<PageInfo<OnlineUserItem>>('/sys/online-user/list', { pageNum, pageSize });
}

/** 获取在线用户总数 */
export function getOnlineUserCount() {
  return http.get<number>('/sys/online-user/count');
}

/** 强制踢人下线 */
export function forceLogout(userId: number) {
  return http.deleteReq<null>('/sys/online-user/forceLogout/' + userId);
}
