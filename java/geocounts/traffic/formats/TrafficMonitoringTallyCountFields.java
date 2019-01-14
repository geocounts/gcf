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
 * Represents tallied fields in the trafficmonitoring namespace
 * @see TrafficMonitoringTallyFieldNamespace
 *
 */
public class TrafficMonitoringTallyCountFields {
  
  public TrafficMonitoringTallyCountFields() {
  }
  
  /**
   * Total number of motorized vehicles counted.
   * This number contributes to Annual Average Daily Traffic.
   * @see TrafficMonitoringTallyFieldNamespace#motorized
   */
  public long totalMotorized;
  /**
   * Total number of motorized axles counted.
   */
  public long totalMotorizedAxles;
  /**
   * Total number of pedestrians counted
   */
  public long totalPedestrians;
  /**
   * Total number of bicyles counted
   */
  public long totalBicycles;
  /**
   * The sum of motorized speeds in Kph
   */
  public double sumMotorizedSpeedsKph;
  
  /**
   * Number of counts on up to two sensors
   */
  public int[] sensorCount = new int[2];
  
  /**
   * The average speed in Kph
   */
  public double averageSpeedKph;
  /**
   * The average occupancy as a fraction. This value ranges from 0 to 1
   */
  public double averageOccupancy;
  /**
   * Average air temperature in degrees celcius
   */
  public double averageTempAirC;
  
  
  /**
   * Average received signal strength.
   * @see VehicleFieldsEnum#rssi
   */
  public double averageRSSI;

// ANNUAL TRAFFIC DATA
  
  /**
   * Annual Average Daily Traffic
   * @see TrafficMonitoringTallyFieldNamespace#aadt
   */
  public double aadt;
  
  /**
   * Annual Average Weekday Traffic
   * @see TrafficMonitoringTallyFieldNamespace#aawt
   */
  public double aawt;
  
  /**
   * @see TrafficMonitoringTallyFieldNamespace#aadtobs
   */
  public int aadtobs;

  /**
   * @see TrafficMonitoringTallyFieldNamespace#qc
   */
  public int countaccuracy;
  
  /**
   * Percentage of trucks.
   * @see TrafficMonitoringTallyFieldNamespace#truckpct
   */
  public double truckpct;
  
  /**
   * Annual percentage change of the {@link #aadt AADT} value
   */
  public double aadtpctchange = Double.NaN;
  
  /**
   * Represented in kph
   * @see AnnualTrafficTallyFieldNamespace#speedpct85
   */
  public double speedpct85;

  /**
   * Axle correction factor
   * @see AnnualTrafficTallyFieldNamespace#acf
   */
  public double acf;
  
  /**
   * Add the source record to this object's fields which are "totals"
   * @param source
   */
  public void addToTotals(TrafficMonitoringTallyCountFields source) {
    if (source == null)
      return;
    this.totalMotorized += source.totalMotorized;
    this.totalMotorizedAxles += source.totalMotorizedAxles;
    this.sumMotorizedSpeedsKph += source.sumMotorizedSpeedsKph;
    
    this.totalPedestrians += source.totalPedestrians;
    this.totalBicycles += source.totalBicycles;
    
    for (int i=0; i<source.sensorCount.length; i++)
      this.sensorCount[i] += source.sensorCount[i];
    
    this.aadtobs = source.aadtobs;
  }
}
