import { Card } from 'antd';
import type { CardProps } from 'antd';

export type SxwlCardProps = CardProps;

/**
 * SxwlCard — 基于 antd Card 的二次封装
 *
 * 默认无边框（variant="borderless"），匹配 ant-design-pro 风格。
 * 如需带边框，显式传 variant="outlined" 即可。
 *
 * 用法：
 * ```tsx
 * <SxwlCard title="标题">
 *   内容
 * </SxwlCard>
 * ```
 */
const SxwlCard = (props: SxwlCardProps) => <Card variant="borderless" {...props} />;

export default SxwlCard;
