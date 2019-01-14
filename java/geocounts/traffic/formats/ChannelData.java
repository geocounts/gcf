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
 * 
 * @see EnumHeaderRecords#SC
 *
 */
public class ChannelData extends RawTrafficDataHeaderElement implements Comparable<ChannelData> {
  public static final int MAX_CHANNELID = 99;
  
  private int channelID;
  protected void _changeID(int newID) {
    channelID = newID;
  }
  
  private final RawTrafficDataStation header;
  protected ChannelData(RawTrafficDataStation parent, int id) {
    this.header = parent;
    if ((id < 0) || (id > MAX_CHANNELID))
      throw new ArrayIndexOutOfBoundsException(id);
    channelID = id;
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.SC;
  }
  
  /**
   * The channel ID is intended to be immutable for the life of the object. 
   * (Note it can still be changed through {@link #read(String[]), which you should never do after its been set}
   * @return The channel ID
   */
  public int getChannelID() {
    return channelID;
  }
  
  /**
   * 
   * @param _dirToCode
   * @param _laneToCode
   * @param addFrom If true, sets the from direction as the opposite of the 'to'
   */
  public void setTo(int _dirToCode, int _laneToCode, boolean addFrom) {
    setToDir(_dirToCode);
    to1.lane = ChannelLaneData.fromCode(_laneToCode);
    if (addFrom) {
      from.setDir(to1.getDir().getOppositeDir());
      from.lane = to1.lane;
    }
  }
  
  public boolean hasLabel() {
    return header.SL.hasChannelLabel(this.channelID);
  }
  
  /**
   * This never returns null
   */
  public StationLabels.LabelSetForElement getLabels() {
    return header.SL.getChannelLabel(this.channelID);
  }
  
  /**
   * The global position of this channel
   */
  public final LatLongCoordinate coordinate = new LatLongCoordinate();
  
  /**
   * Information about where the counted objects are heading to.<br />
   * If describing a turning movement, the {@link DirInfo#dir dir} in this case represents the direction an observer must face to see the departing object.<br />
   * This is never null.
   */
  public final ToFromInfo to1 = new ToFromInfo();
  public final DirInfo to2 = new DirInfo();
  public final DirInfo to3 = new DirInfo();
  
  /**
   * -1 = undefined, N = 1, NW = 8, 
   * @return Code for the 2 direction
   */
  public int getToDirID() {
    return to1.getDir().getCode() + 
          (to2.dir != ChannelDirEnum.undefined ? to2.dir.getCode()*10 : 0) + 
          (to3.dir != ChannelDirEnum.undefined ? to3.dir.getCode()*100 : 0);
  }

  public boolean toIsBidirectional() {
    return to2.dir != ChannelDirEnum.undefined;
  }
  
  /**
   * Sets the to1, to2 and to3 directions
   * @param code
   */
  public void setToDir(int code) {
    to2.dir = ChannelDirEnum.undefined;
    to3.dir = ChannelDirEnum.undefined;
    if (code == 0) {
      to1.dir = ChannelDirEnum.e;
      to2.dir = ChannelDirEnum.w;
    } else if (code == 9) {
      to1.dir = ChannelDirEnum.n;
      to2.dir = ChannelDirEnum.s;
    } else if (code > 9) {
      String c = Integer.toString(code);
      to1.dir = ChannelDirEnum.fromCode(Integer.parseInt(c.substring(0, 1)));
      to2.dir = ChannelDirEnum.fromCode(Integer.parseInt(c.substring(1, 2)));
      if (c.length() > 2) {
        to3.dir = ChannelDirEnum.fromCode(Integer.parseInt(c.substring(2, 3)));
      }
    } else {
      ChannelDirEnum d = ChannelDirEnum.fromCode(code);
      to1.setDir(d);
    }
  }
  
  /**
   * Information about where the counted objects are coming from.<br />
   * If describing a turning movement, the {@link ToFromInfo#dir dir} in this case represents the direction an observer must face to see the approaching object.<br />
   * This is never null.
   */
  public final FromInfo from = new FromInfo();
  
