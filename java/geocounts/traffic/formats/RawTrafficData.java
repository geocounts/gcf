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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Represents the data in a GEOCOUNTS file.
 * All data is stored as metric, but can be encoded and decoded as US Customary.
 *
 */
public class RawTrafficData {
  /**
   * The header record. This is never null
   */
  public final RawTrafficDataHeader header;
  
  private final HeaderChannelFilter headerChannelFilter;
  
  /**
   * {@link VehicleTallyRec Tally} records
   */
  private final List<VehicleTallyRec> tallies = new ArrayList<VehicleTallyRec>();
  
  /**
   * Vehicle records
   */
  private final List<VehicleRec> observations = new ArrayList<VehicleRec>();
  
  /**
   * {@link MonitoringEventRec Monitoring events}
   */
  private final List<MonitoringEventRec> monitoringevents = new ArrayList<MonitoringEventRec>();
  
  public RawTrafficData() {
    this(new RawTrafficDataHeader());
  }
  
  public RawTrafficData(RawTrafficDataHeader header) {
    this.header = header;
    this.headerChannelFilter = new HeaderChannelFilter(this.header);
  }
  
  public void clearTallies() {
    tallies.clear();
  }
  
  public void removeTallies(Collection<VehicleTallyRec> t) {
    tallies.removeAll(t);
  }
  
  /**
   * Get all {@link VehicleTallyRec}
   * @return Iterable of {@link VehicleTallyRec}
   */
  public Iterable<VehicleTallyRec> tallies() {
    return tallies;
  }
  
  /**
   * Get tallies for the given channel ID
   * @param channelID
   * @return Iterable of {@link VehicleTallyRec}
   */
  public Iterable<VehicleTallyRec> tallies(int channelID) {
    ArrayList<VehicleTallyRec> result = new ArrayList<VehicleTallyRec>();
    for (VehicleTallyRec vt: tallies)
      if (vt.channelID == channelID)
        result.add(vt);
    return result;
  }
  
  public VehicleTallyRec getTally(int index) {
    return tallies.get(index);
  }

  /**
   * 
   * @return The first tally, or null
   */
  public VehicleTallyRec getFirstTally() {
    if (tallies.size() > 0)
      return tallies.get(0);
    return null;
  }
  
  /**
   * 
   * @return The last tally, or null
   */
  public VehicleTallyRec getLastTally() {
    if (tallies.size() > 0)
      return tallies.get(tallies.size()-1);
    return null;
  }
  
  /**
   * Set the {@link EnumHeaderRecords#SR SR}.
   * This assumes the data is already {@link #sortTallies() sorted by time}
   * @param addTDToLast If true, add the value of TD to the end time
   */
  public void setStationStartStopFromTallies(boolean addTDToLast) {
    if (this.numberOfTallies() == 0)
      return;

    header.S.SR.set(getFirstTally().getTime(), 
                    getLastTally().getTime() + (addTDToLast ? header.T.TD.getMSecs() : 0));
  }
  
  public void setStationStartStopFromObservations() {
    if (observations.size() == 0)
      return;
    
    ObservationRec[] firstAndLast = this.getFirstAndLastObservation();
    this.header.S.SR.set(firstAndLast[0].getTime(), firstAndLast[1].getTime());
  }
  
  public int numberOfTallies() {
    return tallies.size();
  }
  
  /**
   * Compute tallies into periods. Each period object contains all the tallies in that period.
   * @return List of {@link TalliesInPeriod}, sorted in time order
   */
  public List<TalliesInPeriod> toTalliesByPeriod() {
    HashMap<Long, TalliesInPeriod> periods = new HashMap<Long, TalliesInPeriod>();
    TalliesInPeriod tfh = null;

    for (VehicleTallyRec tally: tallies) {
      tfh = periods.get(tally.getTimeActualStart());
      if (tfh == null) {
        tfh = new TalliesInPeriod(tally);
        periods.put(tfh.getTallyActualStart(), tfh);
      } else {
        if (!tfh.add(tally)) {
          tfh = new TalliesInPeriod(tally);
          periods.put(tfh.getTallyActualStart(), tfh);
        }
      }
    }
    
    ArrayList<TalliesInPeriod> result = new ArrayList<TalliesInPeriod>();
    result.addAll(periods.values());
    Collections.sort(result);
    return result;
  }
  
  public void changeChannel(int fromChannel, int toChannel) throws Exception {
    if (fromChannel == toChannel)
      return;
    if (toChannel < 0)
      return;
    
    // change header
    header.changeChannel(fromChannel, toChannel);
    
    // change tallies
    ChangeTallyChannels tly = new ChangeTallyChannels(this);
    tly.changeChannel(fromChannel, toChannel);
    
    // change vehicles
    ChangeVehicleChannels veh = new ChangeVehicleChannels(this);
    veh.changeChannel(fromChannel, toChannel);
  }
  
