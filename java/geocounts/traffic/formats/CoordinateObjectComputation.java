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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @see XYPoint
 * @see CoordinateObject
 */
public class CoordinateObjectComputation {
  
  public static CoordinateAngle getAngle(LatLongCoordinate point1, LatLongCoordinate point2) {
    return CoordinateAngle.create(point1, point2);
  }
  
  public static double getGreatCircleDistance(CoordinateObject point1, CoordinateObject point2) {
    return getGreatCircleDistance(point1.getCoordinate(), point2.getCoordinate());
  }
  
  public static double getGreatCircleDistance(LatLongCoordinate point1, LatLongCoordinate point2) {
    return getGreatCircleDistance(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude());
  }

  /**
   * Calculate the distance between two points across the Earth's surface.
   * @return Distance in meters
   * @see https://en.wikipedia.org/wiki/Great-circle_distance#Computational_formulas
   * @see com.vaultagemedia.math.Geodesy
   */
  public static double getGreatCircleDistance(double lat1, double lng1, double lat2, double lng2) {
    if ((lat1 == lat2) && (lng1 == lng2)) {
      return 0;
    }

    double lat1_radians = Math.toRadians(lat1);
    double lat2_radians = Math.toRadians(lat2);
    double lon1_radians = Math.toRadians(lng1);
    double lon2_radians = Math.toRadians(lng2);

    double Distanceradians = Math.acos(Math.sin(lat1_radians)*Math.sin(lat2_radians) +
                             Math.cos(lat1_radians)*Math.cos(lat2_radians)*Math.cos(lon2_radians-lon1_radians));

    return 1852*60d*Math.toDegrees(Distanceradians);
  }
  
  /**
   * @return Distance in meters
   */
  public static double getYDistanceLatitude(double change_in_lat) {
    return 110574*change_in_lat;
  }
  
  /**
   * @return Distance in meters
   */
  public static double getXDistanceLongitude(double current_latitude, double change_in_lng) {
    double KM = 40075 * Math.cos(Math.toRadians(current_latitude)) / 360;
    return change_in_lng*KM*1000;
  }
  
  public static XYPoint LatLongToXY(LatLongCoordinate origin, LatLongCoordinate destLatLng) {
    XYPoint result = new XYPoint();
    result.y = getYDistanceLatitude(destLatLng.getLatitude() - origin.getLatitude());
    result.x = getXDistanceLongitude(origin.getLatitude(), destLatLng.getLongitude() - origin.getLongitude());
    return result;
  }

  public static Predicate<LatLongCoordinate> createInsidePolygonTest(List<? extends LatLongCoordinate> polygon) {
    return new IsCoordinateInside(polygon); 
  }
  
  public static class IsXYPointInside extends XYPointList {
    private static final long serialVersionUID = 1L;
    
    final PointInPolygon pip = new PointInPolygon();

    public boolean test(XYPoint xy) {
      return pip.isInside(xy, this);
    }
  }
  
  
  public static class IsCoordinateInside extends IsXYPointInside implements Predicate<LatLongCoordinate>, CoordinateObject {

    private static final long serialVersionUID = 1L;
    
    private final LatLongCoordinate centroid;
    IsCoordinateInside(List<? extends LatLongCoordinate> polygon) {
      centroid = CoordinateObjectComputation.getCentroid(polygon);
      for (LatLongCoordinate ll: polygon) {
        XYPoint xy = CoordinateObjectComputation.LatLongToXY(centroid, ll);
        add(xy);
      }
    }
    
    @Override
    public boolean test(LatLongCoordinate ll) {
      XYPoint xy = CoordinateObjectComputation.LatLongToXY(centroid, ll);
      return super.test(xy);
    }

    @Override
    public LatLongCoordinate getCoordinate() {
      return centroid;
    }
    
  }
  
