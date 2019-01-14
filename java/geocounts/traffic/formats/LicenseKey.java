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
 * The GEOCOUNTS registration key.
 * This is created at geocounts.com and may be used by data providers to indicate the format works for the purpose defined in the registration.
 *
 */
public class LicenseKey extends RawTrafficDataHeaderElement {
  /**
   * The UUID
   */
  private java.util.UUID uuidValue;
  private String uuidStr;
  
  public void encodeFrom(String companyName) {
    uuidValue = java.util.UUID.nameUUIDFromBytes(companyName.getBytes());
    uuidStr = null;
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.GK;
  }
  
  @Override
  protected String[] write() {
    String[] result = new String[1];
    result[0] = toString();
    return result;
  }

  @Override
  protected void read(String[] data) {
    if (data[0] == null) {
      uuidValue = null;
      uuidStr = null;
      return;
    }
    uuidStr = data[0];
  }
  
  @Override
  public String toString() {
    return uuidValue != null ? uuidValue.toString() : uuidStr;
  }

}
