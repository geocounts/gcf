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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages {@link ChannelData channel data} representing lanes at intersections
 * @see CrossingUtils
 */
public class Intersection {
  private final RawTrafficDataStation S;
  /**
   * Legs keys by the side of the intersection they are on (a vehicle's "approach")
   */
  private final HashMap<ChannelDirEnum, IntersectionLeg> legs = new HashMap<ChannelDirEnum, IntersectionLeg>();
  
  public Intersection(RawTrafficDataStation S) {
    this.S = S;
    
    for (ChannelData cd: S.getAllChannels()) {
      getOrCreate(cd.from.dir).add(cd);
      getOrCreate(cd.to1.dir).add(cd);
    }
  }
  
  private IntersectionLeg getOrCreate(ChannelDirEnum dir) {
    IntersectionLeg result = legs.get(dir);
    if (result == null) {
      result = new IntersectionLeg(dir);
      legs.put(dir, result);
    }
    return result;
  }
  
  public static boolean isIntersection(RawTrafficDataStation S) {
    return S.getNumberOfToDirections() > 2;
  }
  
  /**
   * 
   * @param legs Leg {@link ChannelDirEnum directions}
   * @param addUTurns
   * @param addPedCrossings
   * @return A {@link RawTrafficDataStation}
   */
  public static RawTrafficDataStation createStation(Set<ChannelDirEnum> legs, boolean addUTurns, boolean addPedCrossings) {
    int channelID = 0;
    RawTrafficDataStation result = new RawTrafficDataStation();
    for (ChannelDirEnum fromDir: legs) {
      for (ChannelDirEnum toDir: legs) {
        if (addUTurns || (fromDir != toDir)) {
          channelID++;
          ChannelData cd = result.addChannel(channelID);
          cd.from.laneType = ChannelLaneUsageType.road;
          cd.from.setDir(fromDir);
          cd.to1.laneType = ChannelLaneUsageType.road;
          cd.to1.setDir(toDir);
        }
      }
    }
    
    if (addPedCrossings) {
      for (ChannelDirEnum legDir: legs) {
        channelID++;
        ChannelData cd = result.addChannel(channelID);
        cd.to1.setDir(legDir);
        cd.to1.laneType = ChannelLaneUsageType.crossing;
        cd.to1.lane = ChannelLaneData.combined;
        cd.from.laneType = ChannelLaneUsageType.crossing;
        cd.from.lane = ChannelLaneData.combined;
      }
    }
    
    return result;
  }
  
  public List<IntersectionLeg> getLegsWithCrossings() {
    List<IntersectionLeg> result = new ArrayList<IntersectionLeg>();
    for (IntersectionLeg leg: legs.values()) {
      if (leg.hasCrossing())
        result.add(leg);
    }
    return result;
  }
  
  public boolean hasCrossings() {
    return getLegsWithCrossings().size() > 0;
  }
  
  public class IntersectionLeg {
    /**
     * The approach direction.
     */
    public final ChannelDirEnum dir;
    
    /**
     * 
     * @param dir The approach direction
     */
    private IntersectionLeg(ChannelDirEnum dir) {
      this.dir = dir;
      this.crossing = new IntersectionCrossing(this);
    }
    
    public boolean hasCrossing() {
      return crossing.exists();
    }

    @Override
    public String toString() {
      return dir.getDescription() + " leg. from=" + from.size() + " to=" + to.size() + " crossings=" + crossing.size();
    }
    
    /**
     * Channels that are approaching the intersection from this leg
     */
    public final List<ChannelData> from = new ArrayList<ChannelData>();
    private HashSet<Integer> fromChannels = new HashSet<Integer>();
    
    /**
     * Channels that are departing the intersection via this leg
     */
    public final List<ChannelData> to = new ArrayList<ChannelData>();
    private HashSet<Integer> toChannels = new HashSet<Integer>();
    
    public final IntersectionCrossing crossing;
    
    public void add(ChannelData cd) {
      // crossing
      if (cd.to1.laneType == ChannelLaneUsageType.crossing) {
        if (cd.to1.dir == dir) {
          crossing.add(cd);
        }
        return;
      }
      
      // road
      if ((cd.to1.laneType == ChannelLaneUsageType.road) || (cd.to1.laneType == ChannelLaneUsageType.undefined)) {
        if (cd.from.dir == dir) {
          if (!fromChannels.contains(cd.getChannelID())) {
            from.add(cd);
            fromChannels.add(cd.getChannelID());
          }
        }
        if (cd.to1.dir == dir) {
          if (!toChannels.contains(cd.getChannelID())) {
            to.add(cd);
            toChannels.add(cd.getChannelID());
          }
        }
      }
    }
    