  /**
   * Adds a tally if the tally's computed channel ID is in the header
   * @param rec
   * @return Whether the tally was added
   */
  public boolean addTally(VehicleTallyRec rec) {
    if (headerChannelFilter.include(rec.channelID)) {
      tallies.add(rec);
      return true;
    }
    return false;
  }

  public ObservationRec[] getFirstAndLastObservation() {
    VehicleRec[] result = new VehicleRec[2];
    if (observations.size() == 0)
      return result;
    result[0] = observations.get(0);
    result[1] = observations.get(observations.size()-1);
    return result;
  }
  
  public void clearObservations() {
    observations.clear();
  }
  
  public void addAll(List<VehicleRec> obs) {
    observations.addAll(obs);
  }
  
  public void removeAll(Collection<VehicleRec> obs) {
    observations.removeAll(obs);
  }
  
  /**
   * Sort observations by time
   */
  public void sortObservations() {
    Collections.sort(this.observations);
  }
  
  /**
   * Sort {@link VehicleTallyRec tallies} by time
   */
  public void sortTallies() {
    Collections.sort(this.tallies);
  }
  
  /**
   * Sort monitoring events by time
   */
  public void sortMonitoringEvents() {
    Collections.sort(this.monitoringevents);
  }
  
  /**
   * 
   * @param nameSpace If null, return all the observations
   * @return All the counted events associated with the nameSpace
   */
  public Iterable<VehicleRec> observations() {
    return observations;
  }
  
  public int numberOfObservations() {
    return observations.size();
  }
  
  /**
   * 
   * @return An array having a length the same as the number of channels
   */
  public int[] numberOfObservationsByChannel() {
    List<ChannelData> channels = header.S.getAllChannels();
    int[] result = new int[channels.size()];
    int[] index = new int[100];

    for (int i=0; i<channels.size(); i++) {
      int chID = channels.get(i).getChannelID();
      index[chID] = i;
    }
    
    for (VehicleRec veh: this.observations) {
      result[index[veh.channelID]]++;
    }
    return result;
  }
  
  /**
   * Adds a vehicle event record if the vehicle's computed channel ID is in the header.
   * If a header {@link ChannelContraFlowDef contra-flow record} determines the vehicle should have its channel reassigned,
   * this will:<ul>
   * <li>change the vehicle's channel ID</li>
   * <li>reverse the vehicle's speed</li>
   * </ul:
   * before adding to the list.
   * Only vehicles having channels registered in the header will be added. Others will not.
   * @param obs Observed vehicle record
   * @return True if the vehicle was added. False if not.
   */
  public boolean addObservation(VehicleRec obs) {
    int computedChannel = header.S.computeChannel(obs.channelID, obs.speedKph >= 0);
    if (headerChannelFilter.include(computedChannel)) {
      if (computedChannel != obs.channelID) {
        obs.channelID = computedChannel;
        obs.speedKph = -obs.speedKph;
      }
      observations.add(obs);
      return true;
    }
    return false;
  }

  public boolean addMonitoringEvent(MonitoringEventRec e) {
    return monitoringevents.add(e);
  }
  
  public Iterable<MonitoringEventRec> monitoringevents() {
    return monitoringevents;
  }
  
  public int numberOfMonitoringEvents() {
    return monitoringevents.size();
  }
  
  public void clearMonitoringEvents() {
    monitoringevents.clear();
  }
  
  public List<MonitoringEventRec> getMonitoringEventsByType(MonitoringEventType eventType) {
    return getMonitoringEventsByType(eventType.name());
  }
  
  public List<MonitoringEventRec> getMonitoringEventsByType(final String eventTypeID) {
    ArrayList<MonitoringEventRec> result = new ArrayList<MonitoringEventRec>();
    for (MonitoringEventRec e: monitoringevents) {
      if (e.getEventTypeID().equals(eventTypeID))
        result.add(e);
    }
    return result;
  }
  
  /**
   * Clear monitoring events, observations and tallies 
   */
  public void clearAllEvents() {
    this.clearMonitoringEvents();
    this.clearObservations();
    this.clearTallies();
  }
  
  /**
   * 
   * @return The total number of motorized vehicles
   * @see TrafficMonitoringTallyFieldNamespace#motorized
   * @see RawTrafficData#numberOfVehicles()
   */
  public int getTalliedMotorizedVehs() {
    return getTalliedValue(TrafficMonitoringTallyFieldNamespace.motorized, null).intValue();
  }
  
