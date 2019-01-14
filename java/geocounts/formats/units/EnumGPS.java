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
package geocounts.formats.units;

import geocounts.traffic.formats.LatLongCoordinate;

/**
 * 
 * @since 1.28
 *
 */
public enum EnumGPS {
  /**
   * Lat long height
   */
  llh() {

    @Override
    public void parse(String[] data, LatLongCoordinate result) {
      result.latitude  = data[0] != null ? Double.parseDouble(data[0]) : 0;
      result.longitude = data[1] != null ? Double.parseDouble(data[1]) : 0;
      if (data.length > 2)
        result.height = data[2] != null ? Double.parseDouble(data[2]) : 0;
    }

    @Override
    public String[] format(LatLongCoordinate point) {
      return new String[]{Double.toString(point.latitude), 
                          Double.toString(point.longitude), 
                          Double.toString(point.height)};
    }
    
  },
  
  /** 
   * The Open Location Code system is based on latitudes and longitudes in WGS84 coordinates
   * @see <a href="https://plus.codes/">Plus.codes</a>
   * @see <a href="https://en.wikipedia.org/wiki/Open_Location_Code">Open Location Code Wiki</a>
   * @see <a href="http://openlocationcode.com/">Openlocationcode.com</a>
   * @since 1.28
   */
  olc() {
    
    @Override
    public void parse(String[] data, LatLongCoordinate result) {
// DIGIT  2  3  4  5  6  7  8  9  C  F  G  H  J  M  P  Q  R  V  W  X
// ASCII 50                      67                            87 88
      if (true) {
        
      }
    }

    @Override
    public String[] format(LatLongCoordinate point) {
      String[] result = new String[3];
      return result;
    }
    
  };
  
  public abstract void parse(String[] data, LatLongCoordinate result);

  public abstract String[] format(LatLongCoordinate point);
}
