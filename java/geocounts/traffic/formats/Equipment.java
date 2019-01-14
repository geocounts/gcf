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
import java.util.Map;


/**
 * 
 * Represents the equipment used to count the station
 * 
 * @see EquipmentStationElementField
 * @see EquipmentChannelElementField
 * @see EnumHeaderRecords#SE
 * @since 1.70
 */
public class Equipment {
  
  private final EquipmentSetForElement stationRecs = new EquipmentSetForElement(0, LabelElementType.station);
  /**
   * Keyed by channel ID
   */
  private final Map<Integer, EquipmentSetForElement> channelRecs = new HashMap<Integer, EquipmentSetForElement>();
  private final RawTrafficDataStation header;
  
  protected Equipment(RawTrafficDataStation header) {
    this.header = header;
    header.addChannelIDChange(new _ChangeChannel(this));
  }

  protected void copyFrom(Equipment other) {
    stationRecs.clear();
    channelRecs.clear();
    mergeFrom(other);
  }
  
  protected void mergeFrom(Equipment other) {
    stationRecs.putAll(other.stationRecs);
    for (Map.Entry<Integer, EquipmentSetForElement> rec: other.channelRecs.entrySet()) {
      channelRecs.put(rec.getKey(), rec.getValue());
    }
  }
  
  /**
   * 
   * @since 1.9944
   */
  public void toMap(Map<String, Object> result) {
    for (EquipmentRecBase e: stationRecs)
      if (e.text != null)
        result.put("SE_" + e.getElementType() + "_" + e.ElementID + "_" + e.getFieldID(), e.text);
  }

  public Iterable<EquipmentStationRec> allForStation() {
    ArrayList<EquipmentStationRec> result = new ArrayList<EquipmentStationRec>();
    for (EquipmentRecBase r: stationRecs) {
      result.add((EquipmentStationRec)r);
    }
    return result;
  }

  public Iterable<EquipmentChannelRec> allForChannels() {
    ArrayList<EquipmentChannelRec> result = new ArrayList<EquipmentChannelRec>();
    for (EquipmentSetForElement r: channelRecs.values()) {
      for (EquipmentRecBase er: r)
        result.add((EquipmentChannelRec)er);
    }
    return result;
  }

  public void add(EquipmentRecBase r) {
    EquipmentSetForElement e = null;
    if (r.getElementType() == LabelElementType.station) {
      e = stationRecs;
    } else if (r.getElementType() == LabelElementType.channel) {
      e = channelRecs.get(r.ElementID);
      if (e == null) {
        e = new EquipmentSetForElement(r.ElementID, LabelElementType.channel);
        channelRecs.put(r.ElementID, e);
      }
    }
    if (e != null)
      e.fields.put(r.getFieldID(), r);
  }
  
  public EquipmentChannelRec addChannelRecXX(String fieldWithoutXX, int channelID, Object value) {
    EquipmentHeaderRec er = new EquipmentHeaderRec(TextHeaderEscape.XX + fieldWithoutXX, channelID, value);
    add(er.rec);
    return (EquipmentChannelRec)er.rec;
  }
  
  public EquipmentChannelRec addChannelRec(EquipmentChannelElementField f, int channelID, Object value) {
    EquipmentHeaderRec er = new EquipmentHeaderRec(f.name(), channelID, value);
    add(er.rec);
    return (EquipmentChannelRec)er.rec;
  }

  public void removeChannel(int channelID) {
    channelRecs.remove(channelID);
  }
  
  public void clearChannels() {
    channelRecs.clear();
  }
  
  public EquipmentStationRec addStationRecXX(String fieldWithoutXX, Object value) {
    EquipmentStationRec rec = EquipmentHeaderRec.parseStationRec(TextHeaderEscape.XX + fieldWithoutXX);
    rec.text = value != null ? value.toString() : null;
    
    EquipmentHeaderRec er = new EquipmentHeaderRec(rec);
    add(er.rec);
    return (EquipmentStationRec)er.rec;
  }
  
  public EquipmentStationRec addStationRec(EquipmentStationElementField f, Object value) {
    EquipmentStationRec rec = EquipmentHeaderRec.parseStationRec(f.name());
    rec.text = value != null ? value.toString() : null;
    
    EquipmentHeaderRec er = new EquipmentHeaderRec(rec);
    add(er.rec);
    return (EquipmentStationRec)er.rec;
  }
  
  public String getStationRec(EquipmentStationElementField field) {
    return stationRecs.getValue(field.name());
  }
  
  public String getChannelRec(int chID, EquipmentChannelElementField f) {
    EquipmentSetForElement r = channelRecs.get(chID);
    if (r == null)
      return null;
    return r.getValue(f.name());
  }
  
  
  public static abstract class EquipmentRecBase {
    /**
     * @see LabelElementType
     */
    public abstract LabelElementType getElementType();
    
