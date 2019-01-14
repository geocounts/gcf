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

import java.text.ParseException;
import java.util.Date;

/**
 * There are four categories of {@link EnumRecordType#H header record}. Each category starts with a different letter<ul>
 * <li>F* : Records relating to the file</li>
 * <li>S* : Records relating to the station and/or survey</li>
 * <li>T* : Records relating to how tallies are stored</li>
 * <li>V* : Records relating to how observations are stored</li>
 * </ul>
 * There is also a {@link GK GEOCOUNTS Key} which ties the file to a known data supplier
 *
 */
public enum EnumHeaderRecords implements IdDesc {
  /**
   * The GEOCOUNTS key serves to protect the integrity of the format. 
   * A valid key is proof to end users that the system producing the data file is allowed to use the GEOCOUNTS name to describe its capabilities.
   * It is not used to encrypt data.
 * <a href="http://geocounts.com">geocounts.com</a>/api
   * @formatparam key_value true
   * The value of the key
   * @formatexample aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee
   * @see #FC
   * @see LicenseKey
   */
  GK("GEOCOUNTS Key") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      String uuid = header.GK.toString();
      return uuid != null ? new String[]{uuid} : null;
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.GK.fromLine(value);
      return header.GK;
    }
  },
  
//--------------------------------------------------------------  
// Headers Describing The File
//--------------------------------------------------------------  

  /**
   * The software that created this file may implement file tampering security or content validation
   * @formatparam algorithmid true
   * The algorithm used. Its up to the software that produced this file to understand this value
   * @formatparam checksumvalue true
   * The value of the checksum produced by the algorithm.
   * @formatexample myco2545,940360363
   * 
   * @see FileChecksum
   */
  FC("File checksum") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.F.FC.value != null ? header.F.FC.write() : null;
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.F.FC.fromLine(value);
      return header.F.FC;
    }

  },

  /**
   * The date and time the file was generated
   * @formatparam date true STRING yyyy/MM/dd
   * The date the file was generated
   * @formatparam time true STRING HH:mm:ss.SSS
   * The time the file was generated
   * @formatexample 2016/07/29,10:00:24.353
   * 
   * @see RawTrafficDataHeader#getObservationDateFormat()
   * @see RawTrafficDataHeaderFileDef#FD
   */
  FD("File date produced") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.F.FD != 0 ? new String[]{header.getObservationDateFormat().format(header.F.FD)} : null;
    }
    
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws ParseException {
      header.F.FD = header.getObservationDateFormat().parse(value).getTime();
      return header.F.FD;
    }
  },
  
  /**
   * If the system that attempted to produce this file was unable to complete normally, an error message may be inserted here.
   * The existence of this record implies the file is missing data.
   * @formatparam errormessage true
   * The error message
   * @formatexample Failed to produce a result due to missing information!
   * @see RawTrafficDataHeaderFileDef#FE
   */
  FE("File error message") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.F.FE != null ? new String[]{header.F.FE} : null;
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.F.FE = value;
      return header.F.FE;
    }

    @Override
    public boolean mayHaveText() {
      return true;
    }
  },
  
  /**
   * This records any material changes made to information in the file to explain its contents.
   * 
   * @formatparam fxdate true
   * The date and time of the change
   * @formatparam fxactionid true
   * An action identifer
   * @formatparam fxuser false
   * An identifier of the user or system that made the change
   * @formatparam fxinfo true
   * More information about the change made
   * 
   * @see RawTrafficDataHeader#getObservationDateFormat()
   * @see RawTrafficDataHeaderFileDef#FX
   */
  FX("File change") {

    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return null;
    }
    
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
      return header.F.FX.fromLine(value);
    }

    @Override
    public boolean mayHaveMultiple() {
      return true;
    }

    @Override
    public boolean mayHaveText() {
      return true;
    }
    
  },
  
  /**
   * This describes the units used. It supports a combination of metric and U.S. customary units of measurement systems.
   * @formatparam unit_tag false ENUM_ARRAY EnumUnitsUsed
   * A list of unit tags.
   * @formatdefault metric
   * @formatexample mph,meter,kg
   * @formatexample metric
   * Shorthand to set units to 'kph,meter,kg'
   * @formatexample uscustomary
   * Shorthand to set units to 'mph,foot,lb'
   * @see UnitsUsed
   * @see UnitsUsed.SystemOfUnits
   */
  FU("File Units") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.F.FU.write();
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.F.FU.fromLine(value);
      return header.F.FU;
    }
  },

  /**
   * Describes what version of software was used to produce the data file
   * @formatparam gcversion true
   * This should always be "1" while the file format is in version 1.
   * @formatparam producer true
   * Any short text identifying the system that produced the file. 
   * Data suppliers can use this to track versions of their software that created the data file.
   * Must not begin with whitespace.
   * @formatparam sourcefile false STRING_ARRAY
   * This may be used if conversion was done from another source file.
   * File names must not begin with whitespace.
   * @formatexample 1,ReportMaker 4.3.25
   * @formatexample 1,My system V2.3,sourcefile1.dat,sourcefile2.dat
   * Two source files were used by 'My system V2.3' to produce this output
   * @see SystemVersion
   */
  FV("File Versioning") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.F.FV.write();
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.F.FV.fromLine(value);
      return header.F.FV;
    }

    @Override
    public boolean mayHaveText() {
      return true;
    }
  },
  
  /**
   * Namespace for count records
   * @see FileNamespaces
   * @deprecated
   */
  FN("File Tally Totals Namespace") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) throws Exception {
      return null;
    }
    
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
//      header.F.FN.fromLine(value);
//      return header.F.FN;
      return null;
    }
  },
  
