import { Skeleton } from 'antd';
import type { SkeletonProps } from 'antd';

export type SxwlSkeletonProps = SkeletonProps;

/**
 * SxwlSkeleton — 基于 antd Skeleton 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlSkeleton active />
 * <SxwlSkeleton loading={loading}>
 *   <div>内容</div>
 * </SxwlSkeleton>
 * ```
 */
const SxwlSkeleton = (props: SxwlSkeletonProps) => <Skeleton {...props} />;

export default SxwlSkeleton;
