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
 * Base class for classified vehicle record.
 * This class lets us connect with GEOCOUNTS classification systems
 *
 */
public abstract class ClassifiedDef extends RawTrafficDataHeaderElement {
  private String Name;

  public void setName(String n) {
    try {
      WKID = WKClassificationSystemName.fromName(n);
    } catch (Exception ifNotWellKnownName) {
      try {
        WKID = WKClassificationSystemName.fromID(Long.parseLong(n));
      } catch (Exception ifNotWellKnownID) {}
      Name = n;
    }
  }
  
  public String getName() {
    return WKID != null ? WKID.getName() : Name;
  }
  
  /**
   * Relates to a well-known-id from the GEOCOUNTS classification system registry.<br />
   * Example: {@link WKClassificationSystemName#_fhwaschemef fhwaschemef}
   */
  public WKClassificationSystemName WKID;
  
  /**
   * Relates to a registered ID
   * Example usage: //geocounts.com/api/cls?clsid=1489023592426
   */
  public final long getID() {
    if (WKID != null)
      return WKID.WKID;
    
    try {
      return Long.parseLong(Name);
    } catch (Exception ex) {
      return 0;
    }
  }
  
  /**
   * 
   * @return True if this tally classification can be used
   */
  public abstract boolean isRecording();
  
  /**
   * Clears this definition
   */
  public abstract void clear();
  
  /**
   * 
   * @return The minimum bin defined by this tally classification. This is typcially 1
   */
  public abstract int getMinimumBin();
  
  /**
   * 
   * @return The maximum bin defined by this tally classification
   */
  public abstract int getMaximumBin();
  
  /**
   * 
   * This is always {@link #getMaximumBin()} + 1. The extra represents the zero classification
   * @return The number of elements in the "tally part" related to this record
   */
  public final int getNumberOfBinsInRecord() {
    return getMaximumBin() + 1;
  }
  
  /**
   * 
   * @param tally The tally to pull a value from
   * @param bin The tally bin. This varies between 0 and the {@link #getMaximumBin() maximum bin}
   * @return The binned value in a tally that this object defines
   */
  public abstract double getTally(VehicleTallyRec tally, int bin);
  
  /**
   * 
   * @param source
   * @param destination
   * @throws Exception
   */
  public abstract void addToTotals(VehicleTallyRec source, VehicleTallyRec destination) throws Exception;
  
  /**
   * @param veh
   * @return The bin to which this vehicle record applies
   */
  public abstract int getBin(VehicleRec veh);

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    if (getName() != null) {
      result.append(getName());
    }
    
    if (getMaximumBin() >= 0) {
      if (result.length() > 0) {
        result.append(' ');
      }
      result.append(getMinimumBin());
      result.append('-');
      result.append(getMaximumBin());
    }
    return result.toString();
  }
}