//--------------------------------------------------------------  
// Headers Describing The Station
//--------------------------------------------------------------  

  /**
   * An identifying number of a contract used to purchase the count.
   * This allows the data producer to track data for payment purposes.
   * @formatparam contract_id true
   * A contract ID value
   * @see RawTrafficDataStation#SA
   */
  SA("Contract identifier")  {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.S.SA != null ? new String[]{header.S.SA} : null;
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.S.SA = value;
      return header.S.SA;
    }

    @Override
    public boolean mayHaveText() {
      return true;
    }
  },
  
  /**
   * Defines a channel.
   * Every file with count data must have at least one channel definition record.
   * All 'T' and 'V' count records have a 'channel_id' field which relates to one channel definition record.
   * The channel ID is also used for station labels (SL header).
   * @formatparam channel_id true INTEGER
   * Each channel definition record must contain a channel ID. This is unique across the other channel definition records in this file.
   * @formatparam to_dir false ENUM ChannelDirEnum
   * The direction the counted objects are heading towards. This may be referred to as the 'departure' direction.
   * If a counter is monitoring a combined direction, join the two direction codes together. For example, '15' indicates North and South combined.
   * @formatparam to_lane false INTEGER
   * For all travel (except to_surfacetype = 4) this represents the lane on which counted objects are heading towards. 
   * Allowable values:
   * <ul>
   * <li>-1 = undefined</li>
   * <li>1 = lane closest to a curb (sometimes called the 'slow' lane)</li>
   * <li>Other values 2 and above</li>
   * <li>0 = multiple lanes in the direction are combined</li>
   * </ul>
   * For <keyword>pedestrian crossings</keyword> at an intersection (to_surfacetype = 4), this field is used to represent <keyword>rotational crossing directions</keyword>.
   * <ul>
   * <li>-1 = undefined</li>
   * <li>0 = No distinction between the directions (combined count)</li>
   * <li>1 = Crossing in the <i>clockwise</i> direction</li>
   * <li>2 = Crossing in the <i>counter-clockwise</i> direction</li>
   * </ul>
   * @formatparam to_surfacetype false ENUM ChannelLaneUsageType
   * The type of surface on which the counted objects are traveling towards.
   * @formatparam from_dir false
   * The direction counted objects are coming from. This may be referred to as the 'approach' direction.
   * Options are the same as <parmname>to_dir</parmname>.
   * @formatparam from_lane false INTEGER
   * The lane counted objects are using to approach from. Options are the same as <parmname>to_lane</parmname>
   * @formatparam from_surfacetype false
   * The type of surface on which the counted objects are approaching from.
   * Options are the same as <parmname>to_surfacetype</parmname>
   * 
   * @formatexample 1,3,1,1,5,3,1
   * Channel 1 is used to record traffic at a roadway intersection. Traffic in this channel is making a right turn from the South (leg) lane 3 into East lane 1
   * @formatexample 3
   * Defines a channel 3 with no other information
   * @formatexample 12,1
   * Defines a channel 12 for objects heading North
   * @formatexample 1,13,1
   * Defines a channel 1 for objects heading North AND East. This is used when a counter is monitoring a through/right turn combined lane
   * 
   * @see ChannelData
   */
  SC("Channel Definition") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return null;
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      ChannelData cd = new ChannelData(header.S, 0);
      cd.fromLine(value);
      header.S.setChannelData(cd);
      return cd;
    }

    @Override
    public boolean mayHaveMultiple() {
      return true;
    }
  },

  /**
   * Counter Equipment information contains information about the counting equipment's state
   * @formatparam element_type true
   * The type of equipment element to be labeled: 'station' or 'channel'
   * @formatparam element_id true INTEGER
   * <ul>
   * <li>When <parmname>element_type</parmname> is <u>station</u>, this field is ignored. Simply set it to '0'</li>
   * <li>When <parmname>element_type</parmname> is <u>channel</u>, this is the channel ID</li>
   * </ul>
   * @formatparam field true
   * <ul>
   * <li>When <parmname>element_type</parmname> is <u>station</u>, use fields from {@link EquipmentStationElementField} or use an <keyword>equipment extended field</keyword></li>
   * <li>When <parmname>element_type</parmname> is <u>channel</u>, use fields from {@link EquipmentChannelElementField} or use an <keyword>equipment extended field</keyword></li>
   * </ul>
   * An <keyword>equipment extended field</keyword> allows the file to store data about equipment characteristics which this format does not support more directly.
   * It must start with '<codeph>XX:</codeph>'. See examples below.
   * @formatparam text true
   * The text value. There are no defined values; the software producing this format can enter whatever is appropriate to describe the information.
   * As a guide, if speed, mass or length units are used in the value, they should be written using the units defined in the <parmname>FU</parmname> header.
   * @formatexample station,0,counterversion,V 5.3
   * A counter version is recorded as 'V 5.3'
   * @formatexample channel,2,sensortype,PLP
   * Channel 2's 'sensortype' field is recorded as 'PLP'
   * @formatexample channel,1,XX:theuniversechecksum,42
   * An equipment extended field 'theuniversechecksum' for channel 1 is recorded as '42'
   * 
   * @see Equipment
   * @see EquipmentHeaderRec
   */
  SE("Equipment Labels") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return null;
    }
    
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      EquipmentHeaderRec r = new EquipmentHeaderRec();
      r.fromLine(value);
      header.S.SE.add(r.rec);
      return r;
    }

    @Override
    public boolean mayHaveMultiple() {
      return true;
    }

    @Override
    public boolean mayHaveText() {
      return true;
    }
  },
  
  /**
   * Contra-Flow information
   * @formatparam channelid_from
   * @formatparam channelid_to
   * 
   * @deprecated THIS IDEA IS INCOMPLETE
   */
  SF("Channel Contra-Flow Definition") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return null;
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      ChannelContraFlowDef sf = new ChannelContraFlowDef();
//      sf.fromLine(value);
//      header.S.addChannelContraFlow(sf);
      return sf;
    }

    @Override
    public boolean mayHaveMultiple() {
      return true;
    }
  },

  /**
   * This represents where the counter is placed, which may be different to where the data is observed.
   * Latitude and longitude use the World Geodetic System of 1984 (WGS84) datum
   * @formatdefault 0.0,0.0
   * @formatparam latitude true DOUBLE
   * Value between -90 and +90
   * @formatparam longitude true DOUBLE
   * Value between -180 and +180
   * @formatparam height false DOUBLE
   * Height in meters above sea level (ignores the length settings in 'FU')
   * @see StationGPS
   */
  SG("Station GPS coordinate") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.S.SG.write();
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.S.SG.fromLine(value);
      return header.S.SG;
    }
  },
  
  /**
   * The station ID is an optional field. 
   * It is useful if data is stored in something other than a file. 
   * For example, when data is stored in a "BLOB" database field, or when transmitted using a protocol which does not support file names.
   * If included, it must precisely match the station ID in the file name.
   * 
   * @formatparam station_id true STRING
   * A station ID. The format must satisfy these rules: <ul conref="stationidformat.dita#stationidformat/sfrules"><li> </li></ul>
   * @since 1.9983
   * @see RawTrafficDataStation#SI
   * @see RawTrafficDataFilename#getStationID()
   * @see StationID
   */
  SI("Station ID") {

    @Override
    public String[] getValues(RawTrafficDataHeader header) throws Exception {
      return header.S.SI.getValue() != null ? new String[]{header.S.SI.getValue()} : null;
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
      header.S.SI.set(value);
      return header.S.SI;
    }
    
  },
  
  /**
   * This record provides additional descriptive information about the station being monitored.
   * Labels are used to add human-readable information to diagrams, tables and charts.
   * They could also be used to link the station record in this file with external systems.
   * 
   * @formatparam element_type true ENUM LabelElementType
   * The type of station element to be labeled
   * @formatparam element_id true
   * <ul>
   * <li>When <parmname>element_type</parmname> is <u>station</u>, this field is ignored. Simply set it to '0'</li>
   * <li>When <parmname>element_type</parmname> is <u>direction</u>, this is a direction code (1 through 8. See the SC record's to_dir field for more)</li>
   * <li>When <parmname>element_type</parmname> is <u>channel</u>, this is a channel ID found in one of the SC records</li>
   * </ul>
   * @formatparam fieldid true ENUM StationLabelElementField
   * The <parmname>fieldid</parmname> parameter gives the label a meaning.
   * Possible values of the <parmname>fieldid</parmname> are constrained by the chosen <parmname>element_type</parmname>.
   * The values that may be used for each <parmname>element_type</parmname> are shown in the table. 
   * For example, the regionname <parmname>fieldid</parmname> may be used to label a station but not a channel.
   * 
   * @formatparam text_value true STRING_ARRAY
   * The text value of the label. 
   * <ul>
   * <li>If speed, mass or length units are used, they should use the same units defined in the 'FU' header.</li>
   * <li>If labeling the fieldid "TC", "TS" or "TL", the text_value is repeated for as many elements as are defined by the corresponding classification system.</li>
   * <li>Values must use escape characters. <ul conref="escapechars.dita#gcescapechars/ecrules"><li></li></ul></li>
   * </ul>
   * 
   * 
   * @formatexample station,0,pathid,I-35
   * The route number of the station is set to 'I-35'
   * @formatexample station,0,regionname,Travis
   * The county of the station is set to 'Travis'
   * @formatexample station,0,description,Western Ring RD\, btw Calder Hwy and Tullamarine Fwy
   * The station's <keyword>description</keyword> is set to 'Western Ring RD btw Calder Hwy and Tullamarine Fwy'. Note how the comma in the text is 'escaped' using a backslash.
   * @formatexample station,0,urbanareaname,Austin
   * An urban area / community is set to 'Austin'
   * @formatexample station,0,othersystemid,tmas,123456
   * The station ID as it relates to a 'tmas' external system is set
   * @formatexample station,0,TC,,Pedestrian,Car,Truck,Bus
   * Four classes in the type classification system are labeled, and the first class index is left blank. 
   * This assumes the <parmname>TC</parmname> header field was been defined with a <keyword>highest_id</keyword> value of 4.
   * Note: This TC label <i>always</i> begins with a label for the first TC element, even when the <keyword>lowest_id</keyword> is set to 1: in this example the first element is assigned a blank value.
   * @formatexample station,0,TS,Slow,Fast,Very Fast
   * Three classes of the speed classification system are labeled. The <parmname>TS</parmname> header field should also be defined.
   * @formatexample station,0,TL,Short,Medium,Long
   * Three classes of the length classification system are labeled. The <parmname>TL</parmname> header field should be defined with 3 <keyword>cutoff_length</keyword> values.
   * You may also provide a label for the <keyword>implied final bin</keyword> (not shown in this example)
   * @formatexample direction,3,description,East side entrance
   * The <keyword>description</keyword> for the East direction is set to 'East side entrance'
   * @formatexample channel,2,description,Fast lane
   * Channel 2's <keyword>description</keyword> is set to 'Fast lane'
   * 
   * @see StationLabelElementField
   * @see #TL
   */
  SL("Station Labels") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return null;
    }
    
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
      return header.S.SL.fromLine(value);
    }

    @Override
    public boolean mayHaveMultiple() {
      return true;
    }

    @Override
    public boolean mayHaveText() {
      return true;
    }
  },

  /**
   * A serial number of the counter(s)
   * @formatparam snvalue true STRING_ARRAY
   * The serial number
   * @formatexample M5367374
   * Device 'M5367374' was used to count
   * @formatexample abc123,xyz345
   * Two counters were used
   */
  SN("Device serial number") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.S.SN.write();
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.S.SN.fromLine(value);
      return header.S.SN;
    }
  },

  /**
   * This represents a polyline or simple polygon.
   * @formatparam shape_type true ENUM ShapeTypeEnum
   * The type of shape described by the points.
   * @formatparam numcoords true DOUBLE 
   * The number of coordinates in the shape. 
   * A coordinate is a sequence of 3 values: latitude, longitude and height. 
   * A line must have at least 2 coordinates and an area must have at least three.
   * @formatparam value true DOUBLE_ARRAY
   * The value array specifies the latitude, longitude and height for every point.
   * The number of values representing all the coordinates in the shape must always equal 3 x numcoords.
   * For an 'area' shape, the last coordinate is implicitly connected to the first, so you do not need to specify the same coordinate twice.
   * @formatexample EXAMPLE_11
   * A parking lot area.
   * @formatexample EXAMPLE_12
   * A road segment line.
   * @since 1.27
   */
  SP("Station Shape") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.S.SP.write();
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.S.SP.fromLine(value);
      return header.S.SP;
    }
  },
  
  /**
   * The time extents of data in this file
   * @formatparam start_date true STRING yyyy/MM/dd
   * The date this count data starts
   * @formatparam start_time true STRING HH:mm:ss.SSS
   * The time this count data starts
   * @formatparam end_date true STRING yyyy/MM/dd
   * The date this count data ends
   * @formatparam end_time true STRING HH:mm:ss.SSS
   * The time this count data ends
   * @formatexample 2014/07/11,00:00:00.000,2014/07/11,03:00:00.000
   */
  SR("Start and End Time")  {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      if (!header.S.SR.isSet())
        return null;
      String[] result = new String[4];
      String from = header.getObservationDateFormat().format(header.S.SR.getStartTime());
      String to   = header.getObservationDateFormat().format(header.S.SR.getEndTime());
      result[0] = from.substring(0, 10);
      result[1] = from.substring(11);
      result[2] = to.substring(0, 10);
      result[3] = to.substring(11);
      
      return result;
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws ParseException {
      String[] fields = RawFmtUtils.split(value, ',');
      Date start = header.getObservationDateFormat().parse(fields[0] + "," + fields[1]);
      Date end   = header.getObservationDateFormat().parse(fields[2] + "," + fields[3]);
      header.S.SR.set(start.getTime(), end.getTime());
      return header.S.SR;
    }
  },

  /**
   * A field technician who retrieved the counting device
   * @formatparam tech_name true STRING
   * Name (or user ID) of the technician
   * @formatexample Jane Doe
   * @see #ST
   */
  SS("Field technician retrieved") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.S.SS != null ? new String[]{header.S.SS} : null;
    }
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.S.SS = value;
      return header.S.SS;
    }

    @Override
    public boolean mayHaveText() {
      return true;
    }
  },
  
  /**
   * A field technician who set out the counting device, OR a field technician who performed the count
   * @formatparam tech_name true
   * Name (or user ID) of the technician
   * @formatexample John Doe
   * @see #SS
   */
  ST("Field technician set out") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.S.ST != null ? new String[]{header.S.ST} : null;
    }
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.S.ST = value;
      return header.S.ST;
    }

    @Override
    public boolean mayHaveText() {
      return true;
    }
  },

  /**
   * An analyst who produced the output
   * @formatparam analyst_name true
   * Name (or user ID) of the analyst
   * @formatexample Roger D. Analyst
   * @formatexample rda
   * @see #ST
   * 
   * @since 1.25
   */
  SU("Analyst producing output") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.S.SU != null ? new String[]{header.S.SU} : null;
    }
    
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.S.SU = TextHeaderEscape.instance.decode(value);
      return header.S.SU;
    }

    @Override
    public boolean mayHaveText() {
      return true;
    }
  },

  /**
   * Time zone of the count data
   * @formatdefault +00:00,local
   * @formatparam offset_h_m true STRING xHH:mm
   * Offset from UTC
   * @formatparam tzmeaning false ENUM TimeZoneMeaning
   * Describes how to treat time stamps in body records (V, T and M)
   * @formatexample +06:00,utc
   * The counter is located at +6 hours from UTC (E.g. in Bangladesh). Count data is in UTC time.
   * @formatexample -10:00
   * The counter is located at -10 hours from UTC (E.g. in Hawaii). Count data is in local time.
   * @see <a href="https://en.wikipedia.org/wiki/List_of_tz_database_time_zones">List of tz database time zones</a>
   * @see MonitoringEventType#dsc Daylight savings time change
   */
  SZ("Time zone") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      if (header.S.SZ.isSet())
        return new String[]{header.S.SZ.toString(), header.S.SZ.meaning.name()};
      return null;
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws ParseException {
      String[] fieldsValue = RawFmtUtils.split(value, ',');
      if (fieldsValue[0].length() > 0) {
        String[] tzfields = RawFmtUtils.split(fieldsValue[0], ':');
        int hour = 0;
        if (tzfields[0].charAt(0) == '+')
          hour = Integer.parseInt(tzfields[0].substring(1));
        else if (tzfields[0].charAt(0) == '-')
          hour = -Integer.parseInt(tzfields[0].substring(1));
        else
          hour = Integer.parseInt(tzfields[0]);
        header.S.SZ.set(hour, Integer.parseInt(tzfields[1]));
      }
      // add the meaning of the time zone
      if (fieldsValue.length > 1) {
        header.S.SZ.meaning = TimeZoneMeaning.valueOf(fieldsValue[1]);
      }
      return header.S.SZ;
    }
  },

