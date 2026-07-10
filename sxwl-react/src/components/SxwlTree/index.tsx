import { Tree } from 'antd';
import type { TreeProps } from 'antd';

export type SxwlTreeProps = TreeProps;

/**
 * SxwlTree — 基于 antd Tree 的二次封装
 */
const SxwlTree = (props: SxwlTreeProps) => <Tree {...props} />;

export default SxwlTree;
