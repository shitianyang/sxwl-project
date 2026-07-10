import { Card } from 'antd';
import type { CardProps } from 'antd';

export type SxwlCardProps = CardProps;

/**
 * SxwlCard — 基于 antd Card 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlCard title="标题">
 *   内容
 * </SxwlCard>
 * ```
 */
const SxwlCard = (props: SxwlCardProps) => <Card {...props} />;

export default SxwlCard;
