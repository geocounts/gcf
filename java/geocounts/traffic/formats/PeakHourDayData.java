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

/**
 * @see geocounts.traffic.formats.operators.ComputePeakHourFactor
 */
public class PeakHourDayData implements TimedObject {
  private long date;
  /**
   * Sunday equals 1
   */
  public final int DOW;
  
  public PeakHourDayData(long d, int dowSunEq1) {
    date = d;
    DOW = dowSunEq1;
  }
  
  @Override
  public long getTime() {
    return date;
  }
  
  public final PeakHourData AM = new PeakHourData(this);
  
  public final PeakHourData PM = new PeakHourData(this);
  
  public final PeakHourData ALL = new PeakHourData(this);

  public PeakHourData getPeriod(PeakTimePeriod period) {
    return period.getPeakHourData(this);
  }
  

  @Override
  public String toString() {
    return "D" + DOW + " - AM[" + AM + "] PM[" + PM + "] ALL[" + ALL + "] - " + date;
  }
  
  /**
   * Represents the hour and minute when the peak hour starts
   * @see geocounts.traffic.formats.operators.PeakHourComputer.PeakHourData2
   */
  public static class PeakHourData extends RawFmtUtils.HM implements TimedObject {
    private final PeakHourDayData parent;
    PeakHourData(PeakHourDayData p) {
      parent = p;
    }
    
    public double value;
    
    public double PHF;
    public double getPHF() {
      return PHF;
    }
  
    public void copyFrom(PeakHourData other) {
      hour   = other.hour;
      minute = other.minute;
      value  = other.value;
      PHF    = other.getPHF();
    }

    public PeakHourDayData getDay() {
      return parent;
    }

    @Override
    public long getTime() {
      return parent.getTime() + hour*RawFmtUtils.MSEC_IN_HOUR + minute*60000;
    }

    @Override
    public String toString() {
      return toHHmm() + " = " + getPHF() + " (" + value + ")";
    }
    
  }
  
  /**
   * 
   * @see geocounts.traffic.formats.operators.MetaLabels.PeakPeriod
   */
  public enum PeakTimePeriod {
    AM {
      @Override
      public PeakHourData getPeakHourData(PeakHourDayData result) {
        return result.AM;
      }
      
      @Override
      public boolean test(long time) {
        int hour = RawFmtUtils.getHour(time);
        return hour < 12;
      }
    },
    PM {
      @Override
      public PeakHourData getPeakHourData(PeakHourDayData result) {
        return result.PM;
      }

      @Override
      public boolean test(long time) {
        int hour = RawFmtUtils.getHour(time);
        return hour >= 12;
      }
    },
    ALL {
      @Override
      public PeakHourData getPeakHourData(PeakHourDayData result) {
        return result.ALL;
      }

      @Override
      public boolean test(long time) {
        return true;
      }
    };
    
    public abstract boolean test(long time);
    
//    private String description;
    public String getDescription() {
      return name();
    }
    
    public static PeakTimePeriod getFromHour(int hourOfDay) {
      return hourOfDay < 12 ? AM : PM;
    }
    
    public static PeakTimePeriod getFromTime(long time) {
      int hourOfDay = RawFmtUtils.getHour(time);
      return getFromHour(hourOfDay);
    }
    
    public abstract PeakHourData getPeakHourData(PeakHourDayData result);
  }
}
