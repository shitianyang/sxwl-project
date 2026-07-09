import { Navigate, createBrowserRouter } from 'react-router-dom';
import LoginPage from '@/pages/Login';
import DashboardPage from '@/pages/Dashboard';
import AuthGuard from './AuthGuard';

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: (
      <AuthGuard>
        <DashboardPage />
      </AuthGuard>
    ),
  },
  {
    path: '*',
    element: <Navigate to="/" replace />,
  },
]);

export default router;
