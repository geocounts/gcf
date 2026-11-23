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

import java.util.Map;

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
   * @see TrafficMonitoringTallyFieldNamespace#peds
   */
  public long totalPedestrians;
  /**
   * Total number of bicyles counted
   * @see TrafficMonitoringTallyFieldNamespace#bike
   */
  public long totalBicycles;
  /**
   * Total number of motorcycles counted
   * @see TrafficMonitoringTallyFieldNamespace#mcycles
   */
  public long totalMotorcycles;
  
  /**
   * Number of counts on up to two sensors
   */
  public int[] sensorCount = new int[2];

  public static class SpeedStats {
    /**
     * The sum of motorized speeds in Kph
     */
    public double sumMotorizedKph;
    /**
     * The average speed in Kph
     */
    public double averageKph;
    
    public Double computeAverageKph() {
      return totalMotorized > 0 ? sumMotorizedKph/totalMotorized : null;
    }
    
    /**
     * The total number of motorized observations used in computing average speeds
     */
    public int totalMotorized;
    
    /**
     * Represented in kph
     * @see AnnualTrafficTallyFieldNamespace#speedpct85
     */
    public double pct85;
    
    protected void add(SpeedStats source) {
      sumMotorizedKph += source.sumMotorizedKph;
      totalMotorized += source.totalMotorized;
    }
    
    /**
     * 
     * @param motorized Number of motorized vehicles
     * @param averageKph Average speed of the motorized vehicles in Kph
     */
    public void add(int motorized, double averageKph) {
      totalMotorized += motorized;
      sumMotorizedKph += motorized*averageKph;
    }
  }
  
  /**
   * This is never null
   * @see SpeedStats
   */
  public final SpeedStats speed = new SpeedStats();
  
  /**
   * The average occupancy as a fraction. This value ranges from 0 to 1
   */
  public double averageOccupancy;
  
  /**
   * The average directional split over the year
   */
  public double averageDirSplit;
  
  /**
   * Average air temperature
   * @see VehicleRecTemperature
   */
  public final VehicleRecTemperature averageTemp = new VehicleRecTemperature();
  
  /**
   * Gap in seconds
   */
  public double gap;
  
  /**
   * Headway in seconds
   */
  public double headway;
  /**
   * Average received signal strength.
   * @see VehicleFieldsEnum#rssi
   */
  public double averageRSSI;

// ANNUAL TRAFFIC DATA
  
  /**
   * Annual Average Daily Traffic. 
   * This is the total motorized traffic volume in one year divided by the number of days in the year.
   * @see TrafficMonitoringTallyFieldNamespace#aadt
   */
  public double aadt;
  
  /**
   * Annual Average Weekday Traffic
   * @see TrafficMonitoringTallyFieldNamespace#aawt
   */
  public double aawt;
  
  /**
   * Seasonal Average Daily Traffic
   * @see TrafficMonitoringTallyFieldNamespace#sadt
   */
  public double sadt;
  
  /**
   * @see TrafficMonitoringTallyFieldNamespace#aadtobs
   */
  public int aadtobs;

  /**
   * @see TrafficMonitoringTallyFieldNamespace#qc
   */
  public int countaccuracy;
  
  /**
   * Percentage of trucks. This is computed as the total trucks divided by total motorized volume.
   * @see TrafficMonitoringTallyFieldNamespace#truckpct
   */
  public double truckpct;
  
  /**
   * Average ESALs for each vehicle type
   * @see ClassifiedVehTypeDef
   * @see VehicleFieldsEnum#esal
   */
  public double[] averageESALVehType;
  
  /**
   * Annual percentage change of the {@link #aadt AADT} value
   */
  public double aadtpctchange = Double.NaN;
  
  /**
   * Annual percentage change of the total trucks.
   * This is not the same as computing the change between two "truck percentage" values.
   * For example, if all vehicles increased by 10% this value will say 10%.
   * However two truck percentages would remain the same.
   * @see TrafficMonitoringTallyFieldNamespace#truckpctchange
   * @since 1.325
   */
  public double aadttpctchange = Double.NaN;
  
  /**
   * K highest hourly volume - design hourly volume - as a fraction
   * @see TrafficMonitoringDesignVolumes
   */
  public Map<Integer, Double> designVolumes;
  
  /**
   * D value that matches the K highest hourly volume - as a fraction
   * @see TrafficMonitoringDesignDistributions
   */
  public Map<Integer, Double> designDistributions;

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
    this.speed.add(source.speed);
    
    this.totalPedestrians += source.totalPedestrians;
    this.totalBicycles += source.totalBicycles;
    this.totalMotorcycles += source.totalMotorcycles;
    
    for (int i=0; i<source.sensorCount.length; i++)
      this.sensorCount[i] += source.sensorCount[i];
    
    this.aadtobs = source.aadtobs;
  }
}