  /**
   * Given an existing lat and long, calculate another lat and long given a bearing and distance.
   * Useful for working out where a vehicle will be next given its speed and direction
   * @param startingPoint
   * @param bearingDegrees degrees from North. 0 = due north, 90 = due east, 270 = due west
   * @param distanceM In meters
   * @return {@link LatLongCoordinate Lat and Long}
   */
  public static void moveCoordinateByBearing(LatLongCoordinate pointToMove, double bearingDegrees, double distanceM) {
    computeCoordinateByBearing(pointToMove, pointToMove, bearingDegrees, distanceM);
  }
  
  public static LatLongCoordinate getCoordinate(LatLongCoordinate startingPoint, double bearingDegrees, double distanceM) {
    LatLongCoordinate result = new LatLongCoordinate();
    computeCoordinateByBearing(startingPoint, result, bearingDegrees, distanceM);
    return result;
  }
  
  private static void computeCoordinateByBearing(LatLongCoordinate startingPoint, LatLongCoordinate result, double bearingDegrees, double distanceM) {
    double distanceEastWest   = distanceM * Math.sin(Math.toRadians(bearingDegrees));
    double distanceNorthSouth = distanceM * Math.cos(Math.toRadians(bearingDegrees));

    double latInc = 90d/10000000d * distanceNorthSouth;
    double longInc = degreesLongitude(startingPoint.getLatitude()) * distanceEastWest;
    result.setLatitude(startingPoint.getLatitude() + latInc);
    result.setLongitude(startingPoint.getLongitude() + longInc);
  }
  
  /**
   * <p>
   * This is used to separate a single road centerline into two directional paths.
   * Use this when we want to compute where both carriage-ways are located but you only have a single center line.
   * </p>
   * <p>
   * It needs 3 points. Therefore to compute the full directional path, start with the first 3 points [p1, p2, p3], 
   * then increment by one to the next 3 points [p2, p3, p4], then the next [p3, p4, p5], etc.
   * </p>
   * <p>
   * (The first and last points of a centerline cannot be computed)
   * </p>
   * 
   * @param startPoint The start of a 3 point centerline
   * @param turnPoint A midpoint of a 3 point centerline
   * @param endPoint The endpoint of a 3 point centerline
   * @param offsetDistanceM The separation of the directional pathway from the original centerline. Eg 3 meters.
   * @param positiveOrNegativeTurn Positive for drive on left. Negative for drive on right
   * @return The computed point at the turnPoint for the directional path which is offsetDistanceM meters away.
   */
  public static LatLongCoordinate computeOrthogonalOffset(LatLongCoordinate startPoint, LatLongCoordinate turnPoint, LatLongCoordinate endPoint, double offsetDistanceM, boolean positiveOrNegativeTurn) {
    CoordinateAngle a1 = CoordinateAngle.create(startPoint, turnPoint);
//System.out.println("A1  " + a1.toDegrees());
    CoordinateAngle a2 = CoordinateAngle.create(turnPoint, endPoint);
//System.out.println("A2  " + a2.toDegrees());
    
    CoordinateAngle midAngle = a1.getMidpoint(a2, positiveOrNegativeTurn);
//System.out.println("MID " + midAngle.toDegrees());
    
    return getCoordinate(turnPoint, midAngle.toDegrees(), offsetDistanceM);
  }
  
  /**
   * 
   * @param point Point to adjust
   * @param distanceLongitudeM Meters longitude (X)
   * @param distanceLatitudeM Meters latitude (Y)
   */
  public static void moveCoordinate(LatLongCoordinate point, double distanceLongitudeM, double distanceLatitudeM) {
    LatLongCoordinate result = getCoordinate(point, 0, distanceLatitudeM);
    result = getCoordinate(result, 90, distanceLongitudeM);
    point.setLatLng(result.getLatitude(), result.getLongitude());
  }
  
