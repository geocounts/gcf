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
 * Represents a tally record.
 * Create this using the {@link TallyRecordsDef#create(int, int)}
 * @see TallyRecordsDef
 * @see TalliesInPeriod
 */
public class VehicleTallyRec extends RawTrafficDataTrafficElement implements Comparable<VehicleTallyRec> {
  private final TallyRecordsDef T;
  
  protected VehicleTallyRec(TallyRecordsDef T, int channelID, int period) {
    this.T = T;
    trafficmonitoring = new TrafficMonitoringTallyCountFields();
    userdefined       = new UserDefinedTallyFields(T);

    super.channelID = channelID;
    this._period = period;

    T.TC.resetCounts(this);
    T.TL.resetCounts(this);
    T.TS.resetCounts(this);

    if (T.TO.hasSpeedByLength())
      this.countsSpeedByLength = new TwoDimensionalArray(T.TS.getNumberOfBinsInRecord(), T.TL.getNumberOfBinsInRecord());
    else
      this.countsSpeedByLength = null;

    if (T.TO.hasSpeedByVehType())
      this.countsSpeedByVehType = new TwoDimensionalArray(T.TS.getNumberOfBinsInRecord(), T.TC.getNumberOfBinsInRecord());
    else
      this.countsSpeedByVehType = null;
  }

  /**
   * @see ClassifiedVehTypeDef
   */
  public NumberArray countsVehType;
  public NumberArray initCountsVehType(int count) {
    countsVehType = new NumberArray(count);
    return countsVehType;
  }
  
  public NumberArray initCountsVehType(double[] newValues) {
    countsVehType = new NumberArray(newValues.length);
    for (int i=0; i<newValues.length; i++)
      countsVehType.values[i] = newValues[i];
    return countsVehType;
  }
  
  public static class NumberArray {
    private final double[] values;
    public final int length;
    
    public NumberArray(int count) {
      values = new double[count];
      length = count;
    }

    public int[] asInt() {
      int[] result = new int[values.length];
      for (int i=0; i<values.length; i++)
        result[i] = (int)Math.round(values[i]);
      return result;
    }
    
    public double getDouble(int index) {
      return values[index];
    }

    public int getInt(int index) {
      return (int)Math.round(values[index]);
    }
    
    public double[] asDouble() {
      return values;
    }

    public void set(int index, double value) {
      values[index] = value;
    }

    public void add(int index, double amount) {
      values[index] += amount;
    }

    public void add(NumberArray other) {
      for (int i=0; i<values.length; i++)
        this.add(i, other.values[i]);
    }

    public void inc(int index) {
      values[index] += 1;
    }
  }
  
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
  public TwoDimensionalArray countsSpeedByLength;
  
  /**
   * First dimension is Speed, Second is vehicle type
   * @see ClassifiedSpeedDef
   * @see ClassifiedVehTypeDef
   * @since 1.320
   */
  public TwoDimensionalArray countsSpeedByVehType;
  
  /**
   * @see TrafficMonitoringTallyCountFields
   */
  public final TrafficMonitoringTallyCountFields trafficmonitoring;
  
  /**
   * This is never null
   */
  public final UserDefinedTallyFields userdefined;
  
  private int _period;
  
  /**
   * If this is zero, it means a full hourly period.
   * Non zero means one of the periods in a partial hour. For example if the tally duration is {@link TallyDurationEnum#m15 quarter hourly}, the time between 15 - 30 is period = 2
   * @see {@link TallyRecordsDef#TD TD header}
   */
  public int getHourlySubPeriod() {
    return _period;
  }
  
  /**
   * @return true if this tally represents a full hour.
   */
  public boolean isFullHour() {
    return _period == 0;
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
   * @return The {@link ClassifiedVehTypeDef}
   */
  public ClassifiedVehTypeDef getTC() {
    return T.TC;
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
    
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(year, month, day, hour);
    time = gc.getTimeInMillis();
  }
  
  public void addTDTime(int numberOfTDIncrements) {
    GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(this.getTimeActualStart());
    T.TD.add(gc, numberOfTDIncrements);
    
    if (T.TD.isSubHour()) {
//      long newActualTime = gc.getTimeInMillis();
      int numberOfMins = gc.get(GregorianCalendar.MINUTE);
      RawFmtUtils.resetHour(gc);
      
      this._period = T.TD.getHourlySubPeriod(numberOfMins);
    }

    super.time = gc.getTimeInMillis();
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
      this.countsVehType.add(vclass, addCount);
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
  public int compareTimes(TimedObject o) {
    int diff = super.compareTimes(o);
    if (diff == 0) {
      if (o instanceof VehicleTallyRec) {
        VehicleTallyRec vtr = (VehicleTallyRec)o;
        return this.getHourlySubPeriod() - vtr.getHourlySubPeriod();
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
      return super.getTime() + (_period-1)*T.TD.getMSecs();
    return super.getTime(); // hourly or longer tallies
  }
  
  /**
   * The actual end time of this record, accounting for sub-hourly tallies
   * @return A time
   * @see #getTimeActualStart()
   */
  public long getTimeActualEnd() {
    if (T.TD.periodsPerHour() > 1) // subhourly tallies
      return super.getTime() + _period*T.TD.getMSecs();
    if (T.TD.minutes == 0) { // year and month
      GregorianCalendar gc = RawFmtUtils.newGregorianCalendar(super.getTime());
      T.TD.inc(gc);
      return gc.getTimeInMillis();
    }
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
    countsVehType.add(source.countsVehType);
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
  
  @Override
  public String toString() {
    return RawFmtUtils.ISO8601_MSECS_format(getTimeActualStart()) + " " + T.TD + " " + channelID;
  }
  
  public static class OneDimensionalArray {
    final int[] data;
    int above;
    private OneDimensionalArray(int dim) {
      data = new int[dim];
    }
    
    public void inc(int dim) {
      if (dim < data.length)
        data[dim]++;
      else
        above++;
    }
    
    public void set(int dim, int value) {
      if (dim < data.length)
        data[dim] = value;
      else
        above += value;
    }
  }
  
  public static class TwoDimensionalArray {
    public TwoDimensionalArray(int dim1, int dim2) {
      data = new OneDimensionalArray[dim1];
      for (int i=0; i<data.length; i++)
        data[i] = new OneDimensionalArray(dim2);
      aboveDim1 = new int[dim2];
    }

    private final OneDimensionalArray[] data;
    
    /**
     * Stores the number that exceeded the bounds of dimension 1, for each dimension 2
     * This has the same number of elements as dimension 2
     */
    private final int[] aboveDim1;
    
    public int getAboveDim1(int dim2) {
      return aboveDim1[dim2];
    }
    
    public int getAboveDim2(int dim1) {
      return data[dim1].above;
    }

    public int get(int dim1, int dim2) {
      return data[dim1].data[dim2];
    }
    
    public int getDim1Size() {
      return data.length;
    }
    
    public int getDim2Size() {
      return data[0].data.length;
    }

    public void inc(int dim1, int dim2) {
      if (dim1 < data.length) {
        data[dim1].inc(dim2);
      } else
        aboveDim1[dim2]++;
    }

    public void set(int dim1, int dim2, int value) {
      if (dim1 < data.length) {
        data[dim1].set(dim2, value);
      } else
        aboveDim1[dim2] += value;
    }
    
    public int[] getData(int dim1) {
      return data[dim1].data;
    }
  }
}
