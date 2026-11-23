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
 * Fields for axle records
 * @see AxleRec
 * @see AxleRecordDef
 * @see VehicleFieldsEnum
 */
public enum AxleFieldsEnum implements IdDesc {
  unknown("Unknown axle field", null, GCFieldType.STRING) {

    @Override
    public String get(AxleRec veh, UnitsUsed units) throws Exception {
      return null;
    }
    
    @Override
    public void set(String value, UnitsUsed units, AxleRec result) throws Exception {
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      return 0;
    }
  },
  
  /**
   * @since 1.52
   */
  wheels("Wheels", "0, 2, 4", GCFieldType.INT) {

    @Override
    public String get(AxleRec result, UnitsUsed units) throws Exception {
      return Integer.toString(result.wheels);
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.wheels = Integer.parseInt(field);
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      return target.wheels;
    }
  },
  
  /**
   * Spacing between the current axle and a leading axle.
   * The first axle spacing is always zero.
   * Metric units are meters.
   * @see GroupFieldsEnum#gspace
   */
  aspace("Axle spacing", "FU length", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleRec result, UnitsUsed units) throws Exception {
      if (result.aspaceMeters == 0)
        return "0";
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(result.aspaceMeters));
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.aspaceMeters = units.getLengthShort().toMetric(Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      return target.aspaceMeters;
    }
  },
  
  /**
   * Mass of one axle.
   * Metric units are kg.
   */
  amass("Axle mass", "FU mass", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleRec target, UnitsUsed units) throws Exception {
      return getNativeMassStr(target, units);
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.amassKg = units.mass.toMetric(Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      return RawFmtUtils.checkInt(target.amassKg);
    }
  },
  
  /**
   * How weight is distributed between the two sides of the axle.
   * 0 means all weight is on the left side. 100 means all weight is on the right side. 50 means evenly distributed
   */
  alrb("Axle left/right balance", "From 0 to 100", GCFieldType.INT) {
    @Override
    public String get(AxleRec result, UnitsUsed units) throws Exception {
      return Integer.toString(result.alrb);
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.alrb = Integer.parseInt(field);
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      return target.alrb;
    }
  },
  
  
  /**
   * Mass of one axle as measured by the first hit sensor measuring the combined wheel paths
   */
  amass_1("Combined axle mass on sensor 1", "FU mass", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleRec target, UnitsUsed units) throws Exception {
      return getNativeMassStr(target, units);
    }
    
    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.ensureSensorArray(0).amassKg = units.mass.toMetric(Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      AxleRec.AxleSensorRec rec = target.getSensor(0);
      return rec != null ? rec.amassKg : null;
    }
  },
  
  /**
   * Mass of one axle as measured by the second hit sensor measuring the combined wheel paths
   */
  amass_2("Combined axle mass on sensor 2", "FU mass", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleRec target, UnitsUsed units) throws Exception {
      return getNativeMassStr(target, units);
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.ensureSensorArray(1).amassKg = units.mass.toMetric(Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      AxleRec.AxleSensorRec rec = target.getSensor(1);
      return rec != null ? rec.amassKg : null;
    }
  },
  
  /**
   * Mass of the left wheel path as measured by the first hit sensor
   */
  amass_1l("Left side mass, sensor 1", "FU mass", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleRec target, UnitsUsed units) throws Exception {
      return getNativeMassStr(target, units);
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.ensureSensorArray(0).amassLeft = units.mass.toMetric(Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      AxleRec.AxleSensorRec rec = target.getSensor(0);
      return rec != null ? rec.amassLeft : null;
    }
  },

  /**
   * Mass of the right wheel path as measured by the first hit sensor
   */
  amass_1r("Right side mass, sensor 1", "FU mass", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleRec target, UnitsUsed units) throws Exception {
      return getNativeMassStr(target, units);
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.ensureSensorArray(0).amassRight = units.mass.toMetric(Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      AxleRec.AxleSensorRec rec = target.getSensor(0);
      return rec != null ? rec.amassRight : null;
    }
  },
  
  /**
   * Mass of the left wheel path as measured by the second hit sensor
   */
  amass_2l("Left side mass, sensor 2", "FU mass", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleRec target, UnitsUsed units) throws Exception {
      return getNativeMassStr(target, units);
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.ensureSensorArray(1).amassLeft = units.mass.toMetric(Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      AxleRec.AxleSensorRec rec = target.getSensor(1);
      return rec != null ? rec.amassLeft : null;
    }
  },

  /**
   * Mass of the right wheel path as measured by the second hit sensor
   */
  amass_2r("Right side mass, sensor 2", "FU mass", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleRec target, UnitsUsed units) throws Exception {
      return getNativeMassStr(target, units);
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.ensureSensorArray(1).amassRight = units.mass.toMetric(Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleRec target) {
      AxleRec.AxleSensorRec rec = target.getSensor(1);
      return rec != null ? rec.amassRight : null;
    }
  };
  
  protected String getNativeMassStr(AxleRec target, UnitsUsed units) throws Exception {
    Number n = getMetricValue(target);
    if ((n == null) || (n.doubleValue() == 0))
      return "0";
    return RawFmtUtils.format1CheckInt(units.mass.toNative(n.doubleValue()));
  }
  
  public final String unitsDescription;
  private final String desc;
  
  /**
   * @see GCFieldType
   */
  private final GCFieldType fieldType;


  public GCFieldType getFieldType() {
    return fieldType;
  }
  
  /**
   * 
   * @param d
   * @param units
   * @param fieldType {@link GCFieldType} -1 = Classified (Integer), 0 = String, 1 = Numeric
   */
  private AxleFieldsEnum(String d, String units, GCFieldType fieldType) {
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
  
  public abstract String get(AxleRec result, UnitsUsed units) throws Exception;
  public abstract void set(String field, UnitsUsed units, AxleRec result) throws Exception;
  
  public abstract Number getMetricValue(AxleRec target);
  
}
