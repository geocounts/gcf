/*
 * Licensed to Transmetric America Inc (TAI) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * TAI licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * <a href="http://geocounts.com">geocounts.com</a>
 */
package geocounts.traffic.formats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * Simple utility functions used by count formatting but not specifically 
 * related to count information per-se
 * @see CoordinateObjectComputation
 */
public class RawFmtUtils {
  public static final double KILOMETERS_PER_MILE = 1.60934;
  public static final double INCHES_PER_METER    = 39.3701;
  public static final double METERS_PER_FOOT     = 0.3048;
  public static final double POUNDS_PER_KILOGRAM = 2.20462;
  public static long MSEC_IN_HOUR = 3600000;
  public static long MSEC_IN_DAY = 86400000;
  public static int MINUTES_IN_DAY = 1440;
  public static final DecimalFormat df1 = new DecimalFormat("0.0");
  public static final DecimalFormat df3 = new DecimalFormat("0.000");
  public static final DecimalFormat df6 = new DecimalFormat("0.000000");
  
  private static DecimalFormat df;
  
  public static final String LINE_FEED = "\r\n";
  
  private static SimpleTimeZone GMT = new SimpleTimeZone(0, "GMT+00:00");

  /**
   * yyyy-MM-dd'T'HH:mm:ss.SSS
   */
  private static final String ISO8601_MSECS = "yyyy-MM-dd'T'HH:mm:ss.SSS";

  /**
   * yyyy-MM-dd'T'HH:mm:ss
   */
  private static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";
  
  /**
   * yyyy-MM-dd
   */
  private static final String ISO8601_DATEONLY = "yyyy-MM-dd";

  /**
   * HH:mm:ss.SSS
   */
  private static final String ISO8601_TIMEONLY = "HH:mm:ss.SSS";
  
  /**
   * Parses any date-time format that follows a highest-to-lowest, padded digit structure. Examples:
   * <pre>
   * 2024-05
   * 2024-05-14
   * 2024-05-14 16
   * 2024-05-14 16:24
   * 2024-05-14 16:24:04
   * 2024-05-14 16:24:04.025
   * 2024-05-14,16
   * 2024-05-14,16:24
   * 2024-05-14,16:24:04
   * 2024-05-14,16:24:04.025
   * 2024/05/14
   * 2024/05/14 16
   * 2024/05/14 16:24
   * 2024/05/14 16:24:04
   * 2024/05/14 16:24:04.025
   * 2024/05/14,16
   * 2024/05/14,16:24
   * 2024/05/14,16:24:04
   * 2024/05/14,16:24:04.025
   * </pre>
   * @param line Any of the above formats
   */
  public static long parseUniversalDateTime23(String line) {
    int yyyy = Integer.parseInt(line.substring(0, 4));
    int MM   = Integer.parseInt(line.substring(5, 7));
    int dd   = 1;
    if (line.length() > 7)
      dd = Integer.parseInt(line.substring(8, 10));
    
    int HH   = 0;
    if (line.length() > 11) {
      HH = Integer.parseInt(line.substring(11, 13));
    }
    java.util.GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(yyyy, MM-1, dd, HH);
    if (line.length() > 14) {
      gc.set(java.util.GregorianCalendar.MINUTE, Integer.parseInt(line.substring(14, 16)));
      if (line.length() > 17) {
        gc.set(java.util.GregorianCalendar.SECOND, Integer.parseInt(line.substring(17, 19)));
        if (line.length() >= 23) {
          int max = 23;
          int Z = line.indexOf('Z', 20);
          if ((Z > 20) && (Z < 23))
            max = Z;
          gc.set(java.util.GregorianCalendar.MILLISECOND, Integer.parseInt(line.substring(20, max)));
        }
      }
    }
    
    return gc.getTimeInMillis();
  }

  /**
   * A simple date format as: yyyy-MM-dd'T'HH:mm:ss.SSS<br />
   * This deliberately does not have a 'z' at the end
   * @return SimpleDateFormat formatted {@link #ISO8601_MSECS yyyy-MM-dd'T'HH:mm:ss.SSS}
   */
  public static final SimpleDateFormat ISO8601_MSECS() {
    return simpleDateFormat(ISO8601_MSECS);
  }
  
