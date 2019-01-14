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

public interface TallyPartOrder {
  
  public String getID();
  
  /**
   * Decode counts and update the {@link VehicleTallyRec result}
   * @param COUNTS Counts encoded as comma separated values, no spaces
   * @param header {@link RawTrafficDataHeader}
   * @param opts {@link DecodingOptions}
   * @param result {@link VehicleTallyRec}
   * @throws Exception
   */
  public abstract void setTally(String COUNTS, RawTrafficDataHeader header, DecodingOptions opts, VehicleTallyRec result) throws Exception;
  
  public abstract void encode(VehicleTallyRec tally, RawTrafficDataHeader header, StringBuilder result) throws Exception;
  
  public abstract Number[] toArray(VehicleTallyRec tally, RawTrafficDataHeader header) throws Exception;
}
