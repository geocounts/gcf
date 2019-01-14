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

/**
 * 
 * @see #kph Kilometres per hour
 * @see #mps Meters per second
 * @see #mph Miles per hour
 *
 */
public enum EnumSpeedUnit implements UnitConverter {
  /**
   * Kilometers per hour
   */
  kph(1),
  /**
   * Meters per second
   */
  mps(1.0/3.6),
  /**
   * Miles per hour
   */
  mph(RawFmtUtils.KILOMETERS_PER_MILE);

  private final double TO_KPH;

  private EnumSpeedUnit(double tokph) {
    this.TO_KPH = tokph;
  }

  @Override
  public String getId() {
    return name();
  }
  
  @Override
  public double toMetric(double nativeValue) {
    return nativeValue*TO_KPH;
  }

  @Override
  public double toNative(double metricValue) {
    return metricValue/TO_KPH;
  }

  @Override
  public boolean matches(String value) {
    return name().equals(value);
  }
  
  public static EnumSpeedUnit fromKey(String key) {
    for (EnumSpeedUnit u: EnumSpeedUnit.values())
      if (u.matches(key))
        return u;
    return EnumSpeedUnit.valueOf(key);
  }
  
}
