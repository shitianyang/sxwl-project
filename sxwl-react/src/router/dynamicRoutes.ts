// ============================================
// 动态路由构建工具
// 根据后端返回的菜单树 + routeMap 生成 RouteObject
// ============================================

import { Suspense } from 'react';
import { Spin } from 'antd';
import type { RouteObject } from 'react-router-dom';
import RouteMap from './routeMap';

/** 菜单节点（来自后端 sys_menu 表） */
export interface MenuNode {
  menuId: number;
  menuName: string;
  parentId: number | null;
  path: string;
  component: string | null;
  redirect: string | null;
  visible: boolean;
  keepAlive: boolean;
  children?: MenuNode[];
}

/**
 * 从菜单树构建 React Router 路由配置
 * @param menus 菜单树
 * @returns RouteObject[]
 */
export function buildRoutes(menus: MenuNode[]): RouteObject[] {
  return menus
    .filter((menu) => menu.visible && menu.path)
    .map((menu) => {
      const route: RouteObject = {
        path: menu.path,
      };

      // 有 component 字段 → 查找映射加载组件
      if (menu.component && RouteMap[menu.component]) {
        const Component = RouteMap[menu.component];
        route.element = (
          <Suspense fallback={<Spin style={{ display: 'block', margin: '100px auto' }} />}>
            <Component />
          </Suspense>
        );
      }

      // 有 redirect → 重定向
      if (menu.redirect) {
        route.children = [
          { index: true, path: '', redirect: menu.redirect },
        ];
      }

      // 递归处理子菜单
      if (menu.children?.length) {
        const childRoutes = buildRoutes(menu.children);
        if (childRoutes.length) {
          route.children = [...(route.children || []), ...childRoutes];
        }
      }

      return route;
    });
}
