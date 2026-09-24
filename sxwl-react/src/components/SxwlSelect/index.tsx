import { Select } from 'antd';
import type { SelectProps } from 'antd';

export type SxwlSelectProps = SelectProps;

/**
 * SxwlSelect — 基于 antd Select 的二次封装
 *
 * 默认宽度 100%（与 Input 对齐，表单列内自动撑满），可用 style.width 覆盖。
 *
 * 用法：
 * ```tsx
 * <SxwlSelect
 *   placeholder="请选择"
 *   allowClear
 *   options={[
 *     { value: 1, label: '启用' },
 *     { value: 0, label: '禁用' },
 *   ]}
 * />
 * ```
 */
const SxwlSelect = ({ style, ...rest }: SxwlSelectProps) => (
  <Select style={{ width: '100%', ...style }} {...rest} />
);

export default SxwlSelect;
