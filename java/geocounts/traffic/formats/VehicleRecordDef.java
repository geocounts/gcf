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
 * Definition of a vehicle record's vehicle fields.
 * Add fields using {@link #addField(VehicleFieldsEnum)}
 * @see VehicleRec
 * @see EnumHeaderRecords#VV
 * @see VehicleFieldsEnum
 */
public class VehicleRecordDef extends RawTrafficDataHeaderElement implements Iterable<ObservationField> {

  protected final List<ObservationField> fields = new ArrayList<ObservationField>();
  private final TimeZoneData SZ;
  protected VehicleRecordDef(TimeZoneData SZ) {
    this.SZ = SZ;
  }
  
  /**
   * 
   * @param field {@link VehicleFieldsEnum}
   * @return Whether the fields contain this field
   */
  public boolean contains(ObservationField field) {
    return fields.contains(field);
  }

  /**
   * 
   * @param field {@link VehicleFieldsEnum}
   * @return True if the field has not been added previously, false if the fields already {@link #contains(VehicleFieldsEnum) contains} this field
   */
  public boolean addField(ObservationField field) {
    if (!fields.contains(field)) {
      fields.add(field);
      return true;
    }
    return false;
  }
  
  public void removeField(ObservationField f) {
    fields.remove(f);
  }

  public int numberOfFields() {
    return fields.size();
  }
  
  public ObservationField getField(int index) {
    return fields.get(index);
  }
  
  @Override
  public Iterator<ObservationField> iterator() {
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
  
  /*
   * 
   * @return A new {@link VehicleRec}
   *
  public VehicleRec create(int channelID, java.util.Date timeUTCNotAccountedFor) {
    return new VehicleRec(channelID, timeUTCNotAccountedFor);
  } */
  
  public VehicleRec create(int channelID, long timeUTCNotAccountedFor) {
    return VehicleRec.createNoTZ(channelID, timeUTCNotAccountedFor - SZ.getRecordedTimestampOffset());
  }
  
  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.VV;
  }

  @Override
  protected String[] write() {
    if (fields.size() == 0)
      return null;
    String[] result = new String[fields.size()];
    for (int i=0; i<fields.size(); i++) {
      result[i] = fields.get(i).getId();
    }
    return result;
  }
  
  /**
   * 
   * @return True if there is at least one field in this definition
   */
  public boolean isRecording() {
    return fields.size() > 0;
  }
  
  /**
   * Reads the vehicles definition line and sets the positions of all the elements
   */
  @Override
  protected void read(String[] data) {
    fields.clear();
    for (int idx = 0; idx < data.length; idx++) {
      try {
        boolean didAdd = addField( VehicleFieldsEnum.valueOf(data[idx]) );
        
      } catch (Exception ex) {
        addField( new UserDefinedObservationField(data[idx]) );
      }
    }
  }

  public void copyFrom(VehicleRecordDef other) {
    fields.clear();
    mergeFrom(other);
  }

  public void mergeFrom(VehicleRecordDef other) {
    for (ObservationField e: other.fields)
      addField(e);
  }
}
