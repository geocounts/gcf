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

public class LatLongCoordinateInTime extends LatLongCoordinate implements TimedObject {

  private static final long serialVersionUID = 1L;
  public LatLongCoordinateInTime() {
  }
  
  public long dateTime = 0;
  
  public String title;

  @Override
  public boolean isSet() {
    return super.isSet() && (dateTime != 0);
  }

  @Override
  public void setFrom(LatLongCoordinate other) {
    super.setFrom(other);
    if (other instanceof LatLongCoordinateInTime) {
      LatLongCoordinateInTime o = (LatLongCoordinateInTime)other;
      this.dateTime = o.getTime();
      this.title = o.title;
    }
  }

  @Override
  public void clear() {
    super.clear();
    dateTime = 0;
  }
  
  @Override
  public long getTime() {
    return dateTime;
  }
  
  public long getDifference(long time) {
    return this.dateTime - time;
  }

}
