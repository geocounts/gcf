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
import java.util.function.LongPredicate;

/**
 * A time period.
 * Used to indicate a duration (start and stop) time
 * @since 2.017
 */
public class Timeframe implements Comparable<Timeframe>, TimedObject {
  public long ID;
  
  private final long[] SR = new long[2];
  
  public String label;
  
  public Timeframe() {
  }
  
  public LongPredicate createIsBetweenLongPredicate() {
    return new IsBetween(this);
  }
  
  static class IsBetween implements LongPredicate {
    private final Timeframe tf;
    IsBetween(Timeframe tf) {
      this.tf = tf;
    }
    @Override
    public boolean test(long value) {
      return tf.isBetween(value);
    }
  }

  public void update(long time) {
    conditionallyUpdateStartTime(time);
    conditionallyUpdateEndTime(time);
  }
  
  private void conditionallyUpdateStartTime(long time) {
    if ((time < this.SR[0]) || !this.hasStartTime())
      setStartTime(time);
  }
  
  private void conditionallyUpdateEndTime(long time) {
    if ((time > this.SR[1]) || !this.hasEndTime())
      setEndTime(time);
  }
  
  /**
   * Does not change the SR if start or stop are {@link Long#MAX_VALUE} or {@link Long#MIN_VALUE}
   * @param start
   * @param stop
   */
  public void set(long start, long stop) {
    setStartTime(start);
    setEndTime(stop);
  }
  
  /**
   * 
   * @return True if either {@link #hasStartTime()} OR {@link #hasEndTime()} are true
   * @see #isCompletelySet()
   */
  public boolean isSet() {
    return (hasStartTime() || hasEndTime());
  }
  
  /**
   * 
   * @return True if both {@link #hasStartTime()} AND {@link #hasEndTime()} are true
   */
  public boolean isCompletelySet() {
    return hasStartTime() && hasEndTime() && (this.SR[1] >= this.SR[0]);
  }
  
  public final void copyFrom(Timeframe other) {
    for (int idx = 0; idx < other.SR.length; idx++) {
      this.SR[idx] = other.SR[idx];
    }
  }
  
  /**
   * Checks whether the value is less than or equal to the start and less then or equal to the end time
   * @param value Time to test
   * @return True if the value is between the start and end times
   * @since 1.9943
   */
  public boolean isBetween(long value) {
    if (hasStartTime() && (value >= SR[0]))
      if (hasEndTime() && (value <= SR[1]))
        return true;
    return false;
  }
  
  /**
   * Does not change the SR if value us {@link Long#MAX_VALUE} or {@link Long#MIN_VALUE}
   * @param value
   * @return true if the start time was changed
   */
  public boolean setStartTime(long value) {
    if ((value == Long.MAX_VALUE) || (value == Long.MIN_VALUE))
      return false;
    this.SR[0] = value;
    return true;
  }

  /**
   * 
   * @return Same as {@link #getStartTime()}
   */
  @Override
  public long getTime() {
    return getStartTime();
  }
  
  public long getStartTime() {
    return this.SR[0];
  }

  public boolean hasStartTime() {
    return this.SR[0] != 0;
  }
  
  /**
   * Does not change the SR if value us {@link Long#MAX_VALUE} or {@link Long#MIN_VALUE}
   * @param value
   * @return true if the end time was changed
   */
  public boolean setEndTime(long value) {
    if ((value == Long.MAX_VALUE) || (value == Long.MIN_VALUE))
      return false;
    this.SR[1] = value;
    return true;
  }
  
  public long getEndTime() {
    return this.SR[1];
  }

  public boolean hasEndTime() {
    return this.SR[1] != 0;
  }
/*
  @Deprecated
  public long getTimeSpan() {
    return getDuration();
  }
*/
  public long getDuration() {
    if (isCompletelySet())
      return getEndTime() - getStartTime();
    return 0;
  }
  
  public long getDurationSeconds() {
    return getDuration()/1000l;
  }
  
  public long getDurationMinutes() {
    return getDuration()/60000;
  }
  
  public long getDurationHours() {
    return getDuration()/RawFmtUtils.MSEC_IN_HOUR;
  }
  
  /**
   * Number of hours, including partial hours, which occur during the given day of week.
   * For example, how many hours occur on any Sunday within this Timeframe.
   * @param DOWSunEq0 Day of week, where Sun = 0
   */
  public long getDurationHoursInWeekday(int DOWSunEq0) {
    if (!isCompletelySet())
      return 0;
    
    final int DOWSunEq1 = DOWSunEq0+1;
    long result = 0;
    GregorianCalendar gc = RawFmtUtils.trimHour(this.getStartTime());
    while (gc.getTimeInMillis() < this.getEndTime()) {
      if (gc.get(GregorianCalendar.DAY_OF_WEEK) == DOWSunEq1)
        result++;
      gc.add(GregorianCalendar.HOUR_OF_DAY, 1);
    }
    return result;
  }
  
