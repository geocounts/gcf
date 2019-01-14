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
 * Represents all measured fields in the V record type
 * @see VehicleRecordDef
 * @see EnumRecordType#V Observation
 */
public enum VehicleFieldsEnum implements ObservationField {
  /**
   * 64-bit (signed) integer value representing a sequence number
   * @see VehicleRec#id
   */
  vehicleid("Vehicle ID", null, 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.id >= metric_min) && (value.id < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Long.toString(veh.id);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.id = Long.parseLong(value);
    }

  },
  
  /**
   * Negative values means reverse direction.
   * @see VehicleRec#speedKph
   * @see TrafficMonitoringTallyFieldNamespace#speeda
   */
  speed("Observed speed", "FU speed", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.speedKph >= metric_min) && (value.speedKph < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df1.format(units.getSpeed().toNative(veh.speedKph));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.speedKph = units.getSpeed().toMetric( Double.parseDouble(value) );
    }

  },
  
  naxles("Number of axles", null, 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.naxles >= metric_min) && (value.naxles < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.naxles);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.naxles = Integer.parseInt(value);
    }

  },
  
  /**
   * Distance between the first and last axle of a vehicle (metric = meters)
   */
  wbase("Wheelbase", "FU length", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.wheelbaseMeters >= metric_min) && (value.wheelbaseMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.wheelbaseMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.wheelbaseMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

  },
  
  /**
   * Length of the observation (metric = meters)
   */
  length("Length", "FU length", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.lengthMeters >= metric_min) && (value.lengthMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.lengthMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.lengthMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

  },
  /**
   * Length of the portion overhanging the back axle (metric = meters)
   */
  overhang("Overhang", "FU length", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.overhangMeters >= metric_min) && (value.overhangMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.overhangMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.overhangMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

  },
  
  /**
   * Duration of time a vehicle was delayed or parked
   */
  delay("Delay", "seconds", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.delaySec >= metric_min) && (value.delaySec < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.delaySec);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.delaySec = (int)Double.parseDouble(value);
    }
  },

  /**
   * Height of a vehicle chassis above the surface (metric = meters)
   */
  chassisheight("Chassis height", "FU length", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.chassisheightMeters >= metric_min) && (value.chassisheightMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.chassisheightMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.chassisheightMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

  },

  /**
   * A vehicle chassis code can be any Long integer value
   */
  chassiscode("Chassis code", null, 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.chassisCode >= min) && (value.chassisCode < max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Long.toString(veh.chassisCode);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.chassisCode = Long.parseLong(value);
    }
  },
  
  /**
   * Width of the observation (metric = meters)
   */
  width("Width", "FU length", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.widthMeters >= metric_min) && (value.widthMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.widthMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.widthMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

  },
  
  /**
   * Height of the observation. This is how tall the object is, not its height above sea level. (metric = meters)
   */
  height("Height", "FU length", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.heightMeters >= metric_min) && (value.heightMeters < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.heightMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.heightMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

  },
  
  /**
   * Mass of the observation (metric = kg)
   */
  mass("Mass", "FU mass", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.massKg >= metric_min) && (value.massKg < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df1.format(units.mass.toNative(veh.massKg));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.massKg = units.mass.toMetric( Double.parseDouble(value) );
    }

  },
  
  /**
   * Tare mass of a vehicle. This can also be called the 'curb weight' or 'unladen weight'. (metric = kg)
   * @see <a href="https://en.wikipedia.org/wiki/Curb_weight">Curb weight</a>
   * @see <a href="https://en.wikipedia.org/wiki/Tare_weight">Tare weight</a>
   */
  masstare("Tare Mass", "FU mass", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.massTareKg >= metric_min) && (value.massTareKg < metric_max);
    }

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df1.format(units.mass.toNative(veh.massTareKg));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.massTareKg = units.mass.toMetric( Double.parseDouble(value) );
    }
  },
  /**
   * Legal mass of the vehicle (metric = kg)
   */
  masslegal("Legal Mass", "FU mass", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.massLegalKg >= metric_min) && (value.massLegalKg < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df1.format(units.mass.toNative(veh.massLegalKg));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.massLegalKg = units.mass.toMetric( Double.parseDouble(value) );
    }
  },
  /**
   * The measured vehicle color. Eg FF0000 is 'red'
   */
  color("Color", "6-digit RGB", 0) {

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
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
  center("Distance from center", "FU length", 1) {

    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.centerMeters >= metric_min) && (value.centerMeters < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(veh.centerMeters));
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.centerMeters = units.getLengthShort().toMetric( Double.parseDouble(value) );
    }

  },
  /**
   * State of the traffic signal controlling the vehicle
   * @see SignalPhase
   */
  tsig("Traffic Signal", null, 0) {

    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.tsig != null ? veh.tsig.ordinal() : 0);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.tsig = SignalPhase.values()[ Integer.parseInt(value) ];
    }
  },

  /**
   * An observed type classification which may be defined in the 'TC' header field
   * @see ClassifiedVehTypeDef
   * @see WKObservationClassifications#vclassType
   */
  vclass("Type Class (primary)", null, -1) {
    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.otherClassifications.vclassType[0] >= metric_min) && (value.otherClassifications.vclassType[0] < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.otherClassifications.vclassType[0]);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.otherClassifications.vclassType[0] = Integer.parseInt(value);
    }
  },
  /**
   * An alternate (1) type classification
   */
  vclass1("Type Class (alternate 1)", null, -1) {
    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.otherClassifications.vclassType[1] >= metric_min) && (value.otherClassifications.vclassType[1] < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.otherClassifications.vclassType[1]);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.otherClassifications.vclassType[1] = Integer.parseInt(value);
    }
  },
  /**
   * An alternate (2) type classification
   */
  vclass2("Type Class (alternate 2)", null, -1) {
    @Override
    public boolean isValueBetween(VehicleRec value, double metric_min, double metric_max) {
      return (value.otherClassifications.vclassType[2] >= metric_min) && (value.otherClassifications.vclassType[2] < metric_max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.otherClassifications.vclassType[2]);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.otherClassifications.vclassType[2] = Integer.parseInt(value);
    }
  },
  
  /**
   * A length classification. This property is derived by comparing against a length-based classification system which may be defined in the 'TL' header field
   * @see ClassifiedLengthDef
   * @see WKObservationClassifications#vclassLength
   */
  vclassl("Length Class", null, -1) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.otherClassifications.vclassLength);
    }
    
    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.otherClassifications.vclassLength = Integer.parseInt(value);
    }
  },
  
  /**
   * A numeric value assigned by the sensing equipment. This can explain how a vehicle was resolved (or 'coerced'), or whether the vehicle missed a sensor
   * @see WKObservationClassifications#qc
   * @see TrafficMonitoringTallyFieldNamespace#qc
   */
  qc("Quality Rating", null, 1) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.otherClassifications.qc);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.otherClassifications.qc = Integer.parseInt(value);
    }
  },
  
  /**
   * 
   * @see VehicleRec.VehicleRecTemperature#airC
   */
  tempair("Air temperature", "Degrees centigrade", 1) {
    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.getTemperature().airC >= min) && (value.getTemperature().airC < max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.getTemperature().airC);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.getTemperature().airC = Integer.parseInt(value);
    }
  },

  /**
   * Age of the observation: this may be a vehicle, its driver or a pedestrian
   * @see VehicleRec#age
   */
  age("Age", "Years", 1) {
    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.age >= min) && (value.age < max);
    }
    
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.age);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.age = Integer.parseInt(value);
    }
  },
  
  /**
   * Identity tag of the observation such as its registration number
   * @see VehicleRec#idTag
   */
  idtag("ID Tag", null, 0) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return notNull(veh.idTag);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.idTag = value;
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
  mac("MAC Address", null, 1) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Long.toString(veh.mac);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.mac = units.mac.parse(value);
    }
  },
  /**
   * Used for wireless or bluetooth devices.
   * A value of 0 typically means full strength while a large negative value indicates low strength.
   * @since 1.261
   */
  rssi("Received Signal Strength Indicator", null, 1) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.rssi);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.rssi = Integer.parseInt(value);
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
  bttype("Bluetooth Type", null, -1) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return Integer.toString(veh.bttype);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.bttype = Integer.parseInt(value);
    }
  },
  /**
   * Identifier of a parking bay or berth
   * @see VehicleRec#bayid
   * @see <a href="https://en.wikipedia.org/wiki/Berth_(moorings)">Berth (moorings)</a>
   * @see <a href="https://en.wikipedia.org/wiki/Parking_space">Parking space</a>
   */
  bayid("Parking bay", null, 1) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
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
  lat("Latitude", "Degrees (-90 to 90)", 1) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.formatDouble(veh.latitude);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.latitude = Double.parseDouble(value);
    }

    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.latitude >= min) && (value.latitude < max);
    }
  },
  /**
   * Longitude of the observation
   */
  lng("Longitude", "Degrees (-180 to 180)", 1) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return RawFmtUtils.formatDouble(veh.longitude);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.longitude = Double.parseDouble(value);
    }

    @Override
    public boolean isValueBetween(VehicleRec value, double min, double max) {
      return (value.longitude >= min) && (value.longitude < max);
    }
  },
  
  /**
   * Image of the observation
   * @see VehicleRec#img
   */
  img("Image", "Base 64", 0) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
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
  imgfile("Image File", "URL", 0) {
    @Override
    public String get(VehicleRec veh, UnitsUsed units) throws Exception {
      return notNull(veh.imgfile);
    }

    @Override
    public void set(String value, UnitsUsed units, VehicleRec result) throws Exception {
      result.imgfile = value;
    }
  };

  private final String desc;
  public final String unitsDescription;
  /**
   * Whether this field is numeric
   */
  private final boolean isNumeric;
  
  public boolean isNumeric() {
    return isNumeric;
  }
  
  /**
   * 
   * @param d
   * @param units
   * @param valueType -1 = Classified (Integer), 0 = String, 1 = Numeric
   */
  private VehicleFieldsEnum(String d, String units, int valueType) {
    this.desc = d;
    this.unitsDescription = units;
    this.isNumeric = valueType != 0;
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

}
