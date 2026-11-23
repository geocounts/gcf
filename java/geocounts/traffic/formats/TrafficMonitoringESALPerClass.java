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

import java.text.DecimalFormat;

/**
 * 
 * @see TrafficMonitoringTallyCountFields#averageESALVehType
 * @since 2.084
 */
public class TrafficMonitoringESALPerClass implements TallyCountField {
  public final int index;
  
  public TrafficMonitoringESALPerClass(int c) {
    index = c;
  }
  
  @Override
  public String getId() {
    return buildID("esalcls", index);
  }

  protected static String buildID(String startChars, int _index) {
    StringBuilder result = new StringBuilder();
    result.append(startChars);
    if (_index < 100)
      result.append('0');
    if (_index < 10)
      result.append('0');
    result.append(_index);
    return result.toString();
  }

  @Override
  public String getDescription() {
    return "ESAL for type class " + getId();
  }

  @Override
  public Number get(VehicleTallyRec tally, UnitsUsed units) {
    if (tally.trafficmonitoring.averageESALVehType == null)
      return null;
    return tally.trafficmonitoring.averageESALVehType[index];
  }

  @Override
  public String format(VehicleTallyRec tally, UnitsUsed units) {
    Number esal = get(tally, units);
    if (esal == null)
      return null;
    DecimalFormat df = new DecimalFormat("0.000");
    return df.format(esal.doubleValue());
  }

  @Override
  public void set(double value, UnitsUsed units, VehicleTallyRec result) throws Exception {
    if (result.trafficmonitoring.averageESALVehType == null)
      result.trafficmonitoring.averageESALVehType = new double[1+result.getTC().getMaximumBin()];
    result.trafficmonitoring.averageESALVehType[index] = value;
  }

  @Override
  public boolean isWithinBounds(VehicleTallyRec tally) {
    return true;
  }

  @Override
  public boolean isAdditive() {
    return false;
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
