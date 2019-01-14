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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an observation
 *
 */
public class ObservationRec extends RawTrafficDataTrafficElement implements Comparable<ObservationRec> {

  public long id;
  
  /**
   * Image of the observation in base 64 format
   * @see VehicleFieldsEnum#img
   */
  public String img;
  
  /**
   * Image file of the observation
   * @see VehicleFieldsEnum#imgfile
   */
  public String imgfile;
  
  /**
   * @see VehicleFieldsEnum#lat
   */
  public double latitude;

  /**
   * @see VehicleFieldsEnum#lng
   */
  public double longitude;
  
  /**
   * @see UserDefinedObservationField
   */
  public final Map<String, String> userdefined = new HashMap<String, String>();
  
  @Override
  public int compareTo(ObservationRec o) {
    int diff = compareTimes(o);
    if (diff == 0) {
      diff = compareChannels(o);
      if (diff == 0) {
        long diff2 = this.id - o.id;
        if (diff2 != 0)
          return diff2 > 0 ? 1 : 0;
      }
    }
    return diff;
  }

}
