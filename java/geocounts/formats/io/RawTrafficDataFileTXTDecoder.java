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
package geocounts.formats.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.function.LongConsumer;

import geocounts.traffic.formats.*;

/**
 * Decodes the GEOCOUNTS file format
 * @see RawTrafficDataFileTXTEncoder
 */
public class RawTrafficDataFileTXTDecoder extends RawTrafficDataFileDecoder {
  
  public RawTrafficDataFileTXTDecoder() {
    this(new DecodingOptions());
  }
  
  protected RawTrafficDataFileTXTDecoder(DecodingOptions o) {
    super(o);
  }
  
  static class ReceiveTime implements TimedObject, LongConsumer {
    private long time = 0;

    @Override
    public long getTime() {
      return time;
    }

    @Override
    public void accept(long value) {
      time = value;
    }
    
  }

  
  /**
   * Helper method to load a {@link RawTrafficData} from a string. 
   * This is used by servlets that receive data posted as a parameter
   * @param data The GC formatted raw data as a String
   * @param addBody Whether to add the body, or only load the header
   * @param sort If true, sort the events and tallies
   * @return {@link RawTrafficData} including the body
   * @throws Exception
   */
  public static RawTrafficData loadFromString(String data, boolean addBody, boolean sort) throws Exception {
    RawTrafficData result = new RawTrafficData();
    RawTrafficDataFileDecoder decoder = create(addBody);
    InputStream in = new ByteArrayInputStream( data.getBytes( "UTF-8" ) );
    try {
      decoder.load(in, result);
    } finally {
      in.close();
    }
    if (addBody && sort) {
      result.sortObservations();
      result.sortTallies();
    }
    return result;
  }
  
  /**
   * Helper method to load a {@link RawTrafficData} from a file
   * @param rawFile
   * @param addBody Whether to add the body, or only load the header
   * @return {@link RawTrafficData}
   * @throws Exception
   */
  public static RawTrafficData loadFromFile(File rawFile, boolean addBody) throws Exception {
    RawTrafficData result = new RawTrafficData();
    RawTrafficDataFileDecoder decoder = create(addBody);
    RawTrafficDataRecordSummary summary = decoder.load(rawFile, result);
    return result;
  }
  
  /**
   * Helper method to load a {@link RawTrafficData} from an input stream
   * @return {@link RawTrafficData}
   * @throws Exception
   */
  public static RawTrafficData loadFromInputStream(InputStream in, boolean addBody) throws Exception {
    RawTrafficData result = new RawTrafficData();
    RawTrafficDataFileDecoder decoder = create(addBody);
    RawTrafficDataRecordSummary summary = decoder.load(in, result);
    return result;
  }
  
  static RawTrafficDataFileDecoder create(boolean addBody) {
    RawTrafficDataFileDecoder result = new RawTrafficDataFileTXTDecoder();
    result.options.setAddBody(addBody);
    return result;
  }
  
  public static EnumRecordType computeRecordType(String line) {
    EnumRecordType type = EnumRecordType.X;
    if (line.length() > 2) {
      if (line.charAt(2) == ' ') {
        type = EnumRecordType.H;
      } else if (line.charAt(0) == '#') {
        type = EnumRecordType.X;
      } else {
        type = EnumRecordType.valueOf(line.substring(0, 1));
      }
    }
    return type;
  }

  public RawTrafficDataRecordSummary load(Iterable<String> lines, RawTrafficData result) throws Exception {
    CallbackFacade f = new CallbackFacade(result, options);
    try {
      for (String line: lines) {
        EnumRecordType type = computeRecordType(line);
        if (type == EnumRecordType.H) {
          Exception hadError = decodeHeaderLine(line, result.header, f.updateTZ);
          if (hadError != null)
            throw hadError;
          f.summaryOfFile.headers++;
          if (f.summaryOfFile.countBodyRecords() > 0)
            throwHeaderFoundAfterBody(null, f.summaryOfFile.countAllRecords());
        } else {
          if (type.isBody && !options.addBody())
            return f.summaryOfFile;
        
          addBodyLine(type, line, result.header, f, f.updateTZ, f.updateBodyTime);
        }
      }
    } finally {
      f.close();
    }
    return f.summaryOfFile;
  }
  
