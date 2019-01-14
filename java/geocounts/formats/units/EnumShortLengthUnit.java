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
package geocounts.formats.units;

import geocounts.traffic.formats.RawFmtUtils;

public enum EnumShortLengthUnit implements UnitConverter {
  /**
   * Meter. This can also be written as "metre"
   */
  meter(1) {
    @Override
    public boolean matches(String value) {
      return name().equals(value) || value.equals("metre");
    }
  },
  /**
   * Foot. This can also be written as "ft"
   */
  foot(RawFmtUtils.METERS_PER_FOOT) {
    @Override
    public boolean matches(String value) {
      return name().equals(value) || value.equals("ft");
    }
  },
  /**
   * Inch
   */
  inch(1/RawFmtUtils.INCHES_PER_METER);

  private final double TO_METERS;

  private EnumShortLengthUnit(double toMeters) {
    this.TO_METERS = toMeters;
  }
  
  @Override
  public String getId() {
    return name();
  }

  @Override
  public double toMetric(double nativeValue) {
    return nativeValue*TO_METERS;
  }

  @Override
  public double toNative(double metricValue) {
    return metricValue/TO_METERS;
  }
  
  @Override
  public boolean matches(String value) {
    return name().equals(value);
  }
  
  public static EnumShortLengthUnit fromKey(String key) {
    for (EnumShortLengthUnit u: EnumShortLengthUnit.values())
      if (u.matches(key))
        return u;
    return EnumShortLengthUnit.valueOf(key);
  }
}
