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

import geocounts.traffic.formats.Equipment.EquipmentChannelRec;
import geocounts.traffic.formats.Equipment.EquipmentRecBase;
import geocounts.traffic.formats.Equipment.EquipmentStationRec;

public class EquipmentHeaderRec extends RawTrafficDataHeaderElement {
  protected EquipmentRecBase rec;
  
  protected static EquipmentStationRec parseStationRec(String f) {
    EquipmentStationRec result = new EquipmentStationRec();
    result.ElementID = 0;
    if (f.startsWith(TextHeaderEscape.XX)) {
      result.fieldExtended = f.substring(3);
    } else
      result.fieldKnown = EquipmentStationElementField.valueOf(f);
    
    return result;
  }
  
  protected static EquipmentChannelRec parseChannelRec(int channelID, String f) {
    EquipmentChannelRec result = new EquipmentChannelRec(channelID);
    if (f.startsWith(TextHeaderEscape.XX)) {
      result.fieldExtended = f.substring(3);
    } else
      result.fieldKnown = EquipmentChannelElementField.valueOf(f);
    return result;
  }

  protected EquipmentHeaderRec(String f, int channelID, Object text) {
    rec = parseChannelRec(channelID, f);
    rec.text = text != null ? text.toString() : null;
  }
  
  protected EquipmentHeaderRec() {}
  
  protected EquipmentHeaderRec(EquipmentRecBase r) {
    this.rec = r;
  }

  @Override
  public final EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.SE;
  }
  
//  protected abstract void copyFrom(EquipmentRec other);
  
  @Override
  protected void read(String[] data) {
    LabelElementType elementType = LabelElementType.valueOf(data[0]);
    switch (elementType) {
    case station: {
      rec = parseStationRec(data[2]);
      if (data.length > 3)
        rec.text = data[3];
      break;
    }
    case channel: {
      rec = parseChannelRec(Integer.parseInt(data[1]), data[2]);
      if (data.length > 3)
        rec.text = data[3];
      break;
    }
    default: {
      throw new java.lang.IllegalArgumentException(elementType.name());
    }
    }
  }
  
  public final static String[] write(EquipmentRecBase rec) {
    String[] result = new String[4];
    result[0] = rec.getElementType().name();
    result[1] = Integer.toString(rec.ElementID);
    result[2] = rec.getFieldID();
    result[3] = rec.text;
    return result;
  }

  @Override
  public final String[] write() {
    return write(rec);
  }
}
