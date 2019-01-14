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
 * The periods (and therefore durations) available to {@link VehicleTallyRec tally records}
 * @see EnumHeaderRecords#TD
 */
public enum TallyDurationEnum implements IdDesc {
  off(0, 0, 0) {
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
  },
  /**
   * 1 minute
   */
  m1(1, GregorianCalendar.MINUTE, 1) {
    @Override
    public int getHourlySubPeriod(int numberOfMins) {
      return numberOfMins/super.minutes + 1;
    }

    @Override
    public void trim(GregorianCalendar result) {
      result.set(GregorianCalendar.SECOND, 0);
      result.set(GregorianCalendar.MILLISECOND, 0);
    }
  }, 
  /**
   * 5 minutes
   */
  m5(5, GregorianCalendar.MINUTE, 5) {
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
  }, 
  /**
   * 15 minutes
   */
  m15(15, GregorianCalendar.MINUTE, 15) {
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
  },
  /**
   * 1 hour (60 minutes)
   */
  hr(60, GregorianCalendar.HOUR_OF_DAY, 1) {

    @Override
    public void trim(GregorianCalendar result) {
      RawFmtUtils.trimHour(result);
    }
    
    @Override
    public String getDescription() {
      return "1 hour";
    }
  }, 
  /**
   * 1 day (24 hours, 1440 minutes)
   */
  day(1440, GregorianCalendar.DAY_OF_MONTH, 1) {
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
  },
  month(0, GregorianCalendar.MONTH, 1) {
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
  },
  year(0, GregorianCalendar.YEAR, 1) {
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
  };
  
  public final int minutes;
  public final int GC_ID;
  public final int GC_COUNT;
  private TallyDurationEnum(int m, int gcType, int gcTypeCount) {
    minutes = m;
    GC_ID = gcType;
    GC_COUNT = gcTypeCount;
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
  
  public final GregorianCalendar trim(long millis) {
    GregorianCalendar result = RawFmtUtils.newGregorianCalendar(millis);
    this.trim(result);
    return result;
  }
}
