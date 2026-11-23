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

public class TrafficMonitoringDayByHourField implements TallyCountField {
  private final String ID;
  
  public TrafficMonitoringDayByHourField(int daySunEq0, int hour) {
    this("aadtdh", daySunEq0, hour);
  }
  
  public TrafficMonitoringDayByHourField(String prefix, int daySunEq0, int hour) {
    // aadtdh_0_00
    StringBuilder id = new StringBuilder(prefix);
    id.append('_');
    id.append(daySunEq0);
    id.append('_');
    if (hour < 10)
      id.append('0');
    id.append(hour);
    ID = id.toString();
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getDescription() {
    return ID;
  }

  @Override
  public Number get(VehicleTallyRec tally, UnitsUsed units) {
    return tally.userdefined.get(getId());
  }

  @Override
  public String format(VehicleTallyRec tally, UnitsUsed units) {
    Number n = get(tally, units);
    if (n == null)
      return null;
    
    return n.toString();
  }

  @Override
  public void set(double value, UnitsUsed units, VehicleTallyRec result) throws Exception {
    result.userdefined.setValue(this.getId(), value);
  }

  @Override
  public boolean isWithinBounds(VehicleTallyRec tally) {
    return true;
  }

  @Override
  public boolean isAdditive() {
    return true;
  }

  @Override
  public GCFieldType getFieldType() {
    return GCFieldType.DOUBLE;
  }

  @Override
  public double getMinValue() {
    return 0;
  }

  @Override
  public String toString() {
    return getId();
  }
}