  /**
   * Computes how this channel 'turns', based on the predominant to direction.
   * This requires both {@link #from} and {@link #to} directions to be set to one of the ordinary compass points: North through NorthWest
   * @return One of the {@link ChannelTurnEnum turning values}, or null if it a turn cannot be computed
   */
  public ChannelTurnEnum computeTurn() {
    return ChannelTurnEnum.compute(from.getDir(), to1.getDir());
  }
  
  /**
   * Computes the from and to directions based on the 'bound' direction and the turning movement
   * @param bound The direction the object is heading (which is opposite to the direction the object is approaching from)
   * @param turningMovement Which way the object turns
   */
  public void setDirectionsUsingBound(ChannelDirEnum bound, ChannelTurnEnum turningMovement) {
    from.setDir(bound.getOppositeDir());
    to1.setDir( turningMovement.compute(from.getDir(), true) );
  }
  
  public class DirInfo {

    /**
     * Never null
     */
    protected ChannelDirEnum dir = ChannelDirEnum.undefined;
    
    /**
     * 
     * @return Never null
     */
    public ChannelDirEnum getDir() {
      return dir;
    }

    public void setDir(ChannelDirEnum d) {
      this.dir = (d != null ? d : ChannelDirEnum.undefined);
    }
    
    public void copyFrom(DirInfo other) {
      setDir(other.dir);
    }
  }
  
  public class ToFromInfo extends DirInfo implements Comparable<ToFromInfo> {
    /**
     * Never null
     */
    public ChannelLaneData lane = ChannelLaneData.undefined;
    /**
     * Never null
     */
    public ChannelLaneUsageType laneType = ChannelLaneUsageType.undefined;

    /**
     * 
     * @return True if some part of this information is not undefined
     */
    public boolean isDefined() {
      return (dir != ChannelDirEnum.undefined) || (lane != ChannelLaneData.undefined) || (laneType != ChannelLaneUsageType.undefined);
    }
    
    @Override
    public int hashCode() {
      return this.laneType.ordinal() + 
             100*(this.lane.getCode()+1) + 
             10000*this.dir.ordinal();
    }
    
    public void copyFrom(ToFromInfo other) {
      this.dir = other.dir;
      this.lane = other.lane;
      this.laneType = other.laneType;
    }
    
    public boolean equals(ToFromInfo e) {
      return ((this.dir == e.dir) && (this.lane == e.lane) && (this.laneType == e.laneType));
    }

    @Override
    public int compareTo(ToFromInfo e) {
      if (this.dir == e.dir)
        return this.lane.compareTo(e.lane);
      return dir.getCode() - e.dir.getCode();
    }

    @Override
    public String toString() {
      return "Dir: " + this.dir + ", Lane: " + this.lane + ", Type: " + this.laneType;
    }
  }
  

  public class FromInfo extends ToFromInfo {
    private FromInfo() {}

    public void setDir(int code) {
      super.dir = ChannelDirEnum.fromCode(code);
    }
  }

  
  @Override
  public String[] write() {
    String[] dirInfo = new String[10];
    dirInfo[0] = Integer.toString(channelID);
    int maxPos = 1;
    if (to1.isDefined() || from.isDefined()) {
      dirInfo[1] = Integer.toString(getToDirID());
      dirInfo[2] = to1.lane.getId();
      maxPos = 3;
      dirInfo[3] = to1.laneType.getId();
      if (to1.laneType != ChannelLaneUsageType.undefined)
        maxPos = 4;
    }
    if (from.isDefined()) {
      dirInfo[4] = from.getDir().getId();
      dirInfo[5] = from.lane.getId();
      maxPos = 6;
      dirInfo[6] = from.laneType.getId();
      if (from.laneType != ChannelLaneUsageType.undefined)
        maxPos = 7;
    }
    if (coordinate.isSet()) {
      dirInfo[7] = Double.toString(coordinate.latitude);
      dirInfo[8] = Double.toString(coordinate.longitude);
      dirInfo[9] = Double.toString(coordinate.height);
      maxPos = 10;
    }
    
    String[] result = new String[maxPos];
    System.arraycopy(dirInfo, 0, result, 0, maxPos);
    return result;
  }
  
