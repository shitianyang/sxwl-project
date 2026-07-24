package com.sxwl.common.utils;

import com.sxwl.common.annotation.SxwlExcel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用 Excel 导入导出工具
 *
 * <p>基于 Apache POI（XSSFWorkbook），支持 .xlsx 格式。
 * 通过 {@link SxwlExcel} 注解标注字段映射关系。</p>
 *
 * <h3>导出示例</h3>
 * <pre>{@code
 * List<SysUserExportVO> list = ...;
 * SxwlExcelUtils.exportToExcel(list, response.getOutputStream(), "用户列表");
 * }</pre>
 *
 * <h3>导入示例</h3>
 * <pre>{@code
 * List<SysUserExportVO> list = SxwlExcelUtils.importFromExcel(inputStream, SysUserExportVO.class);
 * }</pre>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SxwlExcelUtils {

    private static final Logger log = LoggerFactory.getLogger(SxwlExcelUtils.class);

    private SxwlExcelUtils() {
    }

    // ==================== 导出 ====================

    /**
     * 将数据列表导出为 Excel 文件到输出流
     *
     * @param data     数据列表
     * @param output   输出流
     * @param sheetName Sheet 名称
     * @param <T>      数据类型
     */
    public static <T> void exportToExcel(List<T> data, OutputStream output, String sheetName) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Sheet1");

            if (data == null || data.isEmpty()) {
                // 空数据也创建表头
                createHeader(workbook, sheet, null);
                workbook.write(output);
                return;
            }

            // 解析字段映射
            List<FieldInfo> fields = resolveFields(data.get(0).getClass());
            if (fields.isEmpty()) {
                log.warn("未找到 @SxwlExcel 注解字段，无法导出: {}", data.get(0).getClass());
                createHeader(workbook, sheet, null);
                workbook.write(output);
                return;
            }

            // 创建表头行
            createHeader(workbook, sheet, fields);

            // 填充数据行
            CellStyle dateStyle = createDateCellStyle(workbook);
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                T item = data.get(i);
                for (int j = 0; j < fields.size(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = fields.get(j).getValue(item);
                    setCellValue(cell, value, fields.get(j).dateFormat, dateStyle);
                }
            }

            // 自动调整列宽（最大 50 字符）
            for (int i = 0; i < fields.size(); i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) > 256 * 50) {
                    sheet.setColumnWidth(i, 256 * 50);
                }
            }

            workbook.write(output);
        } catch (Exception e) {
            throw new RuntimeException("Excel 导出失败: " + e.getMessage(), e);
        }
    }

    // ==================== 导入 ====================

    /**
     * 从 Excel 文件导入数据列表
     *
     * @param input       输入流
     * @param targetClass 目标类型（需标注 @SxwlExcel）
     * @param <T>         目标类型
     * @return 数据列表
     */
    public static <T> List<T> importFromExcel(InputStream input, Class<T> targetClass) {
        List<T> result = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(input)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                return result; // 只有表头或空文件
            }

            // 解析表头 → 列索引映射
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return result;
            }

            Map<Integer, FieldInfo> columnIndexMap = resolveHeaderMapping(headerRow, targetClass);
            if (columnIndexMap.isEmpty()) {
                log.warn("Excel 表头与 @SxwlExcel 字段名不匹配，无法导入: {}", targetClass);
                return result;
            }

            // 逐行读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                T instance = targetClass.getDeclaredConstructor().newInstance();
                boolean hasValue = false;

                for (Map.Entry<Integer, FieldInfo> entry : columnIndexMap.entrySet()) {
                    Cell cell = row.getCell(entry.getKey());
                    if (cell == null) {
                        continue;
                    }
                    Object cellValue = getCellValue(cell, entry.getValue().field.getType(),
                            entry.getValue().dateFormat);
                    if (cellValue != null) {
                        entry.getValue().field.setAccessible(true);
                        entry.getValue().field.set(instance, cellValue);
                        hasValue = true;
                    }
                }

                if (hasValue) {
                    result.add(instance);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Excel 导入失败: " + e.getMessage(), e);
        }
        return result;
    }

    // ==================== 内部方法 ====================

    /** 解析字段映射（按 order + 字段名排序） */
    private static List<FieldInfo> resolveFields(Class<?> clazz) {
        List<FieldInfo> list = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            SxwlExcel annotation = field.getAnnotation(SxwlExcel.class);
            if (annotation != null) {
                list.add(new FieldInfo(field, annotation));
            }
        }
        // 递归处理父类字段
        Class<?> current = clazz.getSuperclass();
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                SxwlExcel annotation = field.getAnnotation(SxwlExcel.class);
                if (annotation != null) {
                    list.add(new FieldInfo(field, annotation));
                }
            }
            current = current.getSuperclass();
        }
        list.sort((a, b) -> {
            int cmp = Integer.compare(a.annotation.order(), b.annotation.order());
            return cmp != 0 ? cmp : a.field.getName().compareTo(b.field.getName());
        });
        return list;
    }

    /** 解析表头 → 字段映射 */
    private static <T> Map<Integer, FieldInfo> resolveHeaderMapping(Row headerRow, Class<T> targetClass) {
        List<FieldInfo> fields = resolveFields(targetClass);
        Map<String, FieldInfo> nameToField = new HashMap<>();
        for (FieldInfo f : fields) {
            nameToField.put(f.annotation.name(), f);
        }

        Map<Integer, FieldInfo> result = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) continue;
            String headerName = getCellStringValue(cell);
            if (headerName != null && nameToField.containsKey(headerName)) {
                result.put(i, nameToField.get(headerName));
            }
        }
        return result;
    }

    /** 创建表头行 */
    private static void createHeader(Workbook workbook, Sheet sheet, List<FieldInfo> fields) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        if (fields == null || fields.isEmpty()) {
            headerRow.createCell(0).setCellValue("无数据");
            return;
        }

        for (int i = 0; i < fields.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields.get(i).annotation.name());
            cell.setCellStyle(headerStyle);
        }
    }

    private static CellStyle createDateCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        return style;
    }

    private static void setCellValue(Cell cell, Object value, String dateFormat, CellStyle dateStyle) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            if (dateStyle != null) cell.setCellStyle(dateStyle);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DateTimeFormatter.ofPattern(dateFormat)));
        } else if (value instanceof LocalDate) {
            cell.setCellValue(((LocalDate) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static Object getCellValue(Cell cell, Class<?> targetType, String dateFormat) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> convertStringValue(cell.getStringCellValue(), targetType, dateFormat);
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date dateValue = cell.getDateCellValue();
                    if (targetType == LocalDateTime.class) {
                        yield convertToLocalDateTime(dateValue);
                    } else if (targetType == LocalDate.class) {
                        yield convertToLocalDate(dateValue);
                    } else if (targetType == Date.class) {
                        yield dateValue;
                    }
                    yield dateValue;
                } else {
                    double numericValue = cell.getNumericCellValue();
                    yield convertNumericValue(numericValue, targetType);
                }
            }
            case BOOLEAN -> cell.getBooleanCellValue();
            case BLANK -> null;
            default -> null;
        };
    }

    private static String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            default -> null;
        };
    }

    private static Object convertStringValue(String value, Class<?> targetType, String dateFormat) {
        if (value == null || value.isEmpty()) return null;
        if (targetType == String.class) return value;
        if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(value);
        if (targetType == Long.class || targetType == long.class) return Long.parseLong(value);
        if (targetType == BigDecimal.class) return new BigDecimal(value);
        if (targetType == LocalDateTime.class) return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(dateFormat));
        if (targetType == LocalDate.class) return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return value;
    }

    private static Object convertNumericValue(double value, Class<?> targetType) {
        if (targetType == Integer.class || targetType == int.class) return (int) value;
        if (targetType == Long.class || targetType == long.class) return (long) value;
        if (targetType == BigDecimal.class) return BigDecimal.valueOf(value);
        if (targetType == Float.class || targetType == float.class) return (float) value;
        return value;
    }

    private static LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    private static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    // ==================== 内部类 ====================

    /** 字段元信息缓存 */
    private static class FieldInfo {
        final Field field;
        final SxwlExcel annotation;
        final String dateFormat;

        FieldInfo(Field field, SxwlExcel annotation) {
            this.field = field;
            this.annotation = annotation;
            this.dateFormat = annotation.dateFormat();
        }

        Object getValue(Object target) {
            try {
                field.setAccessible(true);
                return field.get(target);
            } catch (IllegalAccessException e) {
                return null;
            }
        }
    }
}
