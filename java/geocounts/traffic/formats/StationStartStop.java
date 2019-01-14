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

import java.text.SimpleDateFormat;

import java.util.Map;

/**
 * 
 * Station start and stop monitoring times
 * @see EnumHeaderRecords#SR
 */
public class StationStartStop {
  private final long[] SR = new long[2];
  protected StationStartStop() {
  }

  public void update(long time) {
    if ((time < this.SR[0]) || (this.SR[0] == 0)) {
      this.SR[0] = time;
    }
    if ((time > this.SR[1]) || (this.SR[1] == 0))  {
      this.SR[1] = time;
    }
  }
  
  public void set(long start, long stop) {
    this.SR[0] = start;
    this.SR[1] = stop;
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
    return (hasStartTime() && hasEndTime());
  }
  
  public final void copyFrom(StationStartStop other) {
    for (int idx = 0; idx < other.SR.length; idx++) {
      this.SR[idx] = other.SR[idx];
    }
  }
  
  public void toMap(Map<String, Object> result) {
    SimpleDateFormat sdf = RawFmtUtils.ISO8601();
    if (hasStartTime())
      result.put("SR_start", sdf.format(getStartTime()));
    if (hasEndTime())
      result.put("SR_end", sdf.format(getEndTime()));
    if (hasStartTime() && hasEndTime()) {
      result.put("SR_durationmsecs", getTimeSpan());
      result.put("SR_durationhrs", getTimeSpan()/3600000);
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
  
  public void setStartTime(long value) {
    this.SR[0] = value;
  }

  public long getStartTime() {
    return this.SR[0];
  }

  public boolean hasStartTime() {
    return this.SR[0] != 0;
  }
  
  public void setEndTime(long value) {
    this.SR[1] = value;
  }
  
  public long getEndTime() {
    return this.SR[1];
  }

  public boolean hasEndTime() {
    return this.SR[1] != 0;
  }

  public long getTimeSpan() {
    if (isCompletelySet())
      return getEndTime() - getStartTime();
    return 0;
  }
  
  public int compareTo(long time) {
    if (time < this.SR[0])
      return 1;
    if (time >= this.SR[1])
      return -1;
    return 0;
  }
}