    public Set<ChannelDirEnum> getDepartureDirections() {
      HashSet<ChannelDirEnum> result = new HashSet<ChannelDirEnum>();
      for (ChannelData cd: this.to) {
        if (cd.to1.getDir() != ChannelDirEnum.undefined)
          result.add(cd.to1.getDir());
        if (cd.to2.getDir() != ChannelDirEnum.undefined)
          result.add(cd.to2.getDir());
        if (cd.to3.getDir() != ChannelDirEnum.undefined)
          result.add(cd.to3.getDir());
      }
      
      return result;
    }
  }
  
  public class IntersectionCrossing {
    public final IntersectionLeg leg;
    private IntersectionCrossing(IntersectionLeg leg) {
      this.leg = leg;
    }
    
    public int size() {
      return CW.size() + CCW.size();
    }

    public boolean add(ChannelData cd) {
      if (cd.to1.laneType != ChannelLaneUsageType.crossing)
        return false;
      if (channelsUsed.contains(cd.getChannelID()))
        return false;
      
      if (cd.to1.lane.getCode() == 1) {
        CW.add(cd);
      } else if (cd.to1.lane.getCode() == 2) {
        CCW.add(cd);
      } else
        CW.add(cd);
      
      channelsUsed.add(cd.getChannelID());
      return true;
    }

    private HashSet<Integer> channelsUsed = new HashSet<Integer>();
    /**
     * Clockwise crossing
     */
    public final ArrayList<ChannelData> CW = new ArrayList<ChannelData>();
    
    /**
     * Counter-clockwise crossing
     */
    public final ArrayList<ChannelData> CCW = new ArrayList<ChannelData>();
    
    public boolean exists() {
      return size() > 0;
    }

    public ChannelData get(int channelID) {
      for (ChannelData c: CW)
        if (c.getChannelID() == channelID)
          return c;
      for (ChannelData c: CCW)
        if (c.getChannelID() == channelID)
          return c;
      return null;
    }
  }
  
  /**
   * Turning movement by the approach and departure legs
   * @since 1.9964
   */
  public class TurnByDir {
    public final ChannelDirEnum approach;
    public final ChannelDirEnum depart;
    
    public TurnByDir(ChannelDirEnum a, ChannelDirEnum d) {
      approach = a;
      depart = d;
    }

    public int getCode() {
      return approach.getCode() * 10 + depart.getCode();
    }
  }
  
  public Map<ChannelDirEnum, IntersectionLeg> getLegs() {
    return legs;
  }

  public int numberOfLegs() {
    return legs.size();
  }

  public Iterable<IntersectionLeg> legs() {
    return legs.values();
  }

  /**
   * 
   * @param channelID
   * @return The {@link IntersectionCrossing crossing} corresponding to the channelID, or null if its not a crossing channel
   */
  public IntersectionCrossing getCrossing(int channelID) {
    for (IntersectionLeg leg: legs.values()) {
      ChannelData c = leg.crossing.get(channelID);
      if (c != null)
        return leg.crossing;
    }
    return null;
  }
  
  public List<ChannelData> getTurn(ChannelDirEnum bound, ChannelTurnEnum turn) {
    ArrayList<ChannelData> result = new ArrayList<ChannelData>();
    IntersectionLeg fromLeg = legs.get(bound.getOppositeDir());
    if (fromLeg == null)
      return result;
    ChannelDirEnum dest = turn.compute(bound, false);
    for (ChannelData cd: fromLeg.from) {
      if (cd.to1.getDir() == dest)
        result.add(cd);
    }
    return result;
  }
  
  public List<TurnByDir> getTurnByDirections() {
    ArrayList<TurnByDir> result = new ArrayList<TurnByDir>();
    for (IntersectionLeg leg_app: legs.values()) {
      for (ChannelDirEnum dir_dep: leg_app.getDepartureDirections()) {
        result.add(new TurnByDir(leg_app.dir, dir_dep));
      }
    }
    return result;
  }
  
  public boolean hasTurn(ChannelDirEnum approach, ChannelDirEnum depart) {
    for (TurnByDir tbd: getTurnByDirections()) {
      if ((tbd.approach == approach) && (tbd.depart == depart))
        return true;
    }
    
    return false;
  }
}
