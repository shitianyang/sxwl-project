// ============================================
// 仪表盘 API
// ============================================

import { http } from '@/api/http';

/** 首页统计数据 */
export interface DashboardStatistics {
  userCount: number;
  roleCount: number;
  menuCount: number;
  todayLogCount: number;
}

/** 获取首页统计数据 */
export function getDashboardStatistics() {
  return http.get<DashboardStatistics>('/system/dashboard/statistics');
}