  /**
   * Calculate how many degrees longitude we cover traveling 1 meter due east, at the given latitude.
   * This is a wild estimate based on a circular earth and my own sketchy trigonometry.
   * Knowing this we can roughly estimate what our change in longitude will be for a given distance east/west
   * @param degreesLatitude
   * @return Degrees longitude
   */
  public static double degreesLongitude(double degreesLatitude) {
    double degrees_per_meter_at_equator = 360d/40000000d;
//  double factor = (90d - degreesLatitude)/90d;
    double factor = 1 / Math.cos(Math.toRadians(degreesLatitude));
    return Math.abs(degrees_per_meter_at_equator*factor);
  }
  
  public static Comparator<LatLongCoordinate> newProximityComparator(LatLongCoordinate from) {
    return new SortByProximityLatLongCoordinate(from);
  }
  
  public static Comparator<CoordinateObject> newProximityComparatorCoordinateObject(LatLongCoordinate from) {
    return new SortByProximityCoordinateObject(from);
  }
  
  public static CoordinateObjectPair getClosestPoint(Iterable<? extends CoordinateObject> line1, Iterable<? extends CoordinateObject> line2) {
    CoordinateObjectPair result = new CoordinateObjectPair(Double.MAX_VALUE);
    
    for (CoordinateObject p1: line1) {
      for (CoordinateObject p2: line2) {
        double lengthM = p1.getCoordinate().getDistanceTo(p2.getCoordinate());
        if (lengthM < result.lengthM) {
          result.lengthM = lengthM;
          result.p1 = p1;
          result.p2 = p2;
        }
      }
    }
    if (result.p1 == null)
      return null;
    return result;
  }
  
  /**
   * Computes which of the items is nearest to the target Coordinate
   */
  public static LatLongCoordinateInStraightLine getClosestPoint(Iterable<LatLongCoordinate> items, LatLongCoordinate targetCoordinate) {
    LatLongCoordinateInStraightLine nearest = new LatLongCoordinateInStraightLine();
    for (LatLongCoordinate c: items) {
      double m = CoordinateObjectComputation.getGreatCircleDistance(targetCoordinate, c);
      if (m < nearest.currentDistanceTo) {
        nearest.coordinate = c;
        nearest.currentDistanceTo = m;
      }
    }
    return nearest;
  }
  
  public static CoordinateObjectPair getFurthestPoint(List<? extends CoordinateObject> coordinates) {
    CoordinateObjectPair result = new CoordinateObjectPair(-Double.MAX_VALUE);
    
    for (int i=0; i<coordinates.size(); i++) {
      CoordinateObject p1 = coordinates.get(i);
      for (int j=i+1; j<coordinates.size(); j++) {
        CoordinateObject p2 = coordinates.get(j);
        double lengthM = p1.getCoordinate().getDistanceTo(p2.getCoordinate());
        if (lengthM > result.lengthM) {
          result.lengthM = lengthM;
          result.p1 = p1;
          result.p2 = p2;
        }
      }
    }
    if (result.p1 == null)
      return null;
    return result;
  }

  
  public static class CoordinateObjectPair implements CoordinateObject {
    private double lengthM;
    private CoordinateObjectPair(double l) {
      lengthM = l;
    }
    
    public CoordinateObject p1;
    public CoordinateObject p2;
    
    public double getLengthM() {
      return lengthM;
    }
    
