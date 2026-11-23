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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Manages {@link ChannelData channel data} representing lanes at intersections
 * @see CrossingUtils
 */
public class Intersection implements Serializable, CoordinateObject {

  private static final long serialVersionUID = 1L;
  
  private final RawTrafficDataStation S;
  
  /**
   * Legs keyed by the side of the intersection they are on (this is the vehicle's "approach")
   */
  private final HashMap<ChannelDirEnum, IntersectionLeg> legs = new HashMap<ChannelDirEnum, IntersectionLeg>();
  
  private final IntersectionGeometry geometry;
  
  public Intersection(RawTrafficDataStation S) {
    this.S = S;
    this.geometry = new IntersectionGeometry(this);
    _initFromStation();
  }
  
  public Intersection(List<TurnByDir> turns) {
    this.S = new RawTrafficDataStation();
    this.geometry = new IntersectionGeometry(this);
    
    int channelID = 0;
    for (TurnByDir d: turns) {
      channelID++;
      ChannelData cd = S.addChannel(channelID);
      cd.from.setDir(d.approach);
      cd.to1.setDir(d.depart);
    }

    _initFromStation();
  }
  
  private void _initFromStation() {
    for (ChannelData cd: S.getAllChannels()) {
      getOrCreate(cd.from.dir).add(cd);
      getOrCreate(cd.to1.dir).add(cd);
    }
    
    for (IntersectionLeg leg_app: legs.values()) {
      leg_app._initTurns();
    }
  }

  private IntersectionLeg getOrCreate(ChannelDirEnum dir) {
    IntersectionLeg result = legs.get(dir);
    if (result == null) {
      result = new IntersectionLeg(this, dir);
      legs.put(dir, result);
    }
    return result;
  }

  public boolean isValid() {
    return isIntersection.test(S);
  }

  public static Predicate<RawTrafficDataStation> isIntersection = new IsIntersectionTest();
  
  public static class IsIntersectionTest implements Predicate<RawTrafficDataStation> {
    @Override
    public boolean test(RawTrafficDataStation s) {
      boolean result = hasMoreThan2Departures(s);
      if (result)
        return true;

      // A one-way road leading to a T-intersection
      result = isOneWayRoadLeadingToT(s);
      if (result)
        return true;

      return false;
    }
    
    public boolean hasMoreThan2Departures(RawTrafficDataStation s) {
      return s.getNumberOfToDirections() > 2;
    }
    
    public boolean isOneWayRoadLeadingToT(RawTrafficDataStation s) {
      int numberOfToDirections = s.getNumberOfToDirections();
      if ((numberOfToDirections == 2) && (s.getFromDirections().size() == 1)) {
        ChannelDirEnum d = s.getFromDirections().getFirst();
        if (d == null)
          return false;
        if (!s.getToDirections().contains(d))
          return true;
      }

      return false;
    }
    
  }
  
  /**
   * 
   * @param approachLeg
   * @param turn
   * @param target
   * @return A channel with set directions but undefined lanes
   */
  public static ChannelData addChannel(ChannelDirEnum approachLeg, ChannelTurnEnum turn, RawTrafficDataStation target) {
    ChannelDirEnum departLeg = turn.compute(approachLeg, true);

    int maxch = target.getMinMaxChannelID()[1];
    int channelID = maxch + 1;
      
    ChannelData result = target.addChannel(channelID);
    result.from.setDir(approachLeg);
    result.to1.setDir(departLeg);
    return result;
  }
  
  public ChannelDataSetImpl getOnRoadChannels() {
    ChannelDataSetImpl result = new ChannelDataSetImpl();
    for (IntersectionLeg leg: legs()) {
      for (ChannelData cd: leg.from) {
        if (!result.hasChannel(cd.getChannelID()))
          result.addChannel(cd);
      }
    }
    return result;
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
      ChannelData cd = null;
      for (ChannelDirEnum legDir: legs) {
        channelID++;
        cd = CrossingUtils.addChannelAsIntersectionCrossing(result, channelID, legDir, CrossingDirectionEnum.combined);
      }
    }
    