  /**
   * @see #ISO8601_MSECS()
   */
  public static String ISO8601_MSECS_format(long value) {
//    return ISO8601_MSECS.format(value);
    return ISO8601_MSECS().format(value);
  }
  
  /**
   * @param value
   * @return String formatted date as {@link #ISO8601 yyyy-MM-dd'T'HH:mm:ss}
   */
  public static final String ISO8601_format(long value) {
//    return ISO8601.format(value);
    return ISO8601().format(value);
  }

  /**
   * A simple date format as: yyyy-MM-dd'T'HH:mm:ss<br />
   * This deliberately does not have a 'z' at the end
   * @return SimpleDateFormat formatted {@link #ISO8601 yyyy-MM-dd'T'HH:mm:ss}
   */
  public static final SimpleDateFormat ISO8601() {
    return simpleDateFormat(ISO8601);
  }
  
  /**
   * @param value
   * @return SimpleDateFormat formatted date value as {@link #ISO8601_DATEONLY}
   */
  public static final String ISO8601_DATEONLY_format(long value) {
//    return ISO8601_DATEONLY.format(value);
    return ISO8601_DATEONLY().format(value);
  }
  
  /**
   * @param value
   * @return String formatted date as {@link #ISO8601_TIMEONLY}
   */
  public static final String ISO8601_TIMEONLY_format(long value) {
//    return ISO8601_TIMEONLY.format(value);
    return ISO8601_TIMEONLY().format(value);
  }
  
  /**
   * Creates a new SimpleDateFormat
   * @return SimpleDateFormat Formatted as {@link #ISO8601_DATEONLY yyyy-MM-dd}
   */
  public static final SimpleDateFormat ISO8601_DATEONLY() {
    return simpleDateFormat(ISO8601_DATEONLY);
  }
  
  /**
   * Creates a new SimpleDateFormat
   * @return SimpleDateFormat Formatted as {@link #ISO8601_TIMEONLY HH:mm:ss.SSS}
   */
  public static final SimpleDateFormat ISO8601_TIMEONLY() {
    return simpleDateFormat(ISO8601_TIMEONLY);
  }
  
  /*
   * Used by the header to define time for tallies<br />
   * yyyy/MM/dd,HH
   * @return SimpleDateFormat formatted as yyyy/MM/dd,HH
   *
  protected static SimpleDateFormat sdfHeaderT() {
    return simpleDateFormat("yyyy/MM/dd,HH");
  }

  /**
   * yyyy/MM/dd
   *
  public static final String V_DateFormat = "yyyy/MM/dd";

  /**
   * Used by the header to define time for observations
   * @return SimpleDateFormat formatted as yyyy/MM/dd,HH:mm:ss.SSS
   *
  protected static SimpleDateFormat sdfHeaderV() {
    return simpleDateFormat("yyyy/MM/dd,HH:mm:ss.SSS");
  }
  */
  
  public static String[] split(String line, char c) {
    if (line == null)
      return null;
    if (line.length() == 0)
      return new String[0];
    
    ArrayList<Integer> points = new ArrayList<Integer>();
    for (int pos=0; pos<line.length(); pos++) {
      if (line.charAt(pos) == c)
        points.add(pos);
    }
    if (points.size() == 0)
      return new String[]{line};
    
    int from, to;
    
    String[] result = new String[points.size()+1];
    for (int i=0; i<points.size(); i++) {
      from = i > 0 ? points.get(i-1)+1 : 0;
      to = points.get(i);
      if (to-from > 0)
        result[i] = line.substring(from, to);
      else
        result[i] = "";
    }
    // get the last point
    from = points.get(points.size()-1);
    from++;
    if (from >= line.length())
      result[result.length-1] = "";
    else
      result[result.length-1] = line.substring(from);
    
    return result;
  }
  