  @Override
  public RawTrafficDataRecordSummary load(BufferedReader br, RawTrafficData result) throws Exception {
    CallbackFacade f = new CallbackFacade(result, options);
    try {
      String line = br.readLine();
      try {
        while (line != null) {
          f.summaryOfFile.linesRead++;
          EnumRecordType type = computeRecordType(line);
          if (type == EnumRecordType.H) {
            Exception hadError = decodeHeaderLine(line, result.header, f.updateTZ);
            if (hadError != null)
              throw hadError;
            f.summaryOfFile.headers++;
            if (f.summaryOfFile.countBodyRecords() > 0)
              throwHeaderFoundAfterBody(null, f.summaryOfFile.countAllRecords());
          } else {
            if (type.isBody && !options.addBody())
              return f.summaryOfFile;
          
            addBodyLine(type, line, result.header, f, f.updateTZ, f.updateBodyTime);
          }
          line = br.readLine();
        }
      } catch (Throwable ex) {
        if (ex instanceof RawTrafficDataFileDecoderException)
          throw ex;
        if (ex instanceof java.lang.OutOfMemoryError)
          throw (java.lang.OutOfMemoryError)ex;
        throw new RawTrafficDataFileDecoderException(f.summaryOfFile.linesRead, ex);
      }
    } finally {
      f.close();
    }
    return f.summaryOfFile;
  }
  
  protected static void throwHeaderFoundAfterBody(File rawFile, int lineNumber) throws RawTrafficDataFileDecoderException {
    RawTrafficDataFileDecoderException e = new RawTrafficDataFileDecoderException(lineNumber, "Header record found after body records have started");
    e.sourceFile = rawFile;
    throw e;
  }
  
  /**
   * A callback to load into an in-memory {@link RawTrafficData}
   * @see geocounts.formats.reports.NumericReportID#kludgeLoadAsync
   *
   */
  static class CallbackFacade implements RawTrafficDataFileDecoderCallback {
    final RawTrafficData d;
    private boolean hasHeader = false;
    
    private final UTCToLocalTimeHelper updateTZ;
    private final ReceiveTime updateBodyTime = new ReceiveTime();
    
    RawTrafficDataRecordSummary summaryOfFile = new RawTrafficDataRecordSummary();
    private CallbackFacade(RawTrafficData f, DecodingOptions options) {
      d = f;
      updateTZ = new UTCToLocalTimeHelper(options);
    }

    @Override
    public RawTrafficDataHeader getHeader() {
      return hasHeader ? d.header : null;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean onHeader(RawTrafficDataHeader header) {
      hasHeader = true;
      return true;
    }

    @Override
    public boolean addTally(VehicleTallyRec tally) {
      summaryOfFile.tallies++;
      d.addTally(tally);
      return true;
    }

    @Override
    public boolean addObservation(VehicleRec obs) {
      summaryOfFile.vehicles++;
      d.addObservation(obs);
      return true;
    }

    @Override
    public boolean addMonitoringEvent(MonitoringEventRec evt) {
      summaryOfFile.events++;
      d.addMonitoringEvent(evt);
      return true;
    }
  }
  
  protected boolean addBodyLine(EnumRecordType type, String line, RawTrafficDataHeader header, RawTrafficDataFileDecoderCallback callback, UTCToLocalTimeHelper updateTZ, LongConsumer updateLastBodyTime) throws Exception {
    switch (type) {
    case X: {
      // ignoring comments
//        result.comments.add(line);
      break;
    }
    case T: {
      VehicleTallyRec tally = decodeTallyRec(line, header);
      updateTZ.setToLocalTime(tally);
      updateLastBodyTime.accept(tally.getTime());
      if (options.addTallies)
        return callback.addTally(tally);
      return true;
    }
    case V: {
      VehicleRec vehicle = decodeObservationRec(line, header);
      updateTZ.setToLocalTime(vehicle);
      updateLastBodyTime.accept(vehicle.getTime());
      if (options.addObservations) {
        if (options.observationRule != null)
          options.observationRule.accept(vehicle);
        return callback.addObservation(vehicle);
      }
      return true;
    }
    case M: {
      MonitoringEventRec event = decodeMonitoringEventRec(line, header);
      updateTZ.setToLocalTime(event);
      updateLastBodyTime.accept(event.getTime());
      if (options.addMonitoringEvents)
        return callback.addMonitoringEvent(event);
      return true;
    }
    }
    return true;
  }
  
