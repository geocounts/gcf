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
 * An axle
 * @see AxleRecList
 * @see VehicleRec
 */
public class AxleRec {
  /**
   * Spacing between the current axle and a leading axle.
   * The first axle spacing is always zero.
   * @see AxleFieldsEnum#aspace
   */
  public double aspaceMeters;
  /**
   * @see AxleFieldsEnum#amass
   */
  public double amassKg;
  /**
   * Left-right balance
   * @see AxleFieldsEnum#alrb
   */
  public int alrb;
  
  /**
   * @see AxleFieldsEnum#wheels
   */
  public int wheels;
  
  /**
   * Measurement of mass by different sensors
   */
  public AxleSensorRec[] sensors;
  
  /**
   * 
   * @param index Zero-based
   * @return {@link AxleSensorRec} or null
   */
  public AxleSensorRec getSensor(int index) {
    if (sensors == null)
      return null;
    return index < sensors.length ? sensors[index] : null;
  }
  
  protected AxleSensorRec ensureSensorArray(int index) {
    if (sensors == null)
      sensors = new AxleSensorRec[2];
    if (sensors[index] == null)
      sensors[index] = new AxleSensorRec();
    return sensors[index];
  }
  
  @Override
  public String toString() {
    return aspaceMeters + ":" + amassKg;
  }
  
  /**
   * Represents an axle as measured by one sensor
   */
  public static class AxleSensorRec {
    /**
     * Combined mass
     */
    public double amassKg;
    
    /**
     * 
     * @see AxleFieldsEnum#alrb
     */
    public int alrb() {
      return (int)(100d*amassBalanceFraction());
    }
    
    private double amassBalanceFraction() {
      double combined = amassRight+amassLeft;
      if (combined == 0)
        return 0;
      return amassRight/combined;
    }
    
    /**
     * Mass on the left side
     */
    public double amassLeft;
    
    /**
     * Mass on the right side
     */
    public double amassRight;

  }
}
