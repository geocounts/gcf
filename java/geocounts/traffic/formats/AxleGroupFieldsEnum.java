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
 * @see AxleGroupRecordDef
 * @see AxleGroupRec
 * @since 2.072
 */
public enum AxleGroupFieldsEnum implements IdDesc {
  unknown("Unknown group field", null, GCFieldType.STRING) {

    @Override
    public String get(AxleGroupRec result, UnitsUsed units) throws Exception {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void set(String field, UnitsUsed units, AxleGroupRec result) throws Exception {
      // TODO Auto-generated method stub
    }

    @Override
    public Number getMetricValue(AxleGroupRec target) {
      return 0;
    }
    
  },
  
  gnaxles("Number of axles in the group", null, GCFieldType.INT) {

    @Override
    public String get(AxleGroupRec result, UnitsUsed units) throws Exception {
      return Integer.toString(result.naxles());
    }

    @Override
    public void set(String field, UnitsUsed units, AxleGroupRec result) throws Exception {
      // TODO Auto-generated method stub
      
    }

    @Override
    public Number getMetricValue(AxleGroupRec target) {
      return target.naxles();
    }
    
  },
  
  /**
   * Spacing between the first axle in the group and the last axle in a leading group.
   * The first group spacing is always zero.
   * Metric units are meters.
   * @see AxleFieldsEnum#aspace
   */
  gspace("Group spacing", "FU length", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleGroupRec result, UnitsUsed units) throws Exception {
      double gspaceMeters = result.gspaceMeters();
      if (gspaceMeters == 0)
        return "0";
      return RawFmtUtils.df3.format(units.getLengthShort().toNative(gspaceMeters));
    }

    @Override
    public void set(String field, UnitsUsed units, AxleGroupRec result) throws Exception {
//      result.aspaceMeters = units.getLengthShort().toMetric(Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleGroupRec target) {
      return target.gspaceMeters();
    }
  },
  
  /**
   * Mass of one group.
   * Metric units are kg.
   */
  gmass("Group mass", "FU mass", GCFieldType.DOUBLE) {
    @Override
    public String get(AxleGroupRec target, UnitsUsed units) throws Exception {
      return RawFmtUtils.format1CheckInt(units.mass.toNative(target.massKg()));
    }

    @Override
    public void set(String field, UnitsUsed units, AxleGroupRec result) throws Exception {
      double metricValue = units.mass.toMetric(Double.parseDouble(field));
      result.setMassKg(metricValue);
    }

    @Override
    public Number getMetricValue(AxleGroupRec target) {
      return RawFmtUtils.checkInt(target.massKg());
    }
  },
  
  /**
   * A group type ID
   * @see AxleGroupRec.AGRecType
   */
  gtypeid("Group type ID", null, GCFieldType.DOUBLE) {
    @Override
    public String get(AxleGroupRec target, UnitsUsed units) throws Exception {
      return Integer.toString(target.groupTypeID());
    }

    @Override
    public void set(String field, UnitsUsed units, AxleGroupRec result) throws Exception {
      result.setAxleGroupType((int)Double.parseDouble(field));
    }

    @Override
    public Number getMetricValue(AxleGroupRec target) {
      return target.groupTypeID();
    }
  };

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
  private AxleGroupFieldsEnum(String d, String units, GCFieldType fieldType) {
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
  
  public abstract String get(AxleGroupRec result, UnitsUsed units) throws Exception;
  public abstract void set(String field, UnitsUsed units, AxleGroupRec result) throws Exception;
  
  public abstract Number getMetricValue(AxleGroupRec target);
}
