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
 * Used to get and set a field in a {@link VehicleTallyRec tally record}
 * @see TrafficMonitoringTallyFieldNamespace
 * @see ObservationField
 */
public interface TallyCountField extends IdDesc {
  /**
   * Get a value from the tally record
   * @param tally The tally record to get the value from
   * @param units The units to provide the number in
   * @return A value from the tally record, in the chosen units
   */
  public Number get(VehicleTallyRec tally, UnitsUsed units);
  
  /**
   * Format the value as a String. This takes care of significant figures
   * @param tally
   * @param units
   * @return A String representation of {@link #get(VehicleTallyRec, UnitsUsed)}
   */
  public String format(VehicleTallyRec tally, UnitsUsed units);
  
  /**
   * Set a field in the tally record
   * @param value The value to assign to the result
   * @param units The units in which the value is recorded in
   * @param result The tally record to modify
   * @throws Exception
   */
  public void set(double value, UnitsUsed units, VehicleTallyRec result) throws Exception;
  
  /**
   * Verify the value of this tally is within the bound of this field
   * @param tally The tally to check
   * @return True if the value in the tally is within bounds, false otherwise.
   */
  public boolean isWithinBounds(VehicleTallyRec tally);
  
  /**
   * Counts can be added and then averaged over time whereas averages (such as an average speed) cannot be summed and averaged over time.
   * @return True if this field represents a count that can be added. False if it represents an average.
   */
  public boolean isAdditive();
  
  /**
   * 
   * @return Whether this field's values must be an integer
   */
  public boolean isInteger();
  
  /**
   * 
   * @return The field's minimum value which may be -Double.MAX_VALUE
   */
  public double getMinValue();
  
}
