import { Dropdown } from 'antd';
import type { DropdownProps } from 'antd';

export type SxwlDropdownProps = DropdownProps;

/**
 * SxwlDropdown — 基于 antd Dropdown 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlDropdown menu={{ items: menuItems }} placement="bottomRight">
 *   <SxwlButton>点击</SxwlButton>
 * </SxwlDropdown>
 * ```
 */
const SxwlDropdown = (props: SxwlDropdownProps) => <Dropdown {...props} />;

export default SxwlDropdown;
