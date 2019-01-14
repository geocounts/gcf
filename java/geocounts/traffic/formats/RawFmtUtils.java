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
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;

/**
 * Simple utility functions used by count formatting but not specifically 
 * related to count information per-se
 *
 */
public class RawFmtUtils {
  public static final double KILOMETERS_PER_MILE = 1.60934;
  public static final double INCHES_PER_METER    = 39.3701;
  public static final double METERS_PER_FOOT     = 0.3048;
  public static final double POUNDS_PER_KILOGRAM = 2.20462;
  public static long MSEC_IN_HOUR = 3600000;
  public static long MSEC_IN_DAY = 86400000;
  public static final DecimalFormat df1 = new DecimalFormat("0.0");
  public static final DecimalFormat df3 = new DecimalFormat("0.000");
  public static final DecimalFormat df6 = new DecimalFormat("0.000000");
  
  private static DecimalFormat df;
  
  public static final String LINE_FEED = "\r\n";
  
  private static SimpleTimeZone GMT = new SimpleTimeZone(0, "GMT+00:00");

  private static final SimpleDateFormat ISO8601 = simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  
  /**
   * yyyy-MM-dd
   */
  private static final SimpleDateFormat ISO8601_DATEONLY = simpleDateFormat("yyyy-MM-dd");

  /**
   * HH:mm:ss.SSS
   */
  private static final SimpleDateFormat ISO8601_TIMEONLY = simpleDateFormat("HH:mm:ss.SSS");

  /**
   * A simple date format as: yyyy-MM-dd'T'HH:mm:ss.SSS<br />
   * This deliberately does not have a 'z' at the end
   */
  public static final SimpleDateFormat ISO8601_MSECS() {
    return simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
  }
  
  private static final SimpleDateFormat ISO8601_MSECS = simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
  
  public static String ISO8601_MSECS_format(long value) {
    return ISO8601_MSECS.format(value);
  }
  
  /**
   * @param value
   * @return Formatted date value as {@link #ISO8601()}
   */
  public static final String ISO8601_format(long value) {
    return ISO8601.format(value);
  }

  /**
   * A simple date format as: yyyy-MM-dd'T'HH:mm:ss<br />
   * This deliberately does not have a 'z' at the end
   */
  public static final SimpleDateFormat ISO8601() {
    return simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  }
  
  /**
   * Used by the header to define time for tallies<br />
   * yyyy/MM/dd,HH
   */
  protected static SimpleDateFormat sdfHeaderT() {
    return simpleDateFormat("yyyy/MM/dd,HH");
  }

  /**
   * yyyy/MM/dd
   */
  public static final String V_DateFormat = "yyyy/MM/dd";

  /**
   * Used by the header to define time for observations<br />
   * yyyy/MM/dd,HH:mm:ss.SSS
   */
  protected static SimpleDateFormat sdfHeaderV() {
    return simpleDateFormat("yyyy/MM/dd,HH:mm:ss.SSS");
  }
  
  /**
   * @param value
   * @return Formatted date value as {@link #ISO8601_DATEONLY()}
   */
  public static final String ISO8601_DATEONLY_format(long value) {
    return ISO8601_DATEONLY.format(value);
  }
  
  /**
   * @param value
   * @return Formatted date value as {@link #ISO8601_TIMEONLY()}
   */
  public static final String ISO8601_TIMEONLY_format(long value) {
    return ISO8601_TIMEONLY.format(value);
  }
  
  /**
   * 
   * @return Format as yyyy-MM-dd
   */
  public static final SimpleDateFormat ISO8601_DATEONLY() {
    return simpleDateFormat("yyyy-MM-dd");
  }
  
  /**
   * 
   * @return Format as HH:mm:ss.SSS
   */
  public static final SimpleDateFormat ISO8601_TIMEONLY() {
    return simpleDateFormat("HH:mm:ss.SSS");
  }
  
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
  
  
  public static SimpleDateFormat simpleDateFormat(String fmt) {
    SimpleDateFormat result = new SimpleDateFormat(fmt);
    result.setTimeZone(GMT);
    return result;
  }
  
