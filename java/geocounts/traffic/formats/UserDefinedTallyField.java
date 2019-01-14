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
 * A tally field in the {@link TallyFieldNamespaceEnum#userdefined user defined} namespace. 
 * This is also created when a field in a known namespace is unrecognized
 * @see TallyFieldNamespaceEnum#userdefined
 */
public class UserDefinedTallyField implements TallyCountField {
  private String key;
  private double minValue;
  
  protected UserDefinedTallyField(String name) {
    this(name, -Double.MAX_VALUE);
  }
  
  protected UserDefinedTallyField(String name, double minValue) {
    this.key = name;
    this.minValue = minValue;
  }
  
  @Override
  public double getMinValue() {
    return minValue;
  }
  
  @Override
  public String getId() {
    return key;
  }

  @Override
  public String getDescription() {
    return "User defined field " + key;
  }

  @Override
  public Number get(VehicleTallyRec tally, UnitsUsed units) {
    return tally.userdefined.get(key);
  }
  
  @Override
  public final String format(VehicleTallyRec tally, UnitsUsed units) {
    Number result = get(tally, units);
    if (result == null)
      return null;
    if (result.doubleValue() == 0)
      return "0";
    switch (decimalPlaces) {
    case 0: return Integer.toString(result.intValue());
    case 1: return RawFmtUtils.df1.format(result.doubleValue());
    case 3: return RawFmtUtils.df3.format(result.doubleValue());
    case 6: return RawFmtUtils.df6.format(result.doubleValue());
    }
    
    return result.toString();
  }

  @Override
  public void set(double value, UnitsUsed units, VehicleTallyRec result) throws Exception {
    if (isInteger())
      result.userdefined.setValue(key, (int)value);
    else
      result.userdefined.setValue(key, value);
  }
  
  @Override
  public boolean isWithinBounds(VehicleTallyRec tally) {
    return true;
  }

  private int decimalPlaces = 3;
  
  @Override
  public boolean isInteger() {
    return decimalPlaces == 0;
  }
  
  public void setAsInteger() {
    decimalPlaces = 0;
  }
  
  /**
   * 
   * @param value_1_3_6 Set as 1, 3 or 6
   */
  public void setDecimalPlaces(int value_1_3_6) {
    if (value_1_3_6 == 2)
      value_1_3_6 = 3;
    else if (value_1_3_6 >= 4)
      value_1_3_6 = 6;
    decimalPlaces = value_1_3_6;
  }
  
  @Override
  public boolean isAdditive() {
    return true;
  }
  
  @Override
  public String toString() {
    return getId();
  }
}
