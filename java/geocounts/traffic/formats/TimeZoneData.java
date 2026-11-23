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
 * Time zone of the device
 * @see RawTrafficDataStation#SZ
 * @see DecodingOptions#timeOffset
 * @see CountryCode#getTZMinutes(String)
 * 
 */
public class TimeZoneData {
  /**
   * This is never null
   */
  public TimeZoneMeaning meaning = TimeZoneMeaning.local;
  
  public boolean isUTC() {
    return meaning == TimeZoneMeaning.utc;
  }
  
  private boolean isSet = false;
  private int Hour;
  private int Min;

  public void reset() {
    meaning = TimeZoneMeaning.local;
    Hour = 0;
    Min = 0;
    isSet = false;
  }
  
  /**
   * 
   * @param hours Must be between -23 and +23
   * @param mins Must be between 0 and 60
   */
  public void set(int hours, int mins) {
    this.isSet = true;
    Hour = hours;
    Min = mins;
    if (Hour < 0)
      Min = -Min;
  }
  
  /**
   * 
   * @param minutes May be more than 60
   */
  public void setFromMinutes(int minutes) {
    this.isSet = true;
    Min = minutes%60;
    Hour = (minutes-Min)/60;
  }
  
  public int getTimeInMinutes() {
    return Hour*60 + Min;
  }
  
  public long getTimeInMillis() {
    return getTimeInMinutes()*60000;
  }
  
  /**
   * Number of milliseconds to add to a time stamp read from the file
   * @return Number of milliseconds
   */
  public long getRecordedTimestampOffset() {
    return meaning.getRecordedTimestampOffset(this);
  }
  
  public void copyFrom(TimeZoneData other) {
    this.isSet = other.isSet;
    this.Hour = other.Hour;
    this.Min = other.Min;
    this.meaning = other.meaning;
  }
  
  public long getDifference(int hours) {
    return hours*3600000 - getTimeInMillis();
  }

  public boolean isSet() {
    return isSet;
  }

  public int getHour() {
    return Hour;
  }
  
  public int getMin() {
    return Min;
  }
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    int absHour = 0;
    if ((getHour() < 0) || (getMin() < 0)) {
      result.append('-');
      absHour = -getHour();
    } else {
      result.append('+');
      absHour = getHour();
    }
    
    result.append(RawFmtUtils.formatHHmm(absHour, Math.abs(getMin())));
    return result.toString();
  }

}
