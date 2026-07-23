import React from 'react';
import { Input } from 'antd';
import SxwlMarkdown from './SxwlMarkdown';

const { TextArea } = Input;

export interface SxwlMarkdownEditorProps {
    /** Markdown 原始文本 */
    value?: string;
    /** 值变化回调 */
    onChange?: (value: string) => void;
    /** 占位文本 */
    placeholder?: string;
    /** 最小行数 */
    minRows?: number;
    /** 最大行数 */
    maxRows?: number;
}

/**
 * SxwlMarkdownEditor — Markdown 编辑组件
 *
 * <p>左侧编辑区，右侧实时预览。</p>
 *
 * 用法：
 * ```tsx
 * <SxwlMarkdownEditor value={text} onChange={setText} minRows={4} />
 * ```
 */
const SxwlMarkdownEditor: React.FC<SxwlMarkdownEditorProps> = ({
    value = '',
    onChange,
    placeholder = '支持 Markdown 格式...',
    minRows = 4,
    maxRows = 20,
}) => {
    const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        onChange?.(e.target.value);
    };

    return (
        <div style={{ display: 'flex', gap: 12, minHeight: 200 }}>
            <div style={{ flex: 1 }}>
                <TextArea
                    value={value}
                    onChange={handleChange}
                    placeholder={placeholder}
                    rows={minRows}
                    style={{ minHeight: 180, fontFamily: 'monospace' }}
                />
            </div>
            <div
                style={{
                    flex: 1,
                    padding: '8px 12px',
                    border: '1px solid #d9d9d9',
                    borderRadius: 6,
                    overflowY: 'auto',
                    maxHeight: maxRows * 22,
                    background: '#fafafa',
                }}
            >
                {value ? (
                    <SxwlMarkdown content={value} />
                ) : (
                    <span style={{ color: '#999' }}>预览区域</span>
                )}
            </div>
        </div>
    );
};

export default SxwlMarkdownEditor;
