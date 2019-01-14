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
 * Describes a classification system for vehicle type
 * @see EnumHeaderRecords#TC
 * @see VehicleTallyRec#countsVehType
 */
public final class ClassifiedVehTypeDef extends ClassifiedDef {
  /**
   * Zero or more. 
   */
  private int LowestClassID = 0;
  
  /**
   * Zero or more. 
   * A value of -1 indicates there is no classification of vehicle counts.
   */
  public int HighestClassID = -1;
  
  /**
   * 
   * @param nameOfClassSystem Examples: {@link WKClassificationSystemName#_austroads94 austroads94}, {@link WKClassificationSystemName#_fhwaschemef fhwaschemef}
   * @param highID Highest vehicle classification used in this scheme
   * @param lowID Lowest vehicle classification used in this scheme. This is usually 0 or 1. If not sure, enter 0
   */
  public void setAll(String nameOfClassSystem, int highID, int lowID) {
    this.setName(nameOfClassSystem);
    this.LowestClassID = lowID;
    this.HighestClassID = highID;
  }

  @Override
  public boolean isRecording() {
    return HighestClassID > -1;
  }
  
  @Override
  public int getMinimumBin() {
    return this.LowestClassID;
  }
  
  @Override
  public int getMaximumBin() {
    return this.HighestClassID;
  }
  
  @Override
  public double getTally(VehicleTallyRec tally, int bin) {
    return tally.countsVehType[bin];
  }

  @Override
  public void addToTotals(VehicleTallyRec source, VehicleTallyRec destination) {
    for (int i=0; i<destination.countsVehType.length; i++)
      destination.countsVehType[i] += getTally(source, i);
  }
  
  @Override
  public int getBin(VehicleRec rec) {
    return rec.otherClassifications.vclassType[0];
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.TC;
  }

  @Override
  protected String[] write() {
    if (!isRecording())
      return null;
    if (LowestClassID > 0)
      return new String[]{RawFmtUtils.notNull(getName()), Integer.toString(HighestClassID), null, Integer.toString(LowestClassID)};
    return new String[]{RawFmtUtils.notNull(getName()), Integer.toString(HighestClassID)};
  }
  
  @Override
  protected void read(String[] data) {
    int lowest = 0;
    if (data.length >= 4) {
      try {
        lowest = Integer.parseInt(data[3]);
      } catch (Exception ex) {}
    }
    
    this.setAll(data[0], 
                Integer.parseInt(data[1]),
                lowest);
  }
  
  public void copyFrom(ClassifiedVehTypeDef other) {
    this.setName(other.getName());
    this.LowestClassID = other.LowestClassID;
    this.HighestClassID = other.HighestClassID;
  }

  @Override
  public void clear() {
    super.setName(null);
    this.LowestClassID = 0;
    this.HighestClassID = -1;
  }

  public void resetCounts(VehicleTallyRec tally) {
    tally.countsVehType = new int[this.getNumberOfBinsInRecord()];
  }
}
