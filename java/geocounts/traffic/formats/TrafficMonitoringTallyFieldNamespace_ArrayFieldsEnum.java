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

public enum TrafficMonitoringTallyFieldNamespace_ArrayFieldsEnum {
  designvol("k0") {
    @Override
    public TallyCountField create(String key) {
      return new TrafficMonitoringDesignVolumes(Integer.parseInt(key.substring(1)));
    }
  },
  designdist("d0") {
    @Override
    public TallyCountField create(String key) {
      return new TrafficMonitoringDesignDistributions(Integer.parseInt(key.substring(1)));
    }
  },
  daybyhour("aadtdh_") {
    @Override
    public TallyCountField create(String key) {
      int daySunEq0 = Integer.parseInt(key.substring(7, 8));
      int hour = Integer.parseInt(key.substring(9, 11));
      return new TrafficMonitoringDayByHourField(daySunEq0, hour);
    }
  },
  esalcls("esalcls") {
    @Override
    public TallyCountField create(String key) {
      return new TrafficMonitoringESALPerClass(Integer.parseInt(key.substring(7)));
    }
  };
  
  public final String startsWith;
  private TrafficMonitoringTallyFieldNamespace_ArrayFieldsEnum(String startsWith) {
    this.startsWith = startsWith;
  }

  public abstract TallyCountField create(String key);
  
  public static TallyCountField fromID(String name) {
    for (TrafficMonitoringTallyFieldNamespace_ArrayFieldsEnum fe: TrafficMonitoringTallyFieldNamespace_ArrayFieldsEnum.values())
      if (name.startsWith(fe.startsWith))
        return fe.create(name);
    return null;
  }
}