package com.fijo.cores.utils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 一些有用的日期时间工具类
 *
 * @author zhangbo
 *
 */
public final class DateUtil {

	public static final String datePattern1 = "yyyy-MM-dd";
	public static final String datePattern2 = "yyyyMMdd";
    public static final String datePattern3 = "yyyy/MM/dd";
	public static final String timestampPattern1 = "yyyy-MM-dd HH:mm:ss";
    public static final String timestampPattern2 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String timestampPattern3 = "yyyy-MM-dd HH:mm:ss.SSS Z";
	public static final String timePattern = "HH:mm:ss";


    public static final DateTimeFormatter fullDateTimeFormatter = new DateTimeFormatterBuilder().append(null, //because no printing is required
				 new DateTimeParser[]{
                         DateTimeFormat.forPattern(timePattern).getParser(),
                         DateTimeFormat.forPattern(datePattern1).getParser(),
                         DateTimeFormat.forPattern(datePattern2).getParser(),
                         DateTimeFormat.forPattern(datePattern3).getParser(),
                         DateTimeFormat.forPattern(timestampPattern1).getParser(),
                         DateTimeFormat.forPattern(timestampPattern2).getParser(),
                         DateTimeFormat.forPattern(timestampPattern3).getParser()
                 }).toFormatter();

    private DateUtil() {
        //not called
    }

	public static void main(String[] args) throws ParseException {
		System.out.println(getYesterday("2014-01-02 14:32:55"));
        System.out.println(getNextMonthLastDay());
		System.out.println("您好！");
		System.out.print("Xmx=");
		System.out.println(Runtime.getRuntime().maxMemory()/1024.0/1024+"M");

		System.out.print("free mem=");
		System.out.println(Runtime.getRuntime().freeMemory()/1024.0/1024+"M");

		System.out.print("total mem=");
		System.out.println(Runtime.getRuntime().totalMemory()/1024.0/1024+"M");
		System.gc();


	}

	public static long getNow(){
		return System.currentTimeMillis();
	}

	public static Date getCurrent(){
		return new Date(System.currentTimeMillis());
	}

	public static Date getTodayTimestamp() {
		return DateUtil.startTimeOfDay(DateUtil.getCurrent()).toDate();
	}
	
	public static String getToday() {
		return getDate(getCurrent());
	}
	
	public static String getToday(String pattern) {
		return getDate(getCurrent(), pattern);
	}
	