    @Override
    public LatLongCoordinate getCoordinate() {
      return p1.getCoordinate().getMidPoint(p2.getCoordinate());
    }
  }
  
  
  private static LatLongCoordinate getFurthestFrom(LatLongCoordinate reference, LatLongCoordinate A, LatLongCoordinate B) {
    double ADist = getGreatCircleDistance(A.getLatitude(), A.getLongitude(), reference.getLatitude(), reference.getLongitude());
    double BDist = getGreatCircleDistance(B.getLatitude(), B.getLongitude(), reference.getLatitude(), reference.getLongitude());
    return ADist > BDist ? A : B;
  }
  
  
  public static void computeStraightLine(List<CoordinateObject> coords) {
    // figure out the general starting point
    CoordinateObjectBoundingBox box = new CoordinateObjectBoundingBox();
    for (CoordinateObject o: coords) {
      boolean canAdd = box.expand(o.getCoordinate());
      if (!canAdd)
        throw new RuntimeException("Not a valid coordinate: '" + o + "'");
    }
    
    double latLen = box.getLatitudinalDistance();
    double lngLen = box.getLongitudinalDistance();
    
    LatLongCoordinate cent = box.center();
    LatLongCoordinate start = null;
    
    if (latLen > lngLen) { // if the box is taller than wide, go from bottom to top
      start = getFurthestFrom(cent, box.Bottom.getStationLatLng(), box.Top.getStationLatLng());
    } else {  // if the box is wider than tall, go from left to right
      start = getFurthestFrom(cent, box.Left.getStationLatLng(), box.Right.getStationLatLng());
    }

    // work out the distanced each point is to the start
    LatLongCoordinateInStraightLine currentMin = new LatLongCoordinateInStraightLine();
    final List<LatLongCoordinateInStraightLine> line = new ArrayList<LatLongCoordinateInStraightLine>();
    for (CoordinateObject o: coords) {
      LatLongCoordinateInStraightLine l = new LatLongCoordinateInStraightLine(o);
      l.setDistanceTo(start);
      line.add(l);
      if (l.currentDistanceTo < currentMin.currentDistanceTo)
        currentMin = l;
//      result.add(o);
    }

    int CURRENT_INDEX = 0;
    currentMin.currentIndex = CURRENT_INDEX;  // indicate the first one is used
    
    while (CURRENT_INDEX < line.size()) {
      CURRENT_INDEX++;
      LatLongCoordinateInStraightLine nextMin = new LatLongCoordinateInStraightLine();
      for (LatLongCoordinateInStraightLine llc: line) {
        if (!llc.used()) {
          llc.setDistanceTo(currentMin.getCoordinate());
          if (llc.currentDistanceTo < nextMin.currentDistanceTo)
            nextMin = llc;
        }
      }
      
      nextMin.currentIndex = CURRENT_INDEX;
      currentMin = nextMin;
    }

    Collections.sort(line);
    
    coords.clear();
    for (LatLongCoordinateInStraightLine l: line) {
      coords.add(l.coordinate);
    }
    
/*
    Comparator<CoordinateObject> comparator = newProximityComparatorCoordinateObject(start);
//    for (CoordinateObject o: result)
//      System.out.println(o.toString());

    coords.sort(comparator);
    
//    System.out.println("---------");
//    for (CoordinateObject o: result)
//      System.out.println(o.toString());
*/
//    return result;
  }
  
  static class SortByProximityLatLongCoordinate implements Comparator<LatLongCoordinate> {
    private LatLongCoordinate from;
    private SortByProximityLatLongCoordinate(LatLongCoordinate f) {
      from = f;
    }
    
    @Override
    public int compare(LatLongCoordinate arg0, LatLongCoordinate arg1) {
      double d1 = CoordinateObjectComputation.getGreatCircleDistance(arg0.getLatitude(), arg0.getLongitude(), from.getLatitude(), from.getLongitude());
      double d2 = CoordinateObjectComputation.getGreatCircleDistance(arg1.getLatitude(), arg1.getLongitude(), from.getLatitude(), from.getLongitude());
      return Double.compare(d1, d2);
    }
  }
  
  static class SortByProximityCoordinateObject implements Comparator<CoordinateObject> {
    private SortByProximityLatLongCoordinate delegate;
    private SortByProximityCoordinateObject(LatLongCoordinate f) {
      delegate = new SortByProximityLatLongCoordinate(f);
    }
    
    @Override
    public int compare(CoordinateObject arg0, CoordinateObject arg1) {
      return delegate.compare(arg0.getCoordinate(), arg1.getCoordinate());
    }
  }

