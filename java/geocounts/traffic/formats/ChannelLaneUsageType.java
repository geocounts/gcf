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
import java.util.Collection;

/**
 * Intended Usage of a physical lane which the Channel counts
 * Do not alter, insert or delete any members. To add a new member, add at the bottom
 * @see ChannelLaneData
 */
public enum ChannelLaneUsageType implements IdDesc {
  undefined("Undefined", false),
  /**
   * Parking lot, field, park, etc
   */
  none("Area", true),
  /**
   * Always used by motorized vehicles, frequently includes bicycles, and rarely shared with pedestrians.
   */
  road("Road lane", false),
  /**
   * Intended for use by bicycles but not-motorized vehicles or pedestrians.
   * This may be on the road surface or separated.
   */
  bikelane("Bike lane", true),
  /**
   * Intended for use by non-motorized traffic to travel parallel to a road.
   * It may also describe a road underpass or overpass.
   * If the space usage is shared, such as commonly found at sporting venues, schools, etc, use 1 (Road lane) instead.
   */
  sidewalk("Sidewalk or footpath", true),
  /**
   * Any sort of crossing on a road for non-motorized traffic. 
   * For example a zebra crossing, school crossing with crossing guard, crossing at an intersection, crossing not at an intersection, etc.
   * A pedestrian-only underpass or overpass should use 3.
   */
  crossing("Road crossing", true),
  /**
   * Typically for trams, trolley cars or monorails
   */
  lightrail("Light rail tracks", false),
  /**
   * Typically for trains
   */
  heavyrail("Heavy rail tracks", false),
  /**
   * Used by boats
   */
  river("River", false),
  /**
   * Often found at airports
   */
  walkway("Moving walkway", true),
  /**
   * Elevator
   */
  elevator("Elevator", true),
  /**
   * Use lane numbers as follows:<ul>
   * <li>1 to show objects are entering the count station via an up escalator</li>
   * <li>2 to show objects are exiting the count station via an up escalator</li>
   * <li>3 to show objects are entering the count station via a down escalator</li>
   * <li>4 to show objects are exiting the count station via a down escalator</li>
   * </ul>
   */
  escalator("Escalator", true),
  /**
   * For pedestrians to pass through, such as is typically found at the entrance of subway stations.
   * Use lane number = 1 to show objects are entering the count station or lane number = 2 to show objects are exiting the count station
   */
  turnstyle("Turnstyle", true),
  /**
   * Use lane number = 1 to show objects are entering the count station or lane number = 2 to show objects are exiting the count station
   */
  door("Entrance/exit", true);
  
  public final boolean hasPeds;
  private String desc;
  private ChannelLaneUsageType(String d, boolean hasPeds) {
    desc = d;
    this.hasPeds = hasPeds;
  }
  
  @Override
  public final String getId() {
    return Integer.toString(getCode());
  }

  @Override
  public String getDescription() {
    return desc;
  }
  
  public int getCode() {
    return ordinal()-1;
  }
  
  /**
   * 
   * @param code Must be -1 or more
   * @return The {@link ChannelLaneUsageType}
   */
  public static ChannelLaneUsageType fromCode(int code) {
    return values()[code+1];
  }
  
  public static Collection<ChannelLaneUsageType> withPeds() {
    ArrayList<ChannelLaneUsageType> result = new ArrayList<ChannelLaneUsageType>();
    for (ChannelLaneUsageType c: values())
      if (c.hasPeds)
        result.add(c);
    return result;
  }
}
