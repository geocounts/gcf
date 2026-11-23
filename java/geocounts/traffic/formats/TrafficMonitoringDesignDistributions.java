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
import java.util.HashMap;

/**
 * This is the "d" value that matches the "k" value
 * @see TrafficMonitoringDesignVolumes
 * 
 */
public class TrafficMonitoringDesignDistributions implements TallyCountField {
  public final int index;
  
  public TrafficMonitoringDesignDistributions(int h) {
    index = h;
  }

  protected static String buildID(char startChar, int _index) {
    StringBuilder result = new StringBuilder();
    result.append(startChar);
    if (_index < 100)
      result.append('0');
    if (_index < 10)
      result.append('0');
    result.append(_index);
    return result.toString();
  }
  
  @Override
  public String getId() {
    return buildID('d', index);
  }

  @Override
  public String getDescription() {
    return "Design hourly distribution " + getId();
  }

  @Override
  public Number get(VehicleTallyRec tally, UnitsUsed units) {
    if (tally.trafficmonitoring.designDistributions == null)
      return null;
    return tally.trafficmonitoring.designDistributions.get(index);
  }

  @Override
  public String format(VehicleTallyRec tally, UnitsUsed units) {
    DecimalFormat df = new DecimalFormat("0.000");
    if (tally.trafficmonitoring.designDistributions == null)
      return null;
    return df.format(tally.trafficmonitoring.designDistributions.get(index));
  }

  @Override
  public void set(double value, UnitsUsed units, VehicleTallyRec result) throws Exception {
    if (result.trafficmonitoring.designDistributions == null)
      result.trafficmonitoring.designDistributions = new HashMap<Integer, Double>();
    result.trafficmonitoring.designDistributions.put(index, value);
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
