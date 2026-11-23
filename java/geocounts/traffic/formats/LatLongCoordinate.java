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
 * @see CoordinateObject
 * @see StationGPS
 * @see StationShape
 */
public class LatLongCoordinate implements java.io.Serializable, CoordinateObject {
  private static final long serialVersionUID = 1L;

  public LatLongCoordinate() {
  }

  public LatLongCoordinate(double[] latLng) {
    setLatLng(latLng);
  }
  
  public LatLongCoordinate(double lat, double lng) {
    setLatLng(lat, lng);
  }

  /**
   * Must be between -90 and 90
   */
  public double latitude;
  
  public double getLatitude() {
    return latitude;
  }
  
  public void setLatitude(double value) {
    latitude = value;
    if (latitude > 90)
      latitude = (180 - latitude);
  }
  
  /**
   * Must be between -180 and 180
   */
  public double longitude;
  
  public double getLongitude() {
    return longitude;
  }
  
  public double[] toLatLngHeight() {
    return new double[]{latitude, longitude, altitude};
  }
  
  public double[] toLngLat() {
    return new double[]{longitude, latitude};
  }
  
  public void setLatLng(double lat, double lng) {
    latitude = lat;
    setLongitude(lng);
  }
  
  public void setLongitude(double lng) {
    longitude = lng > 180 ? -(360 - lng) : lng;
  }
  
  /**
   * Safely sets the latitude and longitude
   */
  public void setLatLng(double[] latLng) {
    setLatLng(latLng[0], latLng.length > 1 ? latLng[1] : 0);
    altitude = 0;
    if (latLng.length > 2)
      altitude = latLng[2];
    if (latLng.length > 3)
      direction = latLng[3];
  }
  
  
  /**
   * Altitude is in meters
   */
  public double altitude;
  
  /**
   * A direction facing (or traveling), in radians. 
   * N = 0, NE = Pi/4, E=Pi/2 etc
   * @since 1.506
   */
  public double direction;
  
  /**
   * The direction facing (or traveling), in degrees.
   * N = 0, NE = 45, E=90 etc
   */
  public int directionDegrees() {
    return (int)Math.rint(direction*180d/Math.PI);
  }
  
  /**
   * 
   * @param angDeg 0 to 360
   */
  public void setDirectionDegrees(int angDeg) {
    direction = Math.toRadians(angDeg);
  }
  
  public void setDirection(ChannelDirEnum e) {
    direction = 0;
    if (e != ChannelDirEnum.undefined)
      direction = e.getRadians();
  }
  
  public boolean isSet() {
    if ((latitude > 90) || (latitude < -90))
      return false;
    return (latitude != 0.0) && (longitude != 0.0);
  }
  
  public void setFrom(LatLongCoordinate other) {
    this.latitude  = other.latitude;
    this.longitude = other.longitude;
    this.altitude  = other.altitude;
    this.direction = other.direction;
  }
  
  public boolean equals(LatLongCoordinate other) {
    return equalsLatLong(other) && (this.altitude == other.altitude);
  }
  
  public boolean equalsLatLong(LatLongCoordinate other) {
    return (this.latitude == other.latitude) && (this.longitude == other.longitude);
  }

  public void clear() {
    this.latitude  = 0;
    this.longitude = 0;
    this.altitude  = 0;
    this.direction = 0;
  }
  
  /**
   * This does not check whether either points are set correctly
   * @return Distance in meters to the other point
   */
  public double getDistanceTo(LatLongCoordinate otherPoint) {
    return CoordinateObjectComputation.getGreatCircleDistance(this.latitude, this.longitude, otherPoint.latitude, otherPoint.longitude);
  }

  public void moveTo(ChannelDirEnum dir, double meters) {
    CoordinateObjectComputation.moveCoordinateByBearing(this, dir.getDegrees(), meters);
  }

  @Override
  public String toString() {
    return this.latitude + " " + this.longitude;
  }

  @Override
  public LatLongCoordinate getCoordinate() {
    return this;
  }

  public LatLongCoordinate getMidPoint(LatLongCoordinate other) {
    LatLongCoordinate result = new LatLongCoordinate();
    result.latitude  = (this.latitude  + other.latitude)/2d;
    result.longitude = (this.longitude + other.longitude)/2d;
    result.altitude  = (this.altitude  + other.altitude)/2d;
    result.direction = (this.direction + other.direction)/2d;
    return result;
  }
}
