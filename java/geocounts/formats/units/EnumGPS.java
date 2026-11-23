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
   * Lat, long, altitude and direction
   */
  llh() {

    @Override
    public void parse(String[] data, LatLongCoordinate result) {
      result.setLatLng(data[0] != null ? Double.parseDouble(data[0]) : 0,
                       data[1] != null ? Double.parseDouble(data[1]) : 0);
      if (data.length > 2)
        result.altitude = data[2] != null ? Double.parseDouble(data[2]) : 0;
      if (data.length > 3)
        result.direction = data[3] != null ? Double.parseDouble(data[3]) : 0;
    }

    @Override
    public String[] format(LatLongCoordinate point) {
      return new String[]{Double.toString(point.getLatitude()), 
                          Double.toString(point.getLongitude()), 
                          Double.toString(point.altitude), 
                          Double.toString(point.direction)};
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
      OpenLocationCode olc = new OpenLocationCode();
      if (data[0] != null)
        olc.parse(data[0], result);
    }

    @Override
    public String[] format(LatLongCoordinate point) {
      OpenLocationCode olc = new OpenLocationCode();
      String[] result = new String[3];
      result[0] = olc.format(point);
      return result;
    }
    
  };
  
  public abstract void parse(String[] data, LatLongCoordinate result);

  public abstract String[] format(LatLongCoordinate point);
}
