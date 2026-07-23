import { Switch } from 'antd';
import type { SwitchProps } from 'antd';

export type SxwlSwitchProps = SwitchProps;

/**
 * SxwlSwitch — 基于 antd Switch 的二次封装
 */
const SxwlSwitch = (props: SxwlSwitchProps) => <Switch {...props} />;

export default SxwlSwitch;
