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

import geocounts.formats.units.*;

/**
 * The units used to store information in the file: speed, length, mass
 * @see EnumSpeedUnit
 * @see EnumLengthUnit
 * @see EnumMassUnit
 * @see SystemOfUnits
 * @see EnumUnitsUsed
 */
public class UnitsUsed extends RawTrafficDataHeaderElement {
  /**
   * {@link EnumSpeedUnit} Never null
   */
  private EnumSpeedUnit speed;
  public EnumSpeedUnit getSpeed() {
    return speed;
  }
  
  /**
   * This also sets the {@link #lengthln}
   * @param value {@link EnumSpeedUnit}
   */
  public void setSpeed(EnumSpeedUnit value) {
    speed = value;
    if (speed == EnumSpeedUnit.mph) {
      lengthln = EnumLongLengthUnit.mi;
    } else
      lengthln = EnumLongLengthUnit.km;
  }
  
  /**
   * {@link EnumShortLengthUnit} Never null
   */
  protected EnumShortLengthUnit lengthsh;
  public EnumShortLengthUnit getLengthShort() {
    return lengthsh;
  }
  
  /**
   * {@link EnumLongLengthUnit} Long length unit: Never null
   */
  protected EnumLongLengthUnit lengthln;
  public EnumLongLengthUnit getLengthLong() {
    return lengthln;
  }
  
  /**
   * {@link EnumMassUnit} Never null
   */
  public EnumMassUnit mass;
  
  /**
   * {@link EnumMAC} Never null
   * @since 1.26
   */
  public EnumMAC mac = EnumMAC.maclong;

  /**
   * {@link EnumGPS} Never null
   * @since 1.28
   */
  public EnumGPS gps = EnumGPS.llh;
  /**
   * Creates the units as {@link SystemOfUnits#metric metric}
   */
  public UnitsUsed() {
    SystemOfUnits.metric.set(this);
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.FU;
  }
  
  @Override
  public void read(String[] data) {
    for (String tag: data) {
      EnumUnitsUsed e = EnumUnitsUsed.valueOf(tag);
      e.set(this);
    }
  }

  @Override
  protected String[] write() {
    return new String[]{speed.getId(), lengthsh.getId(), mass.getId()};
  }
  
  /**
   * use the other to set this object
   * @param other
   */
  public void setFrom(UnitsUsed other) {
    speed    = other.speed;
    lengthsh = other.lengthsh;
    lengthln = other.lengthln;
    mass     = other.mass;
    mac      = other.mac;
    gps      = other.gps;
  }

  
  /**
   * Short hand for {@link #metric} or {@link #uscustomary} or {@link #uk}
   * @author scropley
   *
   */
  public enum SystemOfUnits implements IdDesc {
    /**
     * Metric units: kph,meter,km,kg
     */
    metric() {
      @Override
      public void set(UnitsUsed result) {
        result.setSpeed(EnumSpeedUnit.kph);
        result.lengthsh = EnumShortLengthUnit.meter;
        result.mass     = EnumMassUnit.kg;
      }

      @Override
      public boolean match(UnitsUsed units) {
        if (units.speed != EnumSpeedUnit.kph)
          return false;
        if (units.lengthsh != EnumShortLengthUnit.meter)
          return false;
        if (units.lengthln != EnumLongLengthUnit.km)
          return false;
        if (units.mass != EnumMassUnit.kg)
          return false;
        return true;
      }
    },
    
    /**
     * US Customary units: mph,foot,mi,lb
     * @see https://en.wikipedia.org/wiki/United_States_customary_units
     */
    uscustomary() {
      @Override
      public void set(UnitsUsed result) {
        result.setSpeed(EnumSpeedUnit.mph);
        result.lengthsh = EnumShortLengthUnit.foot;
        result.mass     = EnumMassUnit.lb;
      }

      @Override
      public boolean match(UnitsUsed units) {
        if (units.speed != EnumSpeedUnit.mph)
          return false;
        if (units.lengthsh != EnumShortLengthUnit.foot)
          return false;
        if (units.lengthln != EnumLongLengthUnit.mi)
          return false;
        if (units.mass != EnumMassUnit.lb)
          return false;
        return true;
      }
    },
    
    /**
     * UK units: mph,meter,mi,kg
     * @see https://en.wikipedia.org/wiki/Metrication_of_British_transport#Road_system
     */
    uk() {
      @Override
      public void set(UnitsUsed result) {
        result.setSpeed(EnumSpeedUnit.mph);
        result.lengthsh = EnumShortLengthUnit.meter;
        result.mass     = EnumMassUnit.kg;
      }

      @Override
      public boolean match(UnitsUsed units) {
        if (units.speed != EnumSpeedUnit.mph)
          return false;
        if (units.lengthsh != EnumShortLengthUnit.meter)
          return false;
        if (units.lengthln != EnumLongLengthUnit.mi)
          return false;
        if (units.mass != EnumMassUnit.kg)
          return false;
        return true;
      }
    };
    public abstract void set(UnitsUsed result);
    
    /**
     * 
     * @param units
     * @return True if the units match this system of units
     */
    public abstract boolean match(UnitsUsed units);
    
    @Override
    public String getId() {
      return name();
    }
    
    @Override
    public String getDescription() {
      return name();
    }
  }
  
}
