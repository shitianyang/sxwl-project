import { lazy, Suspense, useMemo } from 'react';
import { BrowserRouter, Navigate, Route, Routes, useLocation } from 'react-router-dom';
import { Spin } from 'antd';
import AuthGuard from './AuthGuard';
import { useAuthStore } from '@/stores/authStore';
import { useMenuStore } from '@/stores/menuStore';
import { buildRouteElements } from './dynamicRoutes';
import { resolveComponent } from './pageResolver';

const SxwlLayout = lazy(() => import('@/layouts/SxwlLayout'));

/** 未登录重定向：携带当前路径，登录后可回到原页面 */
function LoginRedirect() {
  const location = useLocation();
  return <Navigate to="/login" state={{ from: location }} replace />;
}

/**
 * 登录守卫：未登录时重定向到 /login（携带 from 状态）
 * 路由树始终存在，避免条件渲染导致的路由时序问题
 */
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());
  const location = useLocation();

  if (!isLoggedIn) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
}

/**
 * 受保护区域内的兜底路由：
 * - 菜单未加载时返回 null（不触发 404 重定向）
 * - 菜单加载后仍未匹配 → 跳转 404
 */
function ProtectedCatchAll() {
  const loaded = useMenuStore((s) => s.loaded);
  if (!loaded) return null;
  return <Navigate to="/404" replace />;
}

/**
 * 公开路由配置（基础设施页面，不存储在 DB 中）
 * 通过 import.meta.glob + resolveComponent 自动解析组件，无手动 lazy import
 */
const PUBLIC_ROUTES: Array<{ path: string; component: string }> = [
  { path: 'login', component: 'Login' },
  { path: '404', component: 'Error/NotFound' },
  { path: '403', component: 'Error/Forbidden' },
  { path: '500', component: 'Error/ServerError' },
];

/**
 * 应用路由根组件
 * - 公开路由：代码定义的基础页面（login/403/404/500）
 * - 受保护路由：始终渲染，ProtectedRoute 守卫登录态
 * - 兜底路由：未登录进 /login（携带 from state）
 */
export default function AppRouter() {
  const menuTree = useMenuStore((s) => s.menuTree);

  const publicRouteElements = useMemo(
    () =>
      PUBLIC_ROUTES
        .map((route) => {
          const Component = resolveComponent(route.component);
          if (!Component) return null;
          return (
            <Route
              key={route.path}
              path={'/' + route.path}
              element={
                <Suspense fallback={<Spin style={{ display: 'block', margin: '100px auto' }} />}>
                  <Component />
                </Suspense>
              }
            />
          );
        })
        .filter(Boolean),
    [],
  );

  const dynamicRoutes = useMemo(() => buildRouteElements(menuTree), [menuTree]);

  return (
    <BrowserRouter>
      <Routes>
        {/* ==================== 公开路由（代码定义） ==================== */}
        {publicRouteElements}

        {/* ==================== 受保护路由（始终存在，不受登录态影响） ==================== */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <AuthGuard>
                <Suspense fallback={null}>
                  <SxwlLayout />
                </Suspense>
              </AuthGuard>
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          {dynamicRoutes}
          {/* 菜单未加载时不跳转，加载后仍未匹配才跳 404 */}
          <Route path="*" element={<ProtectedCatchAll />} />
        </Route>

        {/* ==================== 兜底路由 ==================== */}
        <Route path="*" element={<LoginRedirect />} />
      </Routes>
    </BrowserRouter>
  );
}