  public static int[] splitInt(String line, char c) {
    if (line == null)
      return null;
    if (line.length() == 0)
      return new int[0];
    
    String[] tokens = split(line, c);
    int[] result = new int[tokens.length];
    for (int i=0; i<result.length; i++)
      if (tokens[i].length() == 0)
        result[i] = 0;
      else
        result[i] = Integer.parseInt(tokens[i]);
    return result;
  }

  public static double[] splitDouble(String line, char c) {
    if (line == null)
      return null;
    if (line.length() == 0)
      return new double[0];
    
    String[] tokens = split(line, c);
    double[] result = new double[tokens.length];
    for (int i=0; i<result.length; i++)
      if (tokens[i].length() == 0)
        result[i] = 0;
      else
        result[i] = Double.parseDouble(tokens[i]);
    return result;
  }
  
  public static String notNull(String value) {
    return value != null ? value : "";
  }

  public static int compareLong(long x, long y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }
  
  public static SimpleDateFormat simpleDateFormat(String fmt) {
    SimpleDateFormat result = new SimpleDateFormat(fmt);
    result.setTimeZone(GMT);
    return result;
  }
  
  /**
   * This sets the second and millisecond to zero
   * @param time
   * @return A GregorianCalendar set to the hour in which the time appears
   */
  public static GregorianCalendar trimMinute(long time) {
    GregorianCalendar gc = newGregorianCalendar(time);
    trimMinute(gc);
    return gc;
  }
  
  /**
   * This sets the second and millisecond to zero
   * @param gc The calendar to set
   */
  public static void trimMinute(GregorianCalendar gc) {
    gc.set(GregorianCalendar.SECOND, 0);
    gc.set(GregorianCalendar.MILLISECOND, 0);
  }
  
  /**
   * This sets the minute, second and millisecond to zero
   * @param time
   * @return A GregorianCalendar set to the hour in which the time appears
   */
  public static GregorianCalendar trimHour(long time) {
    GregorianCalendar gc = newGregorianCalendar(time);
    trimHour(gc);
    return gc;
  }
  
  /**
   * This sets the minute, second and millisecond to zero
   * @param gc The calendar to set
   */
  public static void trimHour(GregorianCalendar gc) {
    gc.set(GregorianCalendar.MINUTE, 0);
    gc.set(GregorianCalendar.SECOND, 0);
    gc.set(GregorianCalendar.MILLISECOND, 0);
  }
  
  /**
   * This sets the hour, minute, second, millisecond to zero
   * @param time
   * @return A GregorianCalendar set to the start of the day in which the time appears
   */
  public static GregorianCalendar trimDay(long time) {
    GregorianCalendar gc = trimHour(time);
    gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
    return gc;
  }
  
  /**
   * This sets the day to 1, and the hour, minute, second, millisecond to zero
   * @param time
   */
  public static GregorianCalendar trimMonth(long time) {
    GregorianCalendar gc = trimDay(time);
    gc.set(GregorianCalendar.DAY_OF_MONTH, 1);
    return gc;
  }
  
  /**
   * This sets the month to 9, day to 1, and the hour, minute, second, millisecond to zero
   * @param time
   */
  public static GregorianCalendar trimYear(long time) {
    GregorianCalendar gc = trimMonth(time);
    gc.set(GregorianCalendar.MONTH, 0);
    return gc;
  }
  
  /**
   * This sets the hour, minute, second, millisecond to zero
   * @param gc The calendar to set
   */
  public static void trimDay(GregorianCalendar gc) {
    gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
    trimHour(gc);
  }
  
  /**
   * Trim the calendar to be midnight on the first day of the calendar's current month
   * @param gc
   */
  public static void trimMonth(GregorianCalendar gc) {
    gc.set(GregorianCalendar.DATE, 1);
    trimDay(gc);
  }
  
  public static int getSecond(long time) {
    GregorianCalendar gc = newGregorianCalendar(time);
    return gc.get(GregorianCalendar.SECOND);
  }
  
