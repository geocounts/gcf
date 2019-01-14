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
 * Other well known ways of classifying individual vehicle events.
 * These attributes are not observable properties of a vehicle.
 * They require definition from an external classification systems.
 */
public class WKObservationClassifications {
  /**
   * Vehicle type classification ID.
   * @see ClassifiedVehTypeDef
   * @see VehicleFieldsEnum#vclass
   * @see VehicleFieldsEnum#vclass1
   * @see VehicleFieldsEnum#vclass2
   */
  public final int[] vclassType = new int[3];
  
  /**
   * Vehicle length classification ID.
   * @see ClassifiedLengthDef
   * @see VehicleFieldsEnum#vclassl
   */
  public int vclassLength;
  /**
   * Measurement quality classification ID.
   * An integer value assigned by the equipment explaining how a vehicle was resolved / 'coerced', or whether the vehicle missed a sensor
   */
  public int qc;
  
  @Override
  public String toString() {
    return vclassType[0] + " " + vclassType[1] + " " + vclassType[2] + " " + vclassLength + " " + qc;
  }
}
