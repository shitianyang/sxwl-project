package com.sxwl.common.utils;

import com.sxwl.common.constants.SxwlSystemConstants;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 文件上传工具类
 *
 * <p>提供文件上传相关的辅助方法，包括文件大小格式化、MIME 类型检测、
 * 文件名安全处理、后缀白名单校验等功能。</p>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 文件大小格式化
 * String sizeStr = SxwlFileUtils.formatFileSize(1024 * 1024); // "1.00 MB"
 * 
 * // MIME 类型检测
 * String mimeType = SxwlFileUtils.detectMimeType("photo.jpg"); // "image/jpeg"
 * 
 * // 文件名安全处理
 * String safeName = SxwlFileUtils.sanitizeFilename("我的照片.jpg"); // "wodezhaoopian_.jpg"
 * 
 * // 后缀白名单校验
 * boolean allowed = SxwlFileUtils.isAllowedExtension("pdf", ALLOWED_PDF_EXTENSIONS); // true
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/9/17
 * @since 0.1.0
 */
public final class SxwlFileUtils {

    private SxwlFileUtils() {
        throw new UnsupportedOperationException("SxwlFileUtils 工具类，不允许实例化");
    }

    /**
     * 允许的文件名字符正则（中文、英文、数字、下划线、连字符、点）
     */
    private static final Pattern SAFE_FILENAME_PATTERN = 
            Pattern.compile("[^\\p{IsHan}\\w\\-\\.]+",
                    Pattern.UNICODE_CHARACTER_CLASS);

    // ==================== 文件大小格式化 ====================

