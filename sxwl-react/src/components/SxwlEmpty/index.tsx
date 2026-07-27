import { Empty } from 'antd';
import type { EmptyProps } from 'antd';

export type SxwlEmptyProps = EmptyProps;

/**
 * SxwlEmpty — 基于 antd Empty 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlEmpty description="暂无数据" />
 * <SxwlEmpty description="暂无数据" image={SxwlEmpty.PRESENTED_IMAGE_SIMPLE} />
 * ```
 */
const SxwlEmptyComponent = (props: SxwlEmptyProps) => <Empty {...props} />;

const SxwlEmpty = Object.assign(SxwlEmptyComponent, {
  PRESENTED_IMAGE_SIMPLE: Empty.PRESENTED_IMAGE_SIMPLE,
});

export default SxwlEmpty;