  public static int getMinute(long time) {
    GregorianCalendar gc = newGregorianCalendar(time);
    return gc.get(GregorianCalendar.MINUTE);
  }
  
  /**
   * 
   * @param time
   * @return The hour of the day: 0 to 23
   */
  public static int getHour(long time) {
    GregorianCalendar gc = newGregorianCalendar(time);
    return gc.get(GregorianCalendar.HOUR_OF_DAY);
  }

  public static int getYear(long time) {
    return newGregorianCalendar(time).get(GregorianCalendar.YEAR);
  }

  /**
   * 
   * @return A value between 0 (JAN) and 11 (DEC)
   */
  public static int getMonth(long time) {
    return newGregorianCalendar(time).get(GregorianCalendar.MONTH);
  }
  
  /**
   * 
   * @param time
   * @return A value between 1 and 31
   */
  public static int getDOM(long time) {
    return newGregorianCalendar(time).get(GregorianCalendar.DAY_OF_MONTH);
  }
  
  /**
   * 
   * @return A value between 1 (SUN) and 7 (SAT)
   */
  public static int getDOW(long time) {
    return newGregorianCalendar(time).get(GregorianCalendar.DAY_OF_WEEK);
  }
  
  /**
   * 
   * @return A value between 1 and 365 or 366
   */
  public static int getDOY(long time) {
    return newGregorianCalendar(time).get(GregorianCalendar.DAY_OF_YEAR);
  }
  
  public static GregorianCalendar newGregorianCalendar() {
    return new GregorianCalendar(GMT);
  }

  public static GregorianCalendar newGregorianCalendar(long millis) {
    GregorianCalendar result = newGregorianCalendar();
    result.setTimeInMillis(millis);
    return result;
  }
  
  /**
   * Sets the cal's MONTH to 0, and calls {@link #resetMonth(GregorianCalendar)}
   * @param cal
   * @return the time at midnight on Jan 1
   */
  public static long resetYear(GregorianCalendar cal) {
    cal.set(GregorianCalendar.MONTH, 0);
    return resetMonth(cal);
  }
  
  /**
   * Sets the cal's DATE back to 1, and calls {@link #resetMidnight(GregorianCalendar)}
   * @param cal
   * @return the time at midnight on the first day of the month
   */
  public static long resetMonth(GregorianCalendar cal) {
    cal.set(GregorianCalendar.DATE, 1);
    return resetMidnight(cal);
  }
  
  /**
   * Sets the cal's MINUTE, SECOND and MILLISECOND back to 0,
   * @param cal
   * @return The start of the current hour
   */
  public static long resetHour(GregorianCalendar cal) {
    cal.set(GregorianCalendar.MINUTE, 0);
    cal.set(GregorianCalendar.SECOND, 0);
    cal.set(GregorianCalendar.MILLISECOND, 0);
    return cal.getTimeInMillis();
  }
  
  /**
   * Sets the cal's HOUR_OF_DAY MINUTE, SECOND and MILLISECOND back to 0,
   * @param cal
   * @return The start of the current day
   */
  public static long resetMidnight(GregorianCalendar cal) {
    cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
    cal.set(GregorianCalendar.MINUTE, 0);
    cal.set(GregorianCalendar.SECOND, 0);
    cal.set(GregorianCalendar.MILLISECOND, 0);
    return cal.getTimeInMillis();
  }
  
  /**
   * 
   * @param year Year (4 digits of course)
   * @param month Jan = 0
   * @param day Day of the month - between 1 and the number of days in the month
   * @param hour between 0 and 23
   * @return new GregorianCalendar
   */
  public static GregorianCalendar newGregorianCalendar(int year, int month, int day, int hour) {
    GregorianCalendar gc = newGregorianCalendar();
    gc.set(GregorianCalendar.YEAR, year);
    gc.set(GregorianCalendar.MONTH, month);
    gc.set(GregorianCalendar.DATE, day);
    gc.set(GregorianCalendar.HOUR_OF_DAY, hour);
    gc.set(GregorianCalendar.MINUTE, 0);
    gc.set(GregorianCalendar.SECOND, 0);
    gc.set(GregorianCalendar.MILLISECOND, 0);
    return gc;
  }
  
