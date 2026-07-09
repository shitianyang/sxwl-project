import { Checkbox } from 'antd';
import type { CheckboxProps } from 'antd';

export type SxwlCheckboxProps = CheckboxProps;

/**
 * SxwlCheckbox — 基于 antd Checkbox 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlCheckbox checked={remember} onChange={setRemember}>
 *   记住用户名
 * </SxwlCheckbox>
 * ```
 *
 * > 多选组请使用 SxwlCheckboxGroup
 */
const SxwlCheckbox = (props: SxwlCheckboxProps) => <Checkbox {...props} />;

export default SxwlCheckbox;
