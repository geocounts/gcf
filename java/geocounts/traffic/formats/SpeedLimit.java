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

import geocounts.traffic.formats.ChannelData;
import geocounts.traffic.formats.ChannelDirEnum;
import geocounts.traffic.formats.RawTrafficDataStation;
import geocounts.traffic.formats.StationLabelElementField;
import geocounts.traffic.formats.StationLabels.LabelSetForElement;
import geocounts.traffic.formats.UnitsUsed;
import geocounts.traffic.formats.VehicleRec;

/**
 * 
 * @see com.transmetric.geocounts.webreports.SpeedingComputer
 */
public class SpeedLimit {
  
  private final HashMap<Integer, Double> limitKphByChannel = new HashMap<Integer, Double>();
  private final HashMap<ChannelDirEnum, Double> limitKphByDir = new HashMap<ChannelDirEnum, Double>();
  private double nominalLimitKph;
  
  public SpeedLimit() {
  }
  
  public SpeedLimit(RawTrafficDataStation S, UnitsUsed FU) {
    set(S, FU);
  }
  
  public void set(RawTrafficDataStation S, double kph) {
    limitKphByChannel.clear();
    limitKphByDir.clear();
    nominalLimitKph = 0;
    for (ChannelData channel: S.getAllChannels()) {
      limitKphByChannel.put(channel.getChannelID(), kph);
      limitKphByDir.put(channel.to1.getDir(), kph);
    }
  }
  
  public boolean isSet() {
    return nominalLimitKph > 0;
  }
  
  public void assignNominalSpeed(double kph) {
    nominalLimitKph = Math.abs(kph);
  }
  
  public void assignSpeedsToChannels(RawTrafficDataStation resultS) {
    for (ChannelData channel: resultS.getAllChannels()) {
      double speed = getSpeedLimitKph(channel.getChannelID());
      if (speed == 0)
        speed = nominalLimitKph;
      
      resultS.SL.addChannelLabel(channel.getChannelID(), StationLabelElementField.speedlimit, Double.toString(speed));
    }
  }
  
  public static boolean assignSpeedToChannel(RawTrafficDataStation resultS, int channelID, UnitsUsed FU, double speedKPH) {
    Object c = resultS.getChannel(channelID);
    if (c == null)
      return false;
    resultS.SL.addChannelLabel(channelID, StationLabelElementField.speedlimit, Double.toString(FU.getSpeed().toNative(speedKPH)));
    return true;
  }
  
  public void set(RawTrafficDataStation S, UnitsUsed FU) {
    // first look at the station label
    double stationNative = S.SL.getStationLabelAsDouble(StationLabelElementField.speedlimit, 0.0);
    
    // check each direction
    HashMap<ChannelDirEnum, Double> dirNative = new HashMap<ChannelDirEnum, Double>();
    for (ChannelDirEnum dir: S.getToDirections()) {
      String label = S.SL.getDirectionLabel(dir, StationLabelElementField.speedlimit);
      try {
        double nativeValue = Double.parseDouble(label);
        dirNative.put(dir, FU.getSpeed().toMetric(nativeValue));
      } catch (Exception ex) {}
    }
    
    // check each channel
    for (ChannelData channel: S.getAllChannels()) {
      // default value is the station label
      Double nativeValue = null;
      LabelSetForElement l = S.SL.getChannelLabel(channel.getChannelID());
      try {
        nativeValue = Double.parseDouble(l.get(StationLabelElementField.speedlimit));
      } catch (Exception ex) {}
      
      // if there is no channel, try the direction
      if (nativeValue == null) {
        nativeValue = dirNative.get(channel.to1.getDir());
        // if there is no direction, use the station (this always has a value, even 0)
        if (nativeValue == null) {
          nativeValue = stationNative;
        }
      }
      
      double channelLimitKph = FU.getSpeed().toMetric(nativeValue);
      limitKphByChannel.put(channel.getChannelID(), channelLimitKph);
      limitKphByDir.put(channel.to1.getDir(), channelLimitKph);
      
      if (nominalLimitKph == 0)
        nominalLimitKph = channelLimitKph;
    }
  }
  
  public double getNominalLimitKph() {
    return nominalLimitKph;
  }
  
  public double getSpeedLimitKph(VehicleRec veh) {
    return getSpeedLimitKph(veh.channelID);
  }
  
  public double getSpeedLimitKph(ChannelDirEnum dir) {
    Double result = limitKphByDir.get(dir);
    if (result != null)
      return result;
    return nominalLimitKph;
  }
  
  public double getSpeedLimitKph(int channelID) {
    Double result = limitKphByChannel.get(channelID);
    if (result != null)
      return result;
    return nominalLimitKph;
  }
}