    return result;
  }
  
  /**
   * 
   * @return The value of the underlying station's SI value (station ID)
   */
  public String getID() {
    return S.SI.getValue();
  }
  
  /**
   * This changes the underlying station record
   */
  public void setID(String value) {
    this.S.SI.set(value);
  }
  
  public void setCoordinate(double lat, double lng) {
    getCoordinate().setLatLng(lat, lng);
  }
  
  public void setCoordinate(LatLongCoordinate latlng) {
    getCoordinate().setFrom(latlng);
  }
  
  public RawTrafficDataHeader toHeader() {
    RawTrafficDataHeader result = new RawTrafficDataHeader();
    result.S.copyFrom(S);
    return result;
  }
  
  @Override
  public LatLongCoordinate getCoordinate() {
    return S.getCoordinate();
  }
  
  /**
   * @throws Exception
   */
  public RawTrafficDataHeader toHeader(boolean addRoadAndBike, boolean addPedCrossings, boolean addSidewalks) {
    RawTrafficDataHeader result = new RawTrafficDataHeader();
    result.S.copyFrom(S);
    
    ArrayList<ChannelData> channelsToRemove = new ArrayList<ChannelData>();
    for (ChannelData cd: result.S.getAllChannels()) {
      CrossingUtils.RecognizedCrossingLaneUsageType t = CrossingUtils.RecognizedCrossingLaneUsageType.get(cd);
      switch (t) {
      case roadorbike: {
        if (!addRoadAndBike)
          channelsToRemove.add(cd);
        break;
      }
      case crossing: {
        if (!addPedCrossings)
          channelsToRemove.add(cd);
        break;
      }
      case sidewalk: {
        if (!addSidewalks)
          channelsToRemove.add(cd);
        break;
      }
      case other: {
        channelsToRemove.add(cd);
        break;
      }
      }
    }
    
    for (ChannelData cd: channelsToRemove)
      result.S.removeChannel(cd.getChannelID());
    
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
  
  
  public static class IntersectionLeg implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Turns keyed by their departure leg
     */
    private final HashMap<ChannelDirEnum, TurnByDir> departure_turns = new HashMap<ChannelDirEnum, TurnByDir>();
    
    private void _initTurns() {
      departure_turns.clear();
      for (ChannelData cd: getDepartureChannels()) {
        ChannelDirEnum dir_dep = cd.to1.getDir();
        TurnByDir tbd = new TurnByDir(dir, dir_dep);
        tbd.channel = cd;
/*
        tbd.approach.coordIndex = 0;
        tbd.depart.coordIndex = cd.getGeometry().size()-1;
*/
        departure_turns.put(dir_dep, tbd);
      }
    }
    
    /**
     * The approach direction.
     */
    public final ChannelDirEnum dir;
    
    private final Intersection parent;
    /**
     * 
     * @param dir The approach direction
     */
    private IntersectionLeg(Intersection p, ChannelDirEnum dir) {
      parent = p;
      this.dir = dir;
      this.crossing = new IntersectionCrossing(this);
    }
    
    public boolean hasCrossing() {
      return crossing.exists();
    }

    @Override
    public String toString() {
      return dir.getDescription() + " leg. from=" + from.getNumberOfChannels() + " to=" + to.getNumberOfChannels() + " crossings=" + crossing.size();
    }
    
    /**
     * Channels that are approaching the intersection from this leg
     */
    public final ChannelDataSetImpl from = new ChannelDataSetImpl();
    
    /**
     * Channels that are departing the intersection via this leg
     */
    public final ChannelDataSetImpl to = new ChannelDataSetImpl();
    
    public final IntersectionCrossing crossing;
    
    public void add(ChannelData cd) {
      // crossing
      if (CrossingUtils.RecognizedCrossingLaneUsageType.isCrossing(cd)) {
        if (cd.to1.dir == dir) {
          crossing.add(cd);
        }
        return;
      }
      
      // road
      if (CrossingUtils.RecognizedCrossingLaneUsageType.isRoadOrBike(cd)) {
        if (cd.from.dir == dir) {
          if (!from.hasChannel(cd.getChannelID())) {
            from.addChannel(cd);
          }
        }
        if (cd.to1.dir == dir) {
          if (!to.hasChannel(cd.getChannelID())) {
            to.addChannel(cd);
          }
        }
      }
    }
    
    private java.util.Collection<ChannelData> getDepartureChannels() {
      List<ChannelData> result = new ArrayList<ChannelData>();
      for (ChannelData cd: this.from) {
        if (cd.to1.getDir() != ChannelDirEnum.undefined)
          result.add(cd);
/*
        if (cd.to2.getDir() != ChannelDirEnum.undefined)
          result.put(cd.to2.getDir(), cd);
        if (cd.to3.getDir() != ChannelDirEnum.undefined)
          result.put(cd.to3.getDir(), cd);
*/
      }
      
      return result;
    }
    
    public Set<ChannelDirEnum> getDepartureDirs() {
      HashSet<ChannelDirEnum> result = new HashSet<ChannelDirEnum>();
      for (ChannelData cd: this.from) {
        if (cd.to1.getDir() != ChannelDirEnum.undefined)
          result.add(cd.to1.getDir());
        if (cd.to2.getDir() != ChannelDirEnum.undefined)
          result.add(cd.to2.getDir());
        if (cd.to3.getDir() != ChannelDirEnum.undefined)
          result.add(cd.to3.getDir());
      }
      
      return result;
    }
    
    public String getLabel() {
      String result = IntersectionLegLabel.dir.apply(this);
      if (result != null)
        return result;
      result = IntersectionLegLabel.channel.apply(this);
      if (result != null)
        return result;
      return dir.getDescription();
    }
    
    /*
     * THIS WAS WRONG
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
    */
  }

  public enum IntersectionLegLabel implements Function<IntersectionLeg, String> {
    dir() {
      @Override
      public String apply(IntersectionLeg leg) {
        if (leg == null)
          return null;
        StationLabelElementField[] fields = new StationLabelElementField[] {StationLabelElementField.pathname, StationLabelElementField.pathid, StationLabelElementField.description};
        for (StationLabelElementField f: fields) {
          String result = get(leg, f);
          if (result != null)
            return result;
        }
        return null;
      }
      
      private String get(IntersectionLeg leg, StationLabelElementField label) {
        return leg.parent.S.SL.getDirectionLabel(leg.dir, label);
      }
    },
    channel() {
      @Override
      public String apply(IntersectionLeg leg) {
        if (leg == null)
          return null;
        StationLabelElementField[] fields = new StationLabelElementField[] {StationLabelElementField.pathname, StationLabelElementField.pathid, StationLabelElementField.description};
        for (ChannelData ch: leg.from) {
          for (StationLabelElementField f: fields) {
            String result = get(leg, ch, f);
            if (result != null)
              return result;
          }
        }
        return null;
      }
      
      private String get(IntersectionLeg leg, ChannelData ch, StationLabelElementField label) {
        return leg.parent.S.SL.getChannelLabel(ch.getChannelID(), label);
      }
    };
  }
  
  public enum CrossingDirectionEnum {
    /**
     * {@link ChannelLaneData#combined}
     */
    combined {
      @Override
      protected void initLanes(ChannelData cd) {
        cd.from.lane = ChannelLaneData.combined;
        cd.to1.lane = ChannelLaneData.combined;
      }
    },
    clockwise {
      @Override
      protected void initLanes(ChannelData cd) {
        cd.from.lane = ChannelLaneData.fromCode(1);
        cd.to1.lane = ChannelLaneData.fromCode(1);
      }
    },
    /**
     * Or "Counter-clockwise"
     */
    anticlockwise {
      @Override
      protected void initLanes(ChannelData cd) {
        cd.from.lane = ChannelLaneData.fromCode(2);
        cd.to1.lane = ChannelLaneData.fromCode(2);
      }
    };
    
    protected abstract void initLanes(ChannelData cd);
    
    public void init(ChannelData cd) {
      cd.from.laneType = ChannelLaneUsageType.crossing;
      cd.to1.laneType = ChannelLaneUsageType.crossing;
      initLanes(cd);
    }
  }
  
  public interface CrossingInfo {
    public ChannelDirEnum getLegDir();
    
    public CrossingDirectionEnum getDirection();
  }
  
  /**
   * 
   * @see CrossingInfo
   *
   */
  public static class IntersectionCrossingChannels extends ChannelDataSetImpl implements CrossingInfo {
    private static final long serialVersionUID = 1L;
    
    private final CrossingDirectionEnum direction;
    private final IntersectionCrossing parent;
    private IntersectionCrossingChannels(IntersectionCrossing p, CrossingDirectionEnum d) {
      parent = p;
      direction = d;
    }
    
    @Override
    public CrossingDirectionEnum getDirection() {
      return direction;
    }

    @Override
    public ChannelDirEnum getLegDir() {
      return parent.leg.dir;
    }

    public IntersectionCrossing getCrossing() {
      return parent;
    }
  }
  
  public static class IntersectionCrossing implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public final IntersectionLeg leg;
    private IntersectionCrossing(IntersectionLeg leg) {
      this.leg = leg;
    }
    
    public int size() {
      return COMBINED.getNumberOfChannels() + CW.getNumberOfChannels() + CCW.getNumberOfChannels();
    }

    public boolean add(ChannelData cd) {
      if (!CrossingUtils.RecognizedCrossingLaneUsageType.isCrossing(cd))
        return false;
      if (channelsUsed.contains(cd.getChannelID()))
        return false;
      
      if (cd.to1.lane.getCode() == CrossingDirectionEnum.clockwise.ordinal()) {
        CW.addChannel(cd);
      } else if (cd.to1.lane.getCode() == CrossingDirectionEnum.anticlockwise.ordinal()) {
        CCW.addChannel(cd);
      } else
        COMBINED.addChannel(cd);
      
      channelsUsed.add(cd.getChannelID());
      return true;
    }

    private HashSet<Integer> channelsUsed = new HashSet<Integer>();
    /**
     * Both direction crossing
     */
    public final IntersectionCrossingChannels COMBINED = new IntersectionCrossingChannels(this, CrossingDirectionEnum.combined);
    
    /**
     * Clockwise crossing
     */
    public final IntersectionCrossingChannels CW = new IntersectionCrossingChannels(this, CrossingDirectionEnum.clockwise);
    
    /**
     * Counter-clockwise crossing
     */
    public final IntersectionCrossingChannels CCW = new IntersectionCrossingChannels(this, CrossingDirectionEnum.anticlockwise);
    
    public boolean exists() {
      return size() > 0;
    }

    public ChannelData get(int channelID) {
      ChannelData cd = CW.getChannel(channelID);
      if (cd != null)
        return cd;
      cd = CCW.getChannel(channelID);
      if (cd != null)
        return cd;
      return COMBINED.getChannel(channelID);
    }
  }
  
  public static class TurnByDirEndPoint implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public final ChannelDirEnum leg;
    private final TurnByDir parent;

    private TurnByDirEndPoint(TurnByDir p, ChannelDirEnum l) {
      leg = l;
      parent = p;
    }
    
    /*
     * @return The {@link LatLongCoordinate}, never null
     *
    public final LatLongCoordinate coordinate() {
      LatLongCoordinate result = parent.channel.getGeometry().getPoint(coordIndex);
      return result != null ? result : new LatLongCoordinate();
    }
    */
  }
  
  /**
   * Turning movement by the approach and departure legs
   * @since 1.9964
   */
  public static class TurnByDir implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static TurnByDir fromCode(int code) {
      int app = code/10;
      int dep = code % 10;
      return new TurnByDir(ChannelDirEnum.fromCode(app), ChannelDirEnum.fromCode(dep));
    }
    
    private ChannelData channel;
    
    public int getChannelID() {
      return channel != null ? channel.getChannelID() : -1;
    }
    
    private void addToCoordinates(List<LatLongCoordinate> target) {
      if (channel == null)
        return;
      for (LatLongCoordinate c: channel.getGeometry())
        if (c.isSet())
          target.add(c);
    }
    
    /*
    public void setFrom(TurnByDir in) {
      approach.coordinate.setFrom(in.approach.coordinate);
      depart.coordinate.setFrom(in.depart.coordinate);
      waypoints.clear();
      waypoints.addAll(in.waypoints);
    }
    */

    /**
     * Approach leg (starting position)
     */
    public final ChannelDirEnum approach;

    /**
     * Departure leg (starting position)
     */
    public final ChannelDirEnum depart;
    
    private final ChannelTurnEnum turn;
    
    @Override
    public String toString() {
      return getCode() + " " + (channel != null ? channel.getGeometry().size() : "");
    }


    public StationShape getPath() {
      return StationShape.newLine(channel.getGeometry());
    }

    public TurnByDir(ChannelDirEnum a, ChannelDirEnum d) {
      approach = a;
      depart = d;
      turn = ChannelTurnEnum.compute(approach, depart);
    }

    public int getCode() {
      return approach.getCode() * 10 + depart.getCode();
    }
    
    public ChannelTurnEnum getTurningMovement() {
      return turn;
    }
  }
  
  /**
   * 
   * @return {@link IntersectionGeometry}
   */
  public IntersectionGeometry getGeometry() {
    return geometry;
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
  
  public Iterable<IntersectionLeg> legs(ChannelDirEnum starting, boolean clockwise) {
    ChannelDirEnum[] dirs = clockwise ? ChannelDirEnum.getClockwise(starting) : ChannelDirEnum.getCounterClockwise(starting);
    List<IntersectionLeg> result = new ArrayList<IntersectionLeg>();
    for (ChannelDirEnum d: dirs) {
      IntersectionLeg leg = legs.get(d);
      if (leg != null)
        result.add(leg);
    }
    return result;
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
  
  /**
   * 
   * Gets the turn from a 'bound' direction (opposite direction to the approach leg) and a turning movement
   * @return {@link ChannelDataSet}
   */
  public ChannelDataSet getTurn(ChannelDirEnum bound, ChannelTurnEnum turn) {
    ChannelDataSetImpl result = new ChannelDataSetImpl();
    IntersectionLeg fromLeg = legs.get(bound.getOppositeDir());
    if (fromLeg == null)
      return result;
    ChannelDirEnum dest = turn.compute(bound, false);
    for (ChannelData cd: fromLeg.from) {
      if (cd.to1.getDir() == dest)
        result.addChannel(cd);
    }
    return result;
  }
  
  public List<TurnByDir> getTurnByDirections() {
    ArrayList<TurnByDir> result = new ArrayList<TurnByDir>();
    for (IntersectionLeg leg_app: legs.values()) {
      result.addAll(leg_app.departure_turns.values());
    }
    return result;
  }
  
  public TurnByDir getTurnByDir(ChannelDirEnum approach, ChannelDirEnum depart) {
    IntersectionLeg l = legs.get(approach);
    if (l == null)
      return null;
    return l.departure_turns.get(depart);
  }
  
  public int numberOfUTurns() {
    int result = 0;
    for (int i=1; i<ChannelDirEnum.values().length; i++) {
      if (hasTurn(ChannelDirEnum.values()[i], ChannelDirEnum.values()[i]))
        result++;
    }
    return result;
  }
  
  public boolean hasTurn(ChannelDirEnum approach, ChannelDirEnum depart) {
    return getTurnByDir(approach, depart) != null;
  }
  
  
  public static class IntersectionGeometry {
    private final Intersection parent;
    private IntersectionGeometry(Intersection p) {
      parent = p;
    }
    
    public boolean hasTurnPaths() {
      return parent.S.getFirstChannel().getGeometry().size() > 1;
    }
    
    public LatLongCoordinate getCentroidOfTurns() {
      ArrayList<LatLongCoordinate> coordinates = new ArrayList<LatLongCoordinate>();
      for (IntersectionLeg leg: parent.legs()) {
        for (TurnByDir dt: leg.departure_turns.values()) {
          dt.addToCoordinates(coordinates);
        }
      }
      return CoordinateObjectComputation.getCentroid(coordinates);
    }
    
    public boolean setTurnPath(TurnByDir turn, Iterable<LatLongCoordinate> coords) {
      TurnByDir t = parent.getTurnByDir(turn.approach, turn.depart);
      if (t == null)
        return false;
      if (t.channel == null)
        return false;
      t.channel.getGeometry().setPoints(coords);
      return true;
    }
    
    public LatLongCoordinate getCentroidOfChannels() {
      return parent.S.getCentroidOfChannels();
    }
  }
}
