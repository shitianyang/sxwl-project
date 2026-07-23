import { create } from 'zustand';
import type { MenuTreeItem } from '@/api/system/menuApi';
import { getUserMenuTree } from '@/api/system/menuApi';

interface MenuState {
  /** 菜单树数据 */
  menuTree: MenuTreeItem[];
  /** 是否正在加载 */
  loading: boolean;
  /** 是否已加载完成（避免重复请求） */
  loaded: boolean;

  /** 加载菜单树 */
  fetchMenuTree: () => Promise<void>;
  /** 清空菜单树（登出时调用） */
  clearMenuTree: () => void;
}

export const useMenuStore = create<MenuState>((set, get) => ({
  menuTree: [],
  loading: false,
  loaded: false,

  fetchMenuTree: async () => {
    // 已加载或正在加载中则跳过
    if (get().loaded || get().loading) return;

    set({ loading: true });
    try {
      const res = await getUserMenuTree();
      set({ menuTree: res.data.data || [], loaded: true });
    } catch {
      set({ menuTree: [], loaded: true });
    } finally {
      set({ loading: false });
    }
  },

  clearMenuTree: () => {
    set({ menuTree: [], loading: false, loaded: false });
  },
}));