// --------------------------------------------------------------  
// Headers Describing Tally Records
//--------------------------------------------------------------  

  /**
   * Where objects are counted by "type", this field is used to describe the type classification system used.
   * Even if you don't store tallies, this may be used to describe the observation's 'vclass' attribute.
   * @formatnote You can provide labels for elements in this record using a SL header.
   * @formatparam class_name true
   * A short name to reference this type classification system. This may also be the numeric identifier of a registered classification system. See <a href="https://geocounts.com/api/registeredclasses.html">registered classification systems</a>
   * @formatparam highest_id true INTEGER
   * The highest class ID used in this system
   * @formatparam _ignore_ false RESERVED
   * @formatparam lowest_id false INTEGER 0
   * If used, set this to either 0 or 1 to indicate what the lowest meaningful value in the classification system is
   * @formatexample austroads94,13,,1
   * A system called 'austroads94' with 13 as the highest classification and 1 the lowest. In this case the TC_component in the T body record will contain <b>14</b> elements because it begins with element 0. See the <b>T</b> body record for more.
   * @formatexample fhwaschemef,14
   * A system called 'fhwaschemef' with 14 as the highest classification and 0 the lowest. In this case the TC_component in the T body record will contain <b>15</b> elements because it begins with element 0. See the <b>T</b> body record for more.
   * @see ClassifiedVehTypeDef
   * @see #SL
   */
  TC("Tally Type Classification") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) throws Exception {
      return header.T.TC.write();
    }
    
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
      header.T.TC.fromLine(value);
      return header.T.TC;
    }
  },
  
  /**
   * The period of each tally record
   * @formatnote You can use values without the 'm'. For example, '15' is the same as 'm15'
   * @formatdefault off
   * @formatparam value true ENUM TallyDurationEnum
   * @formatexample off
   * No tallies ('T' records) recorded
   * @formatexample 60
   * Tally data contains 60 minute increments
   * @formatexample 15
   * Tally data contains 15 minute increments. This means tally sub-periods will be used.
   * 
   * @see TallyDurationEnum
   */
  TD("Tally Duration") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      if (header.T.TD == null)
        return new String[]{TallyDurationEnum.off.name()};
      return new String[]{header.T.TD.minutes > 0 ? Integer.toString(header.T.TD.minutes) : header.T.TD.name()};
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
      header.T.TD = TallyDurationEnum.fromString(value);
      return header.T.TD;
    }
  },

  /**
   * Describes the statistics recorded in the tally record.
   * @formatparam totalfield true ENUM_ARRAY TrafficMonitoringTallyFieldNamespace
   * @formatexample motorized,axles
   * @see TallyCountDef
   */
  TF("Tally Totals") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) throws Exception {
      return header.T.TF.write();
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
      header.T.TF.fromLine(value);
      return header.T.TF;
    }
    /*
    @Override
    public String[] getParamNames(RawTrafficDataHeader header) {
      if (!header.T.TF.isRecording())
        return new String[0];
      return header.T.TF.write();
    }
    */
  },

  /**
   * Where objects are counted by "length", this field is used to describe the length classification system used.
   * @formatnote You can provide labels for elements in this record using a SL header.
   * @formatparam class_name true
   * A short name to reference this length classification system. This may also be the numeric identifier of a registered classification system.
   * @formatparam cutoff_length true DOUBLE_ARRAY
   * The right side of each length bin. The units are specified by the 'FU' record. 
   * The left side of the first length bin is 0.
   * There is always an <keyword>implied final bin</keyword> which starts from the last cutoff_length value and extends to infinity.
   * @formatexample US_TPF_5_192,6.5,21.5,49.0
   * Describes a four bin system: 0 to 6.5, 6.5 to 21.5, 21.5 to 49.0, over 49.0 (this is the <keyword>implied final bin</keyword>)
   * 
   * @see ClassifiedLengthDef
   * @see #SL
   */
  TL("Tally Length Classification") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) {
      return header.T.TL.write();
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) {
      header.T.TL.fromLine(value);
      return header.T.TL;
    }
    /*
    @Override
    public String[] getParamNames(RawTrafficDataHeader header) {
      String[] result = new String[1 + header.T.TL.getMaximumBin()];
      result[0] = "name";
      for (int i=1; i<result.length; i++)
        result[i] = "bin_end_" + i;
      return result;
    }
    */
  },

  /**
   * Describes how to interpret the array of count values in each 'T' body record
   * @formatdefault The default behavior is based on
   * <ul>
   * <li>which 'TF', 'TC', 'TS' and 'TL' records are present in the header</li>
   * <li>The "natural ordering" of these components, which is as follows: 'TF', 'TC', 'TS' and 'TL'</li>
   * </ul>
   * For example, if TO is not defined, and the header defines a TL and TC record, the components of the tally count values will be TC,TL.
   * This is because the natural ordering shown above puts TL component values after TC.
   * @formatparam component false ENUM_ARRAY TallyPartOrderEnum
   * The components of each tally ('T') count record
   * @formatexample TF,TS,TC
   * Each 'T' record will contain an array of binned counts for the TF, TS and TC components, in that order.
   * @see TallyPartOrderDef
   */
  TO("Tally Component Ordering") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) throws Exception {
      return header.T.TO.parts.size() > 0 ? header.T.TO.write() : null;
    }
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
      header.T.TO.fromLine(value);
      return header.T.TO;
    }
  },
  
  /**
   * Where objects are counted by "speed", this field is used to describe the speed classification system used.
   * @formatnote You can provide labels for elements in this record using a SL header.
   * @formatparam class_name true
   * A short name to reference this speed classification system. This may also be the numeric identifier of a registered classification system.
   * @formatparam speed_bins true INTEGER
   * The number of bins used in this classification system
   * @formatparam speed_inc true DOUBLE
   * Speed bin increment in the units specified by the 'FU' record. The left side of the first speed bin is 0.
   * @formatparam speed_start false DOUBLE
   * The width of the first speed bin in the units specified by the 'FU' record. If not set, this defaults to the <parmname>speed_inc</parmname> value
   * @formatexample spd10,10,10
   * There are 10 defined bins plus one <keyword>implied final bin</keyword>. The first bin spans 0 to 10.
   * This means the tally record must have 11 bin counts in its speed component array.
   * @formatexample spd05_start40,11,5,40
   * There are 11 defined bins plus one <keyword>implied final bin</keyword>. The first bin spans 0 to 40. Then follows 10 more bins, stepping every 5. The <keyword>implied final bin</keyword> will start at 90 and continue to infinity.
   * This means the tally record must have 12 bin counts in its speed component array.
   * @see ClassifiedSpeedDef
   */
  TS("Tally Speed Classification") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) throws Exception {
      return header.T.TS.write();
    }
    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
      header.T.TS.fromLine(value);
      return header.T.TS;
    }
  },
  
