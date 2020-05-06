package com.zz.redis.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author ASUS
 */
public class DateUtil {

    private static Log log = LogFactory.getLog(DateUtil.class);
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_TIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_YYYYMMDD_PATTERN = "yyyyMMdd";
    public static final String DATE_YYYY_MM_DD_PATTERN = "yyyy-MM-dd";
    public static final String TIME_HHMM_PATTERN = "HH:mm";
    public static final String TIME_HHMM_PATTERN2 = "HHmm";
    public static final String DATE_TIME_NO_HORI_PATTERN = "yyyyMMdd HH:mm:ss";
    public static final String DATE_TIME_NO_SPACE_PATTERN = "yyyyMMddHHmmss";
    public static final String DATE_TIME_NO_SPACE_MS_PATTERN = "yyyyMMddHHmmssS";
    public static final String DATE_TIME_PLAYBILL_PATTERN = "yyyyMMdd HH:mm";
    public static final String DATE_TIME_INDEX_PLAYBILL_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_ENGLISH_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

    public static final String DATE_NOYEAR_FORMATE = "MM.dd HH:mm";

    private static final int[] WEEKSARRAYS = {0, 1, 2, 3, 4, 5, 6};

    private static final String[] WEEKSARRAYSTR = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    private static Map<String, SimpleDateFormat> patternFormatMap;

