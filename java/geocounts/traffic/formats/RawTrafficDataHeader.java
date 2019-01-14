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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Represents the header of a raw count file.
 * Every raw count file has a header.
 * @see RawTrafficData
 */
public class RawTrafficDataHeader {
  
  /**
   * GEOCOUNTS: license key
   * @see LicenseKey
   */
  public final LicenseKey GK = new LicenseKey();

  /**
   * Header component relating to the File
   * @see RawTrafficDataHeaderFileDef
   */
  public final RawTrafficDataHeaderFileDef F;

  /**
   * Header component relating to the station
   * @see RawTrafficDataStation
   */
  public final RawTrafficDataStation S;
  
  /**
   * The channels
   * @see ChannelData
   * @see RawTrafficDataStation#getAllChannels()
   */
  public Iterable<ChannelData> getAllChannels() {
    return S.getAllChannels();
  }
  
  public int getNumberOfChannels() {
    return S.getNumberOfChannels();
  }
  
  /**
   * 
   * @param channelID
   * @return {@link ChannelData} or null if the channel does not exist
   */
  public ChannelData getChannel(int channelID) {
    return S.getChannel(channelID);
  }
  
  public int getIndexOfChannel(int channelID) {
    List<ChannelData> channels = S.getAllChannels();
    for (int i=0; i<channels.size(); i++) {
      if (channels.get(i).getChannelID() == channelID)
       return i;
    } 
    return -1;
  }
  
  /**
   * Used internally to create arrays that are as long as the number of channels.
   * This determines which element of the array each channel applies to.
   * If channels always started at zero and incremented by 1, this would not be needed.
   * However a station could contain 2 channels: 2 and 4, for example.
   * @return A map: key is the channel ID, value is the position in the storage array
   */
  public Map<Integer, Integer> getChannelIndex() {
    HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();
    List<ChannelData> channels = S.getAllChannels();
    for (int i=0; i<channels.size(); i++) {
      result.put(channels.get(i).getChannelID(), i);
    }
    return result;
  }
  
  /**
   * Tally record field definitions
   * @see TallyRecordsDef
   */
  public final TallyRecordsDef T;

  /**
   * Observation record field definitions
   * @see VehicleRecordDef Observation record
   * @see VehicleFieldsEnum
   */
  public final VehicleRecordDef VV;

  /**
   * Axle record field definitions. Axle records are subsets of {@link VV vehicle records}
   * @see AxleRecordDef
   */
  public final AxleRecordDef VA;
  
  public RawTrafficDataHeader() {
    F = new RawTrafficDataHeaderFileDef();
    S = new RawTrafficDataStation(this);
    T = new TallyRecordsDef(this.F, this.S.SZ);
    VV = new VehicleRecordDef(this.S.SZ);
    VA = new AxleRecordDef();
  }
  
  public MonitoringEventRec newMonitoringEventRec(long timeUTCAccountedFor) {
    return MonitoringEventRec.createNoTZ(timeUTCAccountedFor - S.SZ.getRecordedTimestampOffset());
  }
  
  private final List<ChannelIDChange> channelIDChangers = new ArrayList<ChannelIDChange>();
  protected void addChannelIDChange(ChannelIDChange value) {
    channelIDChangers.add(value);
  }
  
  protected void changeChannel(int fromChannel, int toChannel) throws Exception {
    // change the listeners
    for (ChannelIDChange listener: this.channelIDChangers) {
      listener.changeChannel(fromChannel, toChannel);
    }
  }

  private java.text.DateFormat tallyDF = null;
  
  /**
   * yyyy/MM/dd,HH
   * @return The tally date format
   */
  public java.text.DateFormat getTallyDateFormat() {
    if (tallyDF == null)
      tallyDF = RawFmtUtils.sdfHeaderT();
    return tallyDF;
  }

  private java.text.DateFormat obsDF = null;
  
  /**
   * yyyy/MM/dd,HH:mm:ss.SSS
   * @return The vehicle record date format
   */
  public java.text.DateFormat getObservationDateFormat() {
    if (obsDF == null)
      obsDF = RawFmtUtils.sdfHeaderV();
    return obsDF;
  }

  public int compareTo(long time) {
    return S.SR.compareTo(time);
  }

  /**
   * Creates a copy of this object with these sub objects copied: GK, F, S, T, VV and VA
   * @return {@link RawTrafficDataHeader}
   * @throws Exception
   */
  public RawTrafficDataHeader copy() throws Exception {
    RawTrafficDataHeader result = new RawTrafficDataHeader();

    result.GK.copyFrom(this.GK);
    result.F.copyFrom(this.F);
    result.S.copyFrom(this.S);
    result.T.copyFrom(this.T);
    result.VV.copyFrom(this.VV);
    result.VA.copyFrom(this.VA);

    return result;
  }
  
  /**
   * Merges T, F and S record
   * @param other
   */
  public void mergeFrom(RawTrafficDataHeader other) {
    this.F.mergeFrom(other.F);
    this.S.mergeFrom(other.S);
    this.T.mergeFrom(other.T);
    this.VV.mergeFrom(other.VV);
    this.VA.mergeFrom(other.VA);
  }

}