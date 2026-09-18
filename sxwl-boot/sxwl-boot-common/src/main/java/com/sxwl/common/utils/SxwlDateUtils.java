package com.sxwl.common.utils;

import com.sxwl.common.constants.SxwlSystemConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间工具类
 *
 * <p>基于 Java 8 {@code java.time} API 封装，提供常用的日期时间计算功能。
 * 支持工作日计算、节假日预留接口、格式化转换等。</p>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 当前时间
 * LocalDateTime now = SxwlDateUtils.now();
 * 
 * // 日期加减
 * LocalDateTime future = SxwlDateUtils.addDays(now, 7);
 * LocalDateTime past = SxwlDateUtils.addMonths(now, -1);
 * 
 * // 工作日计算
 * LocalDateTime workDay = SxwlDateUtils.addWorkDays(new Date(), 5);
 * 
 * // 格式化
 * String dateStr = SxwlDateUtils.formatDate(new Date(), "yyyy-MM-dd");
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public final class SxwlDateUtils {

    private SxwlDateUtils() {
        throw new UnsupportedOperationException("SxwlDateUtils 工具类，不允许实例化");
    }

    /**
     * 默认日期时间格式化器（线程安全）
     */
    private static final ThreadLocal<DateTimeFormatter> DATETIME_FORMATTER =
            ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern(SxwlSystemConstants.DATE_TIME_PATTERN));

    /**
     * 标准日期格式化器（线程安全）
     */
    private static final ThreadLocal<DateTimeFormatter> DATE_FORMATTER =
            ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern(SxwlSystemConstants.DATE_PATTERN));

    /**
     * 获取当前日期时间
     *
     * @return 当前 {@code LocalDateTime}
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     *
     * @return 当前 {@code LocalDate}
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 获取当前时间
     *
     * @return 当前 {@code LocalTime}
     */
    public static LocalTime currentTime() {
        return LocalTime.now();
    }

    /**
     * 将 Date 转换为 LocalDateTime
     *
     * @param date Date 对象
     * @return {@code LocalDateTime}
     */
    public static LocalDateTime localDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将 LocalDateTime 转换为 Date
     *
     * @param localDateTime {@code LocalDateTime}
     * @return {@code Date}
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 格式化日期时间为字符串
     *
     * @param dateTime {@code LocalDateTime}
     * @param pattern  格式模式（如 "yyyy-MM-dd HH:mm:ss"）
     * @return 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null || pattern.isEmpty()) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化日期为字符串（默认 yyyy-MM-dd）
     *
     * @param date {@code Date} 对象
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return localDateTime(date).format(DATE_FORMATTER.get());
    }

    /**
     * 格式化日期为字符串（自定义格式）
     *
     * @param date    {@code Date} 对象
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null || pattern == null || pattern.isEmpty()) {
            return null;
        }
        return localDateTime(date).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 解析日期字符串为 Date 对象
     *
     * @param dateStr 日期字符串
     * @param pattern 格式模式
     * @return {@code Date} 对象，解析失败返回 null
     */
    public static Date parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty() || pattern == null || pattern.isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate date = LocalDate.parse(dateStr.trim(), formatter);
            return toDate(date.atStartOfDay());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 添加指定的天数
     *
     * @param dateTime 起始时间
     * @param days     天数（正数表示未来，负数表示过去）
     * @return 加法结果 {@code LocalDateTime}
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusDays(days);
    }

    /**
     * 添加指定的月数
     *
     * @param dateTime 起始时间
     * @param months   月数
     * @return 加法结果 {@code LocalDateTime}
     */
    public static LocalDateTime addMonths(LocalDateTime dateTime, int months) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusMonths(months);
    }

    /**
     * 添加指定的年数
     *
     * @param dateTime 起始时间
     * @param years    年数
     * @return 加法结果 {@code LocalDateTime}
     */
    public static LocalDateTime addYears(LocalDateTime dateTime, int years) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusYears(years);
    }

    /**
     * 添加指定的小时数
     *
     * @param dateTime 起始时间
     * @param hours    小时数
     * @return 加法结果 {@code LocalDateTime}
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }

    /**
     * 计算两个日期之间的天数差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 天数差（endDate - startDate）
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 判断是否为工作日（周一到周五）
     *
     * @param date 日期
     * @return true=工作日，false=周末
     */
    public static boolean isWorkDay(Date date) {
        if (date == null) {
            return false;
        }
        LocalDateTime ldt = localDateTime(date);
        DayOfWeek dayOfWeek = ldt.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    /**
     * 添加工作日（跳过周末）
     *
     * @param date   起始日期
     * @param days   工作日数量（正数表示未来，负数表示过去）
     * @return 结果 {@code Date}
     */
    public static Date addWorkDays(Date date, int days) {
        if (date == null) {
            return null;
        }

        LocalDateTime current = localDateTime(date);
        int direction = days > 0 ? 1 : -1;
        int remainingDays = Math.abs(days);

        for (int i = 0; i < remainingDays; i++) {
            do {
                current = current.plusMinutes(1 * direction);
            } while (!isWorkDay(Date.from(current.atZone(java.time.ZoneId.systemDefault()).toInstant())));
        }

        return Date.from(current.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取指定日期的星期几
     *
     * @param date 日期
     * @return 星期几中文名称（星期一、星期二...）
     */
    public static String getWeekName(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime ldt = localDateTime(date);
        DayOfWeek dayOfWeek = ldt.getDayOfWeek();
        String[] weekNames = SxwlSystemConstants.WEEK_DAYS;
        return weekNames[dayOfWeek.getValue() % 7];
    }

    /**
     * 获取当天的开始时间（00:00:00）
     *
     * @param date 日期
     * @return 当天开始时间 {@code LocalDateTime}
     */
    public static LocalDateTime startOfDay(Date date) {
        if (date == null) {
            return null;
        }
        return localDateTime(date).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 获取当天的结束时间（23:59:59.999999999）
     *
     * @param date 日期
     * @return 当天结束时间 {@code LocalDateTime}
     */
    public static LocalDateTime endOfDay(Date date) {
        if (date == null) {
            return null;
        }
        return localDateTime(date).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    /**
     * 比较两个日期是否同一天
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return true=同一天，false=不同天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        LocalDate d1 = localDateTime(date1).toLocalDate();
        LocalDate d2 = localDateTime(date2).toLocalDate();
        return d1.isEqual(d2);
    }

    /**
     * 检查日期是否在指定范围内（包含边界）
     *
     * @param checkDate 待检查日期
     * @param startDate 范围开始日期
     * @param endDate   范围结束日期
     * @return true=在范围内，false=不在范围内
     */
    public static boolean isBetween(Date checkDate, Date startDate, Date endDate) {
        if (checkDate == null || startDate == null || endDate == null) {
            return false;
        }
        LocalDateTime check = localDateTime(checkDate);
        LocalDateTime start = localDateTime(startDate);
        LocalDateTime end = localDateTime(endDate);
        return !check.isBefore(start) && !check.isAfter(end);
    }
}
