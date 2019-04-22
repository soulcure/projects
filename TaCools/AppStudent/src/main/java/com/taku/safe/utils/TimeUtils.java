package com.taku.safe.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * TimeUtils
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    public static final SimpleDateFormat SECOND_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT_TEXT = new SimpleDateFormat(
            "MM月dd日 HH:mm:ss", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT_HOUR = new SimpleDateFormat(
            "HH:mm", Locale.CHINA);
    public static final SimpleDateFormat MONTH_OF_YEAR = new SimpleDateFormat(
            "yyyy-MM", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT_MONTH = new SimpleDateFormat(
            "dd/MM", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT_MONTH_TEXT = new SimpleDateFormat(
            "MM月dd日", Locale.CHINA);

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * 获取当前天
     *
     * @param timeInMillis
     * @return
     */
    public static String getDate(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_DATE);
    }


    /**
     * 获取当前天
     *
     * @param timeInMillis
     * @return
     */
    public static String getDateText(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_TEXT);
    }

    /**
     * 日期格式中解析日历
     *
     * @param str
     * @param sdf
     * @return
     * @throws ParseException
     */
    public static Calendar parseDate(String str, SimpleDateFormat sdf) throws ParseException {
        Date date = sdf.parse(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    /**
     * 日期格式转换
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static String parseDate(String str) throws ParseException {
        Date date = DATE_FORMAT_DATE.parse(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return DATE_FORMAT_MONTH_TEXT.format(calendar.getTime());
    }

    /**
     * 日期格式转换
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static String parseDateFormat(String str, SimpleDateFormat format) throws ParseException {
        Date date = DEFAULT_DATE_FORMAT.parse(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return format.format(calendar.getTime());
    }


    /**
     * 当月第一天
     *
     * @return
     */
    private static String getFirstDayOfMonth(String str) {
        try {
            Date theDate = DATE_FORMAT_DATE.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(theDate);

            calendar.set(Calendar.DATE, 1);
            Date firstDayOfMonth = calendar.getTime();

            return DATE_FORMAT_DATE.format(firstDayOfMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 当月最后一天
     *
     * @return
     */
    private static String getLastDayOfMonth(String str) {
        try {
            Date theDate = DATE_FORMAT_DATE.parse(str);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(theDate);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            Date lastDayOfMonth = calendar.getTime();

            return DATE_FORMAT_DATE.format(lastDayOfMonth);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * @param timeStamp 时间戳
     * @return 格式：月-日（当年）、年-月-日（去年以前）
     */
    public static String getFormateDate5(long timeStamp) {

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int yearNow = calendar.get(Calendar.YEAR);
        Date date = new Date(timeStamp);
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        SimpleDateFormat sdf;
        if (year == yearNow) {
            sdf = new SimpleDateFormat("MM-dd HH:mm");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        return sdf.format(date);
    }


    /**
     * 单位秒转时间
     *
     * @return 20小时18分钟30秒
     */
    public static String calculateTime(long time) {
        int seconds = (int) (time / 1000);

        int hour = 0;
        int minute = 0;
        int second = 0;

        if (time <= 0) {
            time = 0;
        }

        if (time >= 3600) {
            hour = seconds / 3600;
            minute = seconds % 3600 / 60;
            second = seconds % 3600 % 60;
        } else if (time >= 60) {
            minute = seconds / 60;
            second = seconds % 60;
        }

        if (hour >= 1) {
            return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format(Locale.CHINA, "%02d:%02d", minute, second);
        }

    }

}