  /*
  public static class StraightLineMaker {
    private final LatLongCoordinate start;
    
    public StraightLineMaker(LatLongCoordinate start) {
      this.start = start;
    }
    
    private final List<LatLongCoordinateInStraightLine> line = new ArrayList<LatLongCoordinateInStraightLine>();
    private LatLongCoordinateInStraightLine currentMin = new LatLongCoordinateInStraightLine();
    
    public void addLatLng(double lat, double lng) {
      add(new LatLongCoordinate(new double[]{lat, lng}));
    }

    public void addLngLat(double[] lngLat) {
      add(new LatLongCoordinate(new double[]{lngLat[1], lngLat[0]}));
    }
    
    public void add(LatLongCoordinate llc) {
      if (!llc.isSet())
        return;
      
      // convert to LatLongCoordinateInStraightLine
      LatLongCoordinateInStraightLine l = new LatLongCoordinateInStraightLine(llc);
      l.setDistanceTo(start);
      line.add(l);
      if (l.currentDistanceTo < currentMin.currentDistanceTo)
        currentMin = l;
    }
    
    public List<LatLongCoordinateInStraightLine> compute() {
      int CURRENT_INDEX = 0;
      currentMin.currentIndex = CURRENT_INDEX;  // indicate the first one is used
      
      while (CURRENT_INDEX < line.size()) {
        CURRENT_INDEX++;
        LatLongCoordinateInStraightLine nextMin = new LatLongCoordinateInStraightLine();
        for (LatLongCoordinateInStraightLine llc: line) {
          if (!llc.used()) {
            llc.setDistanceTo(currentMin.getCoordinate());
            if (llc.currentDistanceTo < nextMin.currentDistanceTo)
              nextMin = llc;
          }
        }
        
        nextMin.currentIndex = CURRENT_INDEX;
        currentMin = nextMin;
      }

      Collections.sort(line);
      
      return line;
    }
  }
  */
  
  public static class LatLongCoordinateInStraightLine implements CoordinateObject, Comparable<LatLongCoordinateInStraightLine> {

    private CoordinateObject coordinate;
    public int currentIndex = -1;
    
    public boolean used() {
      return currentIndex > -1;
    }
    
    private double currentDistanceTo = 0;
    
    private LatLongCoordinateInStraightLine() {
      currentDistanceTo = Double.MAX_VALUE;
    }
    
    private LatLongCoordinateInStraightLine(CoordinateObject llc) {
      this.coordinate = llc;
    }

    private void setDistanceTo(LatLongCoordinate other) {
      currentDistanceTo = CoordinateObjectComputation.getGreatCircleDistance(this.coordinate.getCoordinate(), other);
    }

    @Override
    public int compareTo(LatLongCoordinateInStraightLine o) {
      return this.currentIndex - o.currentIndex;
    }

    @Override
    public LatLongCoordinate getCoordinate() {
      return coordinate.getCoordinate();
    }
    
    public double getDistance() {
      return currentDistanceTo;
    }
  }
  
  public static LatLongCoordinate getCentroid(List<? extends CoordinateObject> points) {
    if (points.size() == 0)
      return new LatLongCoordinate();
    if (points.size() == 1)
      return points.get(0).getCoordinate();
    
    int count = 0;
    double latitudeTotal = 0;
    double longitudeTotal = 0;
    double altitudeTotal = 0;
    for (CoordinateObject coord: points) {
      LatLongCoordinate llc = coord.getCoordinate();
      if (llc.isSet()) {
        latitudeTotal += llc.getLatitude();
        longitudeTotal += llc.getLongitude();
        altitudeTotal += llc.altitude;
        count++;
      }
    }
    
    if (count > 1) {
      latitudeTotal  /= count;
      longitudeTotal /= count;
      altitudeTotal  /= count;
    }
    
    LatLongCoordinate result = new LatLongCoordinate(latitudeTotal, longitudeTotal);
    result.altitude = altitudeTotal;
    return result;
  }
  
  
  /**
   * Gets the bounds (bottom left and top right)
   * @param points
   * @return The ShapeBounds
   */
  public static CoordinateBoundingBox getBounds(Iterable<? extends CoordinateObject> points) {
    CoordinateBoundingBoxImpl result = new CoordinateBoundingBoxImpl();
    for (CoordinateObject llc: points) {
      result.accept(llc);
    }
    return result;
  }
  
  
  public static void sortByProximity(List<? extends CoordinateObject> coordinates, CoordinateObject anchor) {
    Collections.sort(coordinates, new SortByProximityCoordinateObject(anchor.getCoordinate()));
  }
  
