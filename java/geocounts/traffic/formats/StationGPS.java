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
public class StationGPS extends RawTrafficDataHeaderElement implements CoordinateObject {
  /**
   * @since 1.12
   */
  public final LatLongCoordinate point = new LatLongCoordinate();
  
  @Override
  public LatLongCoordinate getCoordinate() {
    return point;
  }
  
  public void setLatLng(double[] latlng) {
    point.setLatLng(latlng[0], latlng[1]);
    point.altitude  = latlng.length > 2 ? latlng[2] : 0;
    point.direction = latlng.length > 3 ? latlng[3] : 0;
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
    result.put("SG_latitude",  this.point.getLatitude());
    result.put("SG_longitude", this.point.getLongitude());
    result.put("SG_altitude",  this.point.altitude);
    result.put("SG_direction", this.point.direction);
  }

  @Override
  public void read(String[] data) {
    clear();
    if (data == null)
      return;
    EnumGPS.llh.parse(data, point);
  }

  @Override
  protected String[] write() {
    // dont write invalid point
    if ((Math.abs(point.getLatitude()) > 90) || (Math.abs(point.getLongitude()) > 180))
      return null;
    // dont write a point that is not set
    if ((point.getLatitude() == 0) && (point.getLongitude() == 0))
      return null;
    return EnumGPS.llh.format(point);
  }

  @Override
  public void clear() {
    point.clear();
  }
}
