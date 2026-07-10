import { Tag } from 'antd';
import type { TagProps } from 'antd';

export type SxwlTagProps = TagProps;

/**
 * SxwlTag — 基于 antd Tag 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlTag color="green">启用</SxwlTag>
 * <SxwlTag color="red">禁用</SxwlTag>
 * ```
 */
const SxwlTag = (props: SxwlTagProps) => <Tag {...props} />;

export default SxwlTag;
