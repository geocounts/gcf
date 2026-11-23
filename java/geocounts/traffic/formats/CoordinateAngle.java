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
 * An angle, where 0 degrees is due North.
 * @see com.vaultagemedia.math.Geodesy.GeodesyAngle
 */
public class CoordinateAngle implements Comparable<CoordinateAngle> {
  
  /**
   * 0 means North
   */
  private double radians;
  
  public CoordinateAngle(double r) {
    radians = r;
  }
  
  public static CoordinateAngle createFromXY(double x, double y) {
    CoordinateAngle result = new CoordinateAngle(0);
    result.setFromXY(x, y);
    return result;
  }
  
  public static CoordinateAngle createFromXYPoint(XYPoint p) {
    CoordinateAngle result = new CoordinateAngle(0);
    result.setFromXY(p.x, p.y);
    return result;
  }
  
  public static CoordinateAngle createFromDegrees(double degrees) {
    return new CoordinateAngle(Math.toRadians(degrees));
  }
  
  public double getArea(double radius) {
    double totalArea = Math.PI*radius*radius;
    return toFraction()*totalArea;
  }

  /**
   * Copied from https://stackoverflow.com/questions/46590154/calculate-bearing-between-2-points-with-javascript
   */
  public static CoordinateAngle create(LatLongCoordinate start, LatLongCoordinate dest) {
    if (start.equalsLatLong(dest))
      return new CoordinateAngle(0);
    double startLat = Math.toRadians(start.getLatitude());
    double startLng = Math.toRadians(start.getLongitude());
    double destLat  = Math.toRadians(dest.getLatitude());
    double destLng  = Math.toRadians(dest.getLongitude());

    double y = Math.sin(destLng - startLng) * Math.cos(destLat);
    double x = Math.cos(startLat) * Math.sin(destLat) -
               Math.sin(startLat) * Math.cos(destLat) * Math.cos(destLng - startLng);
    double brngRad = Math.atan2(y, x);
    return new CoordinateAngle(brngRad);
  }
  
  
  
  /**
   * 0 means North. PI/2 means East. PI means South. 3*PI/2 means West
   */
  public double getRadians() {
    return radians;
  }
  
  public void setRadians(double value) {
    radians = value;
  }
  
  public void setDegrees(double value) {
    setRadians(Math.toRadians(value));
  }
  
  public boolean isAcute() {
    return toFraction() < 0.25;
  }
  
  public boolean isObtuse() {
    return toFraction() > 0.25;
  }
  
  /**
   * 
   * @return Number of radians, counter-clockwise from due East (pointing along cartesian X axis)
   */
  public double getRadiansUsingEngineeringCoordinates() {
    return Math.PI/2 - radians;
  }
  
  /**
   * When this is used to represent a compass, 0 means North. 90 means East. 180 means South. 270 means West
   */
  public double toDegrees() {
    double brngDeg = Math.toDegrees(radians);
    return (brngDeg + 360) % 360;
  }
  
  public double toFraction() {
    return Math.abs(toDegrees()/360d);
  }
  
  public void setFromXY(double x, double y) {
    if ((y == 0) && (x == 0)) {
      radians = 0;
      return;
    }
    double a = Math.atan2(y, x);
    radians = Math.PI/2 - a;
  }
  
  public void setFromXYPoint(XYPoint p) {
    setFromXY(p.x, p.y);
  }
  
  public void setFromAngleBetween(LatLongCoordinate point1, LatLongCoordinate point2) {
    CoordinateAngle c = create(point1, point2);
    this.radians = c.radians;
  }
  
  /**
   * Add radians in the clockwise direction.
   * @param radsCW Radians clockwise
   */
  public void addRadians(double radsCW) {
    final double TWO_PI = 2*Math.PI;
    radians += radsCW;
    if (radians >= TWO_PI)
      radians -= TWO_PI;
    else if (radians <= -TWO_PI)
      radians += TWO_PI;
  }
  
  /**
   * 
   * @param countClockwise Number of quarter turns clockwise
   */
  public void addQuarterTurns(int countClockwise) {
    addRadians(countClockwise*Math.PI/2);
  }
  
  /**
   * Add a 90 degree turn, clockwise
   */
  public void addClockwiseQuarterTurn() {
    addQuarterTurns(1);
  }
  
  /**
   * 
   * @param startPoint A start {@link LatLongCoordinate point}
   * @param distanceM Distance in meters
   * @return A new {@link LatLongCoordinate point} computed based on a) the start point, b) the given distance, c) this angle
   */
  public LatLongCoordinate getPoint(LatLongCoordinate startPoint, double distanceM) {
    return CoordinateObjectComputation.getCoordinate(startPoint, this.toDegrees(), distanceM);
  }

  @Override
  public int compareTo(CoordinateAngle other) {
    return Double.compare(this.radians, other.radians);
  }
  
  public CoordinateAngle getDifference(CoordinateAngle another) {
    return getDifferenceFromRadians(another.radians);
  }
  
  public CoordinateAngle getDifferenceFromRadians(double anotherRadians) {
    return new CoordinateAngle(anotherRadians - this.radians);
  }
  
  @Override
  public String toString() {
    return Double.toString(radians);
  }
  
  /**
   * @see geocounts.traffic.network.math.OrthogonalNudge
   */
  public CoordinateAngle getMidpointPositive(CoordinateAngle another) {
    return getMidpoint(another, true);
  }
  
  public CoordinateAngle getMidpointNegative(CoordinateAngle another) {
    return getMidpoint(another, false);
  }
  
  public CoordinateAngle getMidpoint(CoordinateAngle another, boolean positiveOrNegative) {
    CoordinateAngle result = new CoordinateAngle(this.radians);
//System.out.println("Head from " + (int)result.toDegrees() + " Deg, then turn to " + (int)another.toDegrees() + " Deg");
    result.addRadians(-Math.PI);
//System.out.println("About face 180 = " + (int)result.toDegrees() + " Deg");
    CoordinateAngle diff = result.getDifference(another);
//System.out.println("Diff is " + (int)diff.toDegrees());
    diff.radians /= 2.0;
//System.out.println("Half Diff is " + (int)diff.toDegrees());
    if (positiveOrNegative)
      result.addRadians(diff.radians);
    else
      result.addRadians(diff.radians + Math.PI);
//System.out.println("Half way is " + (int)result.toDegrees());
    return result;
  }
}
