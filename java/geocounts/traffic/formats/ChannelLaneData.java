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
 * -1 = undefined<br />
 * 0 = combined road lanes. Examples: multiple lanes in the same direction, or N/S or E/W combined<br />
 * 1 - 31 = Individual lanes<br />
 */
public abstract class ChannelLaneData implements IdDesc, Comparable<ChannelLaneData> {
  public static ChannelLaneData undefined = new UndefinedLane();
  public static ChannelLaneData combined  = new CombinedLanes();
  
  public static ChannelLaneData fromString(String value) {
    return fromCode(Integer.parseInt(value));
  }
  
  public static ChannelLaneData fromCode(int value) {
    if (value <= UndefinedLane.CODE)
      return undefined;
    if (value == CombinedLanes.CODE)
      return combined;
    return new NormalLane(value);
  }
  
  @Override
  public String toString() {
    return Integer.toString(getCode());
  }
  
  public abstract int getCode();

  @Override
  public String getId() {
    return toString();
  }
  
  public boolean equals(int value) {
    return getCode() == value;
  }
  
  @Override
  public int compareTo(ChannelLaneData value) {
    return this.getCode() - value.getCode();
  }
  
}

class UndefinedLane extends ChannelLaneData {
  protected static final int CODE = -1;
  @Override
  public int getCode() {
    return CODE;
  }

  @Override
  public String getDescription() {
    return "Undefined";
  }
}

class CombinedLanes extends ChannelLaneData {
  protected static final int CODE = 0;
  @Override
  public int getCode() {
    return CODE;
  }

  @Override
  public String getDescription() {
    return "Combined lanes";
  }
}

class NormalLane extends ChannelLaneData {
  private final int CODE;
  protected NormalLane(int id) {
    CODE = id;
  }
  
  @Override
  public int getCode() {
    return CODE;
  }

  @Override
  public String getDescription() {
    return "Lane " + CODE;
  }
}
