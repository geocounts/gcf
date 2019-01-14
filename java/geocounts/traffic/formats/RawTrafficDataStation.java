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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class RawTrafficDataStation {
  private RawTrafficDataHeader parent;
  
  public RawTrafficDataStation() {
    this.SR = new StationStartStop();
  }
  
  public RawTrafficDataStation(RawTrafficDataHeader header) {
    this();
    this.parent = header;
    header.addChannelIDChange(new _ChangeChannel(this));
//    this.sr = new StationStartStop(this._SR);
  }
  
  /**
   * Station identifier
   * @see EnumHeaderRecords#SI
   * @see StationID
   */
  public final StationID SI = new StationID();
  
  /**
   * Survey: contract identifier
   * @see EnumHeaderRecords#SA
   */
  public String SA;
  
  /**
   * Station: Start and Stop monitoring service time
   * @see EnumHeaderRecords#SR
   */
  public final StationStartStop SR;
  
  /**
   * Station: monitoring device serial numbers
   * @see EnumHeaderRecords#SN
   */
  public final StationSerialNumbers SN = new StationSerialNumbers();

  /**
   * Station: Field technician that set out the device
   * @see EnumHeaderRecords#ST
   */
  public String ST;

  /**
   * Station: Field technician that retrieved the counting device
   * @see EnumHeaderRecords#SS
   */
  public String SS;
  
  /**
   * Station: Field technician that analyzed the data
   * @see EnumHeaderRecords#SU
   */
  public String SU;

  /**
   * Station: Labels
   * @see StationLabels
   * @see EnumHeaderRecords#SL
   */
  public final StationLabels SL = new StationLabels(this);
  
  /**
   * Station: Equipment
   * @see Equipment
   * @see EnumHeaderRecords#SE
   */
  public final Equipment SE = new Equipment(this);

  /**
   * Station: GPS coordinate
   * @see StationGPS
   * @see #getCentroidOfChannels()
   */
  public final StationGPS SG = new StationGPS();

  /**
   * Station: Shape
   * @see StationShape
   * @since 1.27
   */
  public final StationShape SP = new StationShape();
  
  
  /**
   * Station: time zone of the data collection point at the time the file was created.
   * {@link RawTrafficDataTrafficElement#getTime() Observed times} are the local time where the device made the observation.
   * 
   * @see MonitoringEventType#dsc Daylight savings time change
   * @see EnumHeaderRecords#SZ
   */
  public final TimeZoneData SZ = new TimeZoneData();

  private int channels = 0;
  private int minChannelID = ChannelData.MAX_CHANNELID;
  private int maxChannelID = -1;
//  private int[] directionsWithToChannels = new int[ChannelDirEnum.values().length];
  
  protected void recomputeSummaries() {
//    directionsWithToChannels = new int[ChannelDirEnum.values().length];
    minChannelID = ChannelData.MAX_CHANNELID;
    maxChannelID = -1;
    for (ChannelData ch: getAllChannels()) {
      if (ch.getChannelID() < minChannelID)
        minChannelID = ch.getChannelID();
      if (ch.getChannelID() > maxChannelID)
        maxChannelID = ch.getChannelID();
    }
  }

  /**
   * Station Channel data
   * @see ChannelData
   * @see EnumHeaderRecords#SC
   */
  private final ChannelData[] SC = new ChannelData[1+ChannelData.MAX_CHANNELID];

  /*
   * Station contra-flow definition. Each element represents a 'from' channel
   */
//  private final ChannelContraFlowDef[] SF = new ChannelContraFlowDef[1+ChannelData.MAX_CHANNELID];
  

  protected void setChannelData(ChannelData cd) {
    if (SC[cd.getChannelID()] == null)
      channels++;
    SC[cd.getChannelID()] = cd;
    if (cd.getChannelID() > maxChannelID)
      maxChannelID = cd.getChannelID();
    if (cd.getChannelID() < minChannelID)
      minChannelID = cd.getChannelID();
  }
  
  /**
   * The centroid of the {@link ChannelData channels}
   * @return {@link LatLongCoordinate}
   * @since 1.9963
   * @see #SG
   */
  public LatLongCoordinate getCentroidOfChannels() {
    List<LatLongCoordinate> points = new ArrayList<LatLongCoordinate>();
    for (ChannelData cd: this.getAllChannels()) {
      if (cd.coordinate.isSet())
        points.add(cd.coordinate);
    }
    return StationShape.getCentroid(points);
  }

  /**
   * 
   * @param channelID
   * @return {@link ChannelData}
   */
  public ChannelData addChannel(int channelID) {
    ChannelData result = new ChannelData(this, channelID);
    setChannelData(result);
    return result;
  }

  /**
   * 
   * @param channelID
   * @param _dirToCode Direction code
   * @param _laneToCode
   * @return {@link ChannelData}
   */
  public ChannelData addChannel(int channelID, int _dirToCode, int _laneToCode) {
    ChannelData cd = addChannel(channelID);
    cd.setTo(_dirToCode, _laneToCode, false);
    return cd;
  }

  public void removeChannel(int channelID) {
    if (SC[channelID] == null)
      return;
    
    channels--;
    SC[channelID] = null;
//    SF[channelID] = null;
    SE.removeChannel(channelID);
    SL.removeChannel(channelID);
    
    recomputeSummaries();
  }
  
  private void clearChannels() {
    for (int ch=0; ch<SC.length; ch++) {
      SC[ch] = null;
    }
    minChannelID = ChannelData.MAX_CHANNELID;
    maxChannelID = -1;
    channels = 0;
    SL.clearChannels();
    SE.clearChannels();
  }

  /**
   * Copies another station records data to this object
   * @param other The source of the data to copy from
   * @throws Exception
   */
  public final void copyFrom(RawTrafficDataStation other) throws Exception {
    // clear channels
    clearChannels();
    // add channels
    for (ChannelData cd: other.getAllChannels()) { 
      ChannelData addedCD = this.addChannel(cd.getChannelID());
      addedCD.copyFrom(cd);
    }
    
    copyFromExcludeChannels(other);
    
    /*
    for (ChannelContraFlowDef cfd: other.getAllContraFlowDefs()) {
      ChannelContraFlowDef addedCFD = this.addChannelContraFlow(cfd.from_channel_id, cfd.to_channel_id);
      addedCFD.copyFrom(cfd);
    }
    */
  }
  
  public void copyFromExcludeChannels(RawTrafficDataStation other) throws Exception {
    this.SA = other.SA;
    this.SS = other.SS;
    this.ST = other.ST;
    this.SU = other.SU;
    this.SI.copyFrom(other.SI);
    this.SG.copyFrom(other.SG);
    this.SE.copyFrom(other.SE);
    this.SL.copyFrom(other.SL);
    this.SN.copyFrom(other.SN);
    this.SP.copyFrom(other.SP);
    this.SR.copyFrom(other.SR);
    this.SZ.copyFrom(other.SZ);
  }

  /**
   * 
   * @since 1.9951
   */
  public void mergeFrom(RawTrafficDataStation other) {
    if (this.SA == null)
      this.SA = other.SA;
    if (this.SS == null)
      this.SS = other.SS;
    if (this.ST == null)
      this.ST = other.ST;
    if (this.SU == null)
      this.SU = other.SU;
//    this.SG.copyFrom(other.SG);
    this.SE.mergeFrom(other.SE);
    this.SL.mergeFrom(other.SL);
//    this.SN.copyFrom(other.SN);
//    this.SP.copyFrom(other.SP);
//    this.SR.copyFrom(other.SR);
//    this.SZ.copyFrom(other.SZ);
  }
  
  /**
   * 
   * @since 1.9944
   */
  public void toMap(Map<String, Object> result) {
    if (SA != null) result.put("SA", SA);
    if (SS != null) result.put("SS", SS);
    if (ST != null) result.put("ST", ST);
    if (SU != null) result.put("SU", SU);
    SE.toMap(result);
    SG.toMap(result);
    SL.toMap(result);
//    SN.toMap(result); FIXME
//    SP.toMap(result); FIXME
    SR.toMap(result);
//    SZ.toMap(result); FIXME
  }

  @Override
  public String toString() {
    int directions = this.getNumberOfToDirections();
    int channels = this.getNumberOfChannels();
    StringBuilder result = new StringBuilder();
    result.append(channels);
    result.append(" channels ");
    result.append(directions);
    result.append(" directions ");
    result.append(SL.toString());
    return result.toString();
  }
  
  /**
   * Adds channel and contra flow definitions
   * @param other
   */
  public void setChannelsFrom(RawTrafficDataStation other) {
    for (ChannelData sc: other.getAllChannels()) {
      setChannelData(sc);
    }

//    for (ChannelContraFlowDef sf: other.getAllContraFlowDefs()) {
//      addChannelContraFlow(sf);
//    }
  }
  
  private void assertChannelIDBounds(int id) {
    if ((id < 0) || (id > ChannelData.MAX_CHANNELID))
      throw new ArrayIndexOutOfBoundsException(id);
  }
  
  protected void addChannelIDChange(ChannelIDChange value) {
    if (parent != null)
      parent.addChannelIDChange(value);
  }

  /**
   * 
   * @param channelID
   * @return {@link ChannelData} or null if the channel does not exist
   */
  public ChannelData getChannel(int channelID) {
    try {
      return SC[channelID];
    } catch (ArrayIndexOutOfBoundsException aioobe) {
      return null;
    }
  }
  
  /**
   * 
   * @param fromDir
   * @param to1Dir
   * @param toLane
   * @return The first channel found with the given directions and 'to' lane
   */
  public ChannelData getChannel(ChannelDirEnum fromDir, ChannelDirEnum to1Dir, ChannelLaneData toLane) {
    int toLaneCode = toLane != null ? toLane.getCode() : 0;
    for (int ch=minChannelID; ch<=maxChannelID; ch++)
      if (SC[ch] != null)
        if (SC[ch].from.getDir() == fromDir)
          if (SC[ch].to1.getDir() == to1Dir)
            if (SC[ch].to1.lane.getCode() == toLaneCode)
              return SC[ch];
    return null;
  }

  public boolean hasChannel(int channelID) {
    return SC[channelID] != null;
  }

  public List<ChannelData> getAllChannels() {
    ArrayList<ChannelData> result = new ArrayList<ChannelData>();
    for (int i=0; i<SC.length; i++)
      if (SC[i] != null)
        result.add(SC[i]);
    return result;
  }

  public int getNumberOfChannels() {
    return channels;
  }

  public int[] getMinMaxChannelID() {
    return new int[]{minChannelID, maxChannelID};
  }
  
  /**
   * 
   * @return The number of "to" directions
   * @see #getToDirections()
   */
  public int getNumberOfToDirections() {
    return getToDirections().size();
  }
  
  /**
   * 
   * @return The set of {@link ChannelData#to1} directions
   * @see #getNumberOfToDirections()
   */
  public Set<ChannelDirEnum> getToDirections() {
    HashSet<ChannelDirEnum> result = new HashSet<ChannelDirEnum>();
    for (ChannelData ch: getAllChannels())
      result.add(ch.to1.getDir());
    return result;
  }
  
/*
  public List<ChannelContraFlowDef> getAllContraFlowDefs() {
    ArrayList<ChannelContraFlowDef> result = new ArrayList<ChannelContraFlowDef>();
    for (int i=0; i<SF.length; i++)
      if (SF[i] != null)
        result.add(SF[i]);
    return result;
  }

  /*
   * Adds a {@link ChannelContraFlowDef contra-flow definition} record
   * @param sf
   *
  public void addChannelContraFlow(ChannelContraFlowDef sf) {
    SF[sf.from_channel_id] = sf;
  }

  public ChannelContraFlowDef addChannelContraFlow(int fromChannelID, int toChannelID) {
    ChannelContraFlowDef sf = new ChannelContraFlowDef();
    sf.from_channel_id = fromChannelID;
    sf.to_channel_id = toChannelID;
    addChannelContraFlow(sf);
    return sf;
  }
*/
  
  /**
   * If direction is negative (contra-flow) and there is a {@link ChannelContraFlowDef contra-flow} record
   * assigned to the chID, this will use the contra-flow's instructions to compute the new channel ID
   * @param chID
   * @param direction
   * @return The computed channel id
   */
  public int computeChannel(int chID, boolean direction) {
    if (direction)
      return chID;
//    if (SF[chID] != null)
//      return SF[chID].to_channel_id;
    return chID;
  }

  class _ChangeChannel implements ChannelIDChange {
    private final RawTrafficDataStation station;
    private _ChangeChannel(RawTrafficDataStation parent) {
      station = parent;
    }

    /**
     * Change the channels in the header. Warning this does not change the body
     * @param fromChannel
     * @param toChannel
     * @throws Exception
     */
    @Override
    public void changeChannel(int fromChannel, int toChannel) throws Exception {
      if (fromChannel == toChannel)
        return;
      
      station.assertChannelIDBounds(toChannel);
      
      if (station.hasChannel(toChannel))
        RawFmtUtils.throwException(this, "Channel exists", toChannel);
      
      station.SC[fromChannel]._changeID(toChannel);
      
      station.SC[toChannel] = station.SC[fromChannel];
//      station.SF[toChannel] = station.SF[fromChannel];
      
      station.SC[fromChannel] = null;
//      station.SF[fromChannel] = null;

      station.recomputeSummaries();
    }
  }
}
