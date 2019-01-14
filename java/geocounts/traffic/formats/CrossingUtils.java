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
 * Manages {@link ChannelData channel data} representing crossings at intersections
 *
 */
public class CrossingUtils {

  /**
   * Add a channel representing pedestrians crossing at an intersection
   * @param S The station record which will have a channel added to
   * @param channelID
   * @param leg The {@link ChannelDirEnum leg} of an intersection being crossed
   * @param COMB_CW_ACW Direction the pedestrian is crossing the road. 0 = {@link ChannelLaneData#combined}, 1 = Clockwise, 2 = Anticlockwise
   * @return {@link ChannelData}. Only the 'to' direction is set
   */
  public static ChannelData addChannelAsIntersectionCrossing(RawTrafficDataStation S, int channelID, ChannelDirEnum leg, int COMB_CW_ACW) {
    ChannelData cd = S.addChannel(channelID);
    cd.from.laneType = ChannelLaneUsageType.crossing;
    cd.to1.laneType = ChannelLaneUsageType.crossing;
    cd.to1.setDir(leg);
    switch (COMB_CW_ACW) {
    case 0: {
      cd.from.lane = ChannelLaneData.combined;
      cd.to1.lane = ChannelLaneData.combined;
      break;
    }
    case 1: {
      cd.from.lane = ChannelLaneData.fromCode(1);
      cd.to1.lane = ChannelLaneData.fromCode(1);
      break;
    }
    case 2: {
      cd.from.lane = ChannelLaneData.fromCode(2);
      cd.to1.lane = ChannelLaneData.fromCode(2);
      break;
    }
    }
    return cd;
  }
  
  /**
   * 
   * @param S The {@link RawTrafficDataStation station}
   * @param leg The leg of the intersection to add a crossing
   * @param combined_separate If true, add a single 'combined' channel. If false, add two channels: clockwise and anti-clockwise
   * @return The last channel ID created
   */
  public static int addChannelAsIntersectionCrossing(RawTrafficDataStation S, ChannelDirEnum leg, boolean combined_separate) {
    int result = S.getMinMaxChannelID()[1] + 1;
    if (combined_separate) {
      addChannelAsIntersectionCrossing(S, result, leg, 0);
    } else {
      addChannelAsIntersectionCrossing(S, result, leg, 1);
      result++;
      addChannelAsIntersectionCrossing(S, result, leg, 2);
    }
    return result;
  }
  
  public static boolean isCrossing(ChannelData cd) {
    if (cd.to1.laneType == ChannelLaneUsageType.crossing)
      return true;
    return false;
  }

  public static boolean hasCrossing(RawTrafficDataStation S) {
    for (ChannelData cd: S.getAllChannels()) {
      if (isCrossing(cd))
        return true;
    }
    return false;
  }
  
  public static boolean isSidewalk(ChannelData cd) {
    if (cd.to1.laneType == ChannelLaneUsageType.sidewalk)
      return true;
    return false;
  }

  public static boolean hasSidewalk(RawTrafficDataStation S) {
    for (ChannelData cd: S.getAllChannels()) {
      if (isSidewalk(cd))
        return true;
    }
    return false;
  }
}
