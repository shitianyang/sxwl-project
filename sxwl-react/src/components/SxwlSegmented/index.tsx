import { Segmented } from 'antd';
import type { SegmentedProps } from 'antd';

export type SxwlSegmentedProps = SegmentedProps;

/**
 * SxwlSegmented — 基于 antd Segmented 的二次封装
 */
const SxwlSegmented = (props: SxwlSegmentedProps) => <Segmented {...props} />;

export default SxwlSegmented;
