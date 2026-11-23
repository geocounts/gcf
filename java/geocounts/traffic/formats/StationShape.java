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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Represents a geographical shape of the station record. 
 * This can be a line or an area.
 * @see LatLongCoordinate
 * @see EnumHeaderRecords#SP
 * @see StationGPS
 * @since 1.27
 */
public class StationShape extends RawTrafficDataHeaderElement implements Iterable<LatLongCoordinate> {

  /**
   * This is never null
   */
  public ShapeTypeEnum type = ShapeTypeEnum.line;
  public StationShape() {
    this(ShapeTypeEnum.line);
  }
  
  private StationShape(ShapeTypeEnum t) {
    type = t;
  }
  
  private final List<LatLongCoordinate> points = new ArrayList<LatLongCoordinate>();
  
  public BiConsumer<LatLongCoordinate, LatLongCoordinate> onChangeLineCoordinates = null;
  private void _triggerChangeLineCoordinates() {
    if (onChangeLineCoordinates == null)
      return;
    if (this.type != ShapeTypeEnum.line)
      return;
    if (!isSet())
      return;
    onChangeLineCoordinates.accept(getStartPoint(), getEndPoint());
  }

  public static StationShape newLine(Iterable<LatLongCoordinate> coordinates) {
    StationShape result = new StationShape(ShapeTypeEnum.line);
    for (LatLongCoordinate c: coordinates)
      result.points.add(c);
    return result;
  }
  
  public LatLongCoordinate getPoint(int index) {
    return points.get(index);
  }
  
  public int size() {
    return points.size();
  }

  @Override
  public Iterator<LatLongCoordinate> iterator() {
    return points.iterator();
  }
  
  public LatLongCoordinate getStartPoint() {
    return points.get(0);
  }
  
  public LatLongCoordinate getEndPoint() {
    return points.get(points.size()-1);
  }
  
  /**
   * 
   * @return Returns the midpoint. If the shape is a line, this produces a point along the line.
   * @see #getCentroid()
   */
  public LatLongCoordinate getMidPoint() {
    return getCentroid(); // needs to be fixed !!!
  }

  /**
   * 
   * @return The centroid. If the shape is a line, the centroid may not be located on the line.
   * @see #getMidPoint()
   */
  public LatLongCoordinate getCentroid() {
    return CoordinateObjectComputation.getCentroid(points);
  }
  
  public static LatLongCoordinate getCentroid(List<? extends LatLongCoordinate> points) {
    return CoordinateObjectComputation.getCentroid(points);
  }
  
  public CoordinateBoundingBox getBounds() {
    return CoordinateObjectComputation.getBounds(this.points);
  }
  
  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.SP;
  }
  
  public boolean isSet() {
    return (points.size() >= type.minValue);
  }

  @Override
  public void clear() {
    _reset(ShapeTypeEnum.line);
  }
  
  private void _reset(ShapeTypeEnum t) {
    this.points.clear();
    type = t;
  }
  
  public void setFrom(StationShape other) {
    _reset(other.type);
    this.points.addAll(other.points);
  }
  
  /**
   * Sets this object to {@link ShapeTypeEnum#point}
   * @param p The {@link LatLongCoordinate coordinate}
   */
  public void setFrom(LatLongCoordinate p) {
    _reset(ShapeTypeEnum.point);
    points.add(p);
  }
  
  public void addVertex(double lat, double lng) {
    points.add(new LatLongCoordinate(lat, lng));
    _triggerChangeLineCoordinates();
  }

  /**
   * Sets this object using coordinates
   * @param lineOrArea Type of shape: True if the points represent a {@link ShapeTypeEnum#line line}, False if the points represent an {@link ShapeTypeEnum#area area}
   * @param pts The points. Must contain at least the minimum number of points for the chosen type of shape.
   */
  public void setFrom(boolean lineOrArea, LatLongCoordinate... pts) {
    _reset(lineOrArea ? ShapeTypeEnum.line : ShapeTypeEnum.area);
    for (LatLongCoordinate llc: pts)
      points.add(llc);
    
    _triggerChangeLineCoordinates();
  }
  
  public void setFrom(boolean lineOrArea, Collection<LatLongCoordinate> pts) {
    _reset(lineOrArea ? ShapeTypeEnum.line : ShapeTypeEnum.area);
    for (LatLongCoordinate llc: pts)
      points.add(llc);
    
    _triggerChangeLineCoordinates();
  }
  
  public void setFromVertices(boolean lineOrArea, Collection<double[]> latLngVertices) {
    _reset(lineOrArea ? ShapeTypeEnum.line : ShapeTypeEnum.area);
    for (double[] latLng: latLngVertices)
      this.points.add(new LatLongCoordinate(latLng));
    
    _triggerChangeLineCoordinates();
  }
  
  public void moveTo(ChannelDirEnum dir, int meters) {
    for (LatLongCoordinate c: this)
      c.moveTo(dir, meters);

    _triggerChangeLineCoordinates();
  }

  @Override
  public void read(String[] data) {
    if (data == null)
      return;
    
    ShapeTypeEnum st = ShapeTypeEnum.values()[ Integer.parseInt(data[0])-1 ];
    int numPoints = Integer.parseInt(data[1]);
    if (numPoints < st.minValue)
      return;
    
    _reset(st);
    
    int index = 2;
    for (int i=0; i<numPoints; i++) {
      LatLongCoordinate vertice = new LatLongCoordinate();
      vertice.setLatLng( Double.parseDouble(data[index++]), Double.parseDouble(data[index++]) );
      vertice.altitude  = Double.parseDouble(data[index++]);
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
      result[index++] = Double.toString(pt.getLatitude());
      result[index++] = Double.toString(pt.getLongitude());
      result[index++] = Double.toString(pt.altitude);
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
      result[i][0] = coord.getLatitude();
      result[i][1] = coord.getLongitude();
      result[i][2] = coord.altitude;
      i++;
    }
    return result;
  }

  public enum ShapeTypeEnum implements IdDesc {
    /**
     * A point
     */
    point(1),
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
