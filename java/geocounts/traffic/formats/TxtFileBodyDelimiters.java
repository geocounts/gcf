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

import java.util.GregorianCalendar;

/**
 * Manages the body delimiters
 */
public class TxtFileBodyDelimiters extends RawTrafficDataHeaderElement {

  public static final char V1_BODY_PART    = ';';
  public static final char V1_BODY_FIELD   = ',';
  public static final char V1_BODY_DATE    = '/';
  
  /**
   * A semicolon - ;
   */
  public char BODY_PART;
  /**
   * A comma - ,
   */
  public char BODY_FIELD;
  /**
   * A forward slash - /
   */
  public char BODY_DATE;
  
  public TxtFileBodyDelimiters() {
    clear();
  }
  
  public void copyFrom(TxtFileBodyDelimiters other) {
    BODY_PART    = other.BODY_PART;
    BODY_FIELD   = other.BODY_FIELD;
    BODY_DATE    = other.BODY_DATE;
  }
  
  /**
   * 
   * @return yyyy/MM/dd,HH
   */
  public String formatBodyTallyDateTime(long dateTime) {
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(dateTime);
    StringBuilder result = new StringBuilder();
    formatBodyEventDate(gc, BODY_DATE, result);
    result.append(BODY_FIELD);
    RawFmtUtils.toPositive2Digit(gc.get(GregorianCalendar.HOUR_OF_DAY), result);
    // return simpleDateFormat("");
    return result.toString();
  }

  public long parseBodyTallyDateTime(String dateStr, String hourStr) {
    int year = Integer.parseInt(dateStr.substring(0, 4));
    int monthJanEq1 = Integer.parseInt(dateStr.substring(5, 7));
    int day = Integer.parseInt(dateStr.substring(8, 10));
    int hour = Integer.parseInt(hourStr);
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(year, monthJanEq1-1, day, hour);
    return gc.getTimeInMillis();
  }

  public String formatBodyEventDateTime(long dateTime) {
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(dateTime);
    StringBuilder result = new StringBuilder();
    formatBodyEventDate(gc, BODY_DATE, result);
    result.append(BODY_FIELD);   // getObservationDateFormat().format(veh.getTime()));
    result.append(formatBodyEventTime(dateTime));
    return result.toString();
  }
  
  public String formatBodyEventDate(long dateTime) {
    StringBuilder result = new StringBuilder();
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(dateTime);
    formatBodyEventDate(gc, BODY_DATE, result);
    return result.toString();
  }
  
  public static void formatBodyEventDate(GregorianCalendar gc, char DELIM, StringBuilder result) {
    result.append(gc.get(GregorianCalendar.YEAR));
    result.append(DELIM);
    RawFmtUtils.toPositive2Digit(gc.get(GregorianCalendar.MONTH)+1, result);
    result.append(DELIM);
    RawFmtUtils.toPositive2Digit(gc.get(GregorianCalendar.DATE), result);
  }
  
  public static void formatBodyEventTime(GregorianCalendar gc, StringBuilder result) {
    result.append( formatBodyEventTime(gc.getTimeInMillis()) );
  }
  
  /**
   * ISO8601 (time only)
   */
  public static String formatBodyEventTime(long dateTime) {
    return RawFmtUtils.ISO8601_TIMEONLY_format(dateTime);
  }
  
  public long parseBodyEventDateTime(String date, String time) {
    return parseBodyEventDateTime(this, date, time, BODY_DATE);
  }
  
  public static long parseBodyEventDateTime(Object caller, String date, String time, char DATE_DELIM) {
    int[] ymd = RawFmtUtils.splitInt(date, DATE_DELIM);  // 2021/04/30
    if (ymd[0] > 3000)
      RawFmtUtils.throwException(caller, "Invalid year", ymd[0]);
    String[] hms = RawFmtUtils.split(time, ':');  // 13:09:39.291
    int[] sec_msec = RawFmtUtils.splitInt(hms[2], '.');
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(ymd[0], ymd[1]-1, ymd[2], Integer.parseInt(hms[0]));
    gc.set(GregorianCalendar.MINUTE, Integer.parseInt(hms[1]));
    gc.set(GregorianCalendar.SECOND, sec_msec[0]);
    gc.set(GregorianCalendar.MILLISECOND, sec_msec[1]);
    return gc.getTimeInMillis();
  }

  public boolean isDefault() {
    return (BODY_DATE == V1_BODY_DATE) && (BODY_FIELD == V1_BODY_FIELD) && (BODY_PART == V1_BODY_PART);
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.FL;
  }

  @Override
  protected void read(String[] data) {
    BODY_DATE  = (char)Integer.parseInt(data[0]);
    BODY_FIELD = (char)Integer.parseInt(data[1]);
    BODY_PART  = (char)Integer.parseInt(data[2]);
  }

  @Override
  public String[] write() {
    return new String[] {Integer.toString((int)BODY_DATE), Integer.toString((int)BODY_FIELD), Integer.toString((int)BODY_PART)};
  }

  @Override
  public void clear() {
    setAsVersion(1);
  }

  public void setAsVersion(int v) {
    if (v == 1) {
      BODY_PART    = V1_BODY_PART;
      BODY_FIELD   = V1_BODY_FIELD;
      BODY_DATE    = V1_BODY_DATE;
      return;
    }
    RawFmtUtils.throwException(this, "Invalid FL version", v);
  }

}