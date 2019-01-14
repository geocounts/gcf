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
 * Intended Usage of a physical lane which the Channel counts
 *
 */
public enum ChannelLaneUsageType implements IdDesc {
  undefined("Undefined"),
  /**
   * Parking lot, field, park, etc
   */
  none("Area"),
  /**
   * Always used by motorized vehicles, frequently includes bicycles, and rarely shared with pedestrians.
   */
  road("Road lane"),
  /**
   * Intended for use by bicycles but not-motorized vehicles or pedestrians.
   * This may be on the road surface or separated.
   */
  bikelane("Bike lane"),
  /**
   * Intended for use by non-motorized traffic to travel parallel to a road.
   * It may also describe a road underpass or overpass.
   * If the space usage is shared, such as commonly found at sporting venues, schools, etc, use 1 (Road lane) instead.
   */
  sidewalk("Sidewalk or footpath"),
  /**
   * Any sort of crossing on a road for non-motorized traffic. 
   * For example a zebra crossing, school crossing with crossing guard, crossing at an intersection, crossing not at an intersection, etc.
   * A pedestrian-only underpass or overpass should use 3.
   */
  crossing("Road crossing"),
  /**
   * Typically for trams, trolley cars or monorails
   */
  lightrail("Light rail tracks"),
  /**
   * Typically for trains
   */
  heavyrail("Heavy rail tracks"),
  /**
   * Used by boats
   */
  river("River"),
  /**
   * Often found at airports
   */
  walkway("Moving walkway"),
  
  elevator("Elevator"),
  /**
   * Use lane numbers as follows:<ul>
   * <li>1 to show objects are entering the count station via an up escalator</li>
   * <li>2 to show objects are exiting the count station via an up escalator</li>
   * <li>3 to show objects are entering the count station via a down escalator</li>
   * <li>4 to show objects are exiting the count station via a down escalator</li>
   * </ul>
   */
  escalator("Escalator"),
  /**
   * For pedestrians to pass through, such as is typically found at the entrance of subway stations.
   * Use lane number = 1 to show objects are entering the count station or lane number = 2 to show objects are exiting the count station
   */
  turnstyle("Turnstyle"),
  /**
   * Use lane number = 1 to show objects are entering the count station or lane number = 2 to show objects are exiting the count station
   */
  door("Entrance/exit");
  
  private String desc;
  private ChannelLaneUsageType(String d) {
    desc = d;
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
}
