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

import geocounts.formats.units.EnumGPS;
import geocounts.formats.units.EnumLongLengthUnit;
import geocounts.formats.units.EnumMAC;
import geocounts.formats.units.EnumMassUnit;
import geocounts.formats.units.EnumShortLengthUnit;
import geocounts.formats.units.EnumSpeedUnit;

/**
 * List of all tags in the FU header that describe the units used throughout the data
 * @see UnitsUsed
 * @see EnumHeaderRecords#FU
 */
public enum EnumUnitsUsed {
  /**
   * Set speed to kph, length long to km, length short to meter, mass to kg.
   */
  metric {
    @Override
    public void set(UnitsUsed result) {
      UnitsUsed.SystemOfUnits.metric.set(result);
    }
  },
  /**
   * Set speed to mph, length long to mi, length short to foot, mass to lb.
   */
  uscustomary {
    @Override
    public void set(UnitsUsed result) {
      UnitsUsed.SystemOfUnits.uscustomary.set(result);
    }
  },

  /**
   * Set speed to mph, length long to mi, length short to meter, mass to kg.
   */
  uk {
    @Override
    public void set(UnitsUsed result) {
      UnitsUsed.SystemOfUnits.uk.set(result);
    }
  },

  
  /**
   * speed: Set to kilometers per hour
   */
  kph {
    @Override
    public void set(UnitsUsed result) {
      result.setSpeed(EnumSpeedUnit.kph);
    }
  },

  /**
   * speed: Set to miles per hour
   */
  mph {
    @Override
    public void set(UnitsUsed result) {
      result.setSpeed(EnumSpeedUnit.mph);
    }
  },

  /**
   * speed: Set to meters per second
   */
  mps {
    @Override
    public void set(UnitsUsed result) {
      result.setSpeed(EnumSpeedUnit.mps);
    }
  },
  
  /**
   * length short: Set to meter
   */
  meter {
    @Override
    public void set(UnitsUsed result) {
      metre.set(result);
    }
  },

  /**
   * length short: Set to meter
   */
  metre {
    @Override
    public void set(UnitsUsed result) {
      result.lengthsh = EnumShortLengthUnit.meter;
    }
  },

  /**
   * length short: Set to feet
   */
  foot {
    @Override
    public void set(UnitsUsed result) {
      ft.set(result);
    }
  },

  /**
   * length short: Set to feet
   */
  ft {
    @Override
    public void set(UnitsUsed result) {
      result.lengthsh = EnumShortLengthUnit.foot;
    }
  },

  /**
   * length short: Set to inch
   */
  inch {
    @Override
    public void set(UnitsUsed result) {
      result.lengthsh = EnumShortLengthUnit.inch;
    }
  },

  /**
   * length long: Set to kilometers
   */
  km {
    @Override
    public void set(UnitsUsed result) {
      result.lengthln = EnumLongLengthUnit.km;
    }
  },
  
  /**
   * length long: Set to miles
   */
  mi {
    @Override
    public void set(UnitsUsed result) {
      result.lengthln = EnumLongLengthUnit.mi;
    }
  },

  /**
   * mass: Set to kilograms
   */
  kg {
    @Override
    public void set(UnitsUsed result) {
      result.mass = EnumMassUnit.kg;
    }
  },

  /**
   * mass: Set to pounds
   */
  lb {
    @Override
    public void set(UnitsUsed result) {
      result.mass = EnumMassUnit.lb;
    }
  },

  /**
   * mac addressing: Set to 12 digit hex
   */
  mac12 {
    @Override
    public void set(UnitsUsed result) {
      result.mac = EnumMAC.mac12;
    }
  },
  
  /**
   * mac addressing: Set to 17 digit hex
   */
  mac17 {
    @Override
    public void set(UnitsUsed result) {
      result.mac = EnumMAC.mac17;
    }
  },

  /**
   * mac addressing: Set to a Long value
   */
  maclong {
    @Override
    public void set(UnitsUsed result) {
      result.mac = EnumMAC.maclong;
    }
  },
  
  /**
   * coordinates: Set to lat, lng, height
   */
  llh {
    @Override
    public void set(UnitsUsed result) {
      result.gps = EnumGPS.llh;
    }
  },

  /**
   * coordinates: Set to Open Location Code
   */
  olc {
    @Override
    public void set(UnitsUsed result) {
      result.gps = EnumGPS.olc;
    }
  };
  
  public abstract void set(UnitsUsed result);
  
  public String getId() {
    return name();
  }
}