  /**
   * Number of hours, including partial hours, which occur during the given hour of day.
   * For example, how many 3am hours occur within this Timeframe.
   * @param HOD Hour of day, where midnight = 0 and 11 PM = 23
   */
  public long getDurationHoursInHOD(int HOD) {
    if (!isCompletelySet())
      return 0;
    
    long result = 0;
    GregorianCalendar gc = RawFmtUtils.trimHour(this.getStartTime());
    while (gc.getTimeInMillis() < this.getEndTime()) {
      if (gc.get(GregorianCalendar.HOUR_OF_DAY) == HOD)
        result++;
      gc.add(GregorianCalendar.HOUR_OF_DAY, 1);
    }
    return result;
  }
  
  public long getDurationDays() {
    if (!isCompletelySet())
      return 0;
    return getDuration()/RawFmtUtils.MSEC_IN_DAY;
  }
  
  public long getDurationWeeks() {
    if (!isCompletelySet())
      return 0;
    return getDuration()/(7*RawFmtUtils.MSEC_IN_DAY);
  }
  
  public long getDurationMonths() {
    if (!isCompletelySet())
      return 0;
    
    GregorianCalendar gc1 = RawFmtUtils.newGregorianCalendar(getStartTime());
    int months1 = gc1.get(GregorianCalendar.YEAR) * 12 + gc1.get(GregorianCalendar.MONTH);
    GregorianCalendar gc2 = RawFmtUtils.newGregorianCalendar(getEndTime());
    int months2 = gc2.get(GregorianCalendar.YEAR) * 12 + gc2.get(GregorianCalendar.MONTH);

    return months2 - months1;
  }
  
  public long getDurationYears() {
    if (!isCompletelySet())
      return 0;
    
    int year1 = RawFmtUtils.newGregorianCalendar(getStartTime()).get(GregorianCalendar.YEAR);
    int year2 = RawFmtUtils.newGregorianCalendar(getEndTime()).get(GregorianCalendar.YEAR);
    
    return year2 - year1;
  }
  
  public int compareTo(long time) {
    if (time < this.SR[0])
      return 1;
    if (time >= this.SR[1])
      return -1;
    return 0;
  }
  
  /**
   * This method will determine whether this Timeframe represents a given number of durations, 
   * starting from the first available time slot.
   * For example, does it represent exactly one day, 7 days, a month etc.
   * The start time must be an exact integer multiple of the duration. 
   * For example, a duration of 1 day must start and end at midnight
   */
  public boolean matchesDuration(TallyDurationEnum TD, int numberOfDurations) {
    if (!isCompletelySet())
      return false;
    // check the start is on the nearest boundary
    long trimmedStartTime = TD.trim(getStartTime()).getTimeInMillis();
    if (trimmedStartTime != getStartTime())
      return false;

    // check the end is exactly the correct number of durations from the start
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(getStartTime());
    TD.add(gc, numberOfDurations);
    return getEndTime() == gc.getTimeInMillis();
  }
  
  /**
   * 
   * @param TD
   * @return A positive integer representing a precise duration, or -1 if the duration is not precise
   */
  public int getPreciseDurations(TallyDurationEnum TD) {
    if (!isCompletelySet())
      return -1;
    // check the start is on the nearest boundary
    GregorianCalendar trimmedStartTime = TD.trim(getStartTime());
    if (trimmedStartTime.getTimeInMillis() != getStartTime())
      return -1;
    
    // check the end is on the nearest boundary
    GregorianCalendar trimmedEndTime = TD.trim(getEndTime());
    if (trimmedEndTime.getTimeInMillis() != getEndTime())
      return -1;
    
    long result = TD.diff(trimmedStartTime, trimmedEndTime);
    return (int)result;
  }

  @Override
  public int compareTo(Timeframe o) {
    int result = Long.compare(this.getStartTime(), o.getStartTime());
    if (result != 0)
      return result;
    return Long.compare(this.getEndTime(), o.getEndTime());
  }
  
  /**
   * 
   * @return A new {@link Timeframe}, never null.
   */
  public static Timeframe getWidestSpread(Iterable<Timeframe> inputs) {
    Timeframe result = new Timeframe();
    for (Timeframe tf: inputs) {
      if (tf.hasStartTime())
        result.conditionallyUpdateStartTime(tf.getStartTime());
      if (tf.hasEndTime())
        result.conditionallyUpdateEndTime(tf.getEndTime());
    }
    return result;
  }
}
