import { Button } from 'antd';
import type { ButtonProps } from 'antd';

export type SxwlButtonProps = ButtonProps;

/**
 * SxwlButton — 基于 antd Button 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlButton type="primary" htmlType="submit" loading block>
 *   登 录
 * </SxwlButton>
 * ```
 *
 * > 权限控制请使用 SxwlPermissionButton
 */
const SxwlButton = (props: SxwlButtonProps) => <Button {...props} />;

export default SxwlButton;
