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
 * Geographical object type
 * @since 1.323
 *
 */
public enum GeoType implements IdDesc {
  /**
   * A count station
   */
  station,
  
  /**
   * A region<br />Eg a county
   * @see StationLabelElementField#regionname
   */
  region,
  
  /**
   * A route<br />Eg I-35
   * @see StationLabelElementField#pathid
   */
  route,
  
  /**
   * A city<br />Eg Austin
   * @see StationLabelElementField#urbanareaname
   */
  city() {
    @Override
    public String getPluralTerm() {
      return "cities";
    }
  },
  
  /**
   * A traffic segment
   * @see StationLabelElementField#pathlrs
   */
  segment,
  
  /**
   * A non-descript grouping of objects
   * @see StationLabelElementField#groupname
   */
  group;
  
  public String getPluralTerm() {
    return name() + "s";
  }

  @Override
  public String getId() {
    return name();
  }

  @Override
  public String getDescription() {
    return name();
  }
}