  public static EnumHeaderRecords decodeHeaderLine(RawTrafficDataHeader result, String line) throws Exception {
    EnumHeaderRecords key = EnumHeaderRecords.fromKey(line.substring(0, 2));
    key.setValues(result, line.substring(3).trim());
    return key;
  }

  /**
   * 
   * A possible UTC timezone information may arrive before or after the SR record: this works out when to trigger
   *
   */
  static class UTCToLocalTimeHelper {
    private boolean utcIsSet = false;
    private boolean srIsSet = false;
    private long timeToAdd = 0;

    private final DecodingOptions options;
    UTCToLocalTimeHelper(DecodingOptions opts) {
      this.options = opts;
    }
    
    void considerSZ(RawTrafficDataHeader header) {
      if (header.S.SZ.isUTC())
        utcIsSet = true;
      checkTrigger(header);
    }
    
    void considerSR(RawTrafficDataHeader header) {
      if (header.S.SR.isSet())
        srIsSet = true;
      checkTrigger(header);
    }
    
    private void checkTrigger(RawTrafficDataHeader header) {
      if (!utcIsSet)
        return;
      if (!srIsSet)
        return;
      
      timeToAdd = header.S.SZ.getRecordedTimestampOffset();

      // set to local time
      if (header.S.SR.hasStartTime()) {
        header.S.SR.setStartTime(header.S.SR.getStartTime() + timeToAdd);
      }
      if (header.S.SR.hasEndTime()) {
        header.S.SR.setEndTime(header.S.SR.getEndTime() + timeToAdd);
      }
      header.S.SZ.reset();
    }
    
    private void setToLocalTime(RawTrafficDataTrafficElement target) {
      if (timeToAdd == 0)
        return;
      long newTimeNoDaylightSavings = target.addTime(timeToAdd);
      options.updateTimeForDaylightSavings(target);
    }
  }
  
  protected static Exception decodeHeaderLine(String line, RawTrafficDataHeader header, UTCToLocalTimeHelper updateTZ) {
    try {
      EnumHeaderRecords rec = decodeHeaderLine(header, line);
      if (rec == EnumHeaderRecords.SZ) {
        updateTZ.considerSZ(header);
      } else if (rec == EnumHeaderRecords.SR) {
        updateTZ.considerSR(header);
      }
      return null;
    } catch (Exception ex) {
      return ex;
    }
  }

  /**
   * Decodes an {@link VehicleTallyRec tally} record. You do not need to call this directly
   * @see #decodeMonitoringEventRec(String, RawTrafficDataHeader)
   * @see #decodeObservationRec(String, RawTrafficDataHeader)
   */
  public VehicleTallyRec decodeTallyRec(String line, RawTrafficDataHeader header) throws Exception {
    ArrayList<TallyPartOrder> tallyPartsToSet = new ArrayList<TallyPartOrder>();
    tallyPartsToSet.addAll(header.T.TO.getAll());
    if (tallyPartsToSet.size() == 0) {
      TallyPartOrderDef.setDefaultPartOrder(header.T, tallyPartsToSet);
    }
    
    String[] PARTS  = RawFmtUtils.split(line, header.F.FL.BODY_PART);
    String[] referencePART = RawFmtUtils.split(PARTS[1], header.F.FL.BODY_FIELD);
    
    int channelID = Integer.parseInt(referencePART[3]);
    long time = header.F.FL.parseBodyTallyDateTime(referencePART[0], referencePART[1]);
    int period = Integer.parseInt(referencePART[2]);
    
    long timeOffset = options.getTallyTimeCorrection(time, header.S);
    if (timeOffset != 0)
      time += timeOffset;
    
    VehicleTallyRec tally = header.T.create(channelID, period, time);
    
    int tallysetparts = 2;
    for (TallyPartOrder setProp: tallyPartsToSet) {
      setProp.setTally(PARTS[tallysetparts], header, options, tally);
      tallysetparts++;
    }
    return tally;
  }
  
  
  public static class ParsedVehicleStrings {
    // V;2020/03/01,23:59:59.810,1;2;0;3.009;
    public final String[] PARTS;
    private ParsedVehicleStrings(String[] p) {
      PARTS = p;
    }
    
