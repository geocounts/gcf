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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class VehicleRecFileIndex implements Iterable<VehicleRecFileIndex.VehicleRecFilePeriod> {
  private final Map<Long, VehicleRecFilePeriod> byTime = new HashMap<Long, VehicleRecFilePeriod>();
  private final List<VehicleRecFilePeriod> byOrder = new ArrayList<VehicleRecFilePeriod>();
  
  private final TallyDurationEnum TD;
  private RawTrafficData rtd;
  
  protected VehicleRecFileIndex(TallyDurationEnum td, RawTrafficData timeSortedRTD) {
    TD = td;
    set(timeSortedRTD.observations());
    this.rtd = timeSortedRTD;
  }
  
  public void set(Iterable<VehicleRec> timeSortedVehs) {
    byTime.clear();
    byOrder.clear();
    
    int idx = 0;
    VehicleRecFilePeriod currentPeriod = newVehicleRecFilePeriod(0, 0, -1);
    for (VehicleRec v: timeSortedVehs) {
      long periodTime = TD.trim(v.getTime()).getTimeInMillis();
      if (periodTime < currentPeriod.time)
        throw new RuntimeException("Vehicles not sorted");
      
      if (periodTime > currentPeriod.time) {
        currentPeriod = newVehicleRecFilePeriod(periodTime, idx, byOrder.size());
        byOrder.add(currentPeriod);
        byTime.put(Long.valueOf(currentPeriod.time), currentPeriod);
      } 
      currentPeriod.numberOfVehicles++;

      idx++;
    }
  }
  
  protected VehicleRecFilePeriod newVehicleRecFilePeriod(long t, int fvi, int order) {
    return new VehicleRecFilePeriod(t, fvi, order, this);
  }
  
  /**
   * 
   * @param time
   * @return The corresponding {@link VehicleRecFilePeriod} or null
   */
  public VehicleRecFilePeriod getPeriodForTime(long time) {
    long periodTime = TD.trim(time).getTimeInMillis();
    return byTime.get(Long.valueOf(periodTime));
  }
  
  /**
   * 
   * @param time
   * @return The corresponding {@link VehicleRecFilePeriod} or null
   */
  public VehicleRecFilePeriod getPeriodForTime(TimedObject time) {
    return getPeriodForTime(time.getTime());
  }

  /**
   * 
   * @return The first {@link VehicleRecFilePeriod} or null
   */
  public VehicleRecFilePeriod getPeriod(int order) {
    if (order >= size())
      return null;
    return byOrder.get(order);
  }
  
  public int size() {
    return byOrder.size();
  }

  @Override
  public Iterator<VehicleRecFilePeriod> iterator() {
    return byOrder.iterator();
  }
  
  
  public static class VehicleRecFilePeriod implements TimedObject {
    public final long time;
    private final int order;
    public final int firstVehicleIndex;
    private int numberOfVehicles;
    private final VehicleRecFileIndex parent;
    protected VehicleRecFilePeriod(long t, int fvi, int order, VehicleRecFileIndex p) {
      time = t;
      firstVehicleIndex = fvi;
      this.order = order;
      parent = p;
    }
    
    public int getOrder() {
      return order;
    }
    
    public int getNumberOfVehicles() {
      return numberOfVehicles;
    }
    
    @Override
    public long getTime() {
      return time;
    }
    
    public VehicleRec get(int indexInThisPeriod) {
      return parent.rtd._getVehicleRec(firstVehicleIndex+indexInThisPeriod);
    }
  }
}