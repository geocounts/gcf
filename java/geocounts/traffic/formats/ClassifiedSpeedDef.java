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

import geocounts.formats.units.EnumSpeedUnit;

/**
 * Classified speed bins always start at zero. 
 * Each step is the same length except for the first.
 * @see EnumHeaderRecords#TS
 */
public final class ClassifiedSpeedDef extends ClassifiedDef {
  private double firstBinStepKph;
  private double otherBinStepKph;
  
  protected final TallyRecordsDef _parent;
  protected ClassifiedSpeedDef(TallyRecordsDef trd) {
    _parent = trd;
  }

  private EnumSpeedUnit speedUnits() {
    return _parent.getUnits().getSpeed();
  }
  
  /**
   * Number of bins, not including the last bin (which has no upper limit).
   */
  public int numberOfBins;

  @Override
  public boolean isRecording() {
    return numberOfBins > 1;
  }
  
  @Override
  public int getMinimumBin() {
    return 0;
  }
  
  @Override
  public int getMaximumBin() {
    return numberOfBins;
  }
  
  public double getOtherBinStepKph() {
    return this.otherBinStepKph;
  }
  
  public double getFirstBinStepKph() {
    return this.firstBinStepKph;
  }
  
  @Override
  public double getTally(VehicleTallyRec tally, int bin) {
    try {
      return tally.countsSpeed[bin];
    } catch (java.lang.ArrayIndexOutOfBoundsException aiobe) {
      return 0;
    }
  }

  @Override
  public void addToTotals(VehicleTallyRec source, VehicleTallyRec destination) {
    for (int i=0; i<destination.countsSpeed.length; i++)
      destination.countsSpeed[i] += getTally(source, i);
  }

  public double getSumSpeedsKph(VehicleTallyRec tally) {
    if (!isRecording())
      return 0;
    double result = 0;
    int count = 0;
    for (int binID=0; binID<tally.countsSpeed.length; binID++) {
      double avgSpeed = getBinMid(binID);
      result += tally.countsSpeed[binID]*avgSpeed;
      count += tally.countsSpeed[binID];
    }
    return count > 0 ? result/count : 0;
  }
  
  @Override
  public int getBin(VehicleRec veh) {
    return getBin(veh.getSpeedKph());
  }
  
  public int getBin(double kph) {
    if (otherBinStepKph <= 0)
      return 0;
    if (kph < firstBinStepKph)
      return 0;
    double spd = kph-firstBinStepKph;
    int result = (int)Math.floor(spd/otherBinStepKph) + 1;
    if (result > numberOfBins-1)
      result = numberOfBins-1;
    return result;
  }
  
  public double getLower() {
    return 0;
  }
  
  public double getUpper() {
    return getBinUpper(numberOfBins-1);
  }
  
  public double getBinLower(int binID) {
    return binID==0 ? 0 : firstBinStepKph + (otherBinStepKph*(binID-1));
  }
  
  public double getBinUpper(int binID) {
    return getBinLower(binID+1);
  }
  
  public double getBinMid(int binID) {
    double width = getBinUpper(binID) - getBinLower(binID);
    return (width/2) + getBinLower(binID);
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.TS;
  }
  
  @Override
  protected String[] write() {
    if (numberOfBins == 0)
      return null;
    return new String[]{RawFmtUtils.notNull(getName()), 
                        Integer.toString(numberOfBins), 
                        RawFmtUtils.df3.format(speedUnits().toNative(otherBinStepKph)), 
                        RawFmtUtils.df3.format(speedUnits().toNative(firstBinStepKph))};
  }
  
  @Override
  protected void read(String[] data) {
    double _otherBinStep_NativeUnits = Double.parseDouble(data[2]);
    double _firstBinStep_NativeUnits = (data.length > 3 ? Double.parseDouble(data[3]) : _otherBinStep_NativeUnits);

    this.setFrom(data[0], Integer.parseInt(data[1]), speedUnits().toMetric(_otherBinStep_NativeUnits), speedUnits().toMetric(_firstBinStep_NativeUnits));
  }
  
  public void copyFrom(ClassifiedSpeedDef other) {
    this.setFrom(other.getName(), other.numberOfBins, other.otherBinStepKph, other.firstBinStepKph);
  }

  public void setFrom(String name, int numBins, double stepKPH) {
    this.setFrom(name, numBins, stepKPH, stepKPH);
  }
  
  public void setFrom(String name, int numBins, double otherStepKPH, double firstStepKPH) {
    super.setName(name);
    this.numberOfBins = numBins;
    this.firstBinStepKph = firstStepKPH;
    this.otherBinStepKph = otherStepKPH;
  }

  @Override
  public void clear() {
    this.numberOfBins = 0;
    super.setName(null);
    this.firstBinStepKph = 0;
    this.otherBinStepKph = 0;
  }

  public void resetCounts(VehicleTallyRec tally) {
    tally.countsSpeed = new int[this.getNumberOfBinsInRecord()];
  }

}
