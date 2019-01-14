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

import geocounts.traffic.formats.VehicleRec.AxleRec;

/**
 * Fields for axle records
 * @see AxleRecordDef
 * @see VehicleFieldsEnum
 *
 */
public enum AxleFieldsEnum implements IdDesc {
  /**
   * Spacing between the current axle and a leading axle.
   * The first axle spacing is always zero.
   */
  aspace("Axle spacing", "FU length") {
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
  },
  
  /**
   * Weight on one axle
   */
  amass("Axle weight", "FU mass") {
    @Override
    public String get(AxleRec result, UnitsUsed units) throws Exception {
      if (result.amassKg == 0)
        return "0";
      return RawFmtUtils.df1.format(units.mass.toNative(result.amassKg));
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.amassKg = units.mass.toMetric(Double.parseDouble(field));
    }
  },
  /**
   * How weight is distributed between the two sides of the axle.
   * 0 means all weight is on the left side. 100 means all weight is on the right side. 50 means evenly distributed
   */
  alrb("Axle left/right balance", "From 0 to 100") {
    @Override
    public String get(AxleRec result, UnitsUsed units) throws Exception {
      return Integer.toString(result.alrb);
    }

    @Override
    public void set(String field, UnitsUsed units, AxleRec result) throws Exception {
      result.alrb = Integer.parseInt(field);
    }
  },
  
  unknown("Unknown axle field", null) {

    @Override
    public String get(AxleRec veh, UnitsUsed units) throws Exception {
      return null;
    }
    
    @Override
    public void set(String value, UnitsUsed units, AxleRec result) throws Exception {
    }
  };
  
  public final String unitsDescription;
  private final String desc;
  private AxleFieldsEnum(String d, String units) {
    this.desc = d;
    this.unitsDescription = units;
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
}
