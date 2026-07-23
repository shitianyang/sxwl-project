import { useEffect } from 'react';
import { Spin } from 'antd';
import { useMenuStore } from '@/stores/menuStore';

/** 路由守卫：未登录时菜单加载完成前阻止子路由匹配 */
export default function AuthGuard({ children }: { children: React.ReactNode }) {
  const menuLoaded = useMenuStore((s) => s.loaded);
  const fetchMenuTree = useMenuStore((s) => s.fetchMenuTree);

  // 菜单未加载 → 触发菜单加载（处理首次登录 + 页面刷新）
  useEffect(() => {
    if (!menuLoaded) {
      fetchMenuTree();
    }
  }, [menuLoaded, fetchMenuTree]);

  // 菜单未加载完成时，阻止子路由匹配，防止重定向到不存在的路由
  if (!menuLoaded) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" />
      </div>
    );
  }

  return <>{children}</>;
}
