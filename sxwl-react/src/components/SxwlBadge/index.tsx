import { Badge } from 'antd';
import type { BadgeProps } from 'antd';

export type SxwlBadgeProps = BadgeProps;

/**
 * SxwlBadge — 基于 antd Badge 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlBadge count={5}>
 *   <SxwlIcon name="BellOutlined" />
 * </SxwlBadge>
 * ```
 */
const SxwlBadge = (props: SxwlBadgeProps) => <Badge {...props} />;

export default SxwlBadge;
