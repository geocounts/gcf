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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;

/*
 * These periods represent a hierarchy:
 * <ul>
 * <li>{@link #year 1 year}</li>
 * <li>{@link #month 1 month} There are 12 months in a year</li>
 * <li>{@link #day 1 day (of month)} There 28, 29, 30 or 31 days in a month</li>
 * <li>{@link #hr 1 hour} There are 24 hours in a day (ignoring daylight savings events)</li>
 * <li>{@link #m15 15 minutes} There are 60 minutes in an hour</li>
 * <li>{@link #m5 5 minutes} There are 3 5-minute periods in a 15 minute period</li>
 * <li>{@link #m1 1 minute} There are 5 1-minute periods in a 5 minute period. This is the smallest available granularity</li>
 * <li>and {@link #off} This indicates no tallies are counted</li>
 * </ul>
 * Other time periods are multiples of these fundamental types. E.g. a 30 minute period can be stored as 2 15-minute periods.
 * The week is represented in {@link WeekDayHelper WeekDayHelper}
 */

/**
 * The periods (and therefore durations) available to tally records.
 * 
 * @see EnumHeaderRecords#TD
 * @see WeekDayHelper
 * @see VehicleTallyRec tally records
 */
public enum TallyDurationEnum implements IdDesc {
  off(0, 0, 0, "Off") {
    @Override
    public String getDescription() {
      return "Not recording tallies";
    }
    
    @Override
    public int periodsPerHour() {
      return 0;
    }
    
    @Override
    public void trim(GregorianCalendar result) {
      // do nothing
    }

    @Override
    public void add(GregorianCalendar target, int amount) {
      // do nothing
    }

    @Override
    public long getComparatorCode(GregorianCalendar from) {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public boolean isSubHour() {
      return false;
    }
  },
  /**
   * 1 minute
   */
  m1(1, GregorianCalendar.MINUTE, 1, "Minute") {
    @Override
    public int getHourlySubPeriod(int numberOfMins) {
      return numberOfMins/super.minutes + 1;
    }

    @Override
    public void trim(GregorianCalendar result) {
      result.set(GregorianCalendar.SECOND, 0);
      result.set(GregorianCalendar.MILLISECOND, 0);
    }

    @Override
    public void add(GregorianCalendar target, int amount) {
      target.add(GregorianCalendar.MINUTE, amount);
    }

    @Override
    public long getComparatorCode(GregorianCalendar from) {
      return 60*hr.getComparatorCode(from);
    }
  }, 
  /**
   * 5 minutes
   */
  m5(5, GregorianCalendar.MINUTE, 5, "Minute") {
    @Override
    public int getHourlySubPeriod(int numberOfMins) {
      return numberOfMins/super.minutes + 1;
    }

    @Override
    public void trim(GregorianCalendar result) {
      int periodID = this.getHourlySubPeriod(result.get(GregorianCalendar.MINUTE));
      RawFmtUtils.trimHour(result);
      result.set(GregorianCalendar.MINUTE, (periodID-1)*5);
    }

    @Override
    public void add(GregorianCalendar target, int amount) {
      target.add(GregorianCalendar.MINUTE, amount*5);
    }

    @Override
    public long getComparatorCode(GregorianCalendar from) {
      return 12*hr.getComparatorCode(from);
    }
  }, 
  /**
   * 15 minutes
   */
  m15(15, GregorianCalendar.MINUTE, 15, "Minute") {
    @Override
    public int getHourlySubPeriod(int numberOfMins) {
      return numberOfMins/super.minutes + 1;
    }

    @Override
    public void trim(GregorianCalendar result) {
      int periodID = this.getHourlySubPeriod(result.get(GregorianCalendar.MINUTE));
      RawFmtUtils.trimHour(result);
      result.set(GregorianCalendar.MINUTE, (periodID-1)*15);
    }

    @Override
    public void add(GregorianCalendar target, int amount) {
      target.add(GregorianCalendar.MINUTE, amount*15);
    }

    @Override
    public long getComparatorCode(GregorianCalendar from) {
      return 4*hr.getComparatorCode(from);
    }
  },
  
  /**
   * 1 hour (60 minutes)
   */
  hr(60, GregorianCalendar.HOUR_OF_DAY, 1, "Hour") {
    @Override
    public void trim(GregorianCalendar result) {
      RawFmtUtils.trimHour(result);
    }
    
    @Override
    public String getDescription() {
      return "1 hour";
    }

    @Override
    public void add(GregorianCalendar target, int amount) {
      target.add(GregorianCalendar.HOUR_OF_DAY, amount);
    }

    @Override
    public long getComparatorCode(GregorianCalendar from) {
      return 24*day.getComparatorCode(from);
    }
  },
  
  /**
   * 1 day (24 hours, 1440 minutes)
   */
  day(1440, GregorianCalendar.DAY_OF_MONTH, 1, "Day") {
    @Override
    public String getDescription() {
      return "1 day";
    }

    @Override
    public int periodsPerHour() {
      return 0;
    }

    @Override
    public void trim(GregorianCalendar result) {
      RawFmtUtils.trimDay(result);
    }

    @Override
    public void add(GregorianCalendar target, int amount) {
      target.add(GregorianCalendar.DATE, amount);
    }

    @Override
    public long getComparatorCode(GregorianCalendar from) {
      LocalDate now = from.toZonedDateTime().toLocalDate();
      LocalDate epoch = LocalDate.ofEpochDay(0);
      return ChronoUnit.DAYS.between(epoch, now);
    }
  },
  
  month(0, GregorianCalendar.MONTH, 1, "Month") {
    @Override
    public String getDescription() {
      return "1 month";
    }

    @Override
    public int periodsPerHour() {
      return 0;
    }
    
    @Override
    public boolean isSubHour() {
      return false;
    }
    
    @Override
    public void trim(GregorianCalendar result) {
      RawFmtUtils.trimMonth(result);
    }

    @Override
    public void add(GregorianCalendar target, int amount) {
      target.add(GregorianCalendar.MONTH, amount);
    }

    @Override
    public long getComparatorCode(GregorianCalendar from) {
      return 12*from.get(GregorianCalendar.YEAR) + 
                from.get(GregorianCalendar.MONTH);
    }
  },
  /**
   * A year
   */
  year(0, GregorianCalendar.YEAR, 1, "Year") {
    @Override
    public String getDescription() {
      return "1 year";
    }

    @Override
    public int periodsPerHour() {
      return 0;
    }

    @Override
    public boolean isSubHour() {
      return false;
    }
    
    @Override
    public void trim(GregorianCalendar result) {
      RawFmtUtils.trimMonth(result);
      result.set(GregorianCalendar.MONTH, 0);
    }

    @Override
    public void add(GregorianCalendar target, int amount) {
      target.add(GregorianCalendar.YEAR, amount);
    }

    @Override
    public long getComparatorCode(GregorianCalendar from) {
      return from.get(GregorianCalendar.YEAR);
    }
  };
  
  public final int minutes;
  public final int GC_ID;
  public final int GC_COUNT;
  private final String AxisLabel;
  private TallyDurationEnum(int m, int gcType, int gcTypeCount, String AxisLabel) {
    minutes = m;
    GC_ID = gcType;
    GC_COUNT = gcTypeCount;
    this.AxisLabel = AxisLabel;
  }
  
  public int periodsPerHour() {
    return 60/minutes;
  }
  
  public boolean isSubHour() {
    return minutes < 60;
  }
  
  @Override
  public String getId() {
    return name();
  }

  @Override
  public String getDescription() {
    return minutes + " minutes";
  }
  
  public final String getAxisLabel() {
    return AxisLabel;
  }
  
  /**
   * 
   * @return The number of milliseconds represented by this duration
   */
  public long getMSecs() {
    return minutes * 60000;
  }
  
  public static TallyDurationEnum fromPeriodsPerDay(int periods) throws GCTrafficFormatException {
    if (periods <= 0)
      RawFmtUtils.throwException(TallyDurationEnum.class, "Cannot decode duration", periods + " periods per day");
    return fromMinutes(24*60/periods);
  }
  
  public static TallyDurationEnum fromMinutes(int mins) throws GCTrafficFormatException {
    switch (mins) {
    case    0: return off;
    case    1: return m1;
    case    5: return m5;
    case   15: return m15;
    case   60: return hr;
    case 1440: return day;
    }
    RawFmtUtils.throwException(TallyDurationEnum.class, "Cannot decode duration", mins);
    return null; // never reaches here
  }
  
  public static TallyDurationEnum fromString(String field) throws GCTrafficFormatException {
    try {
      return fromMinutes(Integer.parseInt(field));
    } catch (NumberFormatException cantParse) {
      return TallyDurationEnum.valueOf(field);
    }
  }

  /**
   * E.g. for the {@link #m15}, if the numberOfMins is between 0 and 14, this will return 1
   * @param numberOfMins Must be between 0 and 59
   * @return A value greater than 1 representing the sub period, or 0 if this member is larger than an hour
   */
  public int getHourlySubPeriod(int numberOfMins) {
    return 0;
  }
  
  /**
   * Align the calendar by "flooring' to the boundary this duration represents.
   * Example: if the period is a {@link #year}, and the calendar is Jan 17 2017 at 15:24, then set the calendar to midnight on Jan 1, 2017
   * @param result
   */
  public abstract void trim(GregorianCalendar result);
  
  /**
   * Trims (floor) the given millisecond value to the nearest available value in this duration.
   * @return A {@link GregorianCalendar}
   * @see #trim(GregorianCalendar)
   */
  public final GregorianCalendar trim(long millis) {
    GregorianCalendar result = RawFmtUtils.newGregorianCalendar(millis);
    this.trim(result);
    return result;
  }

  /**
   * Adds a number of increments in this duration to the result calendar
   * @since 2.024
   */
  public abstract void add(GregorianCalendar result, int number);

  /**
   * Increments the result calendar by one of this duration
   * @since 2.024
   */
  public final void inc(GregorianCalendar result) {
    add(result, 1);
  }

  /**
   * Decrements the result calendar by one of this duration
   * @since 2.024
   */
  public final void dec(GregorianCalendar result) {
    add(result, -1);
  }
  
  /**
   * A value used to describe the difference in times based on this duration
   * @since 2.070
   * @see #diff(GregorianCalendar, GregorianCalendar)
   */
  public abstract long getComparatorCode(GregorianCalendar from);
  
  /**
   * 
   * @since 2.070
   */
  public final long diff(GregorianCalendar from, GregorianCalendar to) {
    long diffCode = getComparatorCode(to) - getComparatorCode(from);
    return diffCode;
  }
}
