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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @see RawTrafficDataStation#SN
 * @see EnumHeaderRecords#SN
 */
public class StationSerialNumbers extends RawTrafficDataHeaderElement implements Iterable<String> {
  private final ArrayList<String> serials = new ArrayList<String>();

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.SN;
  }

  @Override
  public Iterator<String> iterator() {
    return serials.iterator();
  }

  public boolean hasValues() {
    return serials.size() > 0;
  }

  @Override
  public String toString() {
    if (serials.size() == 0)
      return null;
    StringBuilder result = new StringBuilder(serials.get(0));
    for (int i=1; i<serials.size(); i++) {
      result.append(' ');
      result.append(serials.get(i));
    }
    return result.toString();
  }

  /**
   * This will not add a null value
   * @param value The value to add
   */
  public void add(String value) {
    if (value != null)
      if (!serials.contains(value))
        serials.add(value);
  }
  
  public void mergeFrom(StationSerialNumbers other) {
    for (String s: other.serials)
      add(s);
  }
  
  public void copyFrom(StationSerialNumbers other) {
    serials.clear();
    serials.addAll(other.serials);
  }

  @Override
  protected String[] write() {
    if (serials.size() == 0)
      return null;
    String[] result = new String[serials.size()];
    serials.toArray(result);
    return result;
  }

  @Override
  protected void read(String[] data) {
    serials.clear();
    for (String sn: data)
      add(sn);
  }

}
