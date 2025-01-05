package com.losgai.engineerhelper.helper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    // 使用 ThreadLocal 确保 SimpleDateFormat 的线程安全性
    public static final ThreadLocal<SimpleDateFormat> dateFormat =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault())); // 格式化为"yyyy-MM-dd"

    /**
     * 将年、月、日封装成 Date 对象
     *
     * @param year  年字符串（如 "2023"）
     * @param month 月字符串（如 "12"）
     * @param day   日字符串（如 "30"）
     * @return 对应的 Date 对象，或者 null（如果解析失败）
     */
    public static Date createDateFromInput(String year, String month, String day) throws ParseException {
        // 构造 "yyyy-MM-dd" 格式的中间字符串
        String isoDateStr = year + "-" + month + "-" + day;
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return isoDateFormat.parse(isoDateStr);
    }

    /**
     * 将 Date 对象解析成年、月、日字符串
     *
     * @param date 要解析的日期
     * @return 包含年、月、日的字符串数组
     */
    public static String[] dateToStringArray(Date date) {
        // 格式化 Date 为 "yyyy-MM-dd"
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String formattedDate = isoDateFormat.format(date);
        String[] split = formattedDate.split("-");
        Log.i("日期转年月日", split[0] + " " + split[1] + " " + split[2]);
        return split;
    }

    /**
     * 将 Date 对象解析成对应格式字符串
     *
     * @param date 要解析的日期
     * @return 包含年、月、日的字符串数组
     */
    public static String dateToString(Date date) {
        // 格式化 Date 为 "yyyy-MM-dd"
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return isoDateFormat.format(date);
    }
}
