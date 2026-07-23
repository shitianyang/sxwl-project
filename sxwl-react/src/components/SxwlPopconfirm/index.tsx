import { Popconfirm } from 'antd';
import type { PopconfirmProps } from 'antd';

export type SxwlPopconfirmProps = PopconfirmProps;

/**
 * SxwlPopconfirm — 基于 antd Popconfirm 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlPopconfirm title="确定删除吗？" onConfirm={handleDelete}>
 *   <SxwlButton danger>删除</SxwlButton>
 * </SxwlPopconfirm>
 * ```
 */
const SxwlPopconfirm = (props: SxwlPopconfirmProps) => <Popconfirm {...props} />;

export default SxwlPopconfirm;
