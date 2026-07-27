import { Popover } from 'antd';
import type { PopoverProps } from 'antd';

export type SxwlPopoverProps = PopoverProps;

/**
 * SxwlPopover — 基于 antd Popover 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlPopover content="提示内容" trigger="click">
 *   <SxwlButton>点击</SxwlButton>
 * </SxwlPopover>
 * ```
 */
const SxwlPopover = (props: SxwlPopoverProps) => <Popover {...props} />;

export default SxwlPopover;
