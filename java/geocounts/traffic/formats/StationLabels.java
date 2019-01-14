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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * <h1>SL - Station Labels</h1>
 * This information helps display data on a chart or diagram.<br />
 * Information in labels should never be used to compute or transform numerical values.
 * <br />
 * SL element_type,element_id,field,text<br />
where<br />
{@link StationLabelRec#elementType element_type} is one of: {@link LabelElementType#station station}, {@link LabelElementType#direction direction} or {@link LabelElementType#channel channel}<br />
{@link StationLabelRec#ElementID element_id} an integer value referring to the record being labeled.<ul>
<li>If the element_type is "station", element_id is ignored and should be set to zero</li>
<li>If the element_type is "direction", element_id refers to one of the compass {@link ChannelDirEnum directions}. The compass directions do not need to appear in any of the channels</li>
<li>If the element_type is "channel", element_id refers to a channel ID. The channel ID must match one of the SC records defined in the header</li>
</ul>
{@link StationLabelRec#field field} refers to one of the defined {@link LabelElementField label fields}: description, pathname, pathdist, regionname, from, to, mapref<br />
{@link StationLabelRec#text text} refers to the text in the label. This must have no commas or line breaks.<br />
@see EnumHeaderRecords#SL
 */
public class StationLabels {
  private final LabelSetForElement stationLabels = new LabelSetForElement(0, LabelElementType.station);
  /**
   * Keyed by channel ID
   */
  private final Map<Integer, LabelSetForElement> channelLabels = new HashMap<Integer, LabelSetForElement>();
  private final Map<Integer, LabelSetForElement> directionLabels = new HashMap<Integer, LabelSetForElement>();
  
  private final RawTrafficDataStation header;
  protected StationLabels(RawTrafficDataStation header) {
    this.header = header;
    this.header.addChannelIDChange(new _ChangeChannel(this));
  }
  
  public void copyStationLevelFrom(StationLabels other) {
    stationLabels.copyFrom(other.stationLabels);
  }

  public void copyFrom(StationLabels other) {
    stationLabels.clear();
    channelLabels.clear();
    directionLabels.clear();
    mergeFrom(other);
  }

  public void mergeFrom(StationLabels other) {
    for (StationLabelRec rec: other.all()) {
      this.add(rec);
    }
  }

  public List<StationLabelRec> all() {
    ArrayList<StationLabelRec> result = new ArrayList<StationLabelRec>();
    // labels for the station
    result.addAll(getLabelsForStation());
    // labels for the directions
    result.addAll(getLabelsForDirections());
    // labels for the channels
    result.addAll(getLabelsForChannels());
    return result;
  }
  
  /**
   * 
   * @since 1.9944
   */
  public void toMap(Map<String, Object> result) {
    for (StationLabelRec sl: all()) {
      String key = "SL_" + sl.getElementType() + "_" + sl.ElementID + "_" + sl.field;
      result.put(key, sl.getText().toString());
    }
  }
  
  /**
   * 
   * @return labels for the station
   */
  public List<StationLabelRec> getLabelsForStation() {
    ArrayList<StationLabelRec> result = new ArrayList<StationLabelRec>();
    for (StationLabelRec rec: stationLabels) {
      result.add(rec);
    }
    return result;
  }
  
  /**
   * 
   * @return labels for OtherSystemIDLabelTextValue
   */
  public Collection<OtherSystemIDLabelTextValue> getStationOtherSystemIDs() {
    return stationLabels.otherSystemIDs.values();
  }
  
  public String getStationLabel(StationLabelElementField field) {
    return stationLabels.get(field);
  }
  
  /**
   * 
   * @param field
   * @param defaultValue
   * @return The label as a Double
   * @since 1.9945
   * @see #getOtherSystemStationLabel(String, Double)
   */
  public Double getStationLabel(StationLabelElementField field, Double defaultValue) {
    String result = stationLabels.get(field);
    if (result == null)
      return defaultValue;
    try {
      return Double.parseDouble(result);
    } catch (Exception ex) {
      return defaultValue;
    }
  }

  /**
   * 
   * @param field
   * @param defaultValue
   * @since 1.9943
   */
  public Double getStationLabelAsDouble(StationLabelElementField field, Double defaultValue) {
    try {
      return Double.parseDouble(stationLabels.get(field));
    } catch (Exception ex) {
      return defaultValue;
    }
  }
  
  /**
   * 
   * @param field
   * @return Null if the field does not exist.
   */
  public String[] getStationLabelFields(StationLabelElementField field) {
    LabelTextValue text = stationLabels.getLabel(field);
    if (text == null)
      return null;
    String[] result = new String[text.numberOfFields()];
    for (int idx=0; idx<result.length; idx++)
      result[idx] = text.getField(idx);
    return result;
  }
  
  public boolean hasStationLabel(StationLabelElementField field) {
    return stationLabels.containsKey(field);
  }
  
  public boolean hasDirectionLabel(ChannelDirEnum dir) {
    return directionLabels.containsKey(dir.getCode());
  }

  public Collection<Integer> getDirectionsWithLabels() {
    HashSet<Integer> result = new HashSet<Integer>();
    for (LabelSetForElement set: directionLabels.values()) {
      for (StationLabelRec rec: set) {
        result.add(rec.ElementID);
      }
    }
    return result;
  }
  
  public LabelSetForElement getLabelsForDirection(ChannelDirEnum dir) {
    return getLabelsForDirection(dir.getCode());
  }
  
  public LabelSetForElement getLabelsForDirection(int dirCode) {
    LabelSetForElement set = directionLabels.get(dirCode);
    if (set == null)
      set = new LabelSetForElement(dirCode, LabelElementType.direction);
    return set;
  }
  
  /**
   * 
   * @return labels for all the directions
   */
  public List<StationLabelRec> getLabelsForDirections() {
    ArrayList<StationLabelRec> result = new ArrayList<StationLabelRec>();
    for (LabelSetForElement set: directionLabels.values()) {
      for (StationLabelRec rec: set) {
        result.add(rec);
      }
    }
    return result;
  }
  
  public LabelSetForElement getDirectionLabel(ChannelDirEnum dir) {
    return directionLabels.get(dir.getCode());
  }
  
  public String getDirectionLabel(ChannelDirEnum dir, StationLabelElementField field) {
    LabelSetForElement set = getDirectionLabel(dir);
    if (set == null)
      return null;
    return set.get(field);
  }

  public boolean hasChannelLabel(int ch) {
    return channelLabels.containsKey(ch);
  }
  
  /**
   * 
   * @param chID The channel ID
   * @return {@link LabelSetForElement} or null
   */
  public LabelSetForElement getChannelLabel(int chID) {
    return channelLabels.get(chID);
  }
  
  public Collection<Integer> getChannelsWithLabels() {
    HashSet<Integer> result = new HashSet<Integer>();
    for (LabelSetForElement set: channelLabels.values()) {
      for (StationLabelRec rec: set) {
        result.add(rec.ElementID);
      }
    }
    return result;
  }
  
  /**
   * 
   * @return labels for the channels
   */
  public List<StationLabelRec> getLabelsForChannels() {
    ArrayList<StationLabelRec> result = new ArrayList<StationLabelRec>();
    for (LabelSetForElement set: channelLabels.values()) {
      for (StationLabelRec rec: set) {
        result.add(rec);
      }
    }
    return result;
  }

  public void removeChannel(int channelID) {
    channelLabels.remove(channelID);
  }
  
  public void clearChannels() {
    channelLabels.clear();
  }
  
  /**
   * Never returns null. Add more elements to the result will not affect the labels
   * @param channelID
   * @return List of {@link StationLabelRec}
   */
  public List<StationLabelRec> getLabelsForChannel(int channelID) {
    ArrayList<StationLabelRec> result = new ArrayList<StationLabelRec>();
    LabelSetForElement set = channelLabels.get(channelID);
    if (set == null)
      return result;
    result.addAll(set.all());
    return result;
  }
  
  /**
   * 
   * @param channelID
   * @param field
   * @return May return null
   */
  public String getChannelLabel(int channelID, StationLabelElementField field) {
    LabelSetForElement set = channelLabels.get(channelID);
    if (set == null)
      return null;
    return set.get(field);
  }
  
  /**
   * 
   * @param othersystemname
   * @param channelID
   * @return May return null
   * @see #addOtherSystemChannelLabel(String, int, Object)
   */
  public OtherSystemIDLabelTextValue getOtherSystemChannelLabel(String othersystemname, int channelID) {
    LabelSetForElement ls = getChannelLabel(channelID);
    if (ls == null)
      return null;
    return ls.getOtherSystemID(othersystemname);
  }
  
  public Double getOtherSystemChannelLabel(String othersystemname, int channelID, Double defaultValue) {
    LabelSetForElement ls = getChannelLabel(channelID);
    if (ls == null)
      return defaultValue;
    try {
      return Double.parseDouble(ls.getOtherSystemID(othersystemname).getSystemValueID());
    } catch (Exception ex) {
      return defaultValue;
    }
  }
  
  public OtherSystemIDLabelTextValue getOtherSystemDirectionLabel(String othersystemname, ChannelDirEnum dir) {
    LabelSetForElement ls = getDirectionLabel(dir);
    if (ls == null)
      return null;
    return ls.getOtherSystemID(othersystemname);
  }
  
  public Double getOtherSystemDirectionLabel(String othersystemname, ChannelDirEnum dir, Double defaultValue) {
    LabelSetForElement ls = getDirectionLabel(dir);
    if (ls == null)
      return defaultValue;
    try {
      return Double.parseDouble(ls.getOtherSystemID(othersystemname).getSystemValueID());
    } catch (Exception ex) {
      return defaultValue;
    }
  }
  
  public final StationLabelRec fromLine(String line) throws Exception {
    StationLabelRec r = new StationLabelRec(line);
    add(r);
    return r;
  }
  
  public boolean addStationLabel(StationLabelElementField field, int text) {
    stationLabels.put(field, createLabelTextValue(field, Integer.toString(text)));
    return true;
  }
  
  public boolean addStationLabel(StationLabelElementField field, String text) {
    if ((field == null) || (text == null) || !field.station || (text.length() == 0))
      return false;
    
    stationLabels.put(field, createLabelTextValue(field, text));
    return true;
  }
  
  public boolean removeStationLabel(StationLabelElementField field) {
    return stationLabels.remove(field);
  }
  
  /**
   * Adds an {@link StationLabelElementField#othersystemid}
   * @param othersystemname
   * @param idvalue
   */
  public boolean addOtherSystemStationLabel(String othersystemname, Object idvalue) {
    if (othersystemname == null)
      return false;
    
    stationLabels.removeOtherSystemID(othersystemname);
    if (idvalue == null)
      return false;

    OtherSystemIDLabelTextValue osid = OtherSystemIDLabelTextValue.create(othersystemname, idvalue);
    stationLabels.putOtherSystemID(osid);
    return true;
  }

  /**
   * 
   * @param othersystemname
   * @param dir
   * @param idvalue
   * @return True if the label was set
   * @see #getOtherSystemDirectionLabel(String, ChannelDirEnum, Double)
   */
  public boolean addOtherSystemDirectionLabel(String othersystemname, ChannelDirEnum dir, Object idvalue) {
    if ((othersystemname == null) || (idvalue == null))
      return false;

    if (!header.getToDirections().contains(dir))
      return false;
    
    OtherSystemIDLabelTextValue osid = OtherSystemIDLabelTextValue.create(othersystemname, idvalue);
    
    StationLabelRec r = new StationLabelRec(LabelElementType.direction, dir.getCode());
    r.field = StationLabelElementField.othersystemid;
    r._text = osid;
    add(r);
    return true;
  }
  
  /**
   * 
   * @param othersystemname
   * @param channelID
   * @param idvalue
   * @see #getOtherSystemChannelLabel(String, int)
   */
  public boolean addOtherSystemChannelLabel(String othersystemname, int channelID, Object idvalue) {
    if ((othersystemname == null) || (idvalue == null))
      return false;
    
    if (idvalue.toString().length() == 0)
      return false;

    if (!header.hasChannel(channelID))
      return false;
    
    OtherSystemIDLabelTextValue osid = OtherSystemIDLabelTextValue.create(othersystemname, idvalue);

    StationLabelRec r = new StationLabelRec(LabelElementType.channel, channelID);
    r.field = StationLabelElementField.othersystemid;
    r._text = osid;
    add(r);
    return true;
  }
  
  public String getOtherSystemStationLabel(String othersystemname) {
    OtherSystemIDLabelTextValue ltv = stationLabels.getOtherSystemID(othersystemname);
    return ltv != null ? ltv.getSystemValueID() : null;
  }
  
  /**
   * 
   * @param othersystemname
   * @param defaultValue
   * @since 1.9945
   * @return A Double
   * @see #getStationLabel(StationLabelElementField, Double)
   */
  public Double getOtherSystemStationLabel(String othersystemname, Double defaultValue) {
    String result = getOtherSystemStationLabel(othersystemname);
    if (result == null)
      return defaultValue;
    try {
      return Double.parseDouble(result);
    } catch (Exception ex) {
      return defaultValue;
    }
  }
  
  protected void addStationLabel(StationLabelElementField field, String[] multiValues) {
    StringBuilder labels = new StringBuilder();
    for (int vc=0; vc<multiValues.length; vc++) {
      if (vc > 0)
        labels.append(',');
      labels.append(multiValues[vc] != null ? multiValues[vc] : ""); // RawTrafficDataFileTXTEncoder.escape.decode
    }
    
    MultiLabelTextValue multi = new MultiLabelTextValue(labels.toString(), multiValues);
    stationLabels.put(field, multi);
  }
  
  /**
   * 
   * @param labelField
   * @param rV Raw values
   * @return {@link LabelTextValue}
   */
  private static LabelTextValue createLabelTextValue(StationLabelElementField labelField, String rV) {
    if (rV != null) {
      rV = rV.replace('\r', ' ').replace('\n', ' ');  // remove carriage return and line feeds
    }
    
    // single fields
    if (!labelField.hasMultipleFields()) {
      if (rV == null)
        return new SimpleLabelTextValue(rV);
      return new SimpleLabelTextValue(TextHeaderEscape.instance.decode(rV.trim()));
    }
    
    // multiple fields, E.g. class labels
    if (rV == null) {
      if (labelField == StationLabelElementField.othersystemid)
        return new OtherSystemIDLabelTextValue();
      return new MultiLabelTextValue();
    }

    // RawTrafficDataFileTXTEncoder.escape.decode(value.trim());
    rV = rV.trim();
    int pos = rV.indexOf(TextHeaderEscape.DChars.DELIM.raw);
    // if there is only one value
    if (pos == -1) {
      if (labelField == StationLabelElementField.othersystemid)
        return new OtherSystemIDLabelTextValue(rV, new String[]{rV});
      return new MultiLabelTextValue(rV, new String[]{rV});
    }
    // there are multiple values
    String[] fields = TextHeaderEscape.instance.splitDecode(rV);
    if (labelField == StationLabelElementField.othersystemid)
      return new OtherSystemIDLabelTextValue(rV, fields);
    return new MultiLabelTextValue(rV, fields);
  }
  
  public boolean addChannelLabel(int channelID, StationLabelElementField field, String text) {
    if ((channelID < 0) || (field == null) || (text == null) || !field.channel)
      return false;
    
    StationLabelRec rec = new StationLabelRec(LabelElementType.channel, channelID);
    rec.field = field;
    rec._text = createLabelTextValue(field, text);
    add(rec);
    return true;
  }
  
  public boolean addDirectionLabel(ChannelDirEnum dir, StationLabelElementField field, String text) {
    if ((dir == null) || (field == null) || (text == null) || !field.direction)
      return false;
    
    StationLabelRec rec = new StationLabelRec(LabelElementType.direction, dir.getCode());
    rec.field = field;
    rec._text = createLabelTextValue(field, text);
    add(rec);
    return true;
  }
  
  private void add(StationLabelRec r) {
    LabelSetForElement elementLabels = null;
    switch (r.elementType) {
    case channel  : {
      elementLabels = channelLabels.get(r.ElementID);
      if (elementLabels == null) {
        elementLabels = new LabelSetForElement(r.ElementID, r.elementType);
        channelLabels.put(r.ElementID, elementLabels);
      }
      break;
    }
    case direction: {
      elementLabels = directionLabels.get(r.ElementID);
      if (elementLabels == null) {
        elementLabels = new LabelSetForElement(r.ElementID, r.elementType);
        directionLabels.put(r.ElementID, elementLabels);
      }
      break;
    }
    case station  : {
      elementLabels = stationLabels;
      break;
    }
    }
    
    elementLabels.put(r.field, r.getText());
  }
  
  @Override
  public String toString() {
    ArrayList<String> labels = new ArrayList<String>();
    String r = this.getStationLabel(StationLabelElementField.pathid);
    if (r != null)
      labels.add(r);
    r = this.getStationLabel(StationLabelElementField.regionname);
    if (r != null)
      labels.add(r);
    r = this.getStationLabel(StationLabelElementField.urbanareaname);
    if (r != null)
      labels.add(r);
    r = this.getStationLabel(StationLabelElementField.description);
    if (r != null)
      labels.add(r);
    
    StringBuilder result = new StringBuilder();
    for (String l: labels) {
      if (result.length() > 0)
        result.append(", ");
      result.append(l);
    }
    return result.toString();
  }
  
  public final static class StationLabelRec extends RawTrafficDataHeaderElement {
    private StationLabelRec(LabelElementType eType, int eID) {
      this.elementType = eType;
      this.ElementID = eID;
    }
    
    private StationLabelRec(String unescapedLine) throws Exception {
      int pos1 = 0;
      int pos2 = unescapedLine.indexOf(',', pos1);
      if (pos2 > -1) {
        this.elementType = LabelElementType.valueOf(unescapedLine.substring(pos1, pos2));
        pos1 = pos2+1;
        pos2 = unescapedLine.indexOf(',', pos1);
        this.ElementID = Integer.parseInt(unescapedLine.substring(pos1, pos2));
        pos1 = pos2+1;
        pos2 = unescapedLine.indexOf(',', pos1);
        this.field = StationLabelElementField.valueOf(unescapedLine.substring(pos1, pos2));
        
        if (!this.elementType.accepts(this.field))
          RawFmtUtils.throwException(this.elementType, "Field is incompatible with the element type", this.field);

        pos1 = pos2+1;
        this._text = createLabelTextValue(this.field, unescapedLine.substring(pos1));
      }
    }
    
    /**
     * @see LabelElementType
     */
    private LabelElementType elementType;
    public LabelElementType getElementType() {
      return elementType;
    }
    
    /**
     * If the elementType is "direction" this refers to one of the compass directions.
     * If the elementType is "channel", this refers to one of the channels IDs
     */
    public int ElementID;
    
    /**
     * The field being set the element. This is NEVER null
     * @see LabelElementField
     */
    public StationLabelElementField field = StationLabelElementField.description;
    
    /**
     * The text value of the label.
     * Although this object is never null, its {@link LabelTextValue#toString() value} may be null
     */
    private LabelTextValue _text = new SimpleLabelTextValue(null);
    
    /**
     * 
     * @return Never returns null
     */
    public LabelTextValue getText() {
      return _text;
    }
    
    protected void copyFrom(StationLabelRec other) {
      this.elementType = other.elementType;
      this.ElementID = other.ElementID;
      this.field = other.field;
      this._text = other._text;
    }
    
    @Override
    public EnumHeaderRecords getSourceRecord() {
      return EnumHeaderRecords.SL;
    }

    @Override
    public String[] write() {
      String[] result = new String[3 + _text.numberOfFields()];
      result[0] = elementType.name();
      result[1] = Integer.toString(this.ElementID);
      result[2] = field.name();
      for (int f=0; f<_text.numberOfFields(); f++)
        result[3+f] = _text.getField(f);
      return result;
    }

    @Override
    protected void read(String[] data) {
      this.elementType = LabelElementType.valueOf(data[0]);
      this.ElementID = Integer.parseInt(data[1]);
      this.field = StationLabelElementField.valueOf(data[2]);
      this._text = createLabelTextValue(this.field, data[3]);
    }
  
  }

  public static abstract class LabelTextValue {
    protected final String rawValue;
    protected LabelTextValue(String rV) {
      this.rawValue = rV;
    }
    
    @Override
    public final String toString() {
      return rawValue;
    }
    
    public abstract int numberOfFields();
    
    public abstract String getField(int idx);
    
    public final String[] asArray() {
      String[] result = new String[numberOfFields()];
      for (int idx=0; idx<result.length; idx++)
        result[idx] = getField(idx);
      return result;
    }
  }

  public static class MultiLabelTextValue extends LabelTextValue {
    private final String[] fields;
    private MultiLabelTextValue(String rV, String[] fields) {
      super(rV);
      this.fields = fields;
    }
    
    public MultiLabelTextValue() {
      this(null, new String[1]);
    }
    
    @Override
    public int numberOfFields() {
      return fields.length;
    }

    @Override
    public String getField(int idx) {
      return idx < fields.length ? fields[idx] : null;
    }
  }
  
  /**
   * 
   * @see StationLabelElementField#othersystemid
   */
  public static class OtherSystemIDLabelTextValue extends MultiLabelTextValue {
    
    private static OtherSystemIDLabelTextValue create(String othersystemname, Object idvalue) {
      String rawValue = othersystemname + TextHeaderEscape.DChars.DELIM.raw + idvalue;
      String[] fields = new String[]{othersystemname, idvalue.toString()};
      return new OtherSystemIDLabelTextValue(rawValue, fields);
    }
    
    private OtherSystemIDLabelTextValue(String rawValue, String[] fields) {
      super(rawValue, fields);
    }

    protected OtherSystemIDLabelTextValue() {
      super();
    }
    
    public String getSystemName() {
      return super.getField(0);
    }
    
    public String getSystemValueID() {
      return super.getField(1);
    }
  }
  
  public static class SimpleLabelTextValue extends LabelTextValue {
    private SimpleLabelTextValue(String rV) {
      super(rV);
    }
    
    @Override
    public int numberOfFields() {
      return 1;
    }

    @Override
    public String getField(int idx) {
      return super.rawValue;
    }
  }
  
  /**
   * @see StationLabelElementField
   */
  public class LabelSetForElement implements Iterable<StationLabelRec> {
    private final HashMap<StationLabelElementField, LabelTextValue> fieldValues = new HashMap<StationLabelElementField, LabelTextValue>();
    private final HashMap<String, OtherSystemIDLabelTextValue> otherSystemIDs = new HashMap<String, OtherSystemIDLabelTextValue>();
    
    public final int ElementID;
    public final LabelElementType elementType;
    private LabelSetForElement(int eid, LabelElementType eType) {
      this.ElementID = eid;
      this.elementType = eType;
    }
    
    public void copyFrom(LabelSetForElement other) {
      fieldValues.clear();
      otherSystemIDs.clear();
      
      fieldValues.putAll(other.fieldValues);
      otherSystemIDs.putAll(other.otherSystemIDs);
    }

    public void put(StationLabelElementField field, LabelTextValue text) {
      if (text instanceof OtherSystemIDLabelTextValue) {
        putOtherSystemID((OtherSystemIDLabelTextValue)text);
      } else
        fieldValues.put(field, text);
    }
    
    public boolean remove(StationLabelElementField field) {
      if (field != StationLabelElementField.othersystemid)
        return fieldValues.remove(field) != null;
      return false;
    }
    
    public LabelTextValue getLabel(StationLabelElementField field) {
      if (field == StationLabelElementField.othersystemid) {
        if (otherSystemIDs.size() == 0)
          return null;
        return otherSystemIDs.values().iterator().next();
      }
      
      return fieldValues.get(field);
    }
    
    public String get(StationLabelElementField field) {
      LabelTextValue text = getLabel(field);
      return text != null ? text.toString() : null;
    }
    
    private void putOtherSystemID(OtherSystemIDLabelTextValue tv) {
      otherSystemIDs.put(tv.getSystemName(), tv);
    }
   
    private void removeOtherSystemID(String systemName) {
      otherSystemIDs.remove(systemName);
    }
    
    /**
     * 
     * @param systemName The name of the system. E.g. "tmas" or "vdotsiteid"
     * @return The {@link OtherSystemIDLabelTextValue text label} or null
     */
    public OtherSystemIDLabelTextValue getOtherSystemID(String systemName) {
      return otherSystemIDs.get(systemName);
    }
    
    public boolean containsKey(StationLabelElementField field) {
      if (field == StationLabelElementField.othersystemid)
        return otherSystemIDs.size() > 0;
      return fieldValues.containsKey(field);
    }
    
    @Override
    public Iterator<StationLabelRec> iterator() {
      return all().iterator();
    }
    
    public List<StationLabelRec> all() {
      ArrayList<StationLabelRec> result = new ArrayList<StationLabelRec>();
      for (Map.Entry<StationLabelElementField, LabelTextValue> e: fieldValues.entrySet()) {
        StationLabelRec rec = new StationLabelRec(elementType, ElementID);
        rec.field = e.getKey();
        rec._text = e.getValue();
        result.add(rec);
      }
      for (OtherSystemIDLabelTextValue e: otherSystemIDs.values()) {
        StationLabelRec rec = new StationLabelRec(elementType, ElementID);
        rec.field = StationLabelElementField.othersystemid;
        rec._text = e;
        result.add(rec);
      }
      return result;
    }
    
    public void clear() {
      fieldValues.clear();
      otherSystemIDs.clear();
    }
    
    @Override
    public String toString() {
      return elementType + " " + ElementID;
    }
  }

  class _ChangeChannel implements ChannelIDChange {
    private final StationLabels sl;
    private _ChangeChannel(StationLabels parent) {
      this.sl = parent;
    }

    @Override
    public void changeChannel(int fromChannel, int toChannel) throws Exception {
      List<StationLabelRec> labels = sl.getLabelsForChannel(fromChannel);
      for (StationLabelRec slr: labels)
        slr.ElementID = toChannel;
    }
    
  }
}

