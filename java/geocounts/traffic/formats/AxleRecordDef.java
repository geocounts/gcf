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
import java.util.List;

/**
 * Vehicle record - axle fields
 */
public class AxleRecordDef extends RawTrafficDataHeaderElement implements Iterable<AxleFieldsEnum> {

  public final List<AxleFieldsEnum> fields = new ArrayList<AxleFieldsEnum>();

  public boolean contains(AxleFieldsEnum f) {
    return fields.contains(f);
  }
  
  public boolean addField(AxleFieldsEnum f) {
    if (!fields.contains(f)) {
      fields.add(f);
      return true;
    }
    return false;
  }

  public void removeField(AxleFieldsEnum f) {
    fields.remove(f);
  }
  
  public int numberOfFields() {
    return fields.size();
  }

  @Override
  public Iterator<AxleFieldsEnum> iterator() {
    return fields.iterator();
  }
  
  public void clear() {
    fields.clear();
  }
  
  public String toString(String delim) {
    StringBuilder result = new StringBuilder();
    for (int i=0; i<fields.size(); i++) {
      if (i > 0)
        result.append(delim);
      result.append(fields.get(i).getId());
    }
    return result.toString();
  }

  @Override
  public String toString() {
    return toString(", ");
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.VA;
  }
  
  public boolean isRecording() {
    return fields.size() > 0;
  }
  
  @Override
  protected String[] write() {
    if (!isRecording())
      return null;
    String[] result = new String[fields.size()];
    for (int idx = 0; idx < fields.size(); idx++) {
      result[idx] = fields.get(idx).getId();
    }
    return result;
  }

  @Override
  protected void read(String[] data) {
    fields.clear();
    for (int idx = 0; idx < data.length; idx++) {
      try {
        fields.add(AxleFieldsEnum.valueOf(data[idx]));
      } catch (Exception ex) {
        fields.add(AxleFieldsEnum.unknown);
      }
    }
  }
  
  public void copyFrom(AxleRecordDef other) {
    fields.clear();
    mergeFrom(other);
  }

  public void mergeFrom(AxleRecordDef other) {
    for (AxleFieldsEnum e: other.fields)
      addField(e);
  }
}
