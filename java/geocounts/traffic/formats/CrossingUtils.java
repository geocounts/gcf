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
import java.util.List;
import geocounts.traffic.formats.Intersection.CrossingDirectionEnum;

/**
 * Manages {@link ChannelData channel data} representing crossings at intersections
 *
 */
public class CrossingUtils {

  /**
   * Add a channel representing pedestrians crossing at an intersection
   * @param S The station record which will have a channel added to
   * @param channelID The ID of the newly created channel
   * @param leg The {@link ChannelDirEnum leg} of an intersection being crossed
   * @param COMB_CW_ACW Direction the pedestrian is crossing the road. 0 = {@link ChannelLaneData#combined}, 1 = Clockwise, 2 = Anticlockwise
   * @return {@link ChannelData}. Only the 'to' direction is set
   */
  public static ChannelData addChannelAsIntersectionCrossing(RawTrafficDataStation S, int channelID, ChannelDirEnum leg, int COMB_CW_ACW) {
    CrossingDirectionEnum dir = CrossingDirectionEnum.values()[COMB_CW_ACW];
    return addChannelAsIntersectionCrossing(S, channelID, leg, dir);
  }
  
  /**
   * Add a channel representing pedestrians crossing at an intersection
   * @param S The station record which will have a channel added to
   * @param channelID The ID of the newly created channel
   * @param leg The {@link ChannelDirEnum leg} of an intersection being crossed
   * @param COMB_CW_ACW Direction the pedestrian is crossing the road. 0 = {@link ChannelLaneData#combined}, 1 = Clockwise, 2 = Anticlockwise
   * @return {@link ChannelData}. Only the 'to' direction is set
   */
  public static ChannelData addChannelAsIntersectionCrossing(RawTrafficDataStation S, int channelID, ChannelDirEnum leg, CrossingDirectionEnum COMB_CW_ACW) {
    ChannelData cd = S.addChannel(channelID);
    cd.to1.setDir(leg);
    COMB_CW_ACW.init(cd);
    return cd;
  }
  
  /**
   * 
   * @see Intersection.IntersectionCrossingChannels
   *
   */
  public static class CrossingInfoImpl implements Intersection.CrossingInfo {
    private ChannelDirEnum leg;
    public CrossingDirectionEnum direction;
    
    private CrossingInfoImpl(ChannelDirEnum l) {
      leg = l;
    }

    @Override
    public ChannelDirEnum getLegDir() {
      return leg;
    }

    @Override
    public CrossingDirectionEnum getDirection() {
      return direction;
    }
  }
  
  public static Intersection.CrossingInfo getCrossing(ChannelData cd) {
    if (!isCrossing(cd))
      return null;
    
    CrossingInfoImpl result = new CrossingInfoImpl(cd.to1.getDir());
    if (cd.to1.lane != ChannelLaneData.undefined)
      result.direction = CrossingDirectionEnum.values()[cd.to1.lane.getCode()];
    else if (cd.from.lane != ChannelLaneData.undefined)
      result.direction = CrossingDirectionEnum.values()[cd.from.lane.getCode()];
    else
      result.direction = CrossingDirectionEnum.combined;
    return result;
  }
  
  
  
  /**
   * 
   * @param S The {@link RawTrafficDataStation station}
   * @param leg The leg of the intersection to add a crossing
   * @param combined_separate If true, add a single 'combined' channel. If false, add two channels: clockwise and anti-clockwise
   * @return The last channel ID created
   */
  public static List<ChannelData> addChannelAsIntersectionCrossing(RawTrafficDataStation S, ChannelDirEnum leg, boolean combined_separate) {
    List<ChannelData> result = new ArrayList<ChannelData>();
    int chID = S.getMinMaxChannelID()[1] + 1;
    if (combined_separate) {
      ChannelData cd = addChannelAsIntersectionCrossing(S, chID, leg, CrossingDirectionEnum.combined);
      result.add(cd);
    } else {
      ChannelData cd1 = addChannelAsIntersectionCrossing(S, chID, leg, CrossingDirectionEnum.clockwise);
      chID++;
      ChannelData cd2 = addChannelAsIntersectionCrossing(S, chID, leg, CrossingDirectionEnum.anticlockwise);
      result.add(cd1);
      result.add(cd2);
    }
    return result;
  }
  
  /**
   * 
   * @param S The {@link RawTrafficDataStation station}
   * @param combined_separate If true, add a single 'combined' channel. If false, add two channels: clockwise and anti-clockwise
   */
  public static List<ChannelData> addCrossingsOnAllLegs(RawTrafficDataStation S, boolean combined_separate) {
    List<ChannelData> result = new ArrayList<ChannelData>();
    Intersection i = new Intersection(S);
    for (Intersection.IntersectionLeg leg: i.legs()) {
      List<ChannelData> channels = addChannelAsIntersectionCrossing(S, leg.dir, combined_separate);
      result.addAll(channels);
    }
    return result;
  }
  
  public static boolean isCrossing(ChannelData cd) {
    return RecognizedCrossingLaneUsageType.isCrossing(cd);
  }

  public static boolean hasCrossing(RawTrafficDataStation S) {
    for (ChannelData cd: S.getAllChannels()) {
      if (isCrossing(cd))
        return true;
    }
    return false;
  }
  
  public static boolean isSidewalk(ChannelData cd) {
    return RecognizedCrossingLaneUsageType.isSidewalk(cd);
  }

  public static boolean hasSidewalk(RawTrafficDataStation S) {
    for (ChannelData cd: S.getAllChannels()) {
      if (isSidewalk(cd))
        return true;
    }
    return false;
  }

  
  public enum RecognizedCrossingLaneUsageType {
    roadorbike,
    crossing,
    sidewalk,
    other;
    
    public static RecognizedCrossingLaneUsageType get(ChannelData cd) {
      if (isRoadOrBike(cd))
        return roadorbike;
      if (isCrossing(cd))
        return crossing;
      if (isSidewalk(cd))
        return sidewalk;
      return other;
    }
    
    public static boolean isRoadOrBike(ChannelData cd) {
      return (cd.to1.laneType == ChannelLaneUsageType.road) || 
             (cd.to1.laneType == ChannelLaneUsageType.bikelane) || 
             (cd.to1.laneType == ChannelLaneUsageType.undefined);
    }

    public static boolean isCrossing(ChannelData cd) {
      return (cd.to1.laneType == ChannelLaneUsageType.crossing);
    }

    public static boolean isSidewalk(ChannelData cd) {
      return (cd.to1.laneType == ChannelLaneUsageType.sidewalk);
    }
  }
}
