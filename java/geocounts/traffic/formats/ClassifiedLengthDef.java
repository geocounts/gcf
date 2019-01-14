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

import geocounts.formats.units.EnumShortLengthUnit;

/**
 * The first bin is from 0 to some defined value.
 * The last bin ends at infinity.
 * @see EnumHeaderRecords#TL
 */
public final class ClassifiedLengthDef extends ClassifiedDef {
  
  /**
   * The maximum length of each bin. Eg the first bin is 6.5 feet.
   * An extra bin is added at the end which corresponds to infinity.
   */
  private double[] endBinsMeters;
  
  private final TallyRecordsDef _parent;
  protected ClassifiedLengthDef(TallyRecordsDef trd) {
    _parent = trd;
  }

  private EnumShortLengthUnit lengthUnits() {
    return _parent.getUnits().getLengthShort();
  }
  
  /**
   * 
   * @param bin First bin is 0
   * @return Length in meters
   */
  public double getBinMaxMeters(int bin) {
    return endBinsMeters == null ? 0 : endBinsMeters[bin];
  }
  
  @Override
  public int getMinimumBin() {
    return 0;
  }
  
  @Override
  public int getMaximumBin() {
    return endBinsMeters != null ? endBinsMeters.length : 0;
  }

  @Override
  public boolean isRecording() {
    return getMaximumBin() > 0;
  }
  
  @Override
  public double getTally(VehicleTallyRec tally, int bin) {
    return tally.countsLength[bin];
  }

  @Override
  public void addToTotals(VehicleTallyRec source, VehicleTallyRec destination) {
    for (int i=0; i<destination.countsLength.length; i++)
      destination.countsLength[i] += getTally(source, i);
  }
  
  @Override
  public int getBin(VehicleRec veh) {
    return getBin(veh.lengthMeters);
  }
  
  public int getBin(double lengthMeters) {
    for (int i=0; i<endBinsMeters.length; i++)
      if (lengthMeters < endBinsMeters[i])
        return i;
    return endBinsMeters.length;
  }
  
  public double getBinLower(int bin) {
    if (bin < 1)
      return 0;
    return endBinsMeters[bin-1];
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.TL;
  }
  
  @Override
  protected String[] write() {
    if (endBinsMeters == null)
      return null;
    String[] result = new String[1 + endBinsMeters.length];
    result[0] = RawFmtUtils.notNull(getName());
    for (int i=0; i<endBinsMeters.length; i++)
      result[i+1] = RawFmtUtils.df3.format(lengthUnits().toNative(endBinsMeters[i]));
    return result;
  }

  @Override
  protected void read(String[] data) {
    super.setName(data[0]);
    endBinsMeters = new double[data.length-1];
    for (int i=0; i<endBinsMeters.length; i++)
      endBinsMeters[i] = lengthUnits().toMetric(Double.parseDouble(data[i+1]));
  }
  
  public void copyFrom(ClassifiedLengthDef other) {
    this.setFrom(other.getName(), other.endBinsMeters);
  }
  
  public void setFrom(String name, double... endBinsM) {
    super.setName(name);
    this.endBinsMeters = endBinsM;
  }
  
  @Override
  public void clear() {
    super.setName(null);
    endBinsMeters = null;
  }

  public void resetCounts(VehicleTallyRec tally) {
    tally.countsLength = new int[this.getNumberOfBinsInRecord()];
  }
}
