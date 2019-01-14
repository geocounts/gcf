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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the {@link EnumHeaderRecords#TF count portion} of {@link VehicleTallyRec#trafficmonitoring tally record}
 * @see TallyCountField
 * @see EnumHeaderRecords#TF
 */
public final class TallyCountDef extends RawTrafficDataHeaderElement implements Iterable<TallyCountField> {
  private final HashMap<String, TallyCountField> fieldsByID = new HashMap<String, TallyCountField>();
  private final List<TallyCountField> fields = new ArrayList<TallyCountField>();
  
//  private FileNamespaces fn;
  protected TallyCountDef() {
  }
  
  /**
   * Add a field if it belongs to the current {@link FileNamespaces#tallytotal} name space
   * @param field A {@link TallyCountField}. For example {@link TrafficMonitoringTallyFieldNamespace#motorized}
   * @return Whether the field was added. If the field exists, it cannot be added twice
   * @see #removeField(TallyCountField)
   */
  public boolean addField(TallyCountField field) {
    if (!TallyFieldNamespaceEnum.trafficmonitoring.contains(field))
      return false;
    return _addField(field);
  }
  
  private boolean _addField(TallyCountField field) {
    if (!fieldsByID.containsKey(field.getId())) {
      fieldsByID.put(field.getId(), field);
      fields.add(field);
      return true;
    }
    return false;
  }
  
  /**
   * Creates a field in the current {@link TallyFieldNamespaceEnum namespace}.
   * If the field is not in the current namespace, create and add as a {@link UserDefinedTallyField}
   * @param fieldName The name of the field. This name must not contain illegal characters.
   * @return The {@link TallyCountField} or null if the field has already been added
   * @throws Exception If the field name had illegal characters
   * @see #getField(String)
   */
  public TallyCountField addField(String fieldName) throws Exception {
    return addField(fieldName, 3);
  }
  
  public TallyCountField addField(String fieldName, int decimalPlaces_1_3_6) throws Exception {
    if (fieldsByID.containsKey(fieldName))
      return null;
    
    TallyCountField result = null;
    try {
      result = TallyFieldNamespaceEnum.trafficmonitoring.fromField(fieldName);  // create the field in the existing name space
    } catch (Exception ex) {
      // add as an UserDefinedTallyField field
      result = TallyFieldNamespaceEnum.userdefined.fromField(fieldName);
      if (result instanceof UserDefinedTallyField) {
        UserDefinedTallyField udf = (UserDefinedTallyField)result;
        udf.setDecimalPlaces(decimalPlaces_1_3_6);
      }
    }

    if (_addField(result))
      return result;
    return null;
  }

  public void removeField(TallyCountField f) {
    fields.remove(f);
    fieldsByID.remove(f.getId());
  }
  
  public void removeField(String fieldName) {
    TallyCountField f = fieldsByID.remove(fieldName);
    fields.remove(f);
  }

  public int numberOfFields() {
    return fields.size();
  }
  
  /**
   * 
   * @param index
   * @return Never returns null
   */
  public TallyCountField getField(int index) {
    return fields.get(index);
  }
  
  /**
   * 
   * @param fieldName
   * @return The {@link TallyCountField} or null
   */
  public TallyCountField getField(String fieldName) {
    return fieldsByID.get(fieldName);
  }
  
  @Override
  public Iterator<TallyCountField> iterator() {
    return fields.iterator();
  }
  
  /**
   * 
   * @param field {@link TallyCountField}
   * @return True if the definition contains this field
   */
  public boolean contains(TallyCountField field) {
    return contains(field.getId());
  }
  
  public boolean contains(String fieldName) {
    return fieldsByID.containsKey(fieldName);
  }
  
  public void clear() {
    fields.clear();
    fieldsByID.clear();
  }
  
  /**
   * 
   * @return True if there is more than one field registered
   */
  public boolean isRecording() {
    return fields.size() > 0;
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.TF;
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
    clear();
    for (int idx = 0; idx < data.length; idx++) {
      try {
        TallyCountField tcf = addField(data[idx]);
        if (tcf == null)
          _addField(new DuplicateField((idx+1) + "_" + data[idx]));
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * Sets this object's properties from another {@link TallyCountDef}
   * @param other
   */
  public void copyFrom(TallyCountDef other) {
//    this.fn.copyFrom(other.fn);
    clear();
    for (TallyCountField e: other.fields)
      addField(e);
  }
  
  public static class DuplicateField extends UserDefinedTallyField {
    protected DuplicateField(String name) {
      super(name);
    }
    
    @Override
    public String getDescription() {
      return "Duplicate field " + super.getId();
    }
  }
}
