import { Tooltip } from 'antd';
import type { TooltipProps } from 'antd';

export type SxwlTooltipProps = TooltipProps;

/**
 * SxwlTooltip — 基于 antd Tooltip 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlTooltip title="提示内容">
 *   <Button>Hover me</Button>
 * </SxwlTooltip>
 * ```
 */
const SxwlTooltip = (props: SxwlTooltipProps) => <Tooltip {...props} />;

export default SxwlTooltip;
