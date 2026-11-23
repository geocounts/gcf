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
 * A 3-D point
 */
public class XYZPoint extends XYPoint {
  public XYZPoint() {}
  
  public XYZPoint(double[] xyz) {
    super(xyz[0], xyz[1]);
    z = xyz[2];
  }
  
  public XYZPoint(double x, double y, double z) {
    super(x, y);
    this.z = z;
  }
  
  public double z;

  @Override
  public double getHypotenuse() {
    return Math.sqrt(x*x + y*y + z*z);
  }
  
  public double getXYHypotenuse() {
    return super.getHypotenuse();
  }
  
  @Override
  public String toString() {
    return super.toString() + "," + z;
  }

  /**
   * Computed assuming z = up and down, y = back and forth, x is side to side
   * @return SphericalCoordinates 
   */
  public SphericalCoordinates toSphericalCoordinates() {
    SphericalCoordinates result = new SphericalCoordinates();
    result.horizontalAngle.setFromXYPoint(this);

    double hdist = getXYHypotenuse();
    double vdist = this.z;
    result.setVertical(hdist, vdist);
    return result;
  }
}
