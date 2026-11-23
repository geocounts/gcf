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

public class VehicleRecTemperature {
  
  public static class TemperatureReading {
    /**
     * Centigrade
     */
    public short C = Short.MIN_VALUE;
    public boolean isSet() {
      return C != Short.MIN_VALUE;
    }
    
    public void unset() {
      C = Short.MIN_VALUE;
    }
    
    public void set(int value) {
      C = (short)value;
    }
    
    public void set(double value) {
      C = (short)value;
    }
    
    /**
     * 
     * @return Fahrenheit
     */
    public int F() {
      return (int)Math.rint(C*9/5d + 32);
    }
  }
  
  /**
   * Surface temperature
   */
  public TemperatureReading surface = new TemperatureReading();
  /**
   * Air temperature
   */
  public TemperatureReading air = new TemperatureReading();
  /**
   * Cabinet/enclosure temperature
   */
  public TemperatureReading cabinet = new TemperatureReading();
  
}
