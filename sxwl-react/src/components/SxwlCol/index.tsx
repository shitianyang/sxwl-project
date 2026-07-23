import { Col } from 'antd';
import type { ColProps } from 'antd';

export type SxwlColProps = ColProps;

/**
 * SxwlCol — 基于 antd Col 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlCol xs={24} sm={12} lg={6}>
 *   内容
 * </SxwlCol>
 * ```
 */
const SxwlCol = (props: SxwlColProps) => <Col {...props} />;

export default SxwlCol;
