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

import java.util.Comparator;

public class VehicleFieldsEnumComparator implements Comparator<VehicleRec> {
  public final VehicleFieldsEnum field;
  private final UnitsUsed units;
  
  public VehicleFieldsEnumComparator(VehicleFieldsEnum fT, UnitsUsed u) {
    field = fT;
    units = u;
  }

  @Override
  public int compare(VehicleRec veh1, VehicleRec veh2) {
    int value = compareOriginal(veh1, veh2);
    if (value == 0)
      return veh1.compareTimes(veh2);
    return value;
  }
  
  public int compareOriginal(VehicleRec veh1, VehicleRec veh2) {
    if (field.getFieldType().isNumeric()) {
      Number n1 = field.getNumber(veh1);
      Number n2 = field.getNumber(veh2);
      return Double.compare(n1 != null ? n1.doubleValue() : 0, n2 != null ? n2.doubleValue() : 0);
    }

    String n1 = field.notNull(field.get(veh1, units));
    String n2 = field.notNull(field.get(veh2, units));
    return n1.compareTo(n2);
  }
}