    public static synchronized Map<String, SimpleDateFormat> getInstance() {

        if (patternFormatMap == null) {
            SimpleDateFormat timeHmsFormat = new SimpleDateFormat(
                    TIME_PATTERN);
            SimpleDateFormat timeFormat = new SimpleDateFormat(
                    DATE_TIME_MS_PATTERN);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    DATE_TIME_PATTERN);
            SimpleDateFormat yyyyMMdd = new SimpleDateFormat(
                    DATE_YYYYMMDD_PATTERN);
            SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat(
                    DATE_YYYY_MM_DD_PATTERN);
            SimpleDateFormat HHmm = new SimpleDateFormat(TIME_HHMM_PATTERN);
            SimpleDateFormat HHmm2 = new SimpleDateFormat(TIME_HHMM_PATTERN2);
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat(
                    DATE_TIME_NO_HORI_PATTERN);
            SimpleDateFormat yyyyMMddHHmmssFile = new SimpleDateFormat(
                    DATE_TIME_NO_SPACE_PATTERN);
            SimpleDateFormat yyyyMMddHHmmssSFile = new SimpleDateFormat(
                    DATE_TIME_NO_SPACE_MS_PATTERN);
            SimpleDateFormat PLAYBILL_TIME_PATTERN = new SimpleDateFormat(
                    DATE_TIME_PLAYBILL_PATTERN);
            SimpleDateFormat INDEX_PLAYBILL_TIME_PATTERN = new SimpleDateFormat(
                    DATE_TIME_INDEX_PLAYBILL_PATTERN);
            SimpleDateFormat ENGLISH_SDF = new SimpleDateFormat(
                    DATE_ENGLISH_FORMAT, Locale.ENGLISH);

            patternFormatMap = new HashMap<String, SimpleDateFormat>();
            patternFormatMap.put(DATE_TIME_MS_PATTERN, timeFormat);
            patternFormatMap.put(DATE_TIME_PATTERN, dateFormat);
            patternFormatMap.put(DATE_YYYYMMDD_PATTERN, yyyyMMdd);
            patternFormatMap.put(TIME_HHMM_PATTERN, HHmm);
            patternFormatMap.put(TIME_HHMM_PATTERN2, HHmm2);
            patternFormatMap.put(DATE_TIME_NO_HORI_PATTERN, yyyyMMddHHmmss);
            patternFormatMap
                    .put(DATE_TIME_NO_SPACE_PATTERN, yyyyMMddHHmmssFile);
            patternFormatMap.put(DATE_TIME_PLAYBILL_PATTERN,
                    PLAYBILL_TIME_PATTERN);
            patternFormatMap.put(DATE_ENGLISH_FORMAT, ENGLISH_SDF);
            patternFormatMap.put(DATE_YYYY_MM_DD_PATTERN, yyyy_MM_dd);
            patternFormatMap.put(DATE_TIME_INDEX_PLAYBILL_PATTERN, INDEX_PLAYBILL_TIME_PATTERN);
            patternFormatMap.put(DATE_TIME_NO_SPACE_MS_PATTERN, yyyyMMddHHmmssSFile);
            patternFormatMap.put(TIME_PATTERN, timeHmsFormat);
        }
        return patternFormatMap;

    }

    public static String formatDate(String pattern, Date adate) {
        if (adate == null) {
            return "";
        }

//		SimpleDateFormat sdf = DateUtil.getInstance().get(pattern);
//
//		if (sdf == null) {
//
//			sdf = new SimpleDateFormat(pattern);
//			DateUtil.getInstance().put(pattern, sdf);
//		}

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(adate);
    }

    public static Date parseDate(String pattern, String dateStr) {

        if (dateStr == null || "".equals(dateStr)) {
            return null;
        }

//		SimpleDateFormat sdf = DateUtil.getInstance().get(pattern);
//
//		if (sdf == null) {
//
//			sdf = new SimpleDateFormat(pattern);
//			DateUtil.getInstance().put(pattern, sdf);
//		}
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static Date parseDate(String pattern, String dateStr, Locale locale) {
        if (dateStr == null || "".equals(dateStr)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_ENGLISH_FORMAT, locale);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算时间的起始时间
     */
    private final static String BASIC_DATE = "2000-01-01 00:00:00";

    /**
     * Checkstyle rule: utility classes should not have public constructor
     */
    private DateUtil() {
    }

    /**
     * 把日期字符串yyyy-MM-dd HH:mm:ss转换成HH:mm形式
     */
    public static String strToString(String date) {
        if (date == null || "".equals(date)) {
            return date;
        }
        String temp = "";
        try {
            Date dateStr = DateUtil.parseDate(DATE_TIME_PATTERN, date);
            temp = DateUtil.formatDate(TIME_HHMM_PATTERN, dateStr);
        } catch (Exception ex) {
            log.debug(ex.getStackTrace());
        }
        return temp;
    }

    /**
     * 把日期字符串yyyy-MM-dd HH:mm:ss转换成yyyy-MM-dd HH:mm形式
     */
    public static String strToStr(String date) {
        if (date == null || "".equals(date)) {
            return date;
        }
        String temp = "";
        try {
            Date dateStr = DateUtil.parseDate(DATE_TIME_PATTERN, date);
            temp = DateUtil.formatDate(DATE_TIME_INDEX_PLAYBILL_PATTERN, dateStr);
        } catch (Exception ex) {
            log.debug(ex.getStackTrace());
        }
        return temp;
    }

    public static String dateToString(Date date) {
        SimpleDateFormat df;
        String returnValue = "";
        if (date != null) {
            df = new SimpleDateFormat(DATE_FORMAT);
            returnValue = df.format(date);
        }

        return (returnValue);
    }

    /**
     * Return default datePattern (MM/dd/yyyy)
     *
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
        Locale locale = LocaleContextHolder.getLocale();
        String defaultDatePattern;
        try {
            defaultDatePattern = ResourceBundle.getBundle(
                    "ApplicationResources", locale).getString("date.format");
        } catch (MissingResourceException mse) {
            defaultDatePattern = "yyyy-MM-dd";
        }

        return defaultDatePattern;
    }

    public static String getDateTimePattern() {
        return DateUtil.getDatePattern() + " HH:mm:ss.S";
    }

    /**
     * This method attempts to convert an Oracle-formatted date in the form
     * dd-MMM-yyyy to mm/dd/yyyy.
     *
     * @param aDate date from database as a string
     * @return formatted string for the ui
     */
    public static String getDate(Date aDate) {
        SimpleDateFormat df;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(getDatePattern());
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date/time in the
     * format you specify on input
     *
     * @param aMask   the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @throws ParseException when String doesn't match the expected format
     * @see SimpleDateFormat
     */
    public static Date convertStringToDate(String aMask, String strDate)
            throws ParseException {
        SimpleDateFormat df;
        Date date;
        df = new SimpleDateFormat(aMask);

        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            // log.error("ParseException: " + pe);
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }

        return (date);
    }

    /**
     * This method returns the current date time in the format: MM/dd/yyyy HH:MM
     * a
     *
     * @param theTime the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(TIME_PATTERN, theTime);
    }

    /**
     * This method returns the current date in the format: MM/dd/yyyy
     *
     * @return the current date
     * @throws ParseException when String doesn't match the expected format
     */
    public static Calendar getToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(getDatePattern());

        // This seems like quite a hack (date -> string -> date),
        // but it works ;-)
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));

        return cal;
    }

    /**
     * This method generates a string representation of a date's date/time in
     * the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     * @see SimpleDateFormat
     */
    public static String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            log.error("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date based on the
     * System Property 'dateFormat' in the format you specify on input
     *
     * @param aDate A date to convert
     * @return a string representation of the date
     */
    public static String convertDateToString(Date aDate) {
        return getDateTime(getDatePattern(), aDate);
    }

    /**
     * This method converts a String to a date using the datePattern
     *
     * @param strDate the date to convert (in format MM/dd/yyyy)
     * @return a date object
     * @throws ParseException when String doesn't match the expected format
     */
    public static Date convertStringToDate(String strDate) {
        Date aDate = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug("converting date with pattern: " + getDatePattern());
            }

            aDate = convertStringToDate(getDatePattern(), strDate);
        } catch (ParseException pe) {
            log.error("Could not convert '" + strDate
                    + "' to a date, throwing exception");
            pe.printStackTrace();
        }
        return aDate;
    }

    public static java.sql.Date convertDateToSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Timestamp convertDateToTimestamp(Date date) {
        return new java.sql.Timestamp(date.getTime());
    }

    public static String getNowTime(Date date) {
        if (date == null) {
            return "";
        }
        return DateUtil.formatDate(DATE_TIME_MS_PATTERN, date);
    }

    public static String getNowTime(Object date) {
        if (date == null) {
            return "";
        }
        Date date2 = (Date) date;
        return DateUtil.formatDate(DATE_TIME_MS_PATTERN, date2);
    }

    public static String getDateTime(String sdate) {
        try {
            java.sql.Timestamp date = stringToTimestamp(sdate);
            return DateUtil.formatDate(DATE_TIME_PATTERN, date);
        } catch (Exception e) {
            return sdate;
        }
    }

    public static java.sql.Timestamp stringToTimestamp(String timestampStr) {
        if (timestampStr == null || timestampStr.length() < 1) {
            return null;
        }
        return java.sql.Timestamp.valueOf(timestampStr);
    }

    /**
     * 根据日期计算出所在周的日期，并返回大小为7的数组
     *
     * @param date
     * @return
     */
    public static String[] getWholeWeekByDate(Date date) {
        String[] ss = new String[7];
        Calendar calendar = Calendar.getInstance();
        for (int i = 0, j = 2; i < 6 && j < 8; i++, j++) {
            calendar.setTime(date);
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(Calendar.DAY_OF_WEEK, j);
            ss[i] = getFormatDate(calendar.getTime());
        }
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
        ss[6] = getFormatDate(calendar.getTime());
        return ss;
    }

    /**
     * 返回格式 yyyyMMdd的日期格式
     *
     * @param d
     * @return
     */
    public static String getFormatDate(Date d) {
        return DateUtil.formatDate(DATE_YYYYMMDD_PATTERN, d);
    }

    public static String getHHmm2(Date d) {
        return DateUtil.formatDate(TIME_HHMM_PATTERN2, d);
    }

    public static Date getDateByString(String pattern) throws ParseException {
        return DateUtil.parseDate(DATE_YYYYMMDD_PATTERN, pattern);
    }

    public static Date getPlayBillTimeByPattern(String date)
            throws ParseException {
        return DateUtil.parseDate(DATE_TIME_PLAYBILL_PATTERN, date);
    }

    public static String getNowTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String nowTime = df.format(date);
        return nowTime;
    }

    /**
     * @return 当前标准日期yyyyMMddHHmmss
     */
    public static String getNowTimeNumber() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String nowTime = df.format(date);
        return nowTime;
    }

    /**
     * 获取从2000年1月1日 00:00:00开始到指定日期的秒数
     *
     * @return long
     */
    public static Long getSeconds(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date basicDate = formatter.parse(BASIC_DATE, new ParsePosition(0));
        long secLong = (date.getTime() - basicDate.getTime()) / 1000L;
        return secLong;
    }

    /**
     * 获取从2000年1月1日 00:00:00开始到指定日期的秒数
     * <p>
     * <p>
     * 例如：yyyy-MM-dd HH:mm:ss
     *
     * @return long
     */
    public static Long getSeconds(String dateStr, String df) {
        if (dateStr == null || "".equals(dateStr)) {
            return null;
        }
        if (df == null || "".equals(df)) {
            df = DATE_FORMAT;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(df);
        Date date = formatter.parse(dateStr, new ParsePosition(0));
        return getSeconds(date);
    }

    /**
     * 返回格式 yyyyMMdd的日期格式
     *
     * @return
     */

    public static Date getDateByStringyyyyMMddHHmmss(String pattern)
            throws ParseException {
        return DateUtil.parseDate(DATE_TIME_NO_SPACE_PATTERN, pattern);
    }

    public static Date getFormatDateByEnglishSDF(String s) {
        return DateUtil.parseDate(DATE_ENGLISH_FORMAT, s);
    }

    public static String getFormatDateByyyyyMMddHHmmssFile(Date d) {
        return DateUtil.formatDate(DATE_TIME_NO_SPACE_PATTERN, d);
    }

    public static String formateStrDate(String d) {
        Date formateDate = DateUtil.parseDate(DATE_TIME_PATTERN, d);
        String dateStr = getFormatDateByyyyyMMddHHmmssFile(formateDate);
        return dateStr;
    }

    /**
     * 将格式为yyyyMMddhhmmss的字符串 格式为yyyy-MM-dd
     *
     * @return
     */
    public static String formatDate(String d) {
        Date formateDate = DateUtil.parseDate(DATE_TIME_NO_SPACE_PATTERN, d);
        String dateStr = DateUtil.formatDate(DATE_YYYY_MM_DD_PATTERN, formateDate);
        return dateStr;
    }

    public static String formatLongToTimeStr(Long msl) {
        String str = "";
        Integer day = 0;
        Integer hour = 0;
        Integer minute = 0;
        Integer second = 0;
        Integer ms = 0;

        second = msl.intValue() / 1000;
        ms = msl.intValue() % 1000;

        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        if (hour > 24) {
            day = hour / 24;
            hour = hour % 24;
        }

        if (day > 0) {
            str = day.toString() + "天";
        }
        if (hour > 0) {
            str += hour.toString() + "小时";
        }
        if (minute > 0) {
            str += minute.toString() + "分钟";
        }
        if (second > 0) {
            str += second.toString() + "秒";
        }
        if (ms > 0) {
            str += ms.toString() + "毫秒";
        }

        return str;
    }

    public static String formatLongToTxt(Long msl) {
        String str = "";
        Integer day = 0;
        Integer hour = 0;
        Integer minute = 0;
        Integer second = 0;

        second = msl.intValue() / 1000;

        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        if (hour > 24) {
            day = hour / 24;
            hour = hour % 24;
        }

        if (day > 0) {
            str = day.toString() + "天";
        }
        if (hour > 0) {
            str += hour.toString() + "小时";
        }
        if (minute > 0) {
            str += minute.toString() + "分钟";
        }
        if (second > 0) {
            str += second.toString() + "秒";
        }

        return str;
    }

    public static long getBetweenDays(Date t1, Date t2) throws ParseException {
        return ((t2.getTime()) - t1.getTime()) / 1000 / 60 / 60 / 24;

    }

    public static int getIntervalDaysOfExitDate2(Date exitDateFrom,
                                                 Date exitDateTo) {
        Calendar aCalendar = Calendar.getInstance();
        Calendar bCalendar = Calendar.getInstance();
        aCalendar.setTime(exitDateFrom);
        bCalendar.setTime(exitDateTo);
        int days = 0;
        while (aCalendar.before(bCalendar) || aCalendar.equals(bCalendar)) {
            days++;
            aCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return days;
    }


    /**
     * 日期String转换为Date.并且严格处理日期字符串的格式.
     *
     * @param pattern
     * @param dateStr
     * @return
     */
    public static Date parseDateNotLenient(String pattern, String dateStr) {

        if (dateStr == null || "".equals(dateStr)) {
            return null;
        }

        SimpleDateFormat sdf = DateUtil.getInstance().get(pattern);

        if (sdf == null) {

            sdf = new SimpleDateFormat(pattern);
            DateUtil.getInstance().put(pattern, sdf);
        }

        try {
            sdf.setLenient(false);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /****
     *
     *
     * @param currentTimes
     *            当前时间的毫秒数
     * @param times
     *            输入判断的毫秒数
     * @return yesteday today tomorrow
     */
    public static String getMarkByTime(long currentTimes, long times) {
        String result = "";

        long rate = 24L * 3600L * 1000L;

        long today = currentTimes / rate;
        long yesterday = today - 1;
        long beforeYesterday = yesterday - 1;
        long tomorrow = today + 1;

        long input = times / rate;
        if (input == yesterday) {
            result = "yesterday";
        } else if (input == today) {
            result = "today";
        } else if (input == beforeYesterday) {
            result = "beforeYesterday";
        } else if (input == tomorrow) {
            result = "tomorrow";
        }
        return result;
    }

    /**
     * 获取下月的第一天
     *
     * @param curMonth yyyy-mm : 2011-09
     * @return
     */
    public static String getNextMonth(String curMonth) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
//		String startDate = ""; // 当前月的第一天
        String endDate = ""; // 下个月的第一天
        String[] ta = curMonth.split("-");
        c.setTime(java.sql.Date.valueOf(curMonth + "-01"));
        c.add(Calendar.MONTH, 0);// 当前月
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置月份的第一天
//		startDate = sdf.format(c.getTime());
        c.set(Calendar.MONTH, Integer.parseInt(ta[1]));// 回到当前月
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置当前月第一天
        c.add(Calendar.DAY_OF_YEAR, 0); //
        endDate = sdf.format(c.getTime());
        return endDate;
    }

    /**
     * 获取日期范围内的月份明细
     *
     * @param startMonth 2012-07
     * @param endMonth   2012-09
     * @return {2012-07,2012-08,2012-09}
     * @throws Exception
     */
    public static List<String> getMonthList(String startMonth, String endMonth) throws Exception {
        List<String> monthList = new ArrayList<String>();
        monthList.add(startMonth);
        while (startMonth != null && !startMonth.equals(endMonth)) {
            startMonth = DateUtil.getNextMonth(startMonth);
            if (startMonth != null && !"".equals(startMonth)) {
                startMonth = startMonth.substring(0, 7);
                monthList.add(startMonth);
            }
        }

        return monthList;
    }

    /**
     * 得到某年某月的第一天
     *
     * @param yearMonth 2012-09
     * @return
     */
    public static String getFirstDayOfMonth(String yearMonth) {
        String[] tmp = yearMonth.split("-");
        int year = Integer.parseInt(tmp[0]);
        int month = Integer.parseInt(tmp[1]);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    /**
     * 得到某年某月的最后一天
     *
     * @param yearMonth 2012-09
     * @return
     */
    public static String getLastDayOfMonth(String yearMonth) {
        String[] tmp = yearMonth.split("-");
        int year = Integer.parseInt(tmp[0]);
        int month = Integer.parseInt(tmp[1]);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, value);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    /**
     * @param pattern
     * @param day
     * @return
     */
    public static String getYesterdayOfDay(String pattern, Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return new SimpleDateFormat(pattern).format(cal.getTime());
    }

    public static Date getNextDay(Date date, int next) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, next);
        return cal.getTime();
    }

    public static Date getNextMonth(Date date, int next) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, next);
        return cal.getTime();
    }

    public static Date getNextMinute(Date date, int next) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, next);
        return cal.getTime();
    }

    /**
     * 方法描述: Date转Calendar
     *
     * @param date
     * @return Calendar
     * @version Wondertek
     * @author yaodm
     * @date 2015-6-12 上午9:45:57
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 方法描述: 获得星期
     *
     * @return String
     * @version 1.0
     * @author yaodm
     * @date 2015-6-12 上午10:35:22
     */
    public static String getDayOfWeek(Date date) {
        Calendar calendar = dateToCalendar(date);
        // 根据获取到的值：当前日期处于一周的第几天，这里从周日开始算第一天，所以-1获取对应数组中的值
        int index = WEEKSARRAYS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        return WEEKSARRAYSTR[index];
    }

    /**
     * 取两个时间差
     *
     * @param beginTime yyyy-MM-dd HH:mm:ss
     * @param endTime   yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getTimeDiss(Date beginTime, Date endTime) {
        Long time = endTime.getTime() - beginTime.getTime();
        Long hour = time / 1000 / 60 / 60;
        Long minute = time / 1000 / 60 % 60;

        BigDecimal bigDecimal = new BigDecimal(minute);
        BigDecimal bigDecimal1 = new BigDecimal(60);
        BigDecimal bigDecimal2 = bigDecimal.divide(bigDecimal1, 1, BigDecimal.ROUND_HALF_EVEN);
        return String.valueOf(hour + bigDecimal2.doubleValue());
    }

    /**
     * 方法描述: 日期格式化
     *
     * @param pattern
     * @return String
     * @version 1.0
     * @author yaodm
     * @date 2015-6-12 上午10:51:21
     */
    public static String dateFormat(Date date, String pattern) {
        // 如果pattern为空，设置默认格式
        if (null == pattern || "".equals(pattern.trim())) {
            pattern = "yyyy-MM-dd";
        }
        if (null == date) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * @param currentTime
     * @return 返回日期格式：yyyyMMddHHmmss
     */
    public static String date2str(Date currentTime) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_NO_SPACE_PATTERN);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 方法描述: 计算开始日期至结束日期之间的天数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return int 返回间隔天数
     * @version Wondertek
     * @author yaodm
     * @date 2015-6-29 下午5:20:27
     */
    public static int getDaysBetween(Date startDate, Date endDate) {
        Long betweenDays = 0L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDate = sdf.parse(sdf.format(startDate));
            endDate = sdf.parse(sdf.format(endDate));
            long startTimes = startDate.getTime();
            long endTimes = endDate.getTime();
            betweenDays = (endTimes - startTimes) / (60 * 60 * 1000 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return betweenDays.intValue();
    }

    /**
     * 方法描述: 比较两个日期的先后，前面的日期在前面的，返回1，前面的日期在后面的，返回-1，同样的返回0
     *
     * @return int
     * @author wfg
     * @date 2016-6-7 下午15:28:20
     */
    public static int compareDates(String dateStr1, String dateStr2) {
        try {
            Date date1 = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, dateStr1);
            Date date2 = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, dateStr2);
            if (date1 != null && date2 != null) {
                if (date1.getTime() > date2.getTime()) {
                    return 1;
                } else if (date1.getTime() < date2.getTime()) {
                    return -1;
                }
            }
        } catch (Exception e) {

        }
        return 0;
    }

    /**
     * 方法描述: 比较两个日期的先后，前面的日期在前面的，返回1，前面的日期在后面的，返回-1，同样的返回0
     *
     * @return int
     * @author wfg
     * @date 2016-6-7 下午15:28:20
     */
    public static Time parseSqlTime(String dateStr) {
        Time time = null;
        try {
            SimpleDateFormat timeHmsFormat = new SimpleDateFormat(TIME_PATTERN);
            if (null != timeHmsFormat) {
                time = new Time(timeHmsFormat.parse(dateStr).getTime());
            }
        } catch (Exception e) {

        }
        return time;
    }

    // 获得某天最大时间 2017-10-15 23:59:59
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static int getMonthLastDay(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获取当月第一天 格式 MM.dd
     *
     * @return
     */
    public static String getMonthFirstDay() {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd");
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String first = format.format(c.getTime());
        return first;
    }

    /**
     * 获取当月最后一天 格式 MM.dd
     *
     * @return
     */
    public static String getMonthLastDay() {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd");
        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = format.format(ca.getTime());
        return last;
    }

    /**
     * 获取当月第一天第一秒 格式 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static Date getMonthFirstDayDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取当月最后一天最后一秒 格式 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static Date getMonthLastDayDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * 获取当天第一秒 格式 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static Date getToDayDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当天
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取昨天第一秒 格式 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static Date getYesterdayDayDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取昨天
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR,-1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }


    /**
     * 获取当天最后一秒 格式 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static Date getToDayLast() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当天最后一秒
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取当前是周几
     * 周日 ：1 周一 ：2 ...
     *
     * @return
     */
    public static int getWeekDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.DAY_OF_WEEK);
    }


    public static Date geLastWeekMonday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(new Date()));
        cal.add(Calendar.DATE, -7);
        return cal.getTime();
    }

    public static Date geLastWeekSunday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(new Date()));
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    /**
     * 获取本周一的日期
     *
     * @param date
     * @return
     */
    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    /**
     * 判断当天是否为本月第一天
     *
     * @return
     */
    public static boolean isFirstDayOfMonth() {
        boolean flag = false;
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        if (1 == today) {
            flag = true;
        }
        return flag;
    }

    /**
     * 描述:获取上个月的最后一天.
     *
     * @return
     */
    public static Date getLastMaxMonthDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calendar.getTime();
    }

    /**
     * 描述:获取上个月的第一天.
     *
     * @return
     */
    public static Date getLastMinMonthDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }

    /**
     * 描述:获取上个月的第一天.
     *
     * @return
     */
    public static Date getDateAddEight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 8);

        return calendar.getTime();
    }

    //获取指定月份的天数
    public static int getDaysByYearMonth(int year, int month) {

        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获取某月所有的日期  yyyy-MM-dd
     * @param month
     * @return
     */
    public static List<String> dayReport(Date month) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(month);//month 为指定月份任意日期
        int year = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int dayNumOfMonth = getDaysByYearMonth(year, m);
        cal.set(Calendar.DAY_OF_MONTH, 1);// 从一号开始
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < dayNumOfMonth; i++, cal.add(Calendar.DATE, 1)) {
            Date d = cal.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
            String df = simpleDateFormat.format(d);
            stringList.add(df);
      }
      return stringList;
    }

    /**
     * 获取两个日期之前所有的天数
     * @param minDate yyyy-MM-dd
     * @param maxDate yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public static List<String> getMonthBetweenDateStr(String minDate, String maxDate) throws ParseException{
        List<String> listDate = new ArrayList<>();
        SimpleDateFormat dfh = new SimpleDateFormat("MM-dd");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        listDate.add(dfh.format(df.parse(minDate)));
        if(minDate.equals(maxDate)){
            return listDate;
        }
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        Date startDate = df.parse(minDate);
        startCalendar.setTime(startDate);
        Date endDate = df.parse(maxDate);
        endCalendar.setTime(endDate);

        while(true){
            startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            if(startCalendar.getTimeInMillis() < endCalendar.getTimeInMillis()){
                listDate.add(dfh.format(startCalendar.getTime()));
            }else{
                break;
            }
        }
        listDate.add(dfh.format(df.parse(maxDate)));
        return listDate;
    }
    // 获取当前时间到当天23:59:59的秒数
    public static int getNowEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        long time = todayEnd.getTime().getTime() - System.currentTimeMillis();
        int second = (int)time/1000;
        return second;
    }

    /**
     * 判断某个时间点是否在某个时间范围内
     * @param date
     * @param startDate
     * @param endDate
     * @return
     */
    public static boolean isBetweenTwoDate(Date date, Date startDate, Date endDate) {
        return startDate.before(date) && endDate.after(date);
    }

    public static boolean isDateStr(String visitDate) {
        try {
            Date date = convertStringToDate(visitDate);
            if (date == null){
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static void main(String[] a) throws Exception{
//        List<String>  stringList =   getMonthBetweenDateStr("2018-07-10","2018-07-20");
//        for (String s :stringList){
//            System.out.println(s);
//        }

        String datastr = "2014-05-13 23:11:12";
        boolean flag = isDateStr(datastr);
        System.out.println(datastr + "为" + flag);
    }


}