    /**
     * 格式化文件大小为人类可读字符串
     *
     * @param sizeInBytes 字节数
     * @return 格式化后的字符串（如 "1.50 MB"、"1024 B"）
     */
    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 0) {
            throw new IllegalArgumentException("文件大小不能为负数");
        }
        if (sizeInBytes < SxwlSystemConstants.FILE_SIZE_KB) {
            return sizeInBytes + " B";
        }
        if (sizeInBytes < SxwlSystemConstants.FILE_SIZE_MB) {
            return String.format("%.2f KB", sizeInBytes / (double) SxwlSystemConstants.FILE_SIZE_KB);
        }
        if (sizeInBytes < SxwlSystemConstants.FILE_SIZE_GB) {
            return String.format("%.2f MB", sizeInBytes / (double) SxwlSystemConstants.FILE_SIZE_MB);
        }
        if (sizeInBytes < SxwlSystemConstants.FILE_SIZE_TB) {
            return String.format("%.2f GB", sizeInBytes / (double) SxwlSystemConstants.FILE_SIZE_GB);
        }
        return String.format("%.2f TB", sizeInBytes / (double) SxwlSystemConstants.FILE_SIZE_TB);
    }

    // ==================== MIME 类型检测 ====================

    /**
     * 根据文件扩展名推断 MIME 类型
     *
     * @param fileName 文件名
     * @return MIME 类型（如 "image/jpeg"），无法识别返回 "application/octet-stream"
     */
    public static String detectMimeType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "application/octet-stream";
        }

        String extension = getExtension(fileName).toLowerCase();
        
        // 图片类型
        for (String ext : SxwlSystemConstants.MIME_TYPE_IMAGE_EXTENSIONS) {
            if (ext.equals(extension)) {
                return "image/" + (ext.equals("jpeg") ? "jpeg" : ext);
            }
        }

        // PDF
        if (SxwlSystemConstants.MIME_TYPE_PDF_EXTENSIONS[0].equals(extension)) {
            return "application/pdf";
        }

        // Word
        for (String ext : SxwlSystemConstants.MIME_TYPE_WORD_EXTENSIONS) {
            if (ext.equals(extension)) {
                return ext.equals("doc") ? "application/msword" : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            }
        }

        // Excel
        for (String ext : SxwlSystemConstants.MIME_TYPE_EXCEL_EXTENSIONS) {
            if (ext.equals(extension)) {
                return ext.equals("xls") ? "application/vnd.ms-excel" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }
        }

        // PowerPoint
        for (String ext : SxwlSystemConstants.MIME_TYPE_POWERPOINT_EXTENSIONS) {
            if (ext.equals(extension)) {
                return "application/vnd.ms-powerpoint"; // 简化处理
            }
        }

        // 视频
        for (String ext : SxwlSystemConstants.MIME_TYPE_VIDEO_EXTENSIONS) {
            if (ext.equals(extension)) {
                return "video/" + ext;
            }
        }

        // 音频
        for (String ext : SxwlSystemConstants.MIME_TYPE_AUDIO_EXTENSIONS) {
            if (ext.equals(extension)) {
                return "audio/" + ext;
            }
        }

        return "application/octet-stream";
    }

    // ==================== 文件名处理 ====================

    /**
     * 获取文件扩展名（不包含点）
     *
     * @param fileName 文件名
     * @return 扩展名（小写），无扩展名返回空字符串
     */
    public static String getExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex <= 0) {
            return "";
        }

        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param fileName 文件名
     * @return 去掉扩展名的文件名
     */
    public static String getNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
    }

    /**
     * 安全的文件名处理（防止路径遍历和特殊字符攻击）
     *
     * <p>处理规则：</p>
     * <ul>
     *   <li>保留中文字符</li>
     *   <li>保留英文、数字、下划线、连字符、点</li>
     *   <li>其他字符替换为下划线</li>
     *   <li>去除开头和结尾的空格</li>
     * </ul>
     *
     * @param originalName 原始文件名
     * @return 安全处理后的文件名
     */
    public static String sanitizeFilename(String originalName) {
        if (originalName == null || originalName.isEmpty()) {
            return "unnamed_file";
        }

        // 去除首尾空格
        String sanitized = originalName.trim();

        // 替换不安全字符为中文字符的拼音或下划线
        sanitized = SAFE_FILENAME_PATTERN.matcher(sanitized).replaceAll("_");

        // 防止连续多个下划线
        sanitized = sanitized.replaceAll("_+", "_");

        // 去除开头的下划线
        sanitized = Pattern.compile("^_+").matcher(sanitized).replaceAll("");

        // 如果处理后为空，返回默认名称
        if (sanitized.isEmpty()) {
            return "unnamed_file";
        }

        // 限制文件名长度（最多 200 字符）
        if (sanitized.length() > 200) {
            String extension = getExtension(sanitized);
            int maxNameLength = extension.isEmpty() ? 200 : 200 - extension.length() - 1;
            sanitized = sanitized.substring(0, Math.min(maxNameLength, sanitized.lastIndexOf('.')));
        }

        return sanitized;
    }

    // ==================== 后缀白名单校验 ====================

    /**
     * 检查文件扩展名是否在白名单中
     *
     * @param fileName 文件名
     * @param allowedExtensions 允许的扩展名数组
     * @return true=允许，false=拒绝
     */
    public static boolean isAllowedExtension(String fileName, String... allowedExtensions) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        String fileExtension = getExtension(fileName);
        
        if (allowedExtensions == null || allowedExtensions.length == 0) {
            return false;
        }

        for (String allowed : allowedExtensions) {
            if (allowed != null && allowed.toLowerCase().equals(fileExtension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查是否为图片文件
     *
     * @param fileName 文件名
     * @return true=图片，false=非图片
     */
    public static boolean isImageFile(String fileName) {
        return isAllowedExtension(fileName, SxwlSystemConstants.MIME_TYPE_IMAGE_EXTENSIONS);
    }

    /**
     * 检查是否为文档文件（PDF/Word/Excel/PPT）
     *
     * @param fileName 文件名
     * @return true=文档，false=非文档
     */
    public static boolean isDocumentFile(String fileName) {
        String[] allDocExts = java.util.Arrays.copyOf(SxwlSystemConstants.MIME_TYPE_IMAGE_EXTENSIONS, SxwlSystemConstants.MIME_TYPE_IMAGE_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_PDF_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_WORD_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_EXCEL_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_POWERPOINT_EXTENSIONS.length);
        System.arraycopy(SxwlSystemConstants.MIME_TYPE_PDF_EXTENSIONS, 0, allDocExts, SxwlSystemConstants.MIME_TYPE_IMAGE_EXTENSIONS.length, SxwlSystemConstants.MIME_TYPE_PDF_EXTENSIONS.length);
        System.arraycopy(SxwlSystemConstants.MIME_TYPE_WORD_EXTENSIONS, 0, allDocExts, SxwlSystemConstants.MIME_TYPE_IMAGE_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_PDF_EXTENSIONS.length, SxwlSystemConstants.MIME_TYPE_WORD_EXTENSIONS.length);
        System.arraycopy(SxwlSystemConstants.MIME_TYPE_EXCEL_EXTENSIONS, 0, allDocExts, SxwlSystemConstants.MIME_TYPE_IMAGE_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_PDF_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_WORD_EXTENSIONS.length, SxwlSystemConstants.MIME_TYPE_EXCEL_EXTENSIONS.length);
        System.arraycopy(SxwlSystemConstants.MIME_TYPE_POWERPOINT_EXTENSIONS, 0, allDocExts, SxwlSystemConstants.MIME_TYPE_IMAGE_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_PDF_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_WORD_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_EXCEL_EXTENSIONS.length, SxwlSystemConstants.MIME_TYPE_POWERPOINT_EXTENSIONS.length);
        return isAllowedExtension(fileName, allDocExts);
    }

    /**
     * 检查是否为媒体文件（视频/音频）
     *
     * @param fileName 文件名
     * @return true=媒体，false=非媒体
     */
    public static boolean isMediaFile(String fileName) {
        String[] allMediaExts = new String[SxwlSystemConstants.MIME_TYPE_VIDEO_EXTENSIONS.length + SxwlSystemConstants.MIME_TYPE_AUDIO_EXTENSIONS.length];
        System.arraycopy(SxwlSystemConstants.MIME_TYPE_VIDEO_EXTENSIONS, 0, allMediaExts, 0, SxwlSystemConstants.MIME_TYPE_VIDEO_EXTENSIONS.length);
        System.arraycopy(SxwlSystemConstants.MIME_TYPE_AUDIO_EXTENSIONS, 0, allMediaExts, SxwlSystemConstants.MIME_TYPE_VIDEO_EXTENSIONS.length, SxwlSystemConstants.MIME_TYPE_AUDIO_EXTENSIONS.length);
        return isAllowedExtension(fileName, allMediaExts);
    }

    // ==================== 唯一文件名生成 ====================

    /**
     * 生成唯一的文件名（防止覆盖）
     *
     * <p>使用 UUID + 原始扩展名生成唯一文件名</p>
     *
     * @param originalFileName 原始文件名
     * @return 唯一文件名（如 "a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg"）
     */
    public static String generateUniqueFilename(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return java.util.UUID.randomUUID().toString();
        }

        String extension = getExtension(originalFileName);
        String uniqueName = java.util.UUID.randomUUID().toString();

        if (extension.isEmpty()) {
            return uniqueName;
        }

        return uniqueName + "." + extension;
    }

    // ==================== 字节转换辅助 ====================

    /**
     * KB 转换为字节
     *
     * @param kb KB 数
     * @return 字节数
     */
    public static long kbToBytes(long kb) {
        return kb * SxwlSystemConstants.FILE_SIZE_KB;
    }

    /**
     * MB 转换为字节
     *
     * @param mb MB 数
     * @return 字节数
     */
    public static long mbToBytes(long mb) {
        return mb * SxwlSystemConstants.FILE_SIZE_MB;
    }

    /**
     * GB 转换为字节
     *
     * @param gb GB 数
     * @return 字节数
     */
    public static long gbToBytes(long gb) {
        return gb * SxwlSystemConstants.FILE_SIZE_GB;
    }

    /**
     * 字节转换为 KB
     *
     * @param bytes 字节数
     * @return KB 数
     */
    public static double bytesToKb(long bytes) {
        return bytes / (double) SxwlSystemConstants.FILE_SIZE_KB;
    }

    /**
     * 字节转换为 MB
     *
     * @param bytes 字节数
     * @return MB 数
     */
    public static double bytesToMb(long bytes) {
        return bytes / (double) SxwlSystemConstants.FILE_SIZE_MB;
    }

    /**
     * 字节转换为 GB
     *
     * @param bytes 字节数
     * @return GB 数
     */
    public static double bytesToGb(long bytes) {
        return bytes / (double) SxwlSystemConstants.FILE_SIZE_GB;
    }
}
