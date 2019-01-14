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
 * Direction codes for a {@link ChannelData channel} header record
 * @see ChannelData
 * @see ChannelTurnEnum
 */
public enum ChannelDirEnum implements IdDesc {
  /**
   * Direction not yet known
   */
  undefined(-1, -1, "Undefined", "?") {
    @Override
    public ChannelDirEnum getOppositeDir() {
      return undefined;
    }
  },
  /**
   * 1: North
   */
  n(1, 5, "North", "N"),
  /**
   * 2: North East
   */
  ne(2, 6, "Northeast", "NE"),
  /**
   * 3: East
   */
  e(3, 7, "East", "E"),
  /**
   * 4: South East
   */
  se(4, 8, "Southeast", "SE"),
  /**
   * 5: South
   */
  s(5, 1, "South", "S"),
  /**
   * 6: South West
   */
  sw(6, 2, "Southwest", "SW"),
  /**
   * 7: West
   */
  w(7, 3, "West", "W"),
  /**
   * 8: North West
   */
  nw(8, 4, "Northwest", "NW");
  
  private final int code;
  private final String label;
  private final int oppDirCode;
  public final String shortLabel;
  private ChannelDirEnum(int c, int oppDirC, String label, String shortLabel) {
    this.code = c;
    this.oppDirCode = oppDirC;
    this.label = label;
    this.shortLabel = shortLabel;
  }
  
  public int getCode() {
    return code;
  }

  @Override
  public String getId() {
    return Integer.toString(code);
  }

  @Override
  public String getDescription() {
    return label;
  }
  
  /**
   * North is 0 degrees, East is 90 degrees, South is 180 degrees, West is 270 degrees.
   * Any direction thats not a simple compass value will return Integer.MAX_VALUE
   * @return The number of degrees from North, Integer.MAX_VALUE
   */
  public int getDegrees() {
    if (code < 1)
      return Integer.MAX_VALUE;
    return (code-1)*45;
  }
  
  public static ChannelDirEnum fromCode(int value) {
    try {
      return values()[value];
    } catch (Exception ex) {
      return undefined;
    }
  }
  
  public static ChannelDirEnum fromDegrees(double degrees360) {
    long code = Math.abs(Math.round(8*degrees360/360)) + 1;
    return fromCode((int)code);
  }
  
  public static ChannelDirEnum fromLabel(String l) {
    for (ChannelDirEnum e: ChannelDirEnum.values()) {
      if (e.label.equalsIgnoreCase(l))
        return e;
    }
    return ChannelDirEnum.undefined;
  }
  
  /**
   * Determines whether this direction is predominantly North and East (true) or South and West (false)
   * @return True if it points either {@link #n North} or {@link #e East}, false otherwise
   * @throws Exception If the direction is not one of the single compass points
   */
  public boolean isNorthOrEast() throws RuntimeException {
    if ((code >= 4) && (code <= 7))
      return false;
    if ((code == 8) || ((code >= 1) && (code <= 3)))
      return true;

    throw new RuntimeException("Unknown code " + code);
  }
  
  public ChannelDirEnum getOppositeDir() {
    return values()[oppDirCode];
  }
  
  public ChannelDirEnum getRotatedDir(int numberOf45Degrees) {
    if ((this.code < 1) || (this.code > 8))
      return null;
    int resultID = (this.code + numberOf45Degrees) % 8;
    
    if (resultID < 1) 
      resultID += 8;
    return values()[resultID];
  }
  
  /**
   * 
   * @return Degree value between 0 (North) and 315 (Northwest)
   * @throws Exception If its not a valid compass direction: North through Northwest
   */
  public int toDegrees() throws Exception {
    if (code == -1)
      RawFmtUtils.throwException(this, "Invalid direction code", code);
    return (code-1)*45;
  }
}
