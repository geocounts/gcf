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

import java.util.function.Function;

/**
 * 
 * @since 2.071
 *
 */
public abstract class VehicleRecComputedValue implements Function<VehicleRec, Object> {
  public final String ID;
  public final boolean isNumberOrString;
  public final int fieldSize;
  
  /**
   * 
   * @param id
   * @param isNumberOrString
   * @param fieldSize 
   * If the field is a String, this says how large a VARCHAR field should be. 
   * If the field is a Number, use 0 for INTEGER and 1 for FLOAT.
   */
  public VehicleRecComputedValue(String id, boolean isNumberOrString, int fieldSize) {
    this.ID = id;
    this.isNumberOrString = isNumberOrString;
    this.fieldSize = fieldSize;
  }
  
  public boolean isNumeric() {
    return isNumberOrString;
  }
  
}