  /**
   * 
   * @param year Year (4 digits of course)
   * @param month Jan = 0
   * @param day Day of the month - between 1 and the number of days in the month
   * @return new GregorianCalendar
   */
  public static GregorianCalendar newGregorianCalendar(int year, int month, int day) {
    return RawFmtUtils.newGregorianCalendar(year, month, day, 0);
  }
  
  public static GregorianCalendar newGregorianCalendarTodayStart() {
    GregorianCalendar gc = newGregorianCalendar();
    resetMidnight(gc);
    return gc;
  }
  
  public static GregorianCalendar newGregorianCalendarTomorrowStart() {
    FutureEvents futureEvents = new FutureEvents();
    return futureEvents.tomorrow();
  }
  
  public static int daysDifference(long from, long to) {
    return (int)Math.round((to - from)/MSEC_IN_DAY);
  }
  
  public static int hoursDifference(long from, long to) {
    return (int)Math.round((to - from)/MSEC_IN_HOUR);
  }
  
  public static int secondsDifference(long from, long to) {
    return (int)Math.round((to - from)/1000);
  }
  
  /**
   * Calculates the number of days in the month accounting for leap year.
   * @param year
   * @param month Jan = 0;
   * @return the number of days in the month.
   */
  public static int daysInMonth(int year, int month) {
    YearMonth ym =  YearMonth.of(year, month+1);
    return ym.lengthOfMonth();
  }
  
  public static void throwException(Object caller, String message, Object context) throws GCTrafficFormatException {
    throw new GCTrafficFormatException(caller, message, context);
  }
  
  public static String formatDouble(double value) {
    if (Double.isNaN(value))
      value = 0;
    return df.format(value);
  }
  
  /**
   * If its an integer, round to an Integer
   * @param value
   * @return Either an Integer or Double, never null
   */
  public static Number checkInt(double value) {
    if (Double.isNaN(value))
      return Integer.valueOf(0);
    if (Double.isInfinite(value))
      return Integer.valueOf(0);
    if (value == 0.0d)
      return Integer.valueOf(0);
    if (value == Math.rint(value))
      return Integer.valueOf((int)Math.rint(value));
    return Double.valueOf(value);
  }
  
  /**
   * If the value is within 0.001 of an integer, round to an integer 
   * @param value
   */
  public static String format1CheckInt(double value) {
    final String ZERO = "0";
    if (Double.isNaN(value))
      return ZERO;
    if (Double.isInfinite(value))
      return ZERO;
    if (value == 0.0d)
      return ZERO;
    double rounded = Math.rint(value);
    if (Math.abs(value - rounded) < 0.001)
      return Integer.toString((int)rounded);
    return df1.format(value);
  }
  
  /**
   * 
   * @see CoordinateObjectComputation
   * @deprecated Use {@link CoordinateObjectComputation#getGreatCircleDistance(double, double, double, double)}
   */
  @Deprecated
  public static double getGreatCircleDistance(double lat1, double lng1, double lat2, double lng2) {
    return CoordinateObjectComputation.getGreatCircleDistance(lat1, lng1, lat2, lng2);
  }
  
  public static HourMinPeriodOfDay createHourMin(int periodLengthMin) {
    return new HourMinPeriodOfDay(periodLengthMin);
  }
  
  public static void toPositive2Digit(int value, StringBuilder result) {
    if (value < 0)
      return;
    if (value < 10)
      result.append('0');
    result.append(value);
  }
  
  public static String formatHHmm(int hour, int minute) {
    StringBuilder result = new StringBuilder();
    toPositive2Digit(hour, result);
    result.append(':');
    toPositive2Digit(minute, result);
    return result.toString();
  }
  
  public static class HM {
    final static int _1440_ = 1440;
    public int hour;
    public int minute;

    public int getMinuteOfDay() {
      return hour*60 + minute;
    }
    
    public String toHHmm() {
      return RawFmtUtils.formatHHmm(hour, minute);
    }
    
