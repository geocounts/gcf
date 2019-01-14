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
 * @see EnumHeaderRecords#SI
 */
public class StationID {
  static final char[] illegalChars = new char[]{RawTrafficDataFilename.PART_DELIM, '\\', '/', '.', '@'};
  
  private String id;
  
  public boolean isSet() {
    return id != null;
  }
  
  public void set(String value) {
    if (value == null) {
      id = null;
      return;
    }
    // replace illegal characters
    final char replaceWith = '-';
    String sid = value.toString();
    for (char c: illegalChars)
      sid = sid.replace(c, replaceWith);
    id = sid;
  }
  
  public String getValue() {
    return id;
  }
  
  public final void copyFrom(StationID other) {
    this.id = other.id;
  }
  
  @Override
  public String toString() {
    return getValue();
  }
}
