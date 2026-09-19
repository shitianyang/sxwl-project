import { type JSX } from 'react';
import { TreeSelect } from 'antd';
import type { TreeSelectProps } from 'antd';

export type SxwlTreeSelectProps = TreeSelectProps;

/**
 * SxwlTreeSelect — 基于 antd TreeSelect 的二次封装
 *
 * 用于表单中选择树形数据（如组织、菜单层级），支持单选/多选（multiple）与搜索过滤。
 *
 * 用法：
 * ```tsx
 * <SxwlTreeSelect treeData={treeData} fieldNames={{ label: 'orgName', value: 'id', children: 'children' }} />
 * <SxwlTreeSelect treeData={treeData} multiple treeNodeFilterProp="title" />
 * ```
 */
const SxwlTreeSelect = (props: SxwlTreeSelectProps): JSX.Element => <TreeSelect {...props} />;

export default SxwlTreeSelect;
