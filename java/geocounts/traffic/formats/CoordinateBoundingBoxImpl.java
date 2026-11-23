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

import java.util.function.Consumer;

/**
 * 
 * @see CoordinateObjectBoundingBox
 *
 */
public class CoordinateBoundingBoxImpl extends CoordinateBoundingBox implements Consumer<CoordinateObject> {
  private final LatLongCoordinate bottomLeft = new LatLongCoordinate();
  public final LatLongCoordinate topRight = new LatLongCoordinate();
  
  @Override
  public LatLongCoordinate bottomLeft() {
    return bottomLeft;
  }
  
  @Override
  public LatLongCoordinate topRight() {
    return topRight;
  }

  @Override
  public LatLongCoordinate topLeft() {
    return new LatLongCoordinate(topRight.getLatitude(), bottomLeft.getLongitude());
  }

  @Override
  public LatLongCoordinate bottomRight() {
    return new LatLongCoordinate(bottomLeft.getLatitude(), topRight.getLongitude());
  }
  
  public void setFrom(CoordinateBoundingBox o) {
    bottomLeft.setFrom(o.bottomLeft());
    topRight.setFrom(o.topRight());
  }
  
  public boolean isSet() {
    return bottomLeft.isSet() && topRight.isSet();
  }
  
  @Override
  public String toString() {
    if (!isSet())
      return "?";
    return bottomLeft.toString() + " " + topRight.toString();
  }

  @Override
  public void accept(CoordinateObject coordinate) {
    expand(coordinate.getCoordinate());
  }
  
  @Override
  public boolean expand(LatLongCoordinate llc) {
    if (!llc.isSet())
      return false;
    
    if (!bottomLeft.isSet()) {
      bottomLeft.setFrom(llc);
    } else {
      if (llc.getLatitude() < bottomLeft.getLatitude())
        bottomLeft.setLatitude(llc.getLatitude());
      if (llc.getLongitude() < bottomLeft.getLongitude())
        bottomLeft.setLongitude(llc.getLongitude());
    }
    if (!topRight.isSet()) {
      topRight.setFrom(llc);
    } else {
      if (llc.getLatitude() > topRight.getLatitude())
        topRight.setLatitude(llc.getLatitude());
      if (llc.getLongitude() > topRight.getLongitude())
        topRight.setLongitude(llc.getLongitude());
    }
    return true;
  }
}