/*
 * Copyright 2014 Transmetric America Inc. All rights reserved.
 * Transmetric America Inc PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package geocounts.formats.io;

import geocounts.traffic.formats.ChannelData;
import geocounts.traffic.formats.ChannelDirEnum;
import geocounts.traffic.formats.ChannelLaneData;
import geocounts.traffic.formats.ChannelLaneUsageType;
import geocounts.traffic.formats.RawTrafficDataHeader;
import geocounts.traffic.formats.RawTrafficDataStation;
import geocounts.traffic.formats.StationLabelElementField;
import geocounts.traffic.formats.TallyDurationEnum;

/**
 * 
 * @see RawTrafficDataFileJSONEncoder
 *
 */
public class RawTrafficDataFileJSONDecoder {
  
  public void decodeHeader(java.util.Map params, RawTrafficDataHeader result) {
    if (params == null)
      return;
    
    result.S.SI.set((String)params.get("SI"));
    
    result.F.FE = (String)params.get("FE");
    
    try {
      result.T.TD = TallyDurationEnum.fromString(params.get("TD").toString());
    } catch (Exception ex) {}
    
    try {
      java.util.List<?> TF = (java.util.List<?>)params.get("TF");
      for (int i=0; i<TF.size(); i++) {
        result.T.TF.addField((String)TF.get(i));
      }
    } catch (Exception ex) {}
    
    java.util.Map TC = (java.util.Map)params.get("TC");
    if (TC != null) {
      result.T.TC.setAll((String)TC.get("name"), 
                         getInt(TC, "minclassid", result.T.TC.getMinimumBin()), 
                         getInt(TC, "maxclassid", result.T.TC.getMaximumBin()));
    }
    
    result.S.SA = (String)params.get("SA");
    result.S.SS = (String)params.get("SS");
    result.S.ST = (String)params.get("ST");
    result.S.SU = (String)params.get("SU");
    
    try {
      java.util.Map SL = (java.util.Map)params.get("SL");
      java.util.Map SL_station = (java.util.Map)SL.get("station");
      result.S.SL.addStationLabel(StationLabelElementField.regionname,    (String)SL_station.get("regionname"));
      result.S.SL.addStationLabel(StationLabelElementField.urbanareaname, (String)SL_station.get("urbanareaname"));
      result.S.SL.addStationLabel(StationLabelElementField.zonename,      (String)SL_station.get("zonename"));
      result.S.SL.addStationLabel(StationLabelElementField.pathid,        (String)SL_station.get("pathid"));
      result.S.SL.addStationLabel(StationLabelElementField.pathname,      (String)SL_station.get("pathname"));
      result.S.SL.addStationLabel(StationLabelElementField.description,   (String)SL_station.get("description"));
      result.S.SL.addStationLabel(StationLabelElementField.groupname,     (String)SL_station.get("groupname"));
      result.S.SL.addStationLabel(StationLabelElementField.speedlimit,    getInt((SL_station), "speedlimit", 0));
    } catch (Exception ex) {}
    
    try {
      java.util.List<?> arr = (java.util.List<?>)params.get("SG");
      result.S.SG.point.setLatLng(getDouble(arr, 0), getDouble(arr, 1));
    } catch (Exception ex) {}

    try {
      java.util.List<?> arr = (java.util.List<?>)params.get("SC");
      for (int i=0; i<arr.size(); i++) {
        decodeChannel((java.util.Map)arr.get(i), result.S);
      }
    } catch (Exception ex) {}
  }
  
  private void decodeChannel(java.util.Map json, RawTrafficDataStation S) {
    ChannelData SC = S.addChannel(getInt(json, "channel_id"));
    java.util.Map from = (java.util.Map)json.get("from");
    if (from != null) {
      SC.from.setDir(ChannelDirEnum.fromCode(getInt(from, "dir", -1)));
      SC.from.lane = ChannelLaneData.fromCode(getInt(from, "lane", -1));
      SC.from.laneType = ChannelLaneUsageType.fromCode(getInt(from, "usage", -1));
    }
    
    java.util.Map to = (java.util.Map)json.get("to");
    if (to != null) {
      SC.to1.setDir(ChannelDirEnum.fromCode(getInt(to, "dir", -1)));
      SC.to1.lane = ChannelLaneData.fromCode(getInt(to, "lane", -1));
      SC.to1.laneType = ChannelLaneUsageType.fromCode(getInt(to, "usage", -1));
    }
  }
  
  private int getInt(java.util.Map o, String id, int defaultValue) {
    Object value = o.get(id);
    if (value == null)
      return defaultValue;
    if (value instanceof Number) {
      Number n = (Number)value;
      return n.intValue();
    }
    return (int)Double.parseDouble(value.toString());
  }
  
  private int getInt(java.util.Map o, String id) {
    Object value = o.get(id);
    if (value instanceof Number) {
      Number n = (Number)value;
      return n.intValue();
    }
    return (int)Double.parseDouble(value.toString());
  }
  
  private Double getDouble(java.util.List<?> arr, int index) {
    Number n = (Number)arr.get(index);
    return n.doubleValue();
  }
}
