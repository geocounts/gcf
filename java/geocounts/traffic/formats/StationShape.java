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

/**
 * Represents a geographical shape of the station record. 
 * This can be a line or an area.
 * @see LatLongCoordinate
 * @see EnumHeaderRecords#SP
 * @see StationGPS
 * @since 1.27
 */
public class StationShape extends RawTrafficDataHeaderElement {

  /**
   * This is never null
   */
  public ShapeTypeEnum type = ShapeTypeEnum.line;
  
  public final List<LatLongCoordinate> points = new ArrayList<LatLongCoordinate>();


  public LatLongCoordinate getCentroid() {
    return getCentroid(points);
  }
  
  public static LatLongCoordinate getCentroid(List<? extends LatLongCoordinate> points) {
    LatLongCoordinate result = new LatLongCoordinate();
    if (points.size() == 0)
      return result;
    if (points.size() == 1)
      return points.get(0);
    
    for (LatLongCoordinate llc: points) {
      result.latitude += llc.latitude;
      result.longitude += llc.longitude;
      result.height += llc.height;
    }
    
    result.latitude /= points.size();
    result.longitude /= points.size();
    result.height /= points.size();
    
    return result;
  }
  
  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.SP;
  }
  
  public boolean isSet() {
    return (points.size() >= type.minValue);
  }
  
  public void setFrom(StationShape other) {
    this.points.clear();
    this.points.addAll(other.points);
    this.type = other.type;
  }

  /**
   * Sets this object as a line
   * @param lineOrArea Type of shape: True if the points represent a {@link ShapeTypeEnum#line line}, False if the points represent an {@link ShapeTypeEnum#area area}
   * @param pts The points. Must contain at least the minimum number of points for the chosen type of shape
   */
  public void setFrom(boolean lineOrArea, LatLongCoordinate... pts) {
    type = lineOrArea ? ShapeTypeEnum.line : ShapeTypeEnum.area;
    points.clear();
    for (LatLongCoordinate llc: pts) {
      points.add(llc);
    }
  }

  @Override
  public void read(String[] data) {
    if (data == null)
      return;
    
    ShapeTypeEnum st = ShapeTypeEnum.values()[ Integer.parseInt(data[0])-1 ];
    int numPoints = Integer.parseInt(data[1]);
    if (numPoints < type.minValue)
      return;
    
    type = st;
    points.clear();
    
    int index = 2;
    for (int i=0; i<numPoints; i++) {
      LatLongCoordinate vertice = new LatLongCoordinate();
      vertice.latitude  = Double.parseDouble(data[index++]);
      vertice.longitude = Double.parseDouble(data[index++]);
      vertice.height = Double.parseDouble(data[index++]);
      points.add(vertice);
    }
  }

  @Override
  protected String[] write() {
    if (!isSet())
      return null;
    
    String[] result = new String[2 + 3*points.size()];
    result[0] = Integer.toString(type.ordinal()+1);
    result[1] = Integer.toString(points.size());
    int index = 2;
    for (LatLongCoordinate pt: points) {
      result[index++] = Double.toString(pt.latitude);
      result[index++] = Double.toString(pt.longitude);
      result[index++] = Double.toString(pt.height);
    }
    return result;
  }
  
  @Override
  public String toString() {
    return type + " " + points.size();
  }
  
  public double[][] toLatLngHgt() {
    double[][] result = new double[points.size()][3];
    int i=0;
    for (LatLongCoordinate coord: points) {
      result[i][0] = coord.latitude;
      result[i][1] = coord.longitude;
      result[i][2] = coord.height;
      i++;
    }
    return result;
  }

  public enum ShapeTypeEnum implements IdDesc {
    /**
     * A line could be used to represent a road. This must have at least 2 points.
     */
    line(2),
    /**
     * An area could be used to represent a parking lot, building or intersection. This must have at least three points.
     */
    area(3);
    
    public final int minValue;
    private ShapeTypeEnum(int mv) {
      minValue = mv;
    }
    
    @Override
    public String getId() {
      return Integer.toString(ordinal() + 1);
    }
    
    @Override
    public String getDescription() {
      return name();
    }
  }
}