    protected int ElementID;
    
    /**
     * The text value of the field
     */
    public String text;
    
    public abstract String getFieldID();
    
    public abstract boolean isExtended();
  }
  
  public static class EquipmentStationRec extends EquipmentRecBase {
    protected EquipmentStationElementField fieldKnown;
    protected String fieldExtended;

    protected final void copyFrom(EquipmentStationRec other) {
      this.ElementID = 0;
      this.fieldKnown = other.fieldKnown;
      this.fieldExtended = other.fieldExtended;
      this.text = other.text;
    }

    @Override
    public LabelElementType getElementType() {
      return LabelElementType.station;
    }

    @Override
    public String getFieldID() {
      return fieldKnown != null ? fieldKnown.name() : TextHeaderEscape.XX + fieldExtended;
    }

    @Override
    public boolean isExtended() {
      return fieldExtended != null;
    }
    
    public EquipmentStationElementField getStationField() {
      return fieldKnown;
    }
  }
  
  public static class EquipmentChannelRec extends EquipmentRecBase {
    protected EquipmentChannelElementField fieldKnown;
    protected String fieldExtended;
    
    protected EquipmentChannelRec(int chID) {
      this.ElementID = chID;
    }
    
    protected final void copyFrom(EquipmentChannelRec other) {
      this.ElementID = other.ElementID;
      this.fieldKnown = other.fieldKnown;
      this.fieldExtended = other.fieldExtended;
      this.text = other.text;
    }

    @Override
    public final LabelElementType getElementType() {
      return LabelElementType.channel;
    }

    @Override
    public String getFieldID() {
      return fieldKnown != null ? fieldKnown.name() : TextHeaderEscape.XX + fieldExtended;
    }

    @Override
    public boolean isExtended() {
      return fieldExtended != null;
    }
    
    public int getChannelID() {
      return ElementID;
    }
    
    public EquipmentChannelElementField getChannelField() {
      return fieldKnown;
    }
  }
  
  
  public class EquipmentSetForElement implements Iterable<EquipmentRecBase> {
    private final HashMap<String, EquipmentRecBase> fields = new HashMap<String, EquipmentRecBase>();
    private int chID;
    private final LabelElementType et;
    private EquipmentSetForElement(int id, LabelElementType e) {
      this.chID = id;
      this.et = e;
    }
    
    public String getValue(String fieldName) {
      EquipmentRecBase result = fields.get(fieldName);
      return result != null ? result.text : null;
    }

    private void putAll(EquipmentSetForElement recs) {
      for (EquipmentRecBase r: recs)
        fields.put(r.getFieldID(), r);
    }

    public void clear() {
      fields.clear();
    }
 
    @Override
    public Iterator<EquipmentRecBase> iterator() {
      return fields.values().iterator();
    }
  }
  
  class _ChangeChannel implements ChannelIDChange {
    private final Equipment se;
    
    private _ChangeChannel(Equipment parent) {
      se = parent;
    }

    @Override
    public void changeChannel(int fromChannel, int toChannel) throws Exception {
      EquipmentSetForElement es = se.channelRecs.get(fromChannel);
      if (es == null)
        return;
      es.chID = toChannel;
      
      se.channelRecs.remove(fromChannel);
      se.channelRecs.put(toChannel, es);
    }
    
  }
}
/*
"GK" records cannot be generated (as per our discussion yet).  
Having an "OK" record or something similar would be good. 
The idea of the "OK" would be to be an "Other Key" that third party vendors could use in place of the same purpose as the GK record. It just would be unique to each vendors implementation and be ignored by everyone else.  This way the "GK" record format would still be proprietary to GEOCOUNTS. But the "GK" record would then NOT be required. 

2)  "SC" (channel definition) does not contain enough data to truly characterize a channel by equipment and sensor type.  
    While this may be optional, it is good to include this additional needed data would be:
a.  PerVehicle & Classification data:
        i. Sensors Used (Loop-Loop, Tube-Tube, Loop-Axle-Loop, etc)
       ii. Sensor Spacing
      iii. Loop Length
       iv. Directional Mode Enabled
b.  Volume data:
        i. Sensor Used (Loop, Tube, etc)
       ii. Divide By 2 Enabled
      iii. Volume Lane Mode (Normal, Subtract, Directional)

4)  "TF" (tally total) can be problematical in cases where you have mixed sensor usage (Loop, Tube, or Piezo) and whether or not the axle count is divided by 2.  Centurion tries its best to generate both "motorized" and "axles" values which make sense, depending on exactly how the data itself comes in and what sensors and divide mode was used.
5)  "TS" (tally speed) uses a "speed_inc" value which is the increment between bins.  However, most current speed bin tables use an uneven increment at least in the first and last bins, the "TS" definition might be better if it were like the "TL" definition (by defining the right side of each bin category).

8)  No provisions for "Gap Classification" or "Headway Classification".

*/