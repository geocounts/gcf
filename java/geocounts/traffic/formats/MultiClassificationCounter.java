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
 * 
 * A vehicle classification system that looks for trucks
 * @see WKTypeClass#truck
 * @see WKTypeClass#motorized
 */
public class MultiClassificationCounter implements SpecialClassificationCounter {
  private final int[] Classes;
  private TallyCountField tallyCountField;
  
  public MultiClassificationCounter(WKTypeClass o, int... IDs) {
    this.Classes = IDs;
    this.tallyCountField = o.getTallyCountField();
  }

  @Override
  public int[] getIDs() {
    return Classes;
  }

  @Override
  public boolean isCounted(int classID) {
    for (int vc: Classes)
      if (vc == classID)
        return true;
    return false;
  }
  
  @Override
  public Number getCountFromTC(VehicleTallyRec tally) {
    double result = 0;
    for (int vc: Classes) {
      try {
        result += tally.countsVehType.getDouble(vc);
      } catch (Exception ex) {}
    }
    return result;
  }

  @Override
  public Number getCount(VehicleTallyRec tally, UnitsUsed units) {
    return tallyCountField != null ? tallyCountField.get(tally, units) : null;
  }

}
