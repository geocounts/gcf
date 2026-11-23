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
 * Represents key "type classes" often used in reporting
 * @see TallyRecordsDef#TC
 * @see SpecialClassificationCounter
 * @see WKClassificationSystemName
 */
public enum WKTypeClass {
  motorcycle,
  
  /**
   * @see TrafficMonitoringTallyFieldNamespace#motorized
   */
  motorized() {
    @Override
    public TallyCountField getTallyCountField() {
      return TrafficMonitoringTallyFieldNamespace.motorized;
    }
  },
  
  /**
   * Motorized commercial vehicle - any size or number of trailers. Includes other classes such as truckartic, truckmulti, bus.
   * @see TrafficMonitoringTallyFieldNamespace#truckpct
   */
  truck,
  
  /**
   * Truck with 1 or more trailers (articulated). E.g. Austroads94 class 6 and above, or FHWA SchemeF class 8 and above.
   * This value therefore includes all the {@link #truckmulti}
   */
  truckartic,
  
  /**
   * Truck with 2 or more trailers (multi). E.g. Austroads94 class 10 and above, or FHWA SchemeF class 11 and above
   */
  truckmulti,
  
  /**
   * @see TrafficMonitoringTallyFieldNamespace#bike
   */
  bicycle() {
    @Override
    public TallyCountField getTallyCountField() {
      return TrafficMonitoringTallyFieldNamespace.bike;
    }
  },
  
  /**
   * @see TrafficMonitoringTallyFieldNamespace#peds
   */
  pedestrian() {
    @Override
    public TallyCountField getTallyCountField() {
      return TrafficMonitoringTallyFieldNamespace.peds;
    }
  },
  
  /**
   * Bus
   * @since 2.079
   */
  bus,
  
  /**
   * A vehicle used in verifying the calibration of weigh-in-motion systems
   */
  wimcalibration,
  
  /**
   * An error recorded in the counts
   */
  error;
  
  public TallyCountField getTallyCountField() {
    return null;
  }
}