    public void setFromMSecs(long t) {
      hour   = RawFmtUtils.getHour(t);
      minute = RawFmtUtils.getMinute(t);
    }

    public void setFrom1440(int value1440) {
      hour   = value1440 / 60;
      minute = value1440 % 60;
    }
    
    public int getMinutesFrom(int H, int M) {
      int myValue = getMinuteOfDay();
      int compare = H*60 + M;
      int result = compare-myValue;
      return result > 0 ? result : (_1440_-myValue) + compare;
    }
    
  }
  
  public static class HourMinPeriodOfDay extends HM {
    public final int periodLengthMin;
    
    private HourMinPeriodOfDay(int p) {
      this.periodLengthMin = p;
    }
    
    public HourMinPeriodOfDay addPeriod() {
      HourMinPeriodOfDay result = new HourMinPeriodOfDay(periodLengthMin);
      
      int periodStartMinInDay = getMinuteOfDay() + periodLengthMin;
      if (periodStartMinInDay >= _1440_)
        periodStartMinInDay -= _1440_;
      result.hour = periodStartMinInDay / 60;
      result.minute = periodStartMinInDay % 60;
      return result;
    }
    
    /**
     * 
     * @param endHour From 1 to 24
     * @param endMin From 0 to 59
     */
    public void setEnd(int endHour, int endMin) {
      int periodEndMinInDay = endHour*60 + endMin;
      int periodStartMinInDay = periodEndMinInDay - periodLengthMin;
      if (periodStartMinInDay < 0) { // if its the end of the day
        periodStartMinInDay = _1440_ + periodStartMinInDay;
      }
      
      super.setFrom1440(periodStartMinInDay);
    }
    
    public boolean test(int H, int M) {
      return getMinutesFrom(H, M) < periodLengthMin;
    }
  }
  
  
  public static class DOWHourMin extends HM {
    /**
     * Sunday = 0
     */
    public int DOW;
    
    public static DOWHourMin createFromMinutes(int minutes10080) {
      DOWHourMin result = new DOWHourMin();
      result.setFromMinutes(minutes10080);
      return result;
    }

    /**
     * 
     * @return A number ranging from 0 to 10080-1 (7x24x60)
     * @see #setFromDOWMinutes(int)
     */
    public int toMinutes() {
      return (DOW * _1440_) + hour*60 + minute;
    }

    @Override
    public void setFromMSecs(long t) {
      DOW    = RawFmtUtils.getDOW(t);
      super.setFromMSecs(t);
    }
    
    /**
     * @param minutes10080
     * @see #toMinutes()
     */
    public void setFromMinutes(int minutes10080) {
      DOW = minutes10080/_1440_;

      int value1440 = minutes10080 - (DOW*_1440_);
      setFrom1440(value1440);
    }
  }
  
  
  static class FutureEvents {
    
    /**
     * 
     * @return Beginning of the next available Monday, starting with tomorrow
     * @see #nextDay(int)
     */
    public GregorianCalendar monday() {
      return nextDay(GregorianCalendar.MONDAY);
    }
    
    /**
     * 
     * @return Beginning of the next available Tuesday, starting with tomorrow
     * @see #nextDay(int)
     */
    public GregorianCalendar tuesday() {
      return nextDay(GregorianCalendar.TUESDAY);
    }
    
    /**
     * 
     * @param dow One of the Calendar's day of the week codes. Eg Calendar.MONDAY
     * @return Beginning of the next available day of the week, starting with tomorrow
     */
    public GregorianCalendar nextDay(int dow) {
      GregorianCalendar gc = tomorrow();
      while (gc.get(GregorianCalendar.DATE) != dow)
        gc.add(GregorianCalendar.DATE, 1);
      return gc;
    }
    
    public GregorianCalendar tomorrow() {
      GregorianCalendar gc = RawFmtUtils.newGregorianCalendarTodayStart();
      gc.add(GregorianCalendar.DATE, 1);
      return gc;
    }
  }
  
  static {
    df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
  }

}
