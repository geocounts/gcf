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

public class TruckClassCounter implements SpecialClassificationCounter {
  private final int[] truckClasses;
  private int[] carClasses;
  
  public TruckClassCounter(int... truckIDs) {
    this.truckClasses = truckIDs;
  }
  
  public void setOtherMotorizedClasses(int... carIDs) {
    carClasses = carIDs;
  }
  
  @Override
  public boolean isCounted(int classID) {
    return isTruck(classID);
  }
  
  public boolean isTruck(int classID) {
    for (int vc: truckClasses)
      if (vc == classID)
        return true;
    return false;
  }
  
  public boolean isCar(int classID) {
    if (carClasses == null)
      return false;
    
    for (int vc: carClasses)
      if (vc == classID)
        return true;
    return false;
  }
  
  public int getOtherMotorizedCount(VehicleTallyRec tally) {
    if (carClasses == null)
      return 0;
    
    int result = 0;
    for (int vc: carClasses) {
      try {
        result += tally.countsVehType[vc];
      } catch (Exception ex) {}
    }
    return result;
  }
  
  public int getTruckCount(VehicleTallyRec tally) {
    int result = 0;
    for (int vc: truckClasses) {
      try {
        result += tally.countsVehType[vc];
      } catch (Exception ex) {}
    }
    return result;
  }
  
  @Override
  public double getCount(VehicleTallyRec tally) {
    return getTruckCount(tally);
  }
}