  private int assertIsInt(String value) {
    if (value == null)
      return -1;
    value = value.trim();
    if (value.length() == 0)
      return -1;
    try {
      return Integer.parseInt(value);
    } catch (Exception e) {
      throw new RuntimeException("Value must be an integer: " + value);
    }
  }

  @Override
  public void read(String[] data) {
    channelID = Integer.parseInt(data[0]);
    to1.lane   = ChannelLaneData.undefined;
    to1.laneType = ChannelLaneUsageType.undefined;
    
    if ((channelID < 0) || (channelID > MAX_CHANNELID))
      throw new ArrayIndexOutOfBoundsException(channelID);
    
    if (data.length == 1) return;
    assertIsInt(data[1]);
    
    int codeTo = ChannelDirEnum.undefined.getCode();
    try {
      codeTo = Integer.parseInt(data[1]);
    } catch (Exception expected) {}
    setToDir(codeTo);
    
    if (data.length == 2) return;
    assertIsInt(data[2]);
    
    try {
      to1.lane   = ChannelLaneData.fromCode(Integer.parseInt(data[2]));
    } catch (Exception expected) {}
    
    if (data.length == 3) return;
    assertIsInt(data[3]);
    
    try {
      to1.laneType = ChannelLaneUsageType.fromCode(Integer.parseInt(data[3]));
    } catch (Exception expected) {}
    
    if (data.length == 4) return;
    assertIsInt(data[4]);
    
    try {
      from.setDir(Integer.parseInt(data[4]));
    } catch (Exception expected) {}
    
    if (data.length == 5) return;
    assertIsInt(data[5]);
    
    try {
      from.lane = ChannelLaneData.fromCode(Integer.parseInt(data[5]));
    } catch (Exception expected) {}
    
    if (data.length == 6) return;
    assertIsInt(data[6]);
    
    try {
      from.laneType = ChannelLaneUsageType.fromCode(Integer.parseInt(data[6]));
    } catch (Exception expected) {}
    
    if (data.length == 7) return;
    
    try {
      coordinate.latitude = Double.parseDouble(data[7]);
    } catch (Exception expected) {}
    try {
      coordinate.longitude = Double.parseDouble(data[8]);
    } catch (Exception expected) {}
    try {
      coordinate.height = Double.parseDouble(data[9]);
    } catch (Exception expected) {}
  }
  
  public String toString() {
    return this.channelID + " " + this.from.dir + "[" + from.lane + "]" + " -> " + to1.dir + "[" + to1.lane + "]";
  }

  @Override
  public boolean equals(Object obj) {
    ChannelData cd = (ChannelData)obj;
    return to1.equals(cd.to1) && from.equals(cd.from);
  }

  @Override
  public int compareTo(ChannelData o) {
    int result = this.to1.compareTo(o.to1);
    if (result == 0)
      return this.from.compareTo(o.from);
    
    return result;
  }

  @Override
  public int hashCode() {
    return this.to1.hashCode()*100 + this.from.getDir().ordinal();
  }
  
  /**
   * 
   * @return A unique code for determining whether the channels' direction/lane combination makes them distinguishable from each other
   */
  public String getChannelLayoutHash() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getToDirID());
    sb.append('_');
    sb.append(this.to1.hashCode());
    sb.append('_');
    sb.append(this.from.hashCode());
    return sb.toString();
  }
  
  /*
  public void setFrom(ChannelData other) {
    this.channelID = other.channelID;
    this.from.copyFrom(other.from);
    this.to.copyFrom(other.to);
    
    this.labels.copyFrom(other.labels);
  }
  */
}
