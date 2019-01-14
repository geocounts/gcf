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
 * Represents a tally record.
 * Create this using the {@link TallyRecordsDef#create(int, int)}
 * @see TallyRecordsDef
 * @see TalliesInPeriod
 */
public class VehicleTallyRec extends RawTrafficDataTrafficElement implements Comparable<VehicleTallyRec> {
  private final TallyRecordsDef T;
  private final long subtractTimezone;
  
  protected VehicleTallyRec(TallyRecordsDef T, int channelID, int period, long subtractTimezone) {
    this.T = T;
    this.subtractTimezone = subtractTimezone;
    trafficmonitoring = new TrafficMonitoringTallyCountFields();
    userdefined       = new UserDefinedTallyFields(T);

    super.channelID = channelID;
    this.period = period;

    T.TC.resetCounts(this);
    T.TL.resetCounts(this);
    T.TS.resetCounts(this);

    if (T.TO.hasSpeedByLength())
      this.countsSpeedByLength = new int[T.TS.getNumberOfBinsInRecord()][T.TL.getNumberOfBinsInRecord()];
    else
      this.countsSpeedByLength = null;
  }

  /**
   * @see ClassifiedVehTypeDef
   */
  public int[] countsVehType;
  
  /**
   * @see ClassifiedLengthDef
   */
  public int[] countsLength;

  /**
   * @see ClassifiedSpeedDef
   */
  public int[] countsSpeed;
  
  /**
   * First dimension is Speed, Second is length
   * @see ClassifiedSpeedDef
   * @see ClassifiedLengthDef
   */
  public int[][] countsSpeedByLength;
  
  /**
   * @see TrafficMonitoringTallyCountFields
   */
  public final TrafficMonitoringTallyCountFields trafficmonitoring;
  /**
   * This is never null
   */
  public final UserDefinedTallyFields userdefined;
  
  /**
   * If this is zero, it means a full hourly period.
   * Non zero means one of the periods in a partial hour. For example if the tally duration is {@link TallyDurationEnum#m15 quarter hourly}, the time between 15 - 30 is period = 2
   * @see {@link TallyRecordsDef#TD TD header}
   */
  public final int period;
  public boolean isFullHour() {
    return period == 0;
  }
  
  /**
   * 
   * @return The {@link TallyDurationEnum}
   */
  public TallyDurationEnum getTD() {
    return T.TD;
  }
  
  /**
   * 
   * @param year
   * @param month Jan = 0
   * @param day First day = 1
   * @param hour Midnight = 0
   * @throws Exception 
   */
  public void setTime(int year, int month, int day, int hour) throws Exception {
    if (day < 1)
      RawFmtUtils.throwException(this, "Day must be at least 1", day);
    if ((month < 0) || (month > 11))
      RawFmtUtils.throwException(this, "Month must be between 0 and 11", month);
    if ((hour < 0) || (hour > 23))
      RawFmtUtils.throwException(this, "Hour must be between 0 and 23", hour);
    setHourAdjustTZ(RawFmtUtils.newGregorianCalendar(year, month, day, hour).getTimeInMillis());
  }
  
  public void setHourAdjustTZ(long noTZ) {
    super.time = noTZ - subtractTimezone;
  }
  
  /**
   * Safely add to the {@link TrafficMonitoringTallyCountFields#countsVehClass}. If the vclass does not specify a defined type
   * this will <i>not</i> throw ArrayIndexOutOfBoundsException
   * @param vclass Vehicle classified type
   * @param count Number to add
   * @return Whether the data was added
   */
  public boolean addVehType(int vclass, int addCount) {
    try {
      this.countsVehType[vclass] += addCount;
      return true;
    } catch (ArrayIndexOutOfBoundsException possible) {}
    return false;
  }
  
  /**
   * Adds the count of vehicles to the given {@link TrafficMonitoringTallyCountFields#countsSpeed vehicle speed}
   * @return True if the count was added
   */
  public boolean addVehSpeed(int vehSpeedBin, int addCount) {
    try {
      this.countsSpeed[vehSpeedBin] += addCount;
      return true;
    } catch (ArrayIndexOutOfBoundsException possible) {}
    return false;
  }
  
  /**
   * Adds the count of vehicles to the given {@link TrafficMonitoringTallyCountFields#vehLength vehicle length}
   * @return True if the count was added
   */
  public boolean addVehLength(int vehLengthBin, int addCount) {
    try {
      this.countsLength[vehLengthBin] += addCount;
      return true;
    } catch (ArrayIndexOutOfBoundsException possible) {}
    return false;
  }

  @Override
  public int compareTimes(RawTrafficDataTrafficElement o) {
    int diff = super.compareTimes(o);
    if (diff == 0) {
      if (o instanceof VehicleTallyRec) {
        VehicleTallyRec vtr = (VehicleTallyRec)o;
        return this.period - vtr.period;
      }
    }
    return diff;
  }
  
  @Override
  public int compareTo(VehicleTallyRec o) {
    int diff = compareTimes(o);
    if (diff == 0) {
      diff = compareChannels(o);
    }
    return diff;
  }
  
  /**
   * The actual start time of this record, accounting for sub-hourly tallies
   * @return A time
   * @see #getTimeActualEnd()
   */
  public long getTimeActualStart() {
    if (T.TD.periodsPerHour() > 1) // subhourly tallies
      return super.getTime() + (period-1)*T.TD.getMSecs();
    return super.getTime(); // hourly or longer tallies
  }
  
  /**
   * The actual end time of this record, accounting for sub-hourly tallies
   * @return A time
   * @see #getTimeActualStart()
   */
  public long getTimeActualEnd() {
    if (T.TD.periodsPerHour() > 1) // subhourly tallies
      return super.getTime() + period*T.TD.getMSecs();
    return super.getTime() + T.TD.getMSecs();  // hourly or longer tallies
  }
  
  /**
   * Tests if the time is within the {@link #getTimeActualStart()} and {@link #getTimeActualEnd()}
   * @param time The time to test
   * @return true if the time is within this tally
   */
  public boolean contains(long time) {
    if (time < getTimeActualStart())
      return false;
    if (time >= getTimeActualEnd())
      return false;

    return true;
  }

  public void addToTotals(VehicleTallyRec source) {
    for (int i=0; i<countsVehType.length; i++)
      countsVehType[i] += source.countsVehType[i];
    for (int i=0; i<countsLength.length; i++)
      countsLength[i] += source.countsLength[i];
    for (int i=0; i<countsSpeed.length; i++)
      countsSpeed[i] += source.countsSpeed[i];
    
    if ((trafficmonitoring != null) && (source.trafficmonitoring != null)) {
      trafficmonitoring.addToTotals(source.trafficmonitoring);
    }
    if ((userdefined != null) && (source.userdefined != null)) {
      userdefined.addToTotals(source.userdefined);
    }
  }
}