    public String[] referencePART;
    
    /**
     * Contains vehicle, axle and axlegroup parts
     */
    public String[] vehiclePART;
    
    public int getNumberOfAxleParts() {
      return PARTS.length - 3;  // first three are "V", "Date,Time,Channel" and "Vehicle Fields"
    }
    
    public String getAxlePart(int axleID) {
      return PARTS[3 + axleID];
    }

    public int getChannelID() {
      return Integer.parseInt(referencePART[2]);
    }
  }
  
  public static ParsedVehicleStrings createParsedVehicleStrings(String line, TxtFileBodyDelimiters delim) {
    ParsedVehicleStrings result = new ParsedVehicleStrings(RawFmtUtils.split(line, delim.BODY_PART));
    if (result.PARTS.length > 1)
      result.referencePART = RawFmtUtils.split(result.PARTS[1], delim.BODY_FIELD);
    if (result.PARTS.length > 2)
      result.vehiclePART = RawFmtUtils.split(result.PARTS[2], delim.BODY_FIELD);
    return result;
  }
  
  enum EndOfAxleFieldsEvent {
    noaxles,
    normal,
    groups;
  }
  
  private static EndOfAxleFieldsEvent parseAxleFields(RawTrafficDataHeader header, String axleFields, VehicleRec result) throws Exception {
    String[] axleFIELDS = RawFmtUtils.split(axleFields, header.F.FL.BODY_FIELD);  // for one axle at a time
    if (axleFIELDS.length > 0) {
      if ((axleFIELDS[0].length() == 1) && (axleFIELDS[0].charAt(0) == GROUP_PARTS_DELIM))
        return EndOfAxleFieldsEvent.groups;
      
      AxleRec axle = result.addAxle();
      for (int i=0; i<axleFIELDS.length; i++) {
        header.VA.fields.get(i).set(axleFIELDS[i], header.F.FU, axle);
      }
      return EndOfAxleFieldsEvent.normal;
    }
    return EndOfAxleFieldsEvent.noaxles;
  }
  
  private static void parseGroupFields(RawTrafficDataHeader header, String groupFields, AxleGroupRec axleGroup) throws Exception {
    String[] axleGroupFIELDS = RawFmtUtils.split(groupFields, header.F.FL.BODY_FIELD);  // for one group at a time
    if (axleGroupFIELDS.length > 0) {
      for (int i=0; i<axleGroupFIELDS.length; i++) {
        header.VG.fields.get(i).set(axleGroupFIELDS[i], header.F.FU, axleGroup);
      }
    }
  }
  
