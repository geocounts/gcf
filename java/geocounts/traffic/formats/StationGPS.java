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

import java.util.Map;

import geocounts.formats.units.EnumGPS;

/**
 * Represents latitude, longitude and height of the station record, stored in the SG header
 * @see LatLongCoordinate
 * @see EnumHeaderRecords#SG
 * @see StationShape
 */
public class StationGPS extends RawTrafficDataHeaderElement {
  /**
   * @since 1.12
   */
  public final LatLongCoordinate point = new LatLongCoordinate();
  
  public void setLatLng(double[] latlng) {
    point.latitude = latlng[0];
    point.longitude = latlng[1];
    point.height = latlng.length > 2 ? latlng[2] : 0;
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.SG;
  }
  
  public boolean isSet() {
    return point.isSet();
  }
  
  public void setFrom(StationGPS other) {
    this.point.setFrom(other.point);
  }
  
  /**
   * 
   * @since 1.9944
   */
  public void toMap(Map<String, Object> result) {
    result.put("SG_latitude", this.point.latitude);
    result.put("SG_longitude", this.point.longitude);
    result.put("SG_height", this.point.height);
  }

  @Override
  public void read(String[] data) {
    if (data == null)
      return;
    
    point.clear();
    EnumGPS.llh.parse(data, point);
  }

  @Override
  protected String[] write() {
    if (!point.isSet() || (Math.abs(point.latitude) > 90) || (Math.abs(point.longitude) > 180))
      return null;
    return EnumGPS.llh.format(point);
  }

}