  /**
   * This sets the minute, second and millisecond to zero
   * @param time
   * @return A GregorianCalendar set to the hour in which the time appears
   */
  public static GregorianCalendar trimHour(long time) {
    GregorianCalendar gc = newGregorianCalendar(time);
    gc.set(GregorianCalendar.MINUTE, 0);
    gc.set(GregorianCalendar.SECOND, 0);
    gc.set(GregorianCalendar.MILLISECOND, 0);
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
    trimHour(gc);
    gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
  }
  
  /**
   * Trim the calendar to be midnight on the first day of the calendar's current month
   * @param gc
   */
  public static void trimMonth(GregorianCalendar gc) {
    trimDay(gc);
    gc.set(GregorianCalendar.DATE, 1);
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

  public static int getMonth(long time) {
    return newGregorianCalendar(time).get(GregorianCalendar.MONTH);
  }
  
  public static int getDOW(long time) {
    return newGregorianCalendar(time).get(GregorianCalendar.DAY_OF_WEEK);
  }
  
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
  
  public static GregorianCalendar newGregorianCalendarTodayStart() {
    GregorianCalendar gc = newGregorianCalendar();
    gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
    gc.set(GregorianCalendar.MINUTE, 0);
    gc.set(GregorianCalendar.SECOND, 0);
    gc.set(GregorianCalendar.MILLISECOND, 0);
    
    return gc;
  }
  
  public static int daysDifference(long from, long to) {
    return (int)Math.round((to - from)/MSEC_IN_DAY);
  }
  
  public static int hoursDifference(long from, long to) {
    return (int)Math.round((to - from)/MSEC_IN_HOUR);
  }
  
  public static void throwException(Object caller, String message, Object context) throws GCTrafficFormatException {
    throw new GCTrafficFormatException(caller, message, context);
  }
  
  public static String formatDouble(double value) {
    return df.format(value);
  }
  
  /**
   * Calculate the distance between two points across the Earth's surface.
   * @param lat1
   * @param lng1
   * @param lat2
   * @param lng2
   * @return Distance in meters
   * @see https://en.wikipedia.org/wiki/Great-circle_distance#Computational_formulas
   * @see com.vaultagemedia.math.Geodesy
   */
  public static double getGreatCircleDistance(double lat1, double lng1, double lat2, double lng2) {
    if ((lat1 == lat2) && (lng1 == lng2)) {
      return 0;
    }

    double lat1_radians = Math.toRadians(lat1);
    double lat2_radians = Math.toRadians(lat2);
    double lon1_radians = Math.toRadians(lng1);
    double lon2_radians = Math.toRadians(lng2);

    double Distanceradians = Math.acos(Math.sin(lat1_radians)*Math.sin(lat2_radians) +
                             Math.cos(lat1_radians)*Math.cos(lat2_radians)*Math.cos(lon2_radians-lon1_radians));

    return 1852*60d*Math.toDegrees(Distanceradians);
  }
  
  public static HourMinPeriodOfDay createHourMin(int periodLengthMin) {
    return new HourMinPeriodOfDay(periodLengthMin);
  }
  
  public static class HourMinPeriodOfDay {
    public int hour;
    public int minute;
    public final int periodLengthMin;
    
    private HourMinPeriodOfDay(int p) {
      this.periodLengthMin = p;
    }
    
    public int getMinuteOfDay() {
      return hour*60 + minute;
    }
    
    public HourMinPeriodOfDay addPeriod() {
      HourMinPeriodOfDay result = new HourMinPeriodOfDay(periodLengthMin);
      
      int periodStartMinInDay = getMinuteOfDay() + periodLengthMin;
      if (periodStartMinInDay >= 24*60)
        periodStartMinInDay -= 24*60;
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
        periodStartMinInDay = 24*60 + periodStartMinInDay;
      }
      
      hour = periodStartMinInDay / 60;
      minute = periodStartMinInDay % 60;
    }
  }
  
  static {
    df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
  }
}
