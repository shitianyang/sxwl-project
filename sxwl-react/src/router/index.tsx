import { lazy, Suspense } from 'react';
import type { ReactNode } from 'react';
import { Navigate, createBrowserRouter } from 'react-router-dom';
import AuthGuard from './AuthGuard';

const LoginPage = lazy(() => import('@/pages/Login'));
const SxwlLayout = lazy(() => import('@/layouts/SxwlLayout'));
const DashboardPage = lazy(() => import('@/pages/Dashboard'));
const NotFound = lazy(() => import('@/pages/Error/NotFound'));
const Forbidden = lazy(() => import('@/pages/Error/Forbidden'));
const ServerError = lazy(() => import('@/pages/Error/ServerError'));
const UserPage = lazy(() => import('@/pages/System/User'));
const PositionPage = lazy(() => import('@/pages/System/Position'));
const MenuPage = lazy(() => import('@/pages/System/Menu'));
const OrganizationPage = lazy(() => import('@/pages/System/Organization'));
const DictPage = lazy(() => import('@/pages/System/Dict'));
const RolePage = lazy(() => import('@/pages/System/Role'));
const OperationLogPage = lazy(() => import('@/pages/Log/OperationLog'));
const LoginLogPage = lazy(() => import('@/pages/Log/LoginLog'));
const FilePage = lazy(() => import('@/pages/File'));

function routeElement(element: ReactNode) {
  return <Suspense fallback={null}>{element}</Suspense>;
}

const router = createBrowserRouter([
  // ==================== 公开路由 ====================
  {
    path: '/login',
    element: routeElement(<LoginPage />),
  },
  {
    path: '/403',
    element: routeElement(<Forbidden />),
  },
  {
    path: '/404',
    element: routeElement(<NotFound />),
  },
  {
    path: '/500',
    element: routeElement(<ServerError />),
  },

  // ==================== 受保护路由（需登录） ====================
  {
    path: '/',
    element: (
      <AuthGuard>
        {routeElement(<SxwlLayout />)}
      </AuthGuard>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: 'dashboard', element: routeElement(<DashboardPage />) },
      { path: 'system/user', element: routeElement(<UserPage />) },
      { path: 'system/position', element: routeElement(<PositionPage />) },
      { path: 'system/menu', element: routeElement(<MenuPage />) },
      { path: 'system/organization', element: routeElement(<OrganizationPage />) },
      { path: 'system/dict', element: routeElement(<DictPage />) },
      { path: 'system/role', element: routeElement(<RolePage />) },
      { path: 'log/operation', element: routeElement(<OperationLogPage />) },
      { path: 'log/login', element: routeElement(<LoginLogPage />) },
      { path: 'file', element: routeElement(<FilePage />) },
      // TODO: 后续接入动态路由
    ],
  },

  // ==================== 兜底路由 ====================
  {
    path: '*',
    element: <Navigate to="/404" replace />,
  },
]);

export default router;
