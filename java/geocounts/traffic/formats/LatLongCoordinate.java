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
 * 
 * @see StationGPS
 * @see StationShape
 */
public class LatLongCoordinate implements java.io.Serializable {
  private static final long serialVersionUID = 1L;

  public LatLongCoordinate() {
  }

  public LatLongCoordinate(double[] latLng) {
    setLatLng(latLng);
  }

  /**
   * Must be between -90 and 90
   */
  public double latitude;
  /**
   * Must be between -180 and 180
   */
  public double longitude;
  
  public double[] toLatLngHeight() {
    return new double[]{latitude, longitude, height};
  }
  
  public double[] toLngLat() {
    return new double[]{longitude, latitude};
  }
  
  public void setLatLng(double lat, double lng) {
    latitude = lat;
    longitude = lng > 180 ? -(360 - lng) : lng;
  }
  
  /**
   * Safely sets the latitude and longitude
   */
  public void setLatLng(double[] latLng) {
    setLatLng(latLng[0], latLng[1]);
    if (latLng.length > 2)
      height = latLng[2];
  }
  
  
  /**
   * Height is in meters
   */
  public double height;
  
  public boolean isSet() {
    if ((latitude > 90) || (latitude < -90))
      return false;
    return (latitude != 0.0) && (longitude != 0.0);
  }
  
  public void setFrom(LatLongCoordinate other) {
    this.latitude  = other.latitude;
    this.longitude = other.longitude;
    this.height    = other.height;
  }
  
  public boolean equals(LatLongCoordinate other) {
    return (this.latitude == other.latitude) && (this.longitude == other.longitude) && (this.height == other.height);
  }

  public void clear() {
    this.latitude  = 0;
    this.longitude = 0;
    this.height    = 0;
  }

  @Override
  public String toString() {
    return this.latitude + " " + this.longitude;
  }
}
