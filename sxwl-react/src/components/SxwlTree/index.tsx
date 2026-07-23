import { type JSX } from 'react';
import { Tree } from 'antd';
import type { TreeProps } from 'antd';

export type SxwlTreeProps = TreeProps;

/**
 * SxwlTree — 基于 antd Tree 的二次封装
 *
 * 用于角色分配等场景的 checkable 树组件。
 *
 * 用法：
 * ```tsx
 * <SxwlTree treeData={treeData} defaultExpandAll />
 * <SxwlTree treeData={treeData} checkable checkedKeys={checkedKeys} onCheck={onCheck} />
 * ```
 */
const SxwlTree = (props: SxwlTreeProps): JSX.Element => <Tree {...props} />;

export default SxwlTree;