//--------------------------------------------------------------  
// Headers Describing Observation Records
//--------------------------------------------------------------  
  
  /**
   * Indicates the fields recorded in each observation (vehicle) record
   * @formatparam vfield true ENUM_ARRAY VehicleFieldsEnum
   * @formatexample speed,vclass,wbase,naxles
   * Record speed, vehicle classification, wheelbase and number of axles.
   * @see VehicleRecordDef
   * @see #VA
   */
  VV("Observation Record Fields") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) throws Exception {
      return header.VV.write();
    }

    @Override
    public Object setValues(RawTrafficDataHeader header, String value) throws Exception {
      header.VV.fromLine(value);
      return header.VV;
    }
    /*
    @Override
    public String[] getParamNames(RawTrafficDataHeader header) {
      if (!header.VV.isRecording())
        return new String[0];
      return header.VV.write();
    }
    */
  },

  /**
   * These fields describe subcomponents of an observation.
   * For a vehicle record, these are axles.
   * @formatparam axle_field true ENUM_ARRAY AxleFieldsEnum
   * @formatexample aspace
   * Record inter-axle spacings
   * @see #VV
   * @see AxleRecordDef
   */
  VA("Observation sub-component record fields") {
    @Override
    public String[] getValues(RawTrafficDataHeader header) throws Exception {
      return header.VA.write();
    }

    @Override
    public RawTrafficDataHeaderElement setValues(RawTrafficDataHeader header, String value) throws Exception {
      header.VA.fromLine(value);
      return header.VA;
    }
    /*
    @Override
    public String[] getParamNames(RawTrafficDataHeader header) {
      if (!header.VA.isRecording())
        return new String[0];
      return header.VA.write();
    }
    */
  }
  ;

