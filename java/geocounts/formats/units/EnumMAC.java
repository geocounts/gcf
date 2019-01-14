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
package geocounts.formats.units;

import geocounts.traffic.formats.MACAddress;

/**
 * 
 * @since 1.26
 */
public enum EnumMAC {
  mac12() {
    @Override
    public long parse(String value) {
      return MACAddress.fromHex12(value);
    }
  },
  mac17() {
    @Override
    public long parse(String value) {
      return MACAddress.fromHex17(value);
    }
  },
  maclong() {
    @Override
    public long parse(String value) {
      return Long.parseLong(value);
    }
  };
  
  public abstract long parse(String value);
  
}
