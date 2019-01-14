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

public class UserDefinedObservationField implements ObservationField {
  private String key;
  
  protected UserDefinedObservationField(String key) {
    this.key = key;
  }
  
  @Override
  public String getId() {
    return key;
  }

  @Override
  public String getDescription() {
    return "Unknown VV field " + key;
  }

  @Override
  public boolean isNumeric() {
    return false;
  }

  @Override
  public String get(VehicleRec veh, UnitsUsed units) throws Exception {
    return veh.userdefined.get(key);
  }

  @Override
  public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
    result.userdefined.put(key, value);
  }

}