  /**
   * 
   * @return A map keyed by channel ID, with each value being the number of {@link TrafficMonitoringTallyFieldNamespace.motorized motorized}
   * @see RawTrafficData#numberOfVehiclesByChannel()
   */
  public java.util.Map<Integer, Integer> getTalliedMotorizedVehsByChannel() {
    List<ChannelData> channels = header.S.getAllChannels();
    java.util.HashMap<Integer, int[]> counts = new java.util.HashMap<Integer, int[]>();
    for (ChannelData cd: channels) {
      counts.put(cd.getChannelID(), new int[1]);
    }

    for (VehicleTallyRec tally: tallies) {
      int[] count = counts.get(tally.channelID);
      Number n = TrafficMonitoringTallyFieldNamespace.motorized.get(tally, null);
      if (n != null)
        count[0] += n.intValue();
    }

    java.util.HashMap<Integer, Integer> result = new java.util.HashMap<Integer, Integer>();
    for (java.util.Map.Entry<Integer, int[]> e: counts.entrySet())
      result.put(e.getKey(), e.getValue()[0]);
    return result;
  }
  
  /**
   * 
   * @param field The {@link TallyCountField field to sum}
   * @param units The {@link UnitsUsed units}
   * @return The sum of the {@link TallyCountFieldsEnum field} for the all channels
   */
  public Number getTalliedValue(TallyCountField field, UnitsUsed units) {
    return getTalliedValue(field, units, new AllChannels());
  }
  
  /*
   * Uses {@link #getTalliedValue(TallyCountField, UnitsUsed, ChannelIDFilter)
   * @param field The {@link TallyCountField field to sum}
   * @param units The {@link UnitsUsed units}
   * @return The sum of the {@link TallyCountField field}
   *
  public Number[] getTalliedValuesByChannel(TallyCountField field, UnitsUsed units) {
    List<ChannelData> channels = header.S.getAllChannels();
    Number[] result = new Number[channels.size()];
    for (int i=0; i<channels.size(); i++)
      result[i] = getTalliedValue(field, units, new OneChannel(channels.get(i).getChannelID()));
    return result;
  } */
  
  /**
   * Sums a value in all tallies (both hourly and sub-hourly)
   * @param field The {@link TallyCountField field to sum}
   * @param units The {@link UnitsUsed units}
   * @param chfilter
   * @return The sum of the {@link TallyCountFieldsEnum field} for the given filtered channels
   */
  public Number getTalliedValue(TallyCountField field, UnitsUsed units, ChannelIDFilter chfilter) {
    double result = 0;
    for (VehicleTallyRec tally: tallies) {
      if (chfilter.include(tally.channelID))
        result += field.get(tally, units).doubleValue();
    }
    
    return result;
  }

  /**
   * Creates a new RawTrafficData using a copy of the header
   * @return A new RawTrafficData with header matching this object, and no other data
   * @throws Exception
   */
  public RawTrafficData copyFromHeader() throws Exception {
    return new RawTrafficData(this.header.copy());
  }
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("SC: " + header.S.getNumberOfChannels());
    if (header.T.isRecording()) {
      result.append(" TD: " + header.T.TD);
    }
    if (header.VV.isRecording()) {
      result.append(" VV");
    }
    return result.toString();
  }
  
  /**
   * Used to exclude channels that are not in the header
   * @author scropley
   *
   */
  private class HeaderChannelFilter implements ChannelIDFilter {
    private final RawTrafficDataHeader h;
    private HeaderChannelFilter(RawTrafficDataHeader _h) {
      h = _h;
    }
    
    @Override
    public boolean include(int channelID) {
      return h.S.hasChannel(channelID);
    }
    
  }
  
  /**
   * Used to include all channels
   * @author scropley
   *
   */
  private class AllChannels implements ChannelIDFilter {
    @Override
    public boolean include(int channelID) {
      return true;
    }
  }
  /*
  private class OneChannel implements ChannelIDFilter {
    private final int channelID;
    private OneChannel(int ch) {
      this.channelID = ch;
    }
    
    @Override
    public boolean include(int ch) {
      return this.channelID == ch;
    }
  }
  */
}

class ChangeTallyChannels implements ChannelIDChange {
  private final Iterable<VehicleTallyRec> tallies;
  
  ChangeTallyChannels(RawTrafficData data) {
    tallies = data.tallies();
  }
  
  @Override
  public void changeChannel(int fromChannel, int toChannel) throws Exception {
    for (VehicleTallyRec t: tallies)
      if (t.channelID == fromChannel)
        t.channelID = toChannel;
  }
  
}

class ChangeVehicleChannels implements ChannelIDChange {
  private final Iterable<VehicleRec> observations;
  
  ChangeVehicleChannels(RawTrafficData data) {
    observations = data.observations();
  }
  
  @Override
  public void changeChannel(int fromChannel, int toChannel) throws Exception {
    for (VehicleRec v: observations)
      if (v.channelID == fromChannel)
        v.channelID = toChannel;
  }
  
}