  /**
   * Decodes an {@link VehicleRec observation} record. You do not need to call this directly
   * @param line Line to decode
   * @param header The header to use in decoding the record
   * 
   * @see #decodeMonitoringEventRec(String, RawTrafficDataHeader)
   * @see #decodeTallyRec(String, RawTrafficDataHeader)
   */
  public VehicleRec decodeObservationRec(String line, RawTrafficDataHeader header) throws Exception {
    ParsedVehicleStrings parsedRec = createParsedVehicleStrings(line, header.F.FL);

    // parse the time
    long time = header.F.FL.parseBodyEventDateTime(parsedRec.referencePART[0], parsedRec.referencePART[1]);
    
    long timeOffset = options.getTimestampTimeCorrection(time, header.S);
    if (timeOffset != 0)
      time += timeOffset;
    
//    java.util.Date time = header.getObservationDateFormat().parse(parsedRec.referencePART[0] + "," + parsedRec.referencePART[1]);
//    int channelID = parsedRec.getChannelID();
    
    VehicleRec result = header.VV.create(parsedRec.getChannelID(), time);
    
    // vehicle parts
    if (parsedRec.vehiclePART != null) {
      for (int i=0; i<parsedRec.vehiclePART.length; i++) {
        header.VV.getField(i).set(parsedRec.vehiclePART[i], header.F.FU, result);
      }
    }
    
    // axle parts
    if (header.VA.isRecording()) {
      int numAxleParts = parsedRec.getNumberOfAxleParts();

      for (int axlePartID=0; axlePartID<numAxleParts; axlePartID++) {
        String axleFields = parsedRec.getAxlePart(axlePartID);
        EndOfAxleFieldsEvent endEvent = parseAxleFields(header, axleFields, result);
        
        if (endEvent == EndOfAxleFieldsEvent.groups) {  // possibly axle groups
          if (header.VG.isRecording()) {  // 
            String[] axlesInGroupsStr = RawFmtUtils.split(axleFields, header.F.FL.BODY_FIELD);
            int[] numberOfAxlesInEachGroup = new int[axlesInGroupsStr.length-1];  // "G,1,2,3" is a 1-2-3 vehicle
            for (int g=1; g<axlesInGroupsStr.length; g++) {
              numberOfAxlesInEachGroup[g-1] = Integer.parseInt(axlesInGroupsStr[g]);
            }
            result.setGroups(numberOfAxlesInEachGroup);
            
            for (int axleGroupPartID=0; axleGroupPartID<result.axleGroups.size(); axleGroupPartID++) {
              parseGroupFields(header, parsedRec.getAxlePart(axlePartID + axleGroupPartID + 1), result.axleGroups.get(axleGroupPartID));
            }
          }
          
          break;
        }
      }
    }
    return result;
  }
  
  public static final char GROUP_PARTS_DELIM = 'G';
  
  /**
   * @see #decodeTallyRec(String, RawTrafficDataHeader)
   * @see #decodeObservationRec(String, RawTrafficDataHeader)
   */
  public MonitoringEventRec decodeMonitoringEventRec(String line, RawTrafficDataHeader header) throws Exception {
    String[] PARTS        = RawFmtUtils.split(line, header.F.FL.BODY_PART);
    String[] timePART     = RawFmtUtils.split(PARTS[1], header.F.FL.BODY_FIELD);
    long time = header.F.FL.parseBodyEventDateTime(timePART[0], timePART[1]);
    
    long timeOffset = options.getTimestampTimeCorrection(time, header.S);
    if (timeOffset != 0)
      time += timeOffset;
    
    MonitoringEventRec result = header.newMonitoringEventRec(time);
    try {
      String eventType = PARTS[2].toLowerCase();
      if (eventType.startsWith(TextHeaderEscape.XX)) {
        result.setEventTypeUserDefined(eventType.substring(3));
      } else
        result.setEventType(MonitoringEventType.valueOf(eventType));
    } catch (Exception ex) {
      result.setEventType(MonitoringEventType.comment);
    }
    if (PARTS.length > 3)
      result.text = PARTS[3];
    return result;
  }

  /**
   * Used to quickly pull a {@link EnumRecordType#V V} record line out of a text file
   * @param nthVLine The V line of the file where the first V record is 1
   * @param rawFile The file to decode
   * @return The String representing the nth V rec in the file
   * @throws IOException
   */
  public static String getVLine(int nthVLine, File rawFile) throws IOException {
    return getNthLine(EnumRecordType.V, nthVLine, rawFile);
  }

  public static String getTLine(int nthTLine, File rawFile) throws IOException {
    return getNthLine(EnumRecordType.T, nthTLine, rawFile);
  }
  
  public static String getNthLine(EnumRecordType recType, int nthLine, File rawFile) throws IOException {
    if (nthLine < 1)
      return null;
    
    BufferedReader br = new BufferedReader(new FileReader(rawFile));
    try {
      String line = br.readLine();
      int count = 0;
      while (line != null) {
        EnumRecordType type = computeRecordType(line);
        if (type == recType) {
          count++;
          if (count == nthLine)
            return line;
        }
        line = br.readLine();
      }
    } finally {
      br.close();
    }
    return null;
  }
}
