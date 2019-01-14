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
 */
public class TimeZoneData {
  /**
   * This is never null
   */
  public TimeZoneMeaning meaning = TimeZoneMeaning.local;
  
  private boolean isSet = false;
  private int Hour;
  private int Min;
  
  public void set(int hours, int mins) {
    this.isSet = true;
    Hour = hours;
    Min = mins;
  }
  
  public long getTimeInMillis() {
    return Hour*3600000 + Min*60000;
  }
  
  /**
   * Number of milliseconds to subtract from a time stamp read from the file
   * @return Number of milliseconds
   */
  public long getRecordedTimestampOffset() {
    return meaning == TimeZoneMeaning.utc ? getTimeInMillis() : 0;
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
    if (getHour() < 0) {
      result.append('-');
      absHour = -getHour();
    } else {
      result.append('+');
      absHour = getHour();
    }
    if (absHour < 10)
      result.append('0');
    result.append(absHour);
    result.append(':');
    if (getMin() < 10)
      result.append('0');
    result.append(getMin());
    
    return result.toString();
  }

}
