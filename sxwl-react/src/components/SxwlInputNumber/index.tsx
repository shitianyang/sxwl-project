import { InputNumber } from 'antd';
import type { InputNumberProps } from 'antd';

export type SxwlInputNumberProps = InputNumberProps;

/**
 * SxwlInputNumber — 基于 antd InputNumber 的二次封装
 */
const SxwlInputNumber = (props: SxwlInputNumberProps) => <InputNumber {...props} />;

export default SxwlInputNumber;
