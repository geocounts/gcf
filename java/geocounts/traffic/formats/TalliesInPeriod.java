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

import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * A set of {@link VehicleTallyRec tallies} for the same period
 * @see RawTrafficData#toTalliesByPeriod()
 */
public class TalliesInPeriod implements Iterable<VehicleTallyRec>, Comparable<TalliesInPeriod> {
  private final HashMap<Integer, VehicleTallyRec> recsByChannelID = new HashMap<Integer, VehicleTallyRec>();
  private final int hour;
  private final int periodID;
  private final long firstHourTime;
  private final long tallyActualStart;
  protected TalliesInPeriod(VehicleTallyRec first) {
    this(first.getTime(), first.period, first.getTimeActualStart());
    this.recsByChannelID.put(first.channelID, first);
  }
  
  private TalliesInPeriod(long hourTime, int periodID, long tallyPeriodTm) {
    this.hour = RawFmtUtils.getHour(hourTime);
    this.periodID = periodID;
    this.firstHourTime = hourTime;
    this.tallyActualStart = tallyPeriodTm;
  }
  
  public static TalliesInPeriod create(TallyRecordsDef T, RawTrafficDataStation S, long hour, int periodID) throws Exception {
    TalliesInPeriod result = null;
    for (ChannelData channel: S.getAllChannels()) {
      VehicleTallyRec tally = T.create(channel.getChannelID(), periodID, hour);
      if (result == null)
        result = new TalliesInPeriod(tally);
      else
        result.add(tally);
    }
    return result;
  }
  
  public static TalliesInPeriod create(RawTrafficDataStation S, VehicleTallyRec first) {
    TalliesInPeriod result = new TalliesInPeriod(first);
    return result;
  }
  
  public int getHour() {
    return hour;
  }
  
  public int getPeriodID() {
    return periodID;
  }
  
  public long getTime() {
    return firstHourTime;
  }
  
  public long getTallyActualStart() {
    return tallyActualStart;
  }
  
  @Override
  public Iterator<VehicleTallyRec> iterator() {
    return recsByChannelID.values().iterator();
  }
  
  public double sum(TallyCountField field, UnitsUsed units) {
    double result = 0;
    for (VehicleTallyRec tally: recsByChannelID.values()) {
      Number v = field.get(tally, units);
      if (v != null)
        result += v.doubleValue();
    }
    return result;
  }
  
  /**
   * 
   * @param channelID
   * @return {@link VehicleTallyRec}
   */
  public VehicleTallyRec getTallyRec(int channelID) {
    return recsByChannelID.get(channelID);
  }
  
  public boolean hasTallyRec(int channelID) {
    return recsByChannelID.containsKey(channelID);
  }
  
  public boolean add(VehicleTallyRec vtr) {
    if ((vtr.getTime() == getTime()) && (vtr.period == this.periodID)) {
      recsByChannelID.put(vtr.channelID, vtr);
      return true;
    }
    return false;
  }

  public int size() {
    return recsByChannelID.size();
  }

  @Override
  public int compareTo(TalliesInPeriod other) {
    return compareLong(this.getTallyActualStart(), other.getTallyActualStart());
  }
  
  private int compareLong(long x, long y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }
}