  public static void sortByLatitude(List<? extends LatLongCoordinate> coordinates) {
    Collections.sort(coordinates, new SortByLatitude());
  }
  
  public static void sortByLongitude(List<? extends LatLongCoordinate> coordinates) {
    Collections.sort(coordinates, new SortByLongitude());
  }
  
  public static class SortByLatitude implements Comparator<LatLongCoordinate> {
    @Override
    public int compare(LatLongCoordinate o1, LatLongCoordinate o2) {
      if (o1.getLatitude() > o2.getLatitude())
        return 1;
      
      if (o1.getLatitude() < o2.getLatitude())
        return -1;
      
      return 0;
    }
  }
  
  public static class SortByLongitude implements Comparator<LatLongCoordinate> {
    @Override
    public int compare(LatLongCoordinate o1, LatLongCoordinate o2) {
      if (o1.getLongitude() > o2.getLongitude())
        return 1;
      
      if (o1.getLongitude() < o2.getLongitude())
        return -1;
      
      return 0;
    }
  }

  
  /**
   * 
   * https://www.npmjs.com/package/point-in-polygon-extended?activeTab=code
   *
   */
  public static class PointInPolygon {
    
    public boolean isInside(XYPoint XYPoint, List<XYPoint> polygon) {
      return getWindingNumber(XYPoint, polygon) != 0;  // the point is outside only when this winding number wn===0, otherwise it's inside
    }
    
    /**
     * Returns whether a point is in a polygon using a winding number test
     * Algorithm by Dan Sunday: http://geomalgorithms.com/a03-_inclusion.html
     *
     * @param point should be a 2-item array of coordinates
     * @param polygon should be an array of 2-item arrays of coordinates.
     * @return true if inside, false if outside
     */
    public int getWindingNumber(XYPoint XYPoint, List<XYPoint> polygon) {
      if (polygon.size() == 0)
        return Integer.MIN_VALUE;

      List<XYPoint> newPoints = new ArrayList<XYPoint>();
      newPoints.addAll(polygon);
      newPoints.add(polygon.get(0));  // add the first point to the end
      
      int result = 0; // wn counter

      // loop through all edges of the polygon
      for (int i = 0; i < polygon.size(); i++) {
        if (newPoints.get(i).y <= XYPoint.y) {
          if (newPoints.get(i + 1).y > XYPoint.y) {
            if (isLeft(newPoints.get(i), newPoints.get(i + 1), XYPoint) > 0) {
              result++;
            }
          }
        } else {
          if (newPoints.get(i + 1).y <= XYPoint.y) {
            if (isLeft(newPoints.get(i), newPoints.get(i + 1), XYPoint) < 0) {
              result--;
            }
          }
        }
      }
      // the point is outside only when this winding number wn===0, otherwise it's inside
      return result;
    }

    /**
     * Tests if a point is Left|On|Right of an infinite line.
     *
     * See http://geomalgorithms.com/a01-_area.html
     *
     * @param p0 x,y point
     * @param p1 x,y point
     * @param p2 x,y point
     * @return 
     *  >0 for P2 left of the line through P0 and P1,
     *  =0 for P2  on the line,
     *  <0 for P2  right of the line
     */
    private double isLeft(XYPoint p0, XYPoint p1, XYPoint p2) {
      return ( (p1.x - p0.x) * (p2.y - p0.y) ) -
              ((p2.x - p0.x) * (p1.y - p0.y) );
    }
  }
}