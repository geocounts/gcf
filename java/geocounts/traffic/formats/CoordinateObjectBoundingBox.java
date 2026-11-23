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
import java.util.List;


public class CoordinateObjectBoundingBox extends CoordinateBoundingBox {
  private double[] sumLatLng = new double[2];
  private int countOfValidPoints;
  
  protected final LeftEdge Left;
  protected final RightEdge Right;
  protected final TopEdge Top;
  protected final BottomEdge Bottom;
  
  public CoordinateObjectBoundingBox() {
    Left   = new LeftEdge();
    Right  = new RightEdge();
    Top    = new TopEdge();
    Bottom = new BottomEdge();
  }

  public LatLongCoordinate centroid() {
    double[] latLng = new double[2];
    if (countOfValidPoints > 0) {
      latLng[0] = sumLatLng[0]/countOfValidPoints;
      latLng[1] = sumLatLng[1]/countOfValidPoints;
    }
    return new LatLongCoordinate(latLng);
  }
  
  public LatLongCoordinate center() {
    double averageLat = (Top.value - Bottom.value)/2 + Bottom.value;
    double averageLng = (Right.value - Left.value)/2 + Left.value;
    return new LatLongCoordinate(new double[]{averageLat, averageLng});
  }
  
  public int numberOfValidPoints() {
    return countOfValidPoints;
  }
  
  public double getLongitudinalDistance() {
    double averageLat = (Top.value - Bottom.value)/2 + Bottom.value;
    return CoordinateObjectComputation.getGreatCircleDistance(averageLat, Left.value, averageLat, Right.value);
  }
  
  public double getLatitudinalDistance() {
    double averageLng = (Right.value - Left.value)/2 + Left.value;
    return CoordinateObjectComputation.getGreatCircleDistance(Bottom.value, averageLng, Top.value, averageLng);
  }

  public List<LatLongCoordinate> to3PointLine() {
    ArrayList<LatLongCoordinate> result = new ArrayList<LatLongCoordinate>();
    
    double latLen = getLatitudinalDistance();
    double lngLen = getLongitudinalDistance();
    LatLongCoordinate centroid = this.centroid();
    if (latLen > lngLen) { // if the box is taller than wide, go from bottom to top
      result.add(Bottom.getStationLatLng());
      result.add(centroid);
      result.add(Top.getStationLatLng());
    } else {  // if the box is wider than tall, go from left to right
      result.add(Left.getStationLatLng());
      result.add(centroid);
      result.add(Right.getStationLatLng());
    }
    
    return result;
  }

  
  public boolean examineLngLat(double[] coordLngLat) {
    return examine(new double[]{coordLngLat[1], coordLngLat[0]});
  }
  
  public boolean examine(double[] coordLatLng) {
    if ((coordLatLng[0] == 0.0) && (coordLatLng[1] == 0.0))
      return false;
    
    sumLatLng[0] += coordLatLng[0];
    sumLatLng[1] += coordLatLng[1];
    countOfValidPoints++;
    
    Top.examine(coordLatLng);
    Right.examine(coordLatLng);
    Left.examine(coordLatLng);
    Bottom.examine(coordLatLng);
    return true;
  }
  
  @Override
  public final boolean expand(LatLongCoordinate coordinate) {
    return examine(coordinate.toLatLngHeight());
  }
  
  public final int examine(Iterable<LatLongCoordinate> points) {
    int result = 0;
    for (LatLongCoordinate c: points) {
      if (expand(c))
        result++;
    }
    return result;
  }
  
  @Override
  public LatLongCoordinate topLeft() {
    return new LatLongCoordinate(new double[]{Top.value, Left.value});
  }

  @Override
  public LatLongCoordinate bottomLeft() {
    return new LatLongCoordinate(new double[]{Bottom.value, Left.value});
  }
  
  @Override
  public LatLongCoordinate bottomRight() {
    return new LatLongCoordinate(new double[]{Bottom.value, Right.value});
  }
  
  @Override
  public LatLongCoordinate topRight() {
    return new LatLongCoordinate(new double[]{Top.value, Right.value});
  }

  /**
   * 
   * @param closeLastPoint In GEOJson the first and last points must be identical so this would be true
   */
  public Iterable<LatLongCoordinate> toRectangle(boolean closeLastPoint) {
    ArrayList<LatLongCoordinate> result = new ArrayList<LatLongCoordinate>();
    LatLongCoordinate bottomLeft = bottomLeft();
    result.add(bottomLeft);
    result.add(bottomRight());
    result.add(topRight());
    result.add(topLeft());
    if (closeLastPoint)
      result.add(bottomLeft);
    return result;
  }
  
  
  static abstract class BoxEdge {
    public double value;
    public BoxEdge() {
    }
    
    /**
     * This edge's station
     */
    public LatLongCoordinate getStationLatLng() {
      return new LatLongCoordinate(stationLatLng);
    }
    
    private double[] stationLatLng;
    
    protected abstract boolean doExamine(double[] latLng);
    
    public final void examine(double[] latLng) {
      if (doExamine(latLng))
        stationLatLng = latLng;
    }
    
    public final void examine(LatLongCoordinate coordinate) {
      examine(coordinate.toLatLngHeight());
    }
  }
  
  static class LeftEdge extends BoxEdge {
    public LeftEdge() {
      value = Double.MAX_VALUE;
    }

    @Override
    protected boolean doExamine(double[] latLng) {
      if (latLng[1] <= value) {
        this.value = latLng[1];
        return true;
      }
      return false;
    }
  }
  
  static class TopEdge extends BoxEdge {
    public TopEdge() {
      value = -Double.MAX_VALUE;
    }

    @Override
    protected boolean doExamine(double[] latLng) {
      if (latLng[0] >= value) {
        this.value = latLng[0];
        return true;
      }
      return false;
    }
  }

  static class BottomEdge extends BoxEdge {
    public BottomEdge() {
      value = Double.MAX_VALUE;
    }

    @Override
    protected boolean doExamine(double[] latLng) {
      if (latLng[0] <= value) {
        this.value = latLng[0];
        return true;
      }
      return false;
    }
  }

  static class RightEdge extends BoxEdge {
    public RightEdge() {
      value = -Double.MAX_VALUE;
    }

    @Override
    protected boolean doExamine(double[] latLng) {
      if (latLng[1] >= value) {
        this.value = latLng[1];
        return true;
      }
      return false;
    }
  }
}
