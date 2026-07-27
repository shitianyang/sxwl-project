import { Menu } from 'antd';
import type { MenuProps } from 'antd';

export type SxwlMenuProps = MenuProps;

/**
 * SxwlMenu — 基于 antd Menu 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlMenu
 *   theme="dark"
 *   mode="inline"
 *   selectedKeys={selectedKeys}
 *   items={menuItems}
 *   onClick={handleClick}
 * />
 * ```
 */
const SxwlMenu = (props: SxwlMenuProps) => <Menu {...props} />;

export default SxwlMenu;