//  private final boolean REQUIRED;
  private final String shortDesc;

  private EnumHeaderRecords(String _shortDesc) {
    this.shortDesc = _shortDesc;
  }

  /**
   * 
   * @param header
   * @return May return null
   * @throws Exception
   */
  public abstract String[] getValues(RawTrafficDataHeader header) throws Exception;
  
  /**
   * 
   * @param header
   * @param value
   * @return Normally the {@link RawTrafficDataHeaderElement header record} that was modified
   * @throws Exception
   */
  public abstract Object setValues(RawTrafficDataHeader header, String value) throws Exception;

  @Override
  public final String getId() {
    return name();
  }

  @Override
  public final String getDescription() {
    return shortDesc;
  }
  
  /*
   * Never returns null. If there are no parameters, this returns the empty string array
   * @return String[]
   *
  public String[] getParamNames(RawTrafficDataHeader header) {
    return params.toArray();
  }
  
  public EnumHeaderRecordParams getParams() {
    return params;
  }

  public final boolean isRequired() {
    return REQUIRED;
  }
*/
  
  public boolean mayHaveMultiple() {
    return false;
  }
  
  public boolean mayHaveText() {
    return false;
  }
  
  public static EnumHeaderRecords fromKey(String key) throws Exception {
    try {
      return EnumHeaderRecords.valueOf(key);
    } catch (Exception ex) {
      RawFmtUtils.throwException("EnumHeaderRecords", "Key not found", key);
      return null;
    }
  }

}
