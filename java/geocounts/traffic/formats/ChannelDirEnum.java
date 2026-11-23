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
  undefined(-1, 0, "Undefined", "?", 0) {
    @Override
    public String toHTML() {
      return "";
    }
    
    @Override
    public int getTwoWaySortingCode() {
      RawFmtUtils.throwException(this, "Unknown code", -1);
      return -1;
    }
  },
  /**
   * 1: North
   */
  n(1, 5, "North", "N", 8593),
  /**
   * 2: North East
   */
  ne(2, 6, "Northeast", "NE", 8599),
  /**
   * 3: East
   */
  e(3, 7, "East", "E", 8594) {
    @Override
    public boolean isNorthSouthOrEastWest() throws RuntimeException {
      return false;
    }
  },
  /**
   * 4: South East
   */
  se(4, 8, "Southeast", "SE", 8600),
  /**
   * 5: South
   */
  s(5, 1, "South", "S", 8595),
  /**
   * 6: South West
   */
  sw(6, 2, "Southwest", "SW", 8601),
  /**
   * 7: West
   */
  w(7, 3, "West", "W", 8592) {
    @Override
    public boolean isNorthSouthOrEastWest() throws RuntimeException {
      return false;
    }
  },
  /**
   * 8: North West
   */
  nw(8, 4, "Northwest", "NW", 8598) {
    @Override
    public int getTwoWaySortingCode() {
      return 0;
    }
  };
  
  private final int code;
  private final String label;
  private final int oppDirCode;
  public final String shortLabel;
  /**
   * https://www.w3schools.com/charsets/ref_utf_arrows.asp
   */
  public final int htmlCode;
  
  private ChannelDirEnum(int c, int oppDirC, String label, String shortLabel, int htmlCode) {
    this.code = c;
    this.oppDirCode = oppDirC;
    this.label = label;
    this.shortLabel = shortLabel;
    this.htmlCode = htmlCode;
  }
  
  public String toHTML() {
    return "&#" + htmlCode + ";";
  }

  /**
   * https://en.wikipedia.org/wiki/Miscellaneous_Symbols_and_Arrows
   * FIXME
   */
  public String toHTML(ChannelDirEnum to) {
    if (this == n) {
      if (to == e)
        return "&#" + 8629 + ";";  // U+2BA1
      if (to == w)
        return "&#x2BA0;";  // U+2BA0
      if (to == s)
        return "&#" + 8595 + ";";
    }
    if (this == e) {
      if (to == s)
        return "&#" + 8628 + ";";
      if (to == w)
        return "&#" + 8592 + ";";
    }
    if (this == s) {
      if (to == n)
        return "&#" + 8593 + ";";
    }
    if (this == w) {
      if (to == e)
        return "&#" + 8594 + ";";
    }
    
    return null;
  }

  /**
   * 
   * @return A value from -1 to 8, not including 0.
   */
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
   * @return The number of degrees from North, or Integer.MAX_VALUE if {@link #undefined}
   * @see #toDegrees()
   */
  public int getDegrees() {
    if (code < 1)
      return Integer.MAX_VALUE;
    return (code-1)*45;
  }
  
  /**
   * 
   * N = 0, NE = Pi/4, E = Pi/2, etc
   * @return Direction in radians from North, or Double.MAX_VALUE if {@link #undefined}
   */
  public double getRadians() {
    if (code < 1)
      return Double.MAX_VALUE;
    return Math.toRadians(getDegrees());
  }
  
  public static ChannelDirEnum fromCode(int value) {
    try {
      return values()[value];
    } catch (Exception ex) {
      return undefined;
    }
  }
  
  public static ChannelDirEnum fromAngle(CoordinateAngle angle) {
    return fromDegrees(angle.toDegrees());
  }
  
  public static ChannelDirEnum fromDegrees(double degrees360) {
    if (degrees360 < 0)
      degrees360 += 360;
    long code = Math.abs(Math.round(8*degrees360/360)) + 1;
    if (code > 8)
      code -= 8;
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
   * This value facilitates sorting directions into two ways.<br />
   * The {@link #undefined} will throw a RuntimeException.<br />
   * The {@link #nw} will return 0.<br />
   * All other directions will return the same value as their {@link #getCode()} value.<br />
   * Value 0, 1, 2, 3 indicates North or East. Value 4, 5, 6, 7 indicates South or West.
   * @return A value between 0 and 7.
   * @throws RuntimeException If the direction is not one of the single compass points
   */
  public int getTwoWaySortingCode() throws RuntimeException {
    return getCode();
  }
  
  /**
   * Determines whether this direction is predominantly North and East (true) or South and West (false)
   * @return True if it points either {@link #n North} or {@link #e East}, false otherwise
   * @throws RuntimeException If the direction is {@link #undefined}
   */
  public boolean isNorthOrEast() throws RuntimeException {
    return (getTwoWaySortingCode() < 4); // Northwest to East
  }
  
  /**
   * 
   * @return True if the code is NW, N, NE, SE, S or SW and False if the code is E or W
   * @throws RuntimeException
   */
  public boolean isNorthSouthOrEastWest() throws RuntimeException {
    return true;
  }
  
  /**
   * 
   * @return The opposite {@link ChannelDirEnum direction}, never null
   */
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
   * @throws RuntimeException If its not a valid compass direction: North through Northwest
   */
  public int toDegrees() throws RuntimeException {
    if (code == -1)
      RawFmtUtils.throwException(this, "Invalid direction code", code);
    return (code-1)*45;
  }
  
  public static ChannelDirEnum[] getClockwise(ChannelDirEnum starting) {
    ChannelDirEnum[] result = new ChannelDirEnum[8];
    int count = 0;
    
    for (int degrees360=starting.getDegrees(); count<8; degrees360 += 45) {
      if (degrees360 > 360)
        degrees360 -= 360;
      result[count] = fromDegrees(degrees360);
      count++;
    }
    return result;
  }
  
  public static ChannelDirEnum[] getCounterClockwise(ChannelDirEnum starting) {
    ChannelDirEnum[] result = new ChannelDirEnum[8];
    int count = 0;
    
    for (int degrees360=starting.getDegrees(); count<8; degrees360 -= 45) {
      if (degrees360 < 0)
        degrees360 += 360;
      result[count] = fromDegrees(degrees360);
      count++;
    }
    return result;
  }
  
  public static ChannelDirEnum[] getNtoW() {
    return new ChannelDirEnum[] {ChannelDirEnum.n, ChannelDirEnum.e, ChannelDirEnum.s, ChannelDirEnum.w};
  }
  
  public CoordinateAngle getAngle() {
    if (code < 1)
      return null;
    return new CoordinateAngle(Math.toRadians(getDegrees()));
  }
}
