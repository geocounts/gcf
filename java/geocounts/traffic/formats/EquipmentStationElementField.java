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
 * @since 1.70
 * @see EquipmentChannelElementField
 */
public enum EquipmentStationElementField {
  /**
   * Type of counter: String
   */
  countertype() {
    @Override
    public void set(UnitsUsed units, Equipment SE, String value) throws Exception {
      SE.addStationRec(this, value);
    }
  },
  /**
   * Version of the counter: String
   */
  counterversion() {
    @Override
    public void set(UnitsUsed units, Equipment SE, String value) throws Exception {
      SE.addStationRec(this, value);
    }
  },
  /**
   * Setup of the counter: String.
   * This may describe the binning system. For example speed x class, or pvr etc.
   * @since 1.283
   */
  countersetup() {
    @Override
    public void set(UnitsUsed units, Equipment SE, String value) throws Exception {
      SE.addStationRec(this, value);
    }
  },
  /**
   * ID of the counter: String.
   * This may be a station ID as programmed into the equipment. E.g. '001'
   * @since 1.302
   */
  counterid() {
    @Override
    public void set(UnitsUsed units, Equipment SE, String value) throws Exception {
      SE.addStationRec(this, value);
    }
  },
  /**
   * Time of last configuration: yyyy-MM-dd HH:mm:ss
   */
  configdate() {
    @Override
    public void set(UnitsUsed units, Equipment SE, String value) throws Exception {
      SE.addStationRec(this, value);
    }
  },
  
  /**
   * A compass direction that a camera is facing. This must be a number between 0 and 359.
   * This is equivalent to 'pan'
   * @since 1.321
   */
  facing() {
    @Override
    public void set(UnitsUsed units, Equipment SE, String value) throws Exception {
      SE.removeStationRec(this);
      if ((value == null) || (value.length() == 0))
        return;
      double v = Double.parseDouble(value);
      if ((v < 0) || (v > 359))
        return;

      SE.addStationRec(this, (int)v);
    }
    
    @Override
    public boolean isNumeric() {
      return true;
    }
  },
  
  /**
   * A latitude, longitude and altitude for the location of the equipment.
   * This can be used to distinguish the location of a camera from the location it is monitoring
   * @since 1.324
   */
  countercoordinate() {

    @Override
    public void set(UnitsUsed units, Equipment SE, String value) throws Exception {
      SE.removeStationRec(this);
      if (value == null)
        return;
      double[] latLng = RawFmtUtils.splitDouble(value, RawTrafficDataHeader.DELIM_FIELD);
      LatLongCoordinate point = new LatLongCoordinate();
      point.setLatLng(latLng);
      if (!point.isSet())
        return;
      
      SE.addStationRec(this, point.getLatitude() + Character.toString(RawTrafficDataHeader.DELIM_FIELD) + point.getLongitude() + Character.toString(RawTrafficDataHeader.DELIM_FIELD) + point.altitude);
    }
    
  },
  
  /*
   * Battery voltage at the end of the study
   *
  battery2() {
    @Override
    public void set(UnitsUsed units, Equipment SE, String value) throws Exception {
      SE.setStationRec(this, value);
    }
  } */
  ;
  
  public boolean isNumeric() {
    return false;
  }
  
  public abstract void set(UnitsUsed units, Equipment SE, String value) throws Exception;
  
  public String get(Equipment SE, String defaultValue) {
    String result = SE.getStationRec(this);
    return result != null ? result : defaultValue;
  }
}
