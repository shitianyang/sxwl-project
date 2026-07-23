// ============================================
// 页面组件自动解析器
// 利用 Vite import.meta.glob 自动发现 @/pages/**/index.tsx 下所有页面
// 约定：后端 component 字段存储 PascalCase 路径，如 "Dashboard"、"System/User"
// 新增页面只需：1. 在 pages/ 下创建文件 2. 在 DB sys_menu_info 添加记录
// ============================================

import { lazy, type LazyExoticComponent, type ComponentType } from 'react';

// 自动发现所有 @/pages/**/index.tsx 下的页面组件（懒加载）
const pageModules = import.meta.glob<{ default: ComponentType }>('/src/pages/**/index.tsx');

/**
 * 根据后端 component 路径解析为 React 懒加载组件
 * @param component 后端 component 字段值（PascalCase），如 "Dashboard"、"System/User"
 * @returns 懒加载组件，未找到时返回 null
 */
export function resolveComponent(component: string): LazyExoticComponent<ComponentType> | null {
  // 统一移除末尾 /index 后缀（兼容新旧格式）
  const normalized = component.replace(/\/index$/, '');
  const key = `/src/pages/${normalized}/index.tsx`;

  const factory = pageModules[key];
  if (!factory) {
    console.warn(`[pageResolver] 未找到组件: ${component}（期望路径: ${key}）`);
    return null;
  }

  return lazy(() => factory());
}

/**
 * 获取所有已发现页面的路径列表（用于调试）
 */
export function getDiscoveredPages(): string[] {
  return Object.keys(pageModules);
}
