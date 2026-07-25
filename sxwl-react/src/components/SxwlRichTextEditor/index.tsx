import React, { useCallback, useLayoutEffect, useRef, useState } from 'react';
import { useEditor, EditorContent } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import Placeholder from '@tiptap/extension-placeholder';
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
import SxwlMessage from '@/components/SxwlMessage';
import { simpleUpload } from '@/api/system/fileApi';
import SxwlIcon from '@/components/SxwlIcon';

export interface SxwlRichTextEditorProps {
    value?: string;
    onChange?: (html: string) => void;
    placeholder?: string;
    minHeight?: number;
}

const SxwlRichTextEditor: React.FC<SxwlRichTextEditorProps> = ({
    value = '',
    onChange,
    placeholder = '请输入内容...',
    minHeight = 360,
}) => {
    // ref 初始为 true 以屏蔽 useEditor 创建时的 onUpdate 事件，
    // 防止编辑器用空值污染 Form 表单（后续在 effect 中重置为 false 允许用户编辑）
    const isInternalUpdate = useRef(true);
    // 追踪上一次外部 value，只在 value 变化时同步，不依赖 editor.getHTML() 比较
    const lastSyncedValue = useRef(value);
    // 追踪编辑器自己发出的最新值，用于跳过"onChange→父组件回传相同 value→重复 setContent"的循环
    const lastEmittedValue = useRef(value);

    const editor = useEditor({
        extensions: [
            StarterKit.configure({ codeBlock: false, underline: false, link: false }),
            Placeholder.configure({ placeholder }),
            Underline,
            TextAlign.configure({ types: ['heading', 'paragraph'] }),
            LinkExtension.configure({
                openOnClick: false,
                HTMLAttributes: { rel: 'noopener noreferrer', target: '_blank' },
            }),
            ImageExtension.configure({ inline: false, allowBase64: true }),
            Table.configure({ resizable: true }),
            TableRow, TableCell, TableHeader,
            Highlight.configure({ multicolor: true }),
            TextStyle, Color,
            CodeBlockLowlight.configure({ lowlight: createLowlight(common) }),
        ],
        content: value,
        onUpdate: ({ editor: ed }) => {
            if (!isInternalUpdate.current) {
                const html = ed.getHTML();
                lastEmittedValue.current = html;
                onChange?.(html);
            }
        },
        editorProps: {
            attributes: { style: `min-height: ${minHeight}px; outline: none;` },
        },
    });

    // 编辑器就绪后允许用户编辑触发 onChange
    React.useEffect(() => {
        isInternalUpdate.current = false;
    }, []);

    // 同步外部 value（useLayoutEffect 确保在浏览器绘制前完成，避免闪烁）
    // 直接比较引用值，跳过编辑器自身发出的值以阻止循环更新
    React.useLayoutEffect(() => {
        if (
            editor && !editor.isDestroyed &&
            value !== lastSyncedValue.current &&
            value !== lastEmittedValue.current
        ) {
            lastSyncedValue.current = value;
            isInternalUpdate.current = true;
            editor.commands.setContent(value || '');
            isInternalUpdate.current = false;
        }
    }, [value, editor]);

    React.useEffect(() => {
        return () => { editor?.destroy(); };
    }, []);

    // ========== 状态 ==========
    const [showLinkPopover, setShowLinkPopover] = useState(false);
    const [linkUrl, setLinkUrl] = useState('');
    const [linkText, setLinkText] = useState('');
    const [showTablePicker, setShowTablePicker] = useState(false);
    const [tableSize, setTableSize] = useState({ rows: 0, cols: 0 });
    const [imageUploading, setImageUploading] = useState(false);
    const fileInputRef = useRef<HTMLInputElement>(null);
    const linkPopoverRef = useRef<HTMLDivElement>(null);
    const tablePickerRef = useRef<HTMLDivElement>(null);
    const linkEditRange = useRef<{ from: number; to: number } | null>(null);

    // ========== 点击弹窗外关闭 ==========
    React.useEffect(() => {
        if (!showLinkPopover && !showTablePicker) return;
        const handler = (e: MouseEvent) => {
            const target = e.target as HTMLElement;
            if (showLinkPopover && linkPopoverRef.current && !linkPopoverRef.current.contains(target)) {
                const btn = linkPopoverRef.current.previousElementSibling;
                if (!btn?.contains(target)) setShowLinkPopover(false);
            }
            if (showTablePicker && tablePickerRef.current && !tablePickerRef.current.contains(target)) {
                const btn = tablePickerRef.current.previousElementSibling;
                if (!btn?.contains(target)) setShowTablePicker(false);
            }
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, [showLinkPopover, showTablePicker]);

    // ========== 链接 ==========
    const handleLinkClick = useCallback(() => {
        if (!editor) return;
        const { state } = editor;
        const { selection, doc } = state;
        const attrs = editor.getAttributes('link');
        setLinkUrl(attrs.href || '');
        linkEditRange.current = null;
        // 获取链接显示文本
        let text = '';
        if (!selection.empty) {
            text = doc.textBetween(selection.from, selection.to, ' ');
        } else if (attrs.href) {
            // 光标在链接上：用 ProseMirror 手动搜索标记边界
            const { $from } = selection;
            const linkMark = $from.marks().find(m => m.type.name === 'link');
            if (linkMark) {
                let from = $from.pos;
                let to = $from.pos;
                const isSameLink = (pos: number) => {
                    try {
                        return doc.resolve(pos).marks().some(
                            m => m.type.name === 'link' && m.attrs.href === linkMark.attrs.href
                        );
                    } catch { return false; }
                };
                // 向前搜索
                for (let p = $from.pos - 1; p >= 0; p--) {
                    if (!isSameLink(p)) break;
                    from = p;
                }
                // 向后搜索
                for (let p = $from.pos + 1; p <= doc.content.size; p++) {
                    if (!isSameLink(p)) break;
                    to = p;
                }
                linkEditRange.current = { from, to };
                text = doc.textBetween(from, to);
            }
        }
        setLinkText(text);
        setShowLinkPopover(true);
    }, [editor]);

    const handleLinkConfirm = useCallback(() => {
        if (!editor) return;
        setShowLinkPopover(false);
        const range = linkEditRange.current;
        linkEditRange.current = null;
        if (!linkUrl.trim()) {
            // 删除链接
            if (range) {
                editor.chain().focus().setTextSelection(range).unsetLink().run();
            } else {
                editor.chain().focus().unsetLink().run();
            }
            return;
        }
        // 确保 URL 有协议前缀
        let href = linkUrl.trim();
        if (!/^https?:\/\//i.test(href)) href = 'https://' + href;
        if (range) {
            // 编辑已有链接 → 选中链接文字后更新
            editor.chain().focus().setTextSelection(range).setLink({ href }).run();
        } else if (editor.state.selection.empty) {
            // 无选中文本 → 插入带链接的文本，linkText 为空时用 URL 兜底
            const text = linkText || href;
            editor.chain().focus().insertContent(`<a href="${href}" target="_blank" rel="noopener noreferrer">${text}</a>`).run();
        } else {
            // 有选中文本 → 给选中文本加链接
            editor.chain().focus().setLink({ href }).run();
        }
    }, [editor, linkUrl, linkText]);

    // ========== 图片 ==========
    const handleImageClick = useCallback(() => {
        fileInputRef.current?.click();
    }, []);

    const handleFileChange = useCallback(async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (!file || !editor) return;
        // 重置 input 以便重复选择同一文件
        e.target.value = '';
        setImageUploading(true);
        try {
            const res = await simpleUpload(file);
            const dto = res.data.data;
            const url = dto.presignedUrl || dto.fileUrl;
            if (url) {
                editor.chain().focus().setImage({ src: url }).run();
            }
        } catch {
            SxwlMessage.error('图片上传失败，请重试');
        } finally {
            setImageUploading(false);
        }
    }, [editor]);

    // ========== 表格 ==========
    const insertTable = useCallback((rows: number, cols: number) => {
        if (!editor) return;
        setShowTablePicker(false);
        editor.chain().focus().insertTable({ rows, cols, withHeaderRow: true }).run();
    }, [editor]);

    // ========== 工具栏渲染 ==========
    if (!editor) return null;

    const headingLabel = editor.isActive('heading', { level: 1 }) ? '标题 1'
        : editor.isActive('heading', { level: 2 }) ? '标题 2'
        : editor.isActive('heading', { level: 3 }) ? '标题 3'
        : editor.isActive('heading', { level: 4 }) ? '标题 4'
        : '正文';

    return (
        <>
            <style>{`
                @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
                .sxwl-rich-editor table { border-collapse: collapse; margin: 0; overflow: hidden; table-layout: fixed; width: 100%; }
                .sxwl-rich-editor td, .sxwl-rich-editor th { border: 1px solid #ced4da; padding: 8px 12px; position: relative; min-width: 80px; vertical-align: top; }
                .sxwl-rich-editor th { background: #f8f9fa; font-weight: 600; text-align: left; }
                .sxwl-rich-editor .selectedCell::after { background: rgba(200,200,255,.4); content:''; inset:0; pointer-events:none; position:absolute; z-index:2; }
                .sxwl-rich-editor p { margin: 0; }
                .sxwl-rich-editor img { max-width: 100%; height: auto; border-radius: 6px; }
                .sxwl-rich-editor code { background: #f5f5f5; border: 1px solid #e8e8e8; border-radius: 3px; padding: 2px 6px; font-size: 0.9em; font-family: 'Consolas','Monaco','Courier New',monospace; color: #d63384; }
            `}</style>
            <div className="sxwl-rich-editor" style={{
            border: '1px solid #d9d9d9', borderRadius: 6, overflow: 'hidden',
        }}>
            {/* ========== 工具栏 ========== */}
            <div style={{
                display: 'flex', flexWrap: 'wrap', gap: 1, padding: '6px 8px',
                borderBottom: '1px solid #e8e8e8', background: '#fafafa', alignItems: 'center',
            }}>
                <ToolBtn title="撤销 (Ctrl+Z)" active={false} onClick={() => editor.chain().focus().undo().run()}>
                    <SxwlIcon name="UndoOutlined" size={16} />
                </ToolBtn>
                <ToolBtn title="重做 (Ctrl+Y)" active={false} onClick={() => editor.chain().focus().redo().run()}>
                    <SxwlIcon name="RedoOutlined" size={16} />
                </ToolBtn>
                <Divider />

                {/* 标题下拉 */}
                <select value={headingLabel} onChange={(e) => {
                    const v = e.target.value;
                    if (v === '正文') editor.chain().focus().setParagraph().run();
                    else if (v === '标题 1') editor.chain().focus().toggleHeading({ level: 1 }).run();
                    else if (v === '标题 2') editor.chain().focus().toggleHeading({ level: 2 }).run();
                    else if (v === '标题 3') editor.chain().focus().toggleHeading({ level: 3 }).run();
                    else if (v === '标题 4') editor.chain().focus().toggleHeading({ level: 4 }).run();
                }} style={selectStyle}>
                    <option>正文</option><option>标题 1</option><option>标题 2</option><option>标题 3</option><option>标题 4</option>
                </select>
                <Divider />

                <ToolBtn title="加粗" active={editor.isActive('bold')} onClick={() => editor.chain().focus().toggleBold().run()}><SxwlIcon name="BoldOutlined" size={16} /></ToolBtn>
                <ToolBtn title="斜体" active={editor.isActive('italic')} onClick={() => editor.chain().focus().toggleItalic().run()}><SxwlIcon name="ItalicOutlined" size={16} /></ToolBtn>
                <ToolBtn title="下划线" active={editor.isActive('underline')} onClick={() => editor.chain().focus().toggleUnderline().run()}><SxwlIcon name="UnderlineOutlined" size={16} /></ToolBtn>
                <ToolBtn title="删除线" active={editor.isActive('strike')} onClick={() => editor.chain().focus().toggleStrike().run()}><SxwlIcon name="StrikethroughOutlined" size={16} /></ToolBtn>
                <ToolBtn title="行内代码" active={editor.isActive('code')} onClick={() => editor.chain().focus().toggleCode().run()}><SxwlIcon name="CodeOutlined" size={16} /></ToolBtn>
                <Divider />

                <ColorBtn title="文字颜色" color={editor.getAttributes('textStyle').color || ''}
                    onChange={(c) => { if (c) editor.chain().focus().setColor(c).run(); else editor.chain().focus().unsetColor().run(); }}>
                    <SxwlIcon name="FontColorsOutlined" size={16} />
                </ColorBtn>
                <ColorBtn title="背景高亮" color={editor.isActive('highlight') ? '#fde047' : ''}
                    onChange={(c) => { if (c) editor.chain().focus().toggleHighlight({ color: c }).run(); else editor.chain().focus().unsetHighlight().run(); }}>
                    <SxwlIcon name="HighlightOutlined" size={16} />
                </ColorBtn>
                <Divider />

                <ToolBtn title="左对齐" active={editor.isActive({ textAlign: 'left' })} onClick={() => editor.chain().focus().setTextAlign('left').run()}><SxwlIcon name="AlignLeftOutlined" size={16} /></ToolBtn>
                <ToolBtn title="居中" active={editor.isActive({ textAlign: 'center' })} onClick={() => editor.chain().focus().setTextAlign('center').run()}><SxwlIcon name="AlignCenterOutlined" size={16} /></ToolBtn>
                <ToolBtn title="右对齐" active={editor.isActive({ textAlign: 'right' })} onClick={() => editor.chain().focus().setTextAlign('right').run()}><SxwlIcon name="AlignRightOutlined" size={16} /></ToolBtn>
                <ToolBtn title="两端对齐" active={editor.isActive({ textAlign: 'justify' })} onClick={() => editor.chain().focus().setTextAlign('justify').run()}><IconAlignJustify /></ToolBtn>
                <Divider />

                <ToolBtn title="无序列表" active={editor.isActive('bulletList')} onClick={() => editor.chain().focus().toggleBulletList().run()}><SxwlIcon name="UnorderedListOutlined" size={16} /></ToolBtn>
                <ToolBtn title="有序列表" active={editor.isActive('orderedList')} onClick={() => editor.chain().focus().toggleOrderedList().run()}><SxwlIcon name="OrderedListOutlined" size={16} /></ToolBtn>
                <Divider />

                <ToolBtn title="引用" active={editor.isActive('blockquote')} onClick={() => editor.chain().focus().toggleBlockquote().run()}><SxwlIcon name="CommentOutlined" size={16} /></ToolBtn>
                <ToolBtn title="分隔线" active={false} onClick={() => editor.chain().focus().setHorizontalRule().run()}><SxwlIcon name="MinusOutlined" size={16} /></ToolBtn>
                <ToolBtn title="代码块" active={editor.isActive('codeBlock')} onClick={() => editor.chain().focus().toggleCodeBlock().run()}><SxwlIcon name="BlockOutlined" size={16} /></ToolBtn>
                <Divider />

                {/* 链接按钮 + 弹窗 */}
                <div style={{ position: 'relative' }}>
                    <ToolBtn title="插入链接" active={editor.isActive('link')} onClick={handleLinkClick}><SxwlIcon name="LinkOutlined" size={16} /></ToolBtn>
                    {showLinkPopover && (
                        <div ref={linkPopoverRef} style={popoverStyle}>
                            <div style={{ marginBottom: 6, fontSize: 12, color: '#999' }}>
                                {editor.isActive('link') ? '编辑链接' : '插入链接'}
                            </div>
                            <input placeholder="链接地址" value={linkUrl} onChange={(e) => setLinkUrl(e.target.value)}
                                style={inputStyle} autoFocus onKeyDown={(e) => { if (e.key === 'Enter') handleLinkConfirm(); }} />
                            <input placeholder="显示文本（可选）" value={linkText}
                                onChange={(e) => setLinkText(e.target.value)}
                                style={{ ...inputStyle, marginTop: 4 }}
                                onKeyDown={(e) => { if (e.key === 'Enter') handleLinkConfirm(); }} />
                            <div style={{ display: 'flex', gap: 6, marginTop: 8, justifyContent: 'flex-end' }}>
                                <button onClick={() => setShowLinkPopover(false)} style={btnSmStyle}>取消</button>
                                <button onClick={handleLinkConfirm} style={{ ...btnSmStyle, background: '#1677ff', color: '#fff', borderColor: '#1677ff' }}>确定</button>
                            </div>
                        </div>
                    )}
                </div>

                {/* 图片按钮 + 隐藏文件选择 */}
                <ToolBtn title="上传图片" active={false} onClick={handleImageClick}>
                    {imageUploading ? <Spinner /> : <SxwlIcon name="PictureOutlined" size={16} />}
                </ToolBtn>
                <input ref={fileInputRef} type="file" accept="image/*"
                    onChange={handleFileChange} style={{ display: 'none' }} />

                {/* 表格按钮 + 网格选择器 */}
                <div style={{ position: 'relative' }}>
                    <ToolBtn title="插入表格" active={false} onClick={() => setShowTablePicker(!showTablePicker)}><SxwlIcon name="TableOutlined" size={16} /></ToolBtn>
                    {showTablePicker && (
                        <div ref={tablePickerRef} style={{ ...popoverStyle, padding: 8, userSelect: 'none' }}>
                            <div style={{ fontSize: 12, color: '#999', marginBottom: 6 }}>
                                {tableSize.rows > 0 && tableSize.cols > 0
                                    ? `${tableSize.rows} × ${tableSize.cols}`
                                    : '选择表格大小'}
                            </div>
                            <div onMouseLeave={() => setTableSize({ rows: 0, cols: 0 })}>
                                {Array.from({ length: 8 }, (_, r) => (
                                    <div key={r} style={{ display: 'flex' }}>
                                        {Array.from({ length: 10 }, (_, c) => {
                                            const active = r < tableSize.rows && c < tableSize.cols;
                                            return (
                                                <div key={c}
                                                    onMouseEnter={() => setTableSize({ rows: r + 1, cols: c + 1 })}
                                                    onClick={() => insertTable(r + 1, c + 1)}
                                                    style={{
                                                        width: 20, height: 20, margin: 1,
                                                        background: active ? '#bae0ff' : '#f0f0f0',
                                                        border: `1px solid ${active ? '#1677ff' : '#e0e0e0'}`,
                                                        cursor: 'pointer', borderRadius: 2,
                                                    }}
                                                />
                                            );
                                        })}
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>

                <Divider />
                <ToolBtn title="清除格式" active={false} onClick={() => editor.chain().focus().clearNodes().unsetAllMarks().run()}><SxwlIcon name="ClearOutlined" size={16} /></ToolBtn>
            </div>

            {/* ========== 编辑区 ========== */}
            <div style={{ padding: '12px 16px' }}>
                <EditorContent editor={editor} />
            </div>
        </div>
        </>
    );
};

/* ======================== 子组件 ======================== */

const Divider: React.FC = () => (
    <span style={{ display: 'inline-block', width: 1, height: 20, background: '#d9d9d9', margin: '0 4px', alignSelf: 'center' }} />
);

const Spinner: React.FC = () => (
    <span style={{
        display: 'inline-block', width: 14, height: 14, border: '2px solid #ddd',
        borderTopColor: '#1677ff', borderRadius: '50%', animation: 'spin 0.6s linear infinite',
    }} />
);

const ToolBtn: React.FC<{ title: string; active: boolean; onClick: () => void; children: React.ReactNode }> =
    ({ title, active, onClick, children }) => (
        <button type="button" title={title} onClick={onClick}
            style={{
                width: 30, height: 28, border: '1px solid transparent', borderRadius: 4,
                background: active ? '#e6f4ff' : 'transparent', cursor: 'pointer',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                color: active ? '#1677ff' : '#555', padding: 0, flexShrink: 0,
            }}
            onMouseEnter={(e) => { if (!active) e.currentTarget.style.background = '#f0f0f0'; }}
            onMouseLeave={(e) => { if (!active) e.currentTarget.style.background = 'transparent'; }}
        >
            {children}
        </button>
    );

/** rgb(r,g,b) → #rrggbb，<input type=color> 只接受 hex */
const toHexColor = (c: string): string => {
    const m = c.match(/rgb\((\d+),\s*(\d+),\s*(\d+)\)/);
    if (m) {
        return '#' + [m[1], m[2], m[3]].map((n) => parseInt(n).toString(16).padStart(2, '0')).join('');
    }
    return c || '#000000';
};

const ColorBtn: React.FC<{ title: string; color: string; onChange: (c: string) => void; children: React.ReactNode }> =
    ({ title, color, onChange, children }) => {
        const ref = useRef<HTMLInputElement>(null);
        return (
            <>
                <button type="button" title={title} onClick={() => ref.current?.click()}
                    style={{
                        width: 30, height: 28, border: '1px solid transparent', borderRadius: 4,
                        background: color ? '#e6f4ff' : 'transparent', cursor: 'pointer',
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        color: color || '#555', padding: 0, flexShrink: 0, position: 'relative',
                    }}>
                    {children}
                    {color && <span style={{ position: 'absolute', bottom: 2, left: '50%', transform: 'translateX(-50%)', width: 14, height: 3, borderRadius: 1, background: color }} />}
                </button>
                <input ref={ref} type="color" value={toHexColor(color)}
                    onChange={(e) => onChange(e.target.value)}
                    style={{ position: 'absolute', width: 0, height: 0, opacity: 0, pointerEvents: 'none' }} />
            </>
        );
    };

/* ======================== 样式常量 ======================== */

const selectStyle: React.CSSProperties = {
    height: 28, border: '1px solid #d9d9d9', borderRadius: 4,
    fontSize: 13, padding: '0 4px', cursor: 'pointer', background: '#fff', color: '#333', outline: 'none',
};
const popoverStyle: React.CSSProperties = {
    position: 'absolute', top: 34, left: 0, zIndex: 1000,
    background: '#fff', border: '1px solid #d9d9d9', borderRadius: 8,
    padding: 12, boxShadow: '0 4px 16px rgba(0,0,0,.12)', minWidth: 220,
};
const inputStyle: React.CSSProperties = {
    width: '100%', height: 30, border: '1px solid #d9d9d9', borderRadius: 4,
    fontSize: 13, padding: '0 8px', outline: 'none', boxSizing: 'border-box',
};
const btnSmStyle: React.CSSProperties = {
    height: 28, padding: '0 12px', border: '1px solid #d9d9d9', borderRadius: 4,
    background: '#fff', cursor: 'pointer', fontSize: 12,
};

/* ======================== SVG 图标（仅两端对齐图标库缺失，保留内联） ======================== */

const svg = (d: string, size = 16) => (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor"
        strokeWidth={2} strokeLinecap="round" strokeLinejoin="round"><path d={d} /></svg>
);
const IconAlignJustify = () => svg('M21 10H3M21 6H3M21 14H3M21 18H3');

export default SxwlRichTextEditor;
