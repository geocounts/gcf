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
import java.io.InputStream;
import java.util.ArrayList;


import geocounts.traffic.formats.*;

/**
 * Decodes the raw traffic format
 *
 */
public class RawTrafficDataFileTXTDecoder extends RawTrafficDataFileDecoder {

  /**
   * The decoding options
   * @see DecodingOptions
   */
  public final DecodingOptions options = new DecodingOptions();
  
  /**
   * @deprecated Use {@link #loadFromString(String, boolean, boolean)}
   */
  public static RawTrafficData loadFromString(String data, boolean sort) throws Exception {
    return loadFromString(data, true, sort);
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
    RawTrafficDataFileDecoder decoder = create();
    InputStream in = new ByteArrayInputStream( data.getBytes( "UTF-8" ) );
    try {
      decoder.load(in, addBody, result);
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
    RawTrafficDataFileDecoder decoder = create();
    RawTrafficDataRecordSummary summary = decoder.load(rawFile, addBody, result);
    return result;
  }
  
  /**
   * Helper method to load a {@link RawTrafficData} from an input stream
   * @param in
   * @param addBody
   * @return {@link RawTrafficData}
   * @throws Exception
   */
  public static RawTrafficData loadFromInputStream(InputStream in, boolean addBody) throws Exception {
    RawTrafficData result = new RawTrafficData();
    RawTrafficDataFileDecoder decoder = create();
    RawTrafficDataRecordSummary summary = decoder.load(in, addBody, result);
    return result;
  }
  
  static RawTrafficDataFileDecoder create() {
    return new RawTrafficDataFileTXTDecoder();
  }
  
  public EnumRecordType computeRecordType(String line) {
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

  public RawTrafficDataRecordSummary load(Iterable<String> lines, boolean addBody, RawTrafficData result) throws Exception {
    RawTrafficDataRecordSummary summaryOfFile = new RawTrafficDataRecordSummary();

    for (String line: lines) {
      boolean doContinue = addLine(line, addBody, result, summaryOfFile);
      if (!doContinue)
        break;
    }
    
    return summaryOfFile;
  }
  
  @Override
  public RawTrafficDataRecordSummary load(BufferedReader br, boolean addBody, RawTrafficData result) throws Exception {
    RawTrafficDataRecordSummary summaryOfFile = new RawTrafficDataRecordSummary();
    String line = br.readLine();
    try {
      while (line != null) {
        summaryOfFile.linesRead++;
        boolean doContinue = addLine(line, addBody, result, summaryOfFile);
        if (!doContinue)
          break;
        line = br.readLine();
      }
    } catch (Exception ex) {
      throw new RawTrafficDataFileDecoderException(summaryOfFile.linesRead, ex);
    }
    
    return summaryOfFile;
  }
  
  private boolean addLine(String line, boolean addBody, RawTrafficData result, RawTrafficDataRecordSummary summaryOfFile) throws Exception {
    EnumRecordType type = computeRecordType(line);
    if (type.isBody && !addBody)
      return false;

    switch (type) {
    case X: {
      // ignoring comments
//        result.comments.add(line);
      break;
    }
    case H: {
      Exception hadError = decodeHeaderLine(line, result.header);
      if (hadError != null)
        throw hadError;
      summaryOfFile.headers++;
      if (summaryOfFile.countBodyRecords() > 0) {
        throw new RawTrafficDataFileDecoderException(summaryOfFile.countAllRecords(), "Header record found after body records have started");
      }
      break;
    }
    case T: {
      VehicleTallyRec tally = decodeTallyRec(line, result.header);
      summaryOfFile.tallies++;
      result.addTally(tally);
      break;
    }
    case V: {
      VehicleRec vehicle = decodeObservationRec(line, result.header);
      summaryOfFile.vehicles++;
      result.addObservation(vehicle);
      break;
    }
    case M: {
      MonitoringEventRec event = decodeMonitoringEventRec(line, result.header);
      summaryOfFile.events++;
      result.addMonitoringEvent(event);
      break;
    }
    }
    return true;
  }
  
  public EnumHeaderRecords decodeHeaderLine(RawTrafficDataHeader result, String line) throws Exception {
    EnumHeaderRecords key = EnumHeaderRecords.fromKey(line.substring(0, 2));
    key.setValues(result, line.substring(3).trim());
    return key;
  }

  private Exception decodeHeaderLine(String line, RawTrafficDataHeader header) {
    try {
      EnumHeaderRecords rec = decodeHeaderLine(header, line);
      return null;
    } catch (Exception ex) {
      return ex;
    }
  }

  /**
   * Decodes an {@link VehicleTallyRec tally} record. You do not need to call this directly
   */
  public VehicleTallyRec decodeTallyRec(String line, RawTrafficDataHeader header) throws Exception {
    ArrayList<TallyPartOrder> tallyPartsToSet = new ArrayList<TallyPartOrder>();
    tallyPartsToSet.addAll(header.T.TO.getAll());
    if (tallyPartsToSet.size() == 0) {
      TallyPartOrderDef.setDefaultPartOrder(header.T, tallyPartsToSet);
    }
    
    String[] PARTS  = RawFmtUtils.split(line, ';');
    String[] referencePART = RawFmtUtils.split(PARTS[1], ',');
    
    int channelID = Integer.parseInt(referencePART[3]);
    int period = Integer.parseInt(referencePART[2]);
    long time = header.getTallyDateFormat().parse(referencePART[0] + "," + referencePART[1]).getTime();
    VehicleTallyRec tally = header.T.create(channelID, period, time);
    
    int tallysetparts = 2;
    for (TallyPartOrder setProp: tallyPartsToSet) {
      setProp.setTally(PARTS[tallysetparts], header, options, tally);
      tallysetparts++;
    }
    return tally;
  }
  
  public class ParsedVehicleStrings {
    public final String[] PARTS;
    private ParsedVehicleStrings(String[] p) {
      PARTS = p;
    }
    public String[] referencePART;
    public String[] vehiclePART;
    
    public int getNumberOfAxleParts() {
      return PARTS.length - 3;
    }
    
    public String getAxleParts(int axleID) {
      return PARTS[3 + axleID];
    }
    
    public int getChannelID() {
      return Integer.parseInt(referencePART[2]);
    }
  }
  
  public ParsedVehicleStrings createParsedVehicleStrings(String line) {
    ParsedVehicleStrings result = new ParsedVehicleStrings(RawFmtUtils.split(line, ';'));
    if (result.PARTS.length > 1)
      result.referencePART = RawFmtUtils.split(result.PARTS[1], ',');
    if (result.PARTS.length > 2)
      result.vehiclePART = RawFmtUtils.split(result.PARTS[2], ',');
    return result;
  }
  
  /**
   * Decodes an {@link VehicleRec observation} record. You do not need to call this directly
   */
  public VehicleRec decodeObservationRec(String line, RawTrafficDataHeader header) throws Exception {
    ParsedVehicleStrings parsedRec = createParsedVehicleStrings(line);

    java.util.Date time = header.getObservationDateFormat().parse(parsedRec.referencePART[0] + "," + parsedRec.referencePART[1]);
    int channelID = parsedRec.getChannelID();
    
    VehicleRec result = header.VV.create(channelID, time.getTime());
    
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
        String[] axlePART = RawFmtUtils.split(parsedRec.getAxleParts(axlePartID), ',');
        if (axlePART.length > 0) {
          VehicleRec.AxleRec axle = result.addAxle();
          for (int i=0; i<axlePART.length; i++) {
            header.VA.fields.get(i).set(axlePART[i], header.F.FU, axle);
          }
        }
      }
    }
    
    return result;
  }
  
  public MonitoringEventRec decodeMonitoringEventRec(String line, RawTrafficDataHeader header) throws Exception {
    String[] PARTS  = RawFmtUtils.split(line, ';');
    String[] timePART = RawFmtUtils.split(PARTS[1], ',');
    java.util.Date time = header.getObservationDateFormat().parse(timePART[0] + "," + timePART[1]);
    MonitoringEventRec result = header.newMonitoringEventRec(time.getTime());
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

}
