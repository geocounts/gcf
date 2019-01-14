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

public enum TallyFieldNamespaceEnum {
  /**
   * @see TrafficMonitoringTallyFieldNamespace
   */
  trafficmonitoring() {

    @Override
    public TallyCountField fromField(String name) throws Exception {
      return TrafficMonitoringTallyFieldNamespace.valueOf(name);
    }
    
  },
  /*
   * @see AnnualTrafficTallyFieldNamespace
   *
  annualtraffic() {

    @Override
    public TallyCountField fromField(String name) throws Exception {
      return AnnualTrafficTallyFieldNamespace.valueOf(name);
    }
    
  },
  */
  /**
   * @see UserDefinedTallyField
   */
  userdefined() {

    @Override
    public TallyCountField fromField(String fieldName) throws Exception {
      if ((fieldName == null) || 
          fieldName.contains(" ") || 
          fieldName.contains("\t") || 
          fieldName.contains("\"") || 
          fieldName.contains("\'") || 
          fieldName.contains(",") || 
         (fieldName.length() == 0) || 
         Character.isDigit(fieldName.charAt(0)))
       RawFmtUtils.throwException("UserDefinedTallyField", "Illegal field name", fieldName);
     return new UserDefinedTallyField(fieldName);
    }
  };
  
  public abstract TallyCountField fromField(String fieldName) throws Exception;
  
  public boolean contains(TallyCountField field) {
    try {
      fromField(field.getId());
      return true;
    } catch (Exception ex) {
      return false;
    }
  }
}
