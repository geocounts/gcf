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
 * This is identical in purpose to PointXY in deusutils.jar
 */
public class XYPoint {
  public double x;
  public double y;
  
  public double getArea() {
    return x*y;
  }
  
  public double getHypotenuse() {
    return Math.sqrt(x*x + y*y);
  }
  
  public XYPoint() {}
  
  public XYPoint(double X, double Y) {
    x = X;
    y = Y;
  }
  
  public int getXAsInt() {
    return (int)x;
  }
  
  public int getYAsInt() {
    return (int)y;
  }
  
  public double[] toDoubleArray() {
    return new double[]{x, y};
  }
  
  public final void copyFrom(XYPoint other) {
    this.x = other.x;
    this.y = other.y;
  }
  
  public void add(XYPoint other) {
    this.x += other.x;
    this.y += other.y;
  }
  
  public XYPoint getMidpoint(XYPoint other) {
    return getFractionPoint(other, 0.5);
  }
  
  public XYPoint getFractionPoint(XYPoint other, double fraction) {
    XYPoint result = new XYPoint();
    result.x = other.x + (other.x - this.x)*fraction;
    result.y = other.y + (other.y - this.y)*fraction;
    return result;
  }
  
  public XYPoint getDistanceTo(XYPoint other) {
    XYPoint result = new XYPoint();
    result.x = other.x - this.x;
    result.y = other.y - this.y;
    return result;
  }
  
  @Override
  public String toString() {
    return x + "," + y;
  }
}
