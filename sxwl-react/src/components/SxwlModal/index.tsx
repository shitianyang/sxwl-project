import { Modal } from 'antd';
import type { ModalProps } from 'antd';

export type SxwlModalProps = ModalProps;

/**
 * SxwlModal — 基于 antd Modal 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlModal title="标题" open={open} onOk={handleOk} onCancel={handleCancel}>
 *   内容
 * </SxwlModal>
 * ```
 */
const SxwlModal = (props: SxwlModalProps) => <Modal {...props} />;

export default SxwlModal;
