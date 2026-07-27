import { Avatar } from 'antd';
import type { AvatarProps } from 'antd';

export type SxwlAvatarProps = AvatarProps;

/**
 * SxwlAvatar — 基于 antd Avatar 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlAvatar size="small" icon={<SxwlIcon name="UserOutlined" />} />
 * ```
 */
const SxwlAvatar = (props: SxwlAvatarProps) => <Avatar {...props} />;

export default SxwlAvatar;
