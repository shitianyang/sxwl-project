import { Row } from 'antd';
import type { RowProps } from 'antd';

export type SxwlRowProps = RowProps;

/**
 * SxwlRow — 基于 antd Row 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlRow gutter={[16, 16]}>
 *   <SxwlCol span={6}>内容</SxwlCol>
 * </SxwlRow>
 * ```
 */
const SxwlRow = (props: SxwlRowProps) => <Row {...props} />;

export default SxwlRow;
