// ============================================
// 后端 component 路径 → 前端组件映射表
// 用于动态路由构建（menuStore 加载菜单树后使用）
// ============================================

import { lazy } from 'react';

/** 组件映射表：key 为后端 sys_menu 表中的 component 字段值 */
const RouteMap: Record<string, React.LazyExoticComponent<React.ComponentType>> = {
  'system/user/index': lazy(() => import('@/pages/System/User')),
  // 后续页面按需添加
  // 'system/role/index': lazy(() => import('@/pages/System/Role')),
  // 'system/menu/index': lazy(() => import('@/pages/System/Menu')),
  // 'system/organization/index': lazy(() => import('@/pages/System/Organization')),
  // 'system/position/index': lazy(() => import('@/pages/System/Position')),
  // 'system/dict/index': lazy(() => import('@/pages/System/Dict')),
  // 'system/log/index': lazy(() => import('@/pages/System/Log')),
  // 'system/file/index': lazy(() => import('@/pages/System/File')),
};

export default RouteMap;
