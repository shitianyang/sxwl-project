import { Modal } from 'antd';
import type { ModalProps, ModalFuncProps } from 'antd';

export type SxwlModalProps = ModalProps;

/**
 * SxwlModal — 基于 antd Modal 的二次封装
 *
 * 自动处理废弃属性转换，避免 antd 控制台警告。
 *
 * 用法：
 * ```tsx
 * <SxwlModal title="标题" open={open} onOk={handleOk} onCancel={handleCancel}>
 *   内容
 * </SxwlModal>
 * ```
 */
const SxwlModalComponent = (props: SxwlModalProps) => <Modal {...props} />;

const SxwlModal = Object.assign(SxwlModalComponent, {
  /** 静态 confirm 方法 */
  confirm: (props: ModalFuncProps) => Modal.confirm(props),
});

export default SxwlModal;
