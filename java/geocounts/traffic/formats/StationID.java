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

import java.io.Serializable;

/**
 * @see EnumHeaderRecords#SI
 * @see GeoType
 */
public class StationID extends RawTrafficDataHeaderElement implements Serializable {
  private static final long serialVersionUID = 1L;

  static final char[] illegalChars = new char[]{RawTrafficDataFilename.PART_DELIM, '\\', '/', '.', ',', '*', '@', '\'', '"', '\r', '\n', '\t'};
  
  private String id;
  
  /**
   * This is never null
   * @see GeoType
   */
  public GeoType geoType = GeoType.station;
  
  public boolean isSet() {
    return id != null;
  }
  
  @Override
  public void clear() {
    id = null;
    geoType = GeoType.station;
  }
  
  public boolean set(String value) {
    clear();
    if (value == null)
      return false;

    value = value.trim();
    if (value.length() == 0)
      return false;
    
    // replace illegal characters
    final char replaceWith = '-';
    String sid = value.toString();
    for (char c: illegalChars)
      sid = sid.replace(c, replaceWith);
    id = sid;
    return true;
  }
  
  public String getValue() {
    return id;
  }
  
  public final void copyFrom(StationID other) {
    this.id = other.id;
    this.geoType = other.geoType;
  }
  
  @Override
  public String toString() {
    return getValue();
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.SI;
  }

  @Override
  protected String[] write() {
    if (id == null)
      return null;
    
    if (geoType != GeoType.station)
      return new String[]{id, geoType.name()};

    return new String[]{id};
  }

  @Override
  protected void read(String[] data) {
    if (data.length == 0)
      return;
    this.set(data[0]);
    if (data.length > 1)
      geoType = GeoType.valueOf(data[1]);
  }
}
