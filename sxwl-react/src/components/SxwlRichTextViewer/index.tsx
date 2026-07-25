import React, { useRef } from 'react';
import { useEditor, EditorContent } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import Underline from '@tiptap/extension-underline';
import TextAlign from '@tiptap/extension-text-align';
import LinkExtension from '@tiptap/extension-link';
import ImageExtension from '@tiptap/extension-image';
import { Table } from '@tiptap/extension-table';
import TableRow from '@tiptap/extension-table-row';
import TableCell from '@tiptap/extension-table-cell';
import TableHeader from '@tiptap/extension-table-header';
import Highlight from '@tiptap/extension-highlight';
import { TextStyle } from '@tiptap/extension-text-style';
import Color from '@tiptap/extension-color';
import CodeBlockLowlight from '@tiptap/extension-code-block-lowlight';
import { common, createLowlight } from 'lowlight';

export interface SxwlRichTextViewerProps {
    content: string;
    className?: string;
    maxHeight?: number;
}

/**
 * SxwlRichTextViewer — 富文本内容展示（TipTap 只读模式）
 *
 * 使用与编辑器完全相同的 TipTap 扩展栈渲染 HTML，
 * 确保编辑效果与预览效果 100% 一致。
 *
 * 支持：标题/加粗/斜体/下划线/删除线/行内代码/
 * 文字颜色/背景高亮/对齐/列表/引用/分隔线/代码块（语法高亮）/
 * 链接/图片/表格
 */
const SxwlRichTextViewer: React.FC<SxwlRichTextViewerProps> = ({
    content,
    className,
    maxHeight,
}) => {
    const prevContentRef = useRef(content);

    const editor = useEditor({
        extensions: [
            StarterKit.configure({ codeBlock: false, underline: false, link: false }),
            Underline,
            TextAlign.configure({ types: ['heading', 'paragraph'] }),
            LinkExtension.configure({
                openOnClick: true,
                HTMLAttributes: { rel: 'noopener noreferrer', target: '_blank' },
            }),
            ImageExtension.configure({ inline: false, allowBase64: true }),
            Table.configure({ resizable: false }),
            TableRow, TableCell, TableHeader,
            Highlight.configure({ multicolor: true }),
            TextStyle, Color,
            CodeBlockLowlight.configure({ lowlight: createLowlight(common) }),
        ],
        content,
        editable: false,
        editorProps: {
            attributes: {
                class: 'sxwl-rich-viewer',
            },
        },
    });

    // 只在外部 content 真的变了才同步（避免 HTML 规范化导致循环）
    React.useEffect(() => {
        if (editor && !editor.isDestroyed && content !== prevContentRef.current) {
            editor.commands.setContent(content);
            prevContentRef.current = content;
        }
    }, [content, editor]);

    React.useEffect(() => {
        return () => { editor?.destroy(); };
    }, []);

    if (!editor) return null;

    return (
        <>
            <style>{viewerStyles}</style>
            <div
                className={`sxwl-rich-viewer-wrapper ${className ?? ''}`}
                style={{
                    maxHeight: maxHeight ?? 'none',
                    overflowY: maxHeight ? 'auto' : 'visible',
                }}
            >
                <EditorContent editor={editor} />
            </div>
        </>
    );
};

/**
 * TipTap 只读模式的内容样式
 * ProseMirror 会在内容区生成自己的 DOM 结构，
 * 这里只补充编辑器不会自动处理的视觉美化
 */
const viewerStyles = `
.sxwl-rich-viewer-wrapper .ProseMirror {
    outline: none;
    line-height: 1.85;
    font-size: 14px;
    color: #333;
    word-break: break-word;
}
.sxwl-rich-viewer-wrapper .ProseMirror > *:first-child { margin-top: 0; }
.sxwl-rich-viewer-wrapper .ProseMirror > *:last-child  { margin-bottom: 0; }

.sxwl-rich-viewer-wrapper h1 { font-size: 1.6em; margin: 0.8em 0 0.4em; font-weight: 700; line-height: 1.4; }
.sxwl-rich-viewer-wrapper h2 { font-size: 1.4em; margin: 0.7em 0 0.35em; font-weight: 700; line-height: 1.4; }
.sxwl-rich-viewer-wrapper h3 { font-size: 1.2em; margin: 0.6em 0 0.3em; font-weight: 600; line-height: 1.5; }
.sxwl-rich-viewer-wrapper h4 { font-size: 1.05em; margin: 0.5em 0 0.25em; font-weight: 600; line-height: 1.5; }

.sxwl-rich-viewer-wrapper p { margin: 0.5em 0; }

.sxwl-rich-viewer-wrapper code {
    background: #f5f5f5; border: 1px solid #e8e8e8; border-radius: 3px;
    padding: 2px 6px; font-size: 0.9em;
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace; color: #d63384;
}
.sxwl-rich-viewer-wrapper pre {
    background: #282c34; color: #abb2bf; border-radius: 6px; padding: 16px;
    margin: 0.8em 0; overflow-x: auto; font-size: 13px; line-height: 1.6;
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}
.sxwl-rich-viewer-wrapper pre code {
    background: none; border: none; padding: 0; font-size: inherit; color: inherit; border-radius: 0;
}

.sxwl-rich-viewer-wrapper blockquote {
    margin: 0.8em 0; padding: 8px 16px; border-left: 4px solid #1677ff;
    background: #f0f5ff; color: #555;
}
.sxwl-rich-viewer-wrapper blockquote p { margin: 0.3em 0; }

.sxwl-rich-viewer-wrapper ul, .sxwl-rich-viewer-wrapper ol { margin: 0.5em 0; padding-left: 1.8em; }
.sxwl-rich-viewer-wrapper li { margin: 0.2em 0; }
.sxwl-rich-viewer-wrapper li p { margin: 0.2em 0; }

.sxwl-rich-viewer-wrapper hr { border: none; border-top: 1px solid #e8e8e8; margin: 1.2em 0; }

.sxwl-rich-viewer-wrapper a { color: #1677ff; text-decoration: none; }
.sxwl-rich-viewer-wrapper a:hover { text-decoration: underline; }

.sxwl-rich-viewer-wrapper img { max-width: 100%; border-radius: 6px; margin: 0.6em 0; height: auto; }

.sxwl-rich-viewer-wrapper table {
    width: 100%; border-collapse: collapse; margin: 0.8em 0; font-size: 13px;
}
.sxwl-rich-viewer-wrapper th {
    background: #fafafa; font-weight: 600; text-align: left;
    padding: 8px 12px; border: 1px solid #e8e8e8;
}
.sxwl-rich-viewer-wrapper td { padding: 8px 12px; border: 1px solid #e8e8e8; }
.sxwl-rich-viewer-wrapper tr:nth-child(even) td { background: #fafafa; }

.sxwl-rich-viewer-wrapper mark { border-radius: 2px; padding: 1px 4px; }
`;

export default SxwlRichTextViewer;
