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

public class XYPointList extends ArrayList<XYPoint> {
  private static final long serialVersionUID = 1L;
  
  public XYPoint add(double X, double Y) {
    XYPoint result = create(X, Y);
    super.add(result);
    return result;
  }
  
  protected XYPoint create(double X, double Y) {
    return new XYPoint(X, Y);
  }
  
  public XYPoint getAverage() {
    XYPoint result = create(0, 0);
    if (this.size() == 0)
      return null;
    if (this.size() == 1)
      return this.get(0);

    for (XYPoint p: this)
      result.add(p);

    result.x = result.x / this.size();
    result.y = result.y / this.size();
    return result;
  }
  
  public final XYPoint getFirst() {
    if (size() == 0)
      return null;
    return this.get(0);
  }
  
  public final XYPoint getLast() {
    if (size() == 0)
      return null;
    return this.get(size()-1);
  }
  
  public void setMinMax(XYPoint min, XYPoint max) {
    min.x =  Double.MAX_VALUE;
    min.y =  Double.MAX_VALUE;
    max.x = -Double.MAX_VALUE;
    max.y = -Double.MAX_VALUE;
    for (XYPoint p: this) {
      if (p.x > max.x)
        max.x = p.x;
      if (p.x < min.x)
        min.x = p.x;
      if (p.y > max.y)
        max.y = p.y;
      if (p.y < min.y)
        min.y = p.y;
    }
  }
}