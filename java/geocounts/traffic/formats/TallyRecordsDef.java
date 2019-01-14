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
 * Describes how traffic {@link VehicleTallyRec tallies} are recorded
 * @see RawTrafficDataHeader#T
 */
public final class TallyRecordsDef {
  /**
   * {@link VehicleTallyRec Tally record} duration in minutes. This is never null. If there are no tallies, this equals {@link TallyDurationEnum#off off}
   * @see TallyDurationEnum
   */
  public TallyDurationEnum TD = TallyDurationEnum.off;
  private final TimeZoneData SZ;
  
  private final RawTrafficDataHeaderFileDef _parent;
  protected TallyRecordsDef(RawTrafficDataHeaderFileDef f, TimeZoneData SZ) {
    TF = new TallyCountDef();
    _parent = f;
    this.SZ = SZ;
  }
  
  /*
  public TallyFieldNamespaceEnum FN() {
    return _parent.FN.tallytotal;
  }
  */
  
  /**
   * 
   * @return {@link UnitsUsed}
   */
  protected UnitsUsed getUnits() {
    return _parent.FU;
  }
  
  public int periodsPerHour() {
    return TD.periodsPerHour();
  }
  
  public void setPeriod(int minutes) throws GCTrafficFormatException {
    TD = TallyDurationEnum.fromMinutes(minutes);
  }

  public boolean isRecording() {
    return TD != TallyDurationEnum.off;
  }
  
  /**
   * Describes the order of the parts of the count summary data
   * @see TallyPartOrderDef
   */
  public final TallyPartOrderDef TO = new TallyPartOrderDef();
  
  /**
   * Describes fields stored in the count summary data
   * @see TallyCountDef
   */
  public final TallyCountDef TF;

  /**
   * Vehicle type classification.
   * This corresponds to {@link VehicleRec#vclassType}
   * @see ClassifiedVehTypeDef
   */
  public final ClassifiedVehTypeDef TC = new ClassifiedVehTypeDef();
  
  /**
   * Speed based classification.
   * This corresponds to {@link VehicleRec#speedKph}
   * @see ClassifiedSpeedDef
   */
  public final ClassifiedSpeedDef TS = new ClassifiedSpeedDef(this);
  
  /**
   * Length based classification.
   * This corresponds to {@link VehicleRec#lengthMeters}
   * @see ClassifiedLengthDef
   */
  public final ClassifiedLengthDef TL = new ClassifiedLengthDef(this);
  
  /**
   * Speed by length based classification.
   * @see ClassifiedSpeedByLengthDef
   */
  public final ClassifiedSpeedByLengthDef TP = new ClassifiedSpeedByLengthDef(this);
  
  public void copyFrom(TallyRecordsDef other) {
    this.TD = other.TD;
    this.TO.copyFrom(other.TO);
    this.TF.copyFrom(other.TF);
    this.TC.copyFrom(other.TC);
    this.TS.copyFrom(other.TS);
    this.TL.copyFrom(other.TL);
  }
  
  /**
   * 
   * @since 1.9951
   */
  public void mergeFrom(TallyRecordsDef other) {
    if (this.TD == TallyDurationEnum.off)
      this.TD = other.TD;
    
    if (!this.TF.isRecording())
      for (TallyCountField field: other.TF)
        this.TF.addField(field);
    if (!this.TC.isRecording())
      this.TC.copyFrom(other.TC);
    if (!this.TS.isRecording())
      this.TS.copyFrom(other.TS);
    if (!this.TL.isRecording())
      this.TL.copyFrom(other.TL);
  }
  
  /**
   * This creates a new {@link VehicleTallyRec tally}. You must then call {@link RawTrafficData#addTally(VehicleTallyRec)} to add it to the data.
   * @param channelID The {@link ChannelData#getChannelID() channel ID} of the new tally
   * @param period The sub hourly period. See {@link TallyDurationEnum}
   * @param hour The time in milliseconds at the hour of the tally. This value must be trimmed to the hour.
   * @return A new {@link VehicleTallyRec}
   * @throws Exception If the header does not specify a duration
   */
  public VehicleTallyRec create(int channelID, int period, long hour) throws Exception {
    if (TD == TallyDurationEnum.off)
      throw new NoTDException(this, TD);
    VehicleTallyRec result = new VehicleTallyRec(this, channelID, period, SZ.getRecordedTimestampOffset());
    result.setHourAdjustTZ(hour);
    return result;
  }

  public class NoTDException extends GCTrafficFormatException {

    public NoTDException(Object _caller, Object _context) {
      super(_caller, "Header does not specify a duration", _context);
    }
    
  }
}