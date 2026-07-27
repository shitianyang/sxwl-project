import { Spin } from 'antd';
import type { SpinProps } from 'antd';

export type SxwlSpinProps = SpinProps;

/**
 * SxwlSpin — 基于 antd Spin 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlSpin size="small" />
 * <SxwlSpin tip="加载中...">
 *   <div>内容</div>
 * </SxwlSpin>
 * ```
 */
const SxwlSpin = (props: SxwlSpinProps) => <Spin {...props} />;

export default SxwlSpin;