	public static String getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
	    return getDate(cal.getTime());
	}
	
	public static String getYesterday(String pattern) {
		Calendar cal = Calendar.getInstance();  
		cal.add(Calendar.DATE, -1);
	    return getDate(cal.getTime(), pattern);
	}
	
	public static String getDate(Date date) {
        DateTime dt = new DateTime(date);
        return dt.toString(datePattern1);
	}

	public static String getDate(Date date, String pattern) {
        DateTime dt = new DateTime(date);
        return dt.toString(pattern);
	}

	public static String getTime(Date date) {
        DateTime dt = new DateTime(date);
        return dt.toString(timePattern);
	}

	public static String getTimestamp(Date date) {
        DateTime dt = new DateTime(date);
        return dt.toString(timestampPattern1);
	}

	public static String getTimestamp(Date date, String pattern) {
        DateTime dt = new DateTime(date);
        return dt.toString(pattern);
	}

	// ===========================字符串转换时间==================================
	public static Date parseDate(String source){
        return fullDateTimeFormatter.parseDateTime(source).toDate();
	}

	public static Date parseTimestamp(String source){
        return fullDateTimeFormatter.parseDateTime(source).toDate();
	}

	public static Date parseCustomDate(String source, String pattern){
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
        return dtf.parseDateTime(source).toDate();
	}

	public static int compareDate(Date src, Date desc) {
		String str1 = getDate(src);
		String str2 = getDate(desc);
		return str1.compareTo(str2);
	}

	public static int compareTimestamp(Date src, Date desc) {
		String str1 = getTimestamp(src);
		String str2 = getTimestamp(desc);
		return str1.compareTo(str2);
	}

	public static int compareTime(Date src, Date desc) {
		String str1 = getTime(src);
		String str2 = getTime(desc);
		return str1.compareTo(str2);
	}

	public static int compareTime(String src, String desc) {
		return src.compareTo(desc);
	}

	// ===========================时间计算==================================
	/**
	 * 当前年
	 * @return 2014
	 */
	public static String getCurrYear() {
		Calendar cal = Calendar.getInstance();
		//cal.add(Calendar.MONTH, 0);
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy");
	    return sdf.format(cal.getTime());
	}

	/**
	 * 当前月
	 * @return 2014-08
	 */
	public static String getCurrMonth() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM");
	    return sdf.format(cal.getTime());
	}

	public static String getCurrSimpleMonth() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf =  new SimpleDateFormat("MM");
	    return sdf.format(cal.getTime());
	}

	/**
	 * 上一个月
	 * @return 2014-08
	 */
	public static String getLastMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM");
	    return sdf.format(cal.getTime());
	}

	/**
	 * 下一个月
	 * @return 2014-08
	 */
	public static String getNextMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM");
	    return sdf.format(cal.getTime());
	}

	/**
	 * 当前月第一天
	 * @return 2014-08-01
	 */
	public static String getCurrMonthFirstDay() {
        DateTime dt = new DateTime();
        DateTime firstday = dt.dayOfMonth().withMinimumValue();
        return firstday.toString(datePattern1);
	}

	/**
	 * 当前月最后一天
	 * @return 2014-08-31
	 */
	public static String getCurrMonthLastDay() {
        DateTime dt = new DateTime();
        DateTime lastday = dt.dayOfMonth().withMaximumValue();
        return lastday.toString(datePattern1);
	}

	/**
	 * 上月第一天
	 * @return 2014-08-01
	 */
	public static String getLastMonthFirstDay() {
        DateTime dt = new DateTime();
        dt = dt.minusMonths(1);
        DateTime firstday = dt.dayOfMonth().withMinimumValue();
        return firstday.toString(datePattern1);
	}

	/**
	 * 上月最后一天
	 * @return 2014-08-31
	 */
	public static String getLastMonthLastDay() {
        DateTime dt = new DateTime();
        dt = dt.minusMonths(1);
        DateTime lastday = dt.dayOfMonth().withMaximumValue();
        return lastday.toString(datePattern1);
	}

	/**
	 * 下月第一天
	 * @return 2014-08-01
	 */
	public static String getNextMonthFirstDay() {
        DateTime dt = new DateTime();
        dt = dt.plusMonths(1);
        DateTime firstday = dt.dayOfMonth().withMinimumValue();
        return firstday.toString(datePattern1);
	}

	/**
	 * 下月最后一天
	 * @return 2014-08-31
	 */
	public static String getNextMonthLastDay() {
        DateTime dt = new DateTime();
        dt = dt.plusMonths(1);
        DateTime lastday = dt.dayOfMonth().withMaximumValue();
        return lastday.toString(datePattern1);
	}

	/**
	 * 当前时间向前增加天数
	 * @param days
	 * @return
	 */
	public static Date addDays(int days) {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusDays(days);
		return dateTime.toDate();
	}

	/**
	 * 当前时间向后减少天数
	 * @param days
	 * @return
	 */
	public static Date subDays(int days) {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.minusDays(days);
		return dateTime.toDate();
	}

	/**
	 * 指定时间向前增加天数
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(Date date, int days) {
		DateTime dateTime = new DateTime(date);
		dateTime = dateTime.plusDays(days);
		return dateTime.toDate();
	}

	/**
	 * 指定时间向后减少天数
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date subDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days * -1);
	    return cal.getTime();
	}

	/**
	 * 在当前时间增加时间
	 * @param minutes
	 * @return
	 */
	public static Date addMinutes(int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes);
	    return cal.getTime();
	}

	/**
	 * 在当前时间向后减少时间
	 * @param minutes
	 * @return
	 */
	public static Date subMinutes(int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes * -1);
	    return cal.getTime();
	}

	/**
	 * 指定时间增加时间
	 * @param date
	 * @param minutes
	 * @return
	 */
	public static Date addMinutes(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
	    return cal.getTime();
	}

	/**
	 * 在指定时间向后减少
	 * @param date
	 * @param minutes
	 * @return
	 */
	public static Date subMinutes(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes * -1);
	    return cal.getTime();
	}

	/**
	 * 时间置零
	 * @param date
	 * @return
	 */
	public static Date removeTime(Date date) {
		return DateUtil.parseDate(DateUtil.getDate(date));
	}

	/**
	 * 获取今天的开始时间：比如：2014-06-19 00:00:00
	 * @param date
	 * @return
	 */
	public static DateTime startTimeOfDay(Date date){
		DateTime nowTime = new DateTime(date.getTime());
        return nowTime.withTimeAtStartOfDay();
	}

	/**
	 * 获取今天的结束时间：比如：2014-06-19 23:59:59
	 * @param date
	 * @return
	 */
	public static DateTime endTimeOfDay(Date date){
		DateTime nowTime = new DateTime(date.getTime());
        return nowTime.millisOfDay().withMaximumValue();
	}

	/**
	 * 获取现在距离今天结束还有多长时间
	 * @return
	 */
	public static long overTimeOfToday(){
		DateTime nowTime = new DateTime();
		DateTime endOfDay = nowTime.millisOfDay().withMaximumValue();
		return endOfDay.getMillis()-nowTime.getMillis();
	}

	/**
	 * 得到两个日期之间相差的天数
	 *
	 * @param startDate
	 *            2006-03-01
	 * @param endDate
	 *            2006-05-01
	 * @return n 61
	 */
	public static int daysBetweenDates(Date startDate, Date endDate) {
		DateTime startTime = new DateTime(startDate.getTime());
		DateTime endTime = new DateTime(endDate.getTime());
        return Days.daysBetween(startTime, endTime).getDays();
	}

	/**
	 * 计算两个时间相差的分钟数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long minuteBetweenDates(Date startDate, Date endDate) {
		  long seconds = (endDate.getTime() - startDate.getTime()) / 1000;
          return seconds / 60;
	}

	/**
	 * 计算两个时间相差的天数、小时数、分数
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static long[] timeBetweenDates(Date startDate, Date endDate) {
		  long diff = endDate.getTime() - startDate.getTime(); //这样得到的差值是微秒级别
		  long days = diff / (1000 * 60 * 60 * 24);
		  long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
		  long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
		  return new long[]{days, hours, minutes};
	}

	public static DateTime getJodaDateTime (Object object){
		DateTime in = new DateTime(object);
//		System.out.println(in.getYear()); //当年
//		System.out.println(in.getMonthOfYear()); //当月
//		System.out.println(in.getDayOfMonth());  //当月第几天
//		System.out.println(in.getDayOfWeek());//本周第几天
//		System.out.println(in.getDayOfYear());//本年第几天
//		System.out.println(in.getHourOfDay());//时
//		System.out.println(in.getMinuteOfHour());//分
//		System.out.println(in.getMinuteOfDay());//当天第几分钟
//		System.out.println(in.getSecondOfMinute());//秒
//		System.out.println(in.getSecondOfDay());//当天第几秒
//		System.out.println(in.getWeekOfWeekyear());//本年第几周
//		System.out.println(in.getZone());//所在时区
//		System.out.println(in.dayOfWeek().getAsText()); //当天是星期几，例如：星期五
//		System.out.println(in.yearOfEra().isLeap()); //当你是不是闰年，返回boolean值
//		System.out.println(in.dayOfMonth().getMaximumValue());//当月day里面最大的值
		return in;
	}

	public static DateTime getJodaDateTime (String dateStr, String datePattern){
		DateTimeFormatter fmt = DateTimeFormat.forPattern(datePattern);//自定义日期格式
		return DateTime.parse(dateStr, fmt);
	}
}
