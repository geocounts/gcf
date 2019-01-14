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

public enum EnumMassUnit implements UnitConverter {
  /**
   * Kilograms
   */
  kg(1),
  /**
   * US pounds
   */
  lb(RawFmtUtils.POUNDS_PER_KILOGRAM);

  private final double TO_KG;

  private EnumMassUnit(double tokg) {
    this.TO_KG = tokg;
  }

  @Override
  public String getId() {
    return name();
  }

  @Override
  public double toMetric(double nativeValue) {
    return nativeValue*TO_KG;
  }

  @Override
  public double toNative(double metricValue) {
    return metricValue/TO_KG;
  }
  
  @Override
  public boolean matches(String value) {
    return name().equals(value);
  }
  
  public static EnumMassUnit fromKey(String key) {
    for (EnumMassUnit u: EnumMassUnit.values())
      if (u.matches(key))
        return u;
    return EnumMassUnit.valueOf(key);
  }
}