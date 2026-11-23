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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents a part of a filename which contains time
 */
public class TimedFilename implements TimedObject {
  
  /**
   * yyyyMMddHHmm
   * For the month field, Jan = 1
   */
  public final int[] startDateTime = new int[5];
  
  public GregorianCalendar getStartDate() {
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar();
    gc.set(GregorianCalendar.YEAR, startDateTime[0]);
    gc.set(GregorianCalendar.MONTH, startDateTime[1]-1);
    gc.set(GregorianCalendar.DATE, startDateTime[2]);
    gc.set(GregorianCalendar.HOUR_OF_DAY, startDateTime[3]);
    gc.set(GregorianCalendar.MINUTE, startDateTime[4]);
    gc.set(GregorianCalendar.SECOND, 0);
    gc.set(GregorianCalendar.MILLISECOND, 0);
    return gc;
  }
  
  @Override
  public long getTime() {
    return getStartDate().getTimeInMillis();
  }
  
  public GregorianCalendar getStartDateNoTime() {
    return RawFmtUtils.newGregorianCalendar(startDateTime[0], startDateTime[1]-1, startDateTime[2], 0);
  }
  
  /**
   * This will return zero if the file {@link #isTimeless()}
   * @return The year
   */
  public int getYear() {
    return startDateTime[0];
  }
  
  /**
   * This will return zero if the file {@link #isTimeless()}
   * @return The month, where Jan = 1
   */
  public int getMonth() {
    return startDateTime[1];
  }
  
  /**
   * This will return zero if the file {@link #isTimeless()}
   * @return The day of the month, where the first day = 1
   */
  public int getDate() {
    return startDateTime[2];
  }
  
  /**
   * This will always return zero if the file {@link #isTimeless()}
   * @return The hour of the day, where the first hour = 0
   */
  public int getHour() {
    return startDateTime[3];
  }
  
  /**
   * This will always return zero if the file {@link #isTimeless()}
   * @return The minute of the hour, where the first minute = 0
   */
  public int getMinute() {
    return startDateTime[4];
  }
  
  public boolean isTimeless() {
    return (startDateTime[0] == 0) && 
           (startDateTime[1] == 0) && 
           (startDateTime[2] == 0) && 
           (startDateTime[3] == 0) && 
           (startDateTime[4] == 0);
  }
  
  public void setStartDate(Calendar gc) {
    startDateTime[0] = gc.get(Calendar.YEAR);
    startDateTime[1] = gc.get(Calendar.MONTH)+1;
    startDateTime[2] = gc.get(Calendar.DATE);
    startDateTime[3] = gc.get(Calendar.HOUR_OF_DAY);
    startDateTime[4] = gc.get(Calendar.MINUTE);
  }
  
  public void setYMD(int year, int monthJanEq1, int day) {
    startDateTime[0] = year;
    startDateTime[1] = monthJanEq1;
    startDateTime[2] = day;
  }
  
  /**
   * 
   * @return A format yyyy-MM-dd
   */
  public String toISODate() {
    StringBuilder result = new StringBuilder();
    result.append(getYear());
    result.append('-');
    
    if (getMonth() < 10)
      result.append('0');
    result.append(getMonth());
    result.append('-');
    
    if (getDate() < 10)
      result.append('0');
    result.append(getDate());
    return result.toString();
  }
}
