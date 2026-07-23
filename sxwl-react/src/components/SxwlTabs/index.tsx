import { Tabs } from 'antd';
import type { TabsProps } from 'antd';

export type SxwlTabsProps = TabsProps;

/**
 * SxwlTabs — 基于 antd Tabs 的二次封装
 */
const SxwlTabs = (props: SxwlTabsProps) => <Tabs {...props} />;

export default SxwlTabs;
