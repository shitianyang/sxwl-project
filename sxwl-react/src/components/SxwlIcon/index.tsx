import * as Icons from '@ant-design/icons';
import type { AntdIconProps } from '@ant-design/icons/es/components/AntdIcon';
import { createElement } from 'react';

export interface SxwlIconProps extends AntdIconProps {
  /** 图标名称，如 'UserOutlined'、'TeamOutlined' 等 */
  name: string;
}

/**
 * SxwlIcon — 统一图标组件
 *
 * 用法：
 * ```tsx
 * <SxwlIcon name="UserOutlined" />
 * <SxwlIcon name="DashboardOutlined" style={{ fontSize: 20, color: '#DE5F0E' }} />
 * ```
 *
 * 所有图标名见 @ant-design/icons 文档，或查看 src/assets/icons/ 下文件列表。
 */
const SxwlIcon = ({ name, ...rest }: SxwlIconProps) => {
  const IconComponent = (Icons as unknown as Record<string, React.ComponentType<AntdIconProps>>)[name];
  if (!IconComponent) {
    // 开发环境下给出警告
    console.warn(`[SxwlIcon] 未找到图标 "${name}"，请检查名称是否正确`);
    return null;
  }
  return createElement(IconComponent, rest);
};

export default SxwlIcon;
