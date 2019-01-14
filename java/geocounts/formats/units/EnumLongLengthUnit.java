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

public enum EnumLongLengthUnit implements UnitConverter {
  /**
   * Kilometer
   */
  km(1),
  /**
   * Mile
   */
  mi(RawFmtUtils.KILOMETERS_PER_MILE);

  @Override
  public String getId() {
    return name();
  }

  private double toKm;
  private EnumLongLengthUnit(double scale2Km) {
    this.toKm = scale2Km;
  }
  
  @Override
  public double toMetric(double nativeValue) {
    return nativeValue*toKm;
  }

  @Override
  public double toNative(double metricValue) {
    return metricValue/toKm;
  }

  @Override
  public boolean matches(String id) {
    return name().equals(id);
  }
  
}