// ============================================
// 动态路由构建工具
// 根据后端返回的菜单树 + routeMap 生成 Route 元素
// ============================================

import { type ReactNode, Suspense } from 'react';
import { Spin } from 'antd';
import { Route } from 'react-router-dom';
import type { MenuTreeItem } from '@/api/system/menuApi';
import { resolveComponent } from './pageResolver';

/**
 * 从菜单树递归构建 React Router Route 元素
 * @param menus 后端返回的菜单树
 * @returns Route 元素数组，可直接放在 <Routes> 中
 */
export function buildRouteElements(menus: MenuTreeItem[]): ReactNode[] {
  return menus
    .filter((menu) => {
      // 只处理可见且启用的菜单，过滤外链菜单
      return menu.visible === 1 && menu.status === 1 && menu.isFrame !== 1;
    })
    .map((menu) => {
      const children = menu.children?.length ? buildRouteElements(menu.children) : [];

      // 有 component → 可导航的页面（渲染组件）
      if (menu.component) {
        const Component = resolveComponent(menu.component);
        if (!Component) return null;
        return (
          <Route
            key={menu.path}
            path={menu.path}
            element={
              <Suspense fallback={<Spin style={{ display: 'block', margin: '100px auto' }} />}>
                <Component />
              </Suspense>
            }
          >
            {children}
          </Route>
        );
      }

      // 目录节点（无 component）→ 只透传子路由
      if (children.length > 0) {
        return (
          <Route key={menu.path || menu.id} path={menu.path || undefined}>
            {children}
          </Route>
        );
      }

      return null;
    })
    .filter(Boolean);
}
