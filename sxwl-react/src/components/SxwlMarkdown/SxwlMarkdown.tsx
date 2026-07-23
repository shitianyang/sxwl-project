import React from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import type { Components } from 'react-markdown';

export interface SxwlMarkdownProps {
    /** Markdown 原始文本 */
    content: string;
    /** 自定义类名 */
    className?: string;
    /** 最大高度（px），超出滚动 */
    maxHeight?: number;
}

/**
 * SxwlMarkdown — Markdown 展示组件
 *
 * <p>基于 react-markdown + remark-gfm + rehype-highlight 渲染。</p>
 *
 * 用法：
 * ```tsx
 * <SxwlMarkdown content="# 标题\n\n这是**加粗**文本" maxHeight={400} />
 * ```
 */
const SxwlMarkdown: React.FC<SxwlMarkdownProps> = ({ content, className, maxHeight }) => {
    const components: Components = {
        table: ({ children }) => (
            <div style={{ overflowX: 'auto' }}>
                <table>{children}</table>
            </div>
        ),
        img: ({ src, alt }) => (
            <img src={src} alt={alt} loading="lazy" style={{ maxWidth: '100%' }} />
        ),
    };

    return (
        <div
            className={className}
            style={{
                maxHeight: maxHeight ?? 'none',
                overflowY: maxHeight ? 'auto' : 'visible',
                lineHeight: 1.8,
                fontSize: 14,
            }}
        >
            <ReactMarkdown
                remarkPlugins={[remarkGfm]}
                // rehypePlugins={[rehypeHighlight]}
                components={components}
            >
                {content}
            </ReactMarkdown>
        </div>
    );
};

export default SxwlMarkdown;
