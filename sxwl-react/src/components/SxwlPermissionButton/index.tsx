import type { ButtonProps } from 'antd';
import SxwlButton from '@/components/SxwlButton';
import { usePermissionStore } from '@/stores/permissionStore';

export interface SxwlPermissionButtonProps extends ButtonProps {
  /** 权限标识，如 'system:user:add'；传数组时支持多权限控制 */
  permission?: string | string[];
  /** 权限逻辑：and=同时拥有 all，or=任一即可。默认 or */
  mode?: 'and' | 'or';
}

/**
 * SxwlPermissionButton — 带权限控制的按钮
 *
 * 基于 SxwlButton 二次封装，自动根据当前用户的权限标识决定是否渲染。
 * 权限列表通过 usePermissionStore 管理，在登录后由应用层注入。
 *
 * ```tsx
 * <SxwlPermissionButton permission="system:user:add" type="primary">
 *   新增用户
 * </SxwlPermissionButton>
 *
 * <SxwlPermissionButton permission={['system:user:edit', 'system:user:add']} mode="or">
 *   编辑
 * </SxwlPermissionButton>
 * ```
 */
const SxwlPermissionButton: React.FC<SxwlPermissionButtonProps> = ({
  permission,
  mode = 'or',
  children,
  ...rest
}) => {
  const { hasPermission, loaded } = usePermissionStore();

  // 未加载完成前默认显示（开发环境），加载后按权限判断
  if (loaded && permission && !hasPermission(permission, mode)) {
    return null;
  }

  return <SxwlButton {...rest}>{children}</SxwlButton>;
};

export default SxwlPermissionButton;
