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

import java.util.function.Consumer;
import java.util.function.ToLongFunction;

public class DecodingOptions {
  
  public DecodingOptions() {
    setUseStrict();
  }
  
  private boolean addBody = true;
  /**
   * If false, only load the header block
   */
  public boolean addBody() {
    return addBody;
  }
  
  public void setAddBody(boolean value) {
    addBody = value;
  }
  
  /**
   * Gets offset in milliseconds based on a station record. 
   * Can be used to convert from the UTC timezone, or make any other timing adjustment.
   * @see TimeZoneData
   */
  public ToLongFunction<RawTrafficDataStation> timeOffset;
  
  public boolean assertTFValues;
  
  public boolean assertTCBins;
  
  public boolean assertTSBins;

  public boolean assertTLBins;
  
  public void setUseStrict() {
    assertTFValues = true;
    assertTCBins = true;
    assertTSBins = true;
    assertTLBins = true;
  }
  
  /**
   * 
   * @return A correction to add to the timestamps
   */
  public long getTimestampTimeCorrection(long rawTime, RawTrafficDataStation S) {
    if (timeOffset == null)
      return 0;
    return timeOffset.applyAsLong(S);
  }

  public long getTallyTimeCorrection(long rawTime, RawTrafficDataStation S) {
    return 0;
  }
  
  public void updateTimeForDaylightSavings(RawTrafficDataTrafficElement target) {
    // COMPLETE THIS TO UPDATE FOR DAYLIGHT SAVINGS TIME
  }
  
  public boolean addTallies = true;
  public boolean addObservations = true;
  public boolean addMonitoringEvents = true;
  /**
   * This is called immediately after decoding a vehicle record and before it is sent further. 
   * It could be used to change the channelID for a vehicle traveling in reverse
   */
  public Consumer<VehicleRec> observationRule;
}
