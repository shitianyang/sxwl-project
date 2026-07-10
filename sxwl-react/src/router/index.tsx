import { Navigate, createBrowserRouter } from 'react-router-dom';
import LoginPage from '@/pages/Login';
import SxwlLayout from '@/layouts/SxwlLayout';
import DashboardPage from '@/pages/Dashboard';
import NotFound from '@/pages/Error/NotFound';
import Forbidden from '@/pages/Error/Forbidden';
import ServerError from '@/pages/Error/ServerError';
import UserPage from '@/pages/System/User';
import AuthGuard from './AuthGuard';

const router = createBrowserRouter([
  // ==================== 公开路由 ====================
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/403',
    element: <Forbidden />,
  },
  {
    path: '/404',
    element: <NotFound />,
  },
  {
    path: '/500',
    element: <ServerError />,
  },

  // ==================== 受保护路由（需登录） ====================
  {
    path: '/',
    element: (
      <AuthGuard>
        <SxwlLayout />
      </AuthGuard>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: 'dashboard', element: <DashboardPage /> },
      { path: 'system/user', element: <UserPage /> },
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
