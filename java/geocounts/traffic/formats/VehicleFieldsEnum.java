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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Represents all measured fields in the V record type
 * @see VehicleRecordDef
 * @see EnumRecordType#V Observation
 * @see AxleFieldsEnum
 * @see AxleGroupFieldsEnum
 * @see TrafficMonitoringTallyFieldNamespace
 */
public enum VehicleFieldsEnum implements ObservationField, Function<VehicleRec, Number> {
  
  /**
   * 64-bit (signed) integer value representing a sequence number
   * @see VehicleRec#id
   */
  vehicleid("Vehicle ID", null, GCFieldType.LONG) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.id >= metric_min) && (value.id < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Long.toString(veh.id);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.id = Long.parseLong(value);
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.id;
    }
  },
  
  channelid("Channel ID", null, GCFieldType.INT) {
    @Override
    public boolean isValueBetween(VehicleRec veh, double metric_min, double metric_max) {
      return (veh.channelID >= metric_min) && (veh.channelID < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.channelID);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.channelID = Integer.parseInt(value);
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.channelID;
    }
  },
  
  hod("Hour of day", null, GCFieldType.INT) {
    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      int hod = getNumber(value).intValue();
      return (hod >= metric_min) && (hod < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return getNumber(veh).toString();
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return RawFmtUtils.getHour(veh.time);
    }
  },
  
  /**
   * Negative values means reverse direction.
   * @see VehicleRec#speedKph
   * @see TrafficMonitoringTallyFieldNamespace#speeda
   */
  speed("Observed speed", "FU speed", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.speedKph >= metric_min) && (value.speedKph < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return RawFmtUtils.format1CheckInt(units.getSpeed().toNative(veh.speedKph));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.speedKph = units.getSpeed().toMetric( Double.parseDouble(value) );
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return RawFmtUtils.checkInt(veh.speedKph);
    }

  },
  
  naxles("Number of axles", null, GCFieldType.INT) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      int naxles = value.naxles();
      return (naxles >= metric_min) && (naxles < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.naxles());
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.naxles = Integer.parseInt(value);
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.naxles();
    }
    
    @Override
    public boolean canCompute(RawTrafficDataHeader header) {
      return header.VV.contains(this) || header.VA.isRecording();
    }
  },
  
  /**
   * @since 2.04
   */
  ngroups("Number of groups", null, GCFieldType.INT) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.ngroups() >= metric_min) && (value.ngroups() < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.ngroups());
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.ngroups = Integer.parseInt(value);
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.ngroups();
    }

    @Override
    public boolean canCompute(RawTrafficDataHeader header) {
      return header.VV.contains(this) || header.VG.isRecording() || header.VA.contains(AxleFieldsEnum.aspace);
    }
  },
  
  /**
   * A string of axle groups showing the number of axles in each, separated by hyphen.
   * E.g. '1-2-2'
   * This is computed from the axle groups and is a read-only property.
   * @since 2.04
   */
  gpattern("Group pattern", null, GCFieldType.STRING) {

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return notNull(VehicleRecText.instance.formatGroupString(veh.axleGroups));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {

    }
  },
  
  /**
   * @since 2.075
   */
  headway("Headway", "Milliseconds", GCFieldType.LONG) {
    
    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.headwayMSecs >= metric_min) && (value.headwayMSecs < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Long.toString(veh.headwayMSecs);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.headwayMSecs = Long.parseLong(value);
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.headwayMSecs;
    }

  },
  
  /**
   * Distance between the first and last axle of a vehicle (metric = meters)
   */
  wbase("Wheelbase", "FU length", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec veh, double metric_min, double metric_max) {
      double wheelbaseMeters = getNumber(veh).doubleValue();
      return (wheelbaseMeters >= metric_min) && (wheelbaseMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      double wheelbaseMeters = getNumber(veh).doubleValue();
      double value = units.getLengthShort().toNative(wheelbaseMeters);
      if (value == 0.0)
        return ZERO;
      return RawFmtUtils.df3.format(value);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.wheelbaseMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      if (veh.wheelbaseMeters > 0)
        return veh.wheelbaseMeters;
      return veh.axles.wbase();
    }

    @Override
    public boolean canCompute(RawTrafficDataHeader header) {
      return header.VV.contains(this) || header.VA.contains(AxleFieldsEnum.aspace);
    }
  },
  
  /**
   * Length of the observation (metric = meters)
   */
  length("Length", "FU length", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.lengthMeters >= metric_min) && (value.lengthMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      double value = units.getLengthShort().toNative(veh.lengthMeters);
      if (value == 0.0)
        return ZERO;
      return RawFmtUtils.df3.format(value);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.lengthMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.lengthMeters;
    }

  },
  
  /**
   * Difference between the length and wheelbase. 
   * Sometimes a negative value may arise.
   * This returns 0 if either the length or wheelbase are 0.
   * @since 2.078
   */
  overhang("Overhang", "FU length", GCFieldType.DOUBLE) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      double value = units.getLengthShort().toNative(getNumber(veh).doubleValue());
      if (value == 0.0)
        return ZERO;
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(value));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      Number len = length.getNumber(veh);
      if (len.doubleValue() == 0)
        return 0;
      Number wbs = wbase.getNumber(veh);
      if (wbs.doubleValue() == 0)
        return 0;
      return len.doubleValue() - wbs.doubleValue();
    }

    @Override
    public boolean canCompute(RawTrafficDataHeader header) {
      return header.VV.contains(this) || (length.canCompute(header) && wbase.canCompute(header));
    }
  },
  
  /**
   * Length of the portion overhanging the rear axle (metric = meters)
   */
  overhangrear("Overhang Rear", "FU length", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.overhangRearMeters >= metric_min) && (value.overhangRearMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      double value = units.getLengthShort().toNative(veh.overhangRearMeters);
      if (value == 0.0)
        return ZERO;
      return RawFmtUtils.df3.format(value);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.overhangRearMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.overhangRearMeters;
    }
  },
  
  /**
   * Length of the portion overhanging the front axle (metric = meters)
   * @since 2.05
   */
  overhangfront("Overhang Front", "FU length", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.overhangFrontMeters >= metric_min) && (value.overhangFrontMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      double value = units.getLengthShort().toNative(veh.overhangFrontMeters);
      if (value == 0.0)
        return ZERO;
      return RawFmtUtils.df3.format(value);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.overhangFrontMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.overhangFrontMeters;
    }
  },
  
  /**
   * Duration of time a vehicle was delayed or parked
   */
  delay("Delay", "seconds", GCFieldType.INT) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.delaySec >= metric_min) && (value.delaySec < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.delaySec);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.delaySec = (int)Double.parseDouble(value);
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.delaySec;
    }
  },

  /**
   * Height of a vehicle chassis above the surface (metric = meters)
   */
  chassisheight("Chassis height", "FU length", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.chassisheightMeters >= metric_min) && (value.chassisheightMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      double value = units.getLengthShort().toNative(veh.chassisheightMeters);
      if (value == 0.0)
        return ZERO;
      return RawFmtUtils.df3.format(value);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.chassisheightMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.chassisheightMeters;
    }
  },

  /**
   * A vehicle chassis code can be any Long integer value
   */
  chassiscode("Chassis code", null, GCFieldType.LONG) {

    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.chassisCode >= min) && (value.chassisCode < max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Long.toString(veh.chassisCode);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.chassisCode = Long.parseLong(value);
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.chassisCode;
    }
  },
  
  /**
   * Width of the observation (metric = meters)
   */
  width("Width", "FU length", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.widthMeters >= metric_min) && (value.widthMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.widthMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.widthMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.widthMeters;
    }
  },
  
  /**
   * Height of the observation. This is how tall the object is, not its height above sea level. (metric = meters)
   */
  height("Height", "FU length", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.heightMeters >= metric_min) && (value.heightMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.heightMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.heightMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.heightMeters;
    }
  },
  
  /**
   * Mass of the observation (metric = kg)
   */
  mass("Mass", "FU mass", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec veh, double metric_min, double metric_max) {
      double massKg = getNumber(veh).doubleValue(); 
      return (massKg >= metric_min) && (massKg < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return RawFmtUtils.format1CheckInt(units.mass.toNative(getDouble(veh)));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.massKg = units.mass.toMetric( Double.parseDouble(value) );
    }

    @Override
    public Number getNumber(VehicleRec value) {
      return RawFmtUtils.checkInt(getDouble(value));
    }
    
    private double getDouble(VehicleRec value) {
      if (value.massKg > 0)
        return value.massKg;
      double result = 0;
      for (AxleRec axle: value.axles) {
        result += axle.amassKg;
      }
      return result;
    }
    
    @Override
    public boolean canCompute(RawTrafficDataHeader header) {
      return header.VV.contains(this) || header.VA.contains(AxleFieldsEnum.amass);
    }
  },
  
  /**
   * Tare mass of a vehicle. This can also be called the 'curb weight' or 'unladen weight'. (metric = kg)
   * @see <a href="https://en.wikipedia.org/wiki/Curb_weight">Curb weight</a>
   * @see <a href="https://en.wikipedia.org/wiki/Tare_weight">Tare weight</a>
   */
  masstare("Tare Mass", "FU mass", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.massTareKg >= metric_min) && (value.massTareKg < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return RawFmtUtils.format1CheckInt(units.mass.toNative(veh.massTareKg));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.massTareKg = units.mass.toMetric( Double.parseDouble(value) );
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return RawFmtUtils.checkInt(value.massTareKg);
    }
  },
  
  /**
   * Legal mass of the vehicle (metric = kg)
   */
  masslegal("Legal Mass", "FU mass", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.massLegalKg >= metric_min) && (value.massLegalKg < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return RawFmtUtils.format1CheckInt(units.mass.toNative(veh.massLegalKg));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.massLegalKg = units.mass.toMetric( Double.parseDouble(value) );
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return RawFmtUtils.checkInt(value.massLegalKg);
    }
  },
  
  /**
   * Damage expressed as equivalent standard axle loads.
   * The algorithm for computing this value is external to this specification.
   * @since 2.072
   */
  esal("ESAL", null, GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.esal >= metric_min) && (value.esal < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      if (veh.esal == 0.0)
        return ZERO;
      return RawFmtUtils.df3.format(veh.esal);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.esal = Double.parseDouble(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.esal;
    }
  },
  
  /**
   * The measured vehicle color. Eg FF0000 is 'red'
   */
  color("Color", "6-digit RGB", GCFieldType.STRING) {

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return notNull(veh.color);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.color = value;
    }

  },
  
  /**
   * Distance from the center of the lane (metric = meters)
   */
  center("Distance from center", "FU length", GCFieldType.DOUBLE) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.centerMeters >= metric_min) && (value.centerMeters < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.centerMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.centerMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.centerMeters;
    }
  },
  
  /**
   * State of the traffic signal controlling the vehicle
   * @see SignalPhase
   */
  tsig("Traffic Signal", null, GCFieldType.ENUM_INT) {

    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.tsig != null ? veh.tsig.ordinal() : 0);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.tsig = SignalPhase.getSignalPhase(value);
    }
    
    @Override
    public Number getNumber(VehicleRec veh) {
      return veh.tsig != null ? veh.tsig.ordinal() : 0;
    }
  },

  /**
   * An observed type classification which may be defined in the 'TC' header field
   * @see ClassifiedVehTypeDef
   * @see WKObservationClassifications#vclassType WKObservationClassifications.vclassType[0]
   */
  vclass("Type Class (primary)", null, GCFieldType.ENUM_INT) {
    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.getTCValue() >= metric_min) && (value.getTCValue() < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.getTCValue());
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.setTCValue(Integer.parseInt(value));
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.getTCValue();
    }
  },
  
  /**
   * An alternate (1) type classification
   * @see WKObservationClassifications#vclassType WKObservationClassifications.vclassType[1]
   */
  vclass1("Type Class (alternate 1)", null, GCFieldType.ENUM_INT) {
    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.otherClassifications.vclassType[1] >= metric_min) && (value.otherClassifications.vclassType[1] < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.otherClassifications.vclassType[1]);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.otherClassifications.vclassType[1] = Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.otherClassifications.vclassType[1];
    }
  },
  
  /**
   * An alternate (2) type classification
   * @see WKObservationClassifications#vclassType WKObservationClassifications.vclassType[2]
   */
  vclass2("Type Class (alternate 2)", null, GCFieldType.ENUM_INT) {
    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.otherClassifications.vclassType[2] >= metric_min) && (value.otherClassifications.vclassType[2] < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.otherClassifications.vclassType[2]);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.otherClassifications.vclassType[2] = Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.otherClassifications.vclassType[2];
    }
  },
  
  /**
   * A length classification. This property is derived by comparing against a length-based classification system which may be defined in the 'TL' header field
   * @see ClassifiedLengthDef
   * @see WKObservationClassifications#vclassLength
   */
  vclassl("Length Class", null, GCFieldType.ENUM_INT) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.otherClassifications.vclassLength);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.otherClassifications.vclassLength = Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.otherClassifications.vclassLength;
    }
  },
  
  /**
   * A numeric (integer) value assigned by the sensing equipment. This can explain how a vehicle was resolved (or 'coerced'), or whether the vehicle missed a sensor
   * @see WKObservationClassifications#qc
   * @see TrafficMonitoringTallyFieldNamespace#qc
   */
  qc("Quality Rating", null, GCFieldType.INT) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.otherClassifications.qc);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.otherClassifications.qc = Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.otherClassifications.qc;
    }
  },
  
  /**
   * @see TrafficMonitoringTallyFieldNamespace#tempair
   * @see VehicleRec.VehicleRecTemperature#airC
   */
  tempair("Air temperature", "Degrees centigrade", GCFieldType.INT) {
    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.getTemperature().air.C >= min) && (value.getTemperature().air.C < max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.getTemperature().air.C);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.getTemperature().air.C = (short)Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.getTemperature().air.C;
    }
  },
  
  /**
   * @see TrafficMonitoringTallyFieldNamespace#tempsurf
   * @see VehicleRec.VehicleRecTemperature#roadC
   * @since 1.322
   */
  tempsurf("Surface temperature", "Degrees centigrade", GCFieldType.INT) {
    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.getTemperature().surface.C >= min) && (value.getTemperature().surface.C < max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.getTemperature().surface.C);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.getTemperature().surface.C = (short)Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.getTemperature().surface.C;
    }
  },
  
  wearinghelmet("Wearing helmet", null, GCFieldType.ENUM_INT) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.getWearing().helmet);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.getWearing().helmet = (short)Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.getWearing().helmet;
    }
  },
  
  wearingsafetyjacket("Wearing safety jacket", null, GCFieldType.ENUM_INT) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.getWearing().safetyJacket);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.getWearing().safetyJacket = (short)Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.getWearing().safetyJacket;
    }
  },

  /**
   * Age of the observation: this may be a vehicle, its driver or a pedestrian
   * @see VehicleRec#age
   */
  age("Age", "Years", GCFieldType.INT) {
    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.age >= min) && (value.age < max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.age);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.age = Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.age;
    }
  },
  
  /**
   * Identity tag of the observation such as its registration number
   * @see VehicleRec#idTag
   */
  idtag("ID Tag", null, GCFieldType.STRING) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      if (!veh.hasLicensePlate())
        return notNull(null);
      return notNull(veh.getLicensePlate().toString());
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) {
      if (value == null) {
        result.clearLicensePlate();
      } else {
        int pos = value.indexOf('|');
        if (pos > -1) {
          result.setLicensePlate(value.substring(0, pos), value.substring(pos+1));
        } else
          result.setLicensePlate(null, value);
      }
    }
  },
  
  /**
   * MAC address of the observation.
   * Default format is an unsigned Long value, not 6 hex values.
   * See 'FU' header for details on how to change this format.
   * @see VehicleRec#mac
   * @see MACAddress
   * @see UnitsUsed.EnumMAC
   */
  mac("MAC Address", null, GCFieldType.LONG) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Long.toString(veh.mac);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.mac = units.mac.parse(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.mac;
    }
  },
  
  /**
   * Used for wireless or bluetooth devices.
   * A value of 0 typically means full strength while a large negative value indicates low strength.
   * @since 1.261
   */
  rssi("Received Signal Strength Indicator", null, GCFieldType.INT) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.rssi);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.rssi = Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.rssi;
    }
  },
  
  /**
   * Type of bluetooth device signal
   * <ul>
   * <li>0 - Standard (V2 Bluetooth)</li>
   * <li>1 - Low Energy Public (V4 Bluetooth)</li>
   * <li>2 - Low Energy Random (V4 Bluetooth)</li>
   * </ul>
   * @since 1.261
   */
  bttype("Bluetooth Type", null, GCFieldType.ENUM_INT) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return Integer.toString(veh.bttype);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.bttype = Integer.parseInt(value);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.bttype;
    }
  },
  
  /**
   * Identifier of a parking bay or berth
   * @see VehicleRec#bayid
   * @see <a href="https://en.wikipedia.org/wiki/Berth_(moorings)">Berth (moorings)</a>
   * @see <a href="https://en.wikipedia.org/wiki/Parking_space">Parking space</a>
   */
  bayid("Parking bay", null, GCFieldType.STRING) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return notNull(veh.bayid);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.bayid = value;
    }
  },
  
  /**
   * Latitude of the observation
   */
  lat("Latitude", "Degrees (-90 to 90)", GCFieldType.DOUBLE) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return RawFmtUtils.formatDouble(veh.coordinate.getLatitude());
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.coordinate.setLatitude(Double.parseDouble(value));
    }

    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.coordinate.getLatitude() >= min) && (value.coordinate.getLatitude() < max);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.coordinate.getLatitude();
    }
  },
  
  /**
   * Longitude of the observation
   */
  lng("Longitude", "Degrees (-180 to 180)", GCFieldType.DOUBLE) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return RawFmtUtils.formatDouble(veh.coordinate.getLongitude());
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.coordinate.setLongitude(Double.parseDouble(value));
    }

    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.coordinate.getLongitude() >= min) && (value.coordinate.getLongitude() < max);
    }
    
    @Override
    public Number getNumber(VehicleRec value) {
      return value.coordinate.getLongitude();
    }
  },
  
  /**
   * Image of the observation
   * @see VehicleRec#img
   */
  img("Image", "Base 64", GCFieldType.STRING) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return notNull(veh.img);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.img = value;
    }
  },

  /**
   * Reference to an image file of the observation
   * @see VehicleRec#imgfile
   */
  imgfile("Image File", "URL", GCFieldType.STRING) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) {
      return notNull(veh.imgfile);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.imgfile = value;
    }
  };

  protected final static String ZERO = "0";
  
  private final String desc;
  public final String unitsDescription;
  
  /**
   * Whether this field is numeric. Never null
   */
  private final GCFieldType fieldType;

  @Override
  public GCFieldType getFieldType() {
    return fieldType;
  }
  
  /**
   * 
   * @param d Description
   * @param units
   * @param fieldType {@link GCFieldType} -1 = Classified (Integer), 0 = String, 1 = Numeric
   */
  private VehicleFieldsEnum(String d, String units, GCFieldType fieldType) {
    this.desc = d;
    this.unitsDescription = units;
    this.fieldType = fieldType;
  }

  @Override
  public String getId() {
    return name();
  }

  @Override
  public String getDescription() {
    return desc;
  }

  protected String notNull(String value) {
    return value == null ? "" : value;
  }
  
  /**
   * 
   * @param value
   * @param metric_min
   * @param metric_max
   * @return True if this fields value is between two values (metric units)
   */
  public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
    return false;
  }

  @Override
  public final Object getValue(VehicleRec veh, UnitsUsed units) throws Exception {
    String result = get(veh, units);
    return getFieldType().fromString(result);
  }

  public Number getNumber(VehicleRec target) {
    return null;
  }
  
  @Override
  public Number apply(VehicleRec target) {
    return getNumber(target);
  }

  public void sortMetric(List<VehicleRec> recs) {
    sort(recs, UnitsUsed.SystemOfUnits.metric.createDefault());
  }
  
  public void sort(List<VehicleRec> recs, UnitsUsed units) {
    Collections.sort(recs, new VehicleFieldsEnumComparator(this, units));
  }
  
  public boolean canCompute(RawTrafficDataHeader header) {
    return header.VV.contains(this);
  }
}
