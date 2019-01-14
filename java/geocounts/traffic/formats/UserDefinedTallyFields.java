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

/**
 * Represents any unknown tallied fields
 * @see TallyFieldNamespaceEnum#userdefined
 * @see UserDefinedTallyField
 * @see TrafficMonitoringTallyFieldNamespace
 */
public class UserDefinedTallyFields {
  protected UserDefinedTallyFields(TallyRecordsDef T) {
  }
  
  private final HashMap<String, Number> values = new HashMap<String, Number>();

  public void setValue(String name, double value) {
    values.put(name, value);
  }
  
  public void setValue(String name, int value) {
    values.put(name, value);
  }

  public Number get(String key) {
    return values.get(key);
  }
  
  public int asInt(String key, int defaultValue) {
    Number result = values.get(key);
    return result != null ? result.intValue() : defaultValue;
  }
  
  public double asDouble(String key, double defaultValue) {
    Number result = values.get(key);
    return result != null ? result.doubleValue() : defaultValue;
  }
  
  public boolean contains(String key) {
    return values.containsKey(key);
  }
  
  public void addToTotals(UserDefinedTallyFields other) {
    // NOT CLEAR HOW TO DEAL WITH THIS
  }
}
