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

public class XYArea extends XYPoint {
  public XYArea(double x, double y, double width, double height) {
    super(x, y);
    this.width = width;
    this.height = height;
  }
  
  public XYArea() {}
  
  public double width;
  public int getWidthAsInt() {
    return (int)width;
  }
  
  public double height;
  public int getHeightAsInt() {
    return (int)height;
  }
  
  public double getSize() {
    return width*height;
  }
  
  public XYPoint tl() {
    return this;
  }
  
  public XYPoint tr() {
    return new XYPoint(x+width, y);
  }

  public XYPoint br() {
    return new XYPoint(x+width, y+height);
  }

  public XYPoint bl() {
    return new XYPoint(x, y+height);
  }
  
  public XYPoint mid() {
    return new XYPoint(x + width/2, y + height/2);
  }
}
