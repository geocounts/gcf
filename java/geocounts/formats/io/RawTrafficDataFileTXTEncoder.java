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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import geocounts.traffic.formats.*;

/**
 * Writes the GEOCOUNTS text file from raw count data. The simplest method to call is {@link #writeData(File, RawTrafficData)}
 * @see RawTrafficData
 *
 */
public class RawTrafficDataFileTXTEncoder extends RawTrafficDataFileEncoder {

  /**
   * As soon you successfully construct this object you <b>must</b> call {@link #close()}
   * @param file
   * @throws Exception
   */
  public RawTrafficDataFileTXTEncoder(File file) throws Exception {
    super(file);
  }

  private RawTrafficDataFileTXTEncoder(PrintWriter out, boolean shouldCloseWriter) throws Exception {
    super(out, shouldCloseWriter);
  }
  
  public static RawTrafficDataFileDecoderCallback newEncoder(File cache) throws Exception {
    return new RawTrafficDataFileDecoderCallbackFileImpl(cache);
  }
  
  /**
   * 
   * @param out You do not need to call close on this object
   * @param header The header to write
   * @throws Exception
   */
  public static void writeHeader(java.io.PrintWriter out, RawTrafficDataFilename fileName, RawTrafficDataHeader header) throws Exception {
    RawTrafficDataFileTXTEncoder e = new RawTrafficDataFileTXTEncoder(out, false);
    e.writeHeader(fileName, header);
  }
  
  public static String toFileHeader(RawTrafficDataHeader header) throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    PrintWriter out = new PrintWriter(bos);
    try {
      writeHeader(out, new RawTrafficDataFilename(), header);
    } finally {
      out.close();
    }
    return bos.toString();
  }
  
  public static List<String> toFileHeaderStrings(RawTrafficDataHeader header) throws Exception {
    ArrayList<String> result = new ArrayList<String>();
    String sttr = toFileHeader(header);
    BufferedReader reader = new BufferedReader(new StringReader(sttr));
    String l = reader.readLine();
    while (l != null) {
      result.add(l);
      l = reader.readLine();
    }
    return result;
  }
  
  /**
   * @param file File to write to
   * @param fileName The {@link RawTrafficDataFilename file name} of the source data
   * @param header The header to write
   * @throws Exception
   */
  public static void writeHeader(File file, RawTrafficDataFilename fileName, RawTrafficDataHeader header) throws Exception {
    PrintWriter out = new PrintWriter(file);
    try {
      RawTrafficDataFileTXTEncoder e = new RawTrafficDataFileTXTEncoder(out, false);
      e.writeHeader(fileName, header);
    } finally {
      out.close();
    }
  }
  
  /**
   * 
   * @param out You do not need to call close on this object
   * @param fileName {@link RawTrafficDataFilename}
   * @param data The {@link RawTrafficData data} to write
   * @throws Exception
   */
  public static void writeData(java.io.PrintWriter out, RawTrafficDataFilename fileName, RawTrafficData data) throws Exception {
    RawTrafficDataFileTXTEncoder e = new RawTrafficDataFileTXTEncoder(out, false);
    e.execWrite(fileName, data);
  }

  /**
   * 
   * @param file The name must be a {@link RawTrafficDataFilename}
   * @param data The {@link RawTrafficData} to write
   * @throws Exception
   */
  public static void writeData(File file, RawTrafficData data) throws Exception {
    RawTrafficDataFilename fileName = new RawTrafficDataFilename(file.getName());
    RawTrafficDataFileTXTEncoder e = new RawTrafficDataFileTXTEncoder(file);
    e.execWrite(fileName, data);
  }
  
  private void execWrite(RawTrafficDataFilename fileName, RawTrafficData data) throws Exception {
    try {
      // write the header
      writeHeader(fileName, data.header);
      // write the body
      writeBody(fileName, data);
    } finally {
      close();
    }
  }
  
  private List<TallyPartOrder> computeTallyParts(RawTrafficDataHeader header) {
    ArrayList<TallyPartOrder> result = new ArrayList<TallyPartOrder>();
    result.addAll(header.T.TO.getAll());
    if (result.size() == 0) {
      TallyPartOrderDef.setDefaultPartOrder(header.T, result);
    }
    return result;
  }

  @Override
  protected void writeBody(RawTrafficDataFilename fileName, RawTrafficData data) throws Exception {
    // get the tally parts being set. This is deduced from the header
    List<TallyPartOrder> tallyPartsToSet = computeTallyParts(data.header);
    
    // print tally records
    for (VehicleTallyRec tally: data.tallies()) {
      println(toTally(data.header, tally, tallyPartsToSet));
    }
    // print monitoring events
    for (MonitoringEventRec event: data.monitoringevents()) {
      println(toMonitoringEvent(data.header, event));
    }
    // print observations
    for (VehicleRec veh: data.observations()) {
      println(toVehicle(data.header, veh));
    }
  }
  
  @Override
  protected void writeHeader(RawTrafficDataFilename fileName, RawTrafficDataHeader header) throws Exception {
    for (EnumHeaderRecords rec : EnumHeaderRecords.values()) {
      if (!rec.mayHaveMultiple()) {
        String[] data = rec.getValues(header);
        String value = null;
        if (data != null)
          value = toHeaderString(data, rec.mayHaveText());

        if (value != null) {
          println(rec.getId() + " " + value);
        } /* else {
          if (rec.isRequired()) {
            RawFmtUtils.throwException(this, "Header element is required", rec.getId());
          }
        } */
      }
    }

    // changes
    for (FileChangeRec o: header.F.FX) {
      printHeaderLine(EnumHeaderRecords.FX, o.write(), true);
    }
    
    // channels
    for (ChannelData ch: header.S.getAllChannels()) {
      printHeaderLine(EnumHeaderRecords.SC, ch.write(), false);
    }

    // station labels (station, direction, channel)
    for (StationLabels.StationLabelRec o: header.S.SL.all()) {
      printHeaderLine(EnumHeaderRecords.SL, o.write(), true);
    }

    // equipment (station)
    for (Equipment.EquipmentRecBase o: header.S.SE.allForStation()) {
      printHeaderLine(EnumHeaderRecords.SE, EquipmentHeaderRec.write(o), false);
    }
    
    // equipment (channel)
    for (Equipment.EquipmentRecBase o: header.S.SE.allForChannels()) {
      printHeaderLine(EnumHeaderRecords.SE, EquipmentHeaderRec.write(o), false);
    }
    /*
    // contra flow
    for (ChannelContraFlowDef ch: header.S.getAllContraFlowDefs()) {
      printHeaderLine(EnumHeaderRecords.SF, ch.write(), false);
    }
    */
  }

  private void printHeaderLine(IdDesc id, String[] values, boolean shouldEscape) {
    println(id.getId() + " " + toHeaderString(values, shouldEscape));
  }

  private final String toHeaderString(String[] fields, boolean shouldEscape) {
    if (fields == null)
      return null;
    if (fields.length == 0)
      return null;

    StringBuilder sb = new StringBuilder();
    for (int i=0; i<fields.length; i++) {
      if (i > 0)
        sb.append(TextHeaderEscape.DChars.DELIM.escaped);
      if (shouldEscape)
        TextHeaderEscape.instance.encode(fields[i], sb);
      else if (fields[i] != null)
        sb.append(fields[i]);
    }
    return sb.toString();
  }
  
  public String toVehicle(RawTrafficDataHeader header, VehicleRec veh) throws Exception {
    StringBuilder result = new StringBuilder();
    result.append(EnumRecordType.V.getId());
    result.append(header.F.FL.BODY_PART);
    result.append(header.F.FL.formatBodyEventDateTime(veh.getTime()));
    result.append(header.F.FL.BODY_FIELD);
    result.append(veh.channelID);
    result.append(header.F.FL.BODY_PART);
    // vehicle subfields
    int i=0;
    for (ObservationField f: header.VV) {
      if (i > 0)
        result.append(header.F.FL.BODY_FIELD);
      result.append(f.get(veh, header.F.FU));
      i++;
    }
    result.append(header.F.FL.BODY_PART);
    // axles
    if (header.VA.isRecording()) {
      for (AxleRec axl: veh.axles) {
        i=0;
        for (; i<header.VA.numberOfFields(); i++) {
          if (i > 0)
            result.append(header.F.FL.BODY_FIELD);
          result.append(header.VA.fields.get(i).get(axl, header.F.FU));
        }
        result.append(header.F.FL.BODY_PART);
      }
      
      // axle groups - only if there are axles and ACTUAL groupings
      if (header.VG.isRecording() && (veh.axleGroups != null)) {
        result.append(RawTrafficDataFileTXTDecoder.GROUP_PARTS_DELIM);
        for (AxleGroupRec axg: veh.axleGroups) {
          result.append(header.F.FL.BODY_FIELD);
          result.append(axg.axlesInGroup.size());
        }
        result.append(header.F.FL.BODY_PART);
        
        for (AxleGroupRec axg: veh.axleGroups) {
          i=0;
          for (; i<header.VG.numberOfFields(); i++) {
            if (i > 0)
              result.append(header.F.FL.BODY_FIELD);
            result.append(header.VG.fields.get(i).get(axg, header.F.FU));
          }
          result.append(header.F.FL.BODY_PART);
        }
      }
    }
    
    return result.toString();
  }
  
  /**
   * 
   * @see RawTrafficDataFileTXTDecoder#decodeTallyRec(String, RawTrafficDataHeader)
   */
  protected String toTally(RawTrafficDataHeader header, VehicleTallyRec tally, Collection<TallyPartOrder> tallyPartsToSet) throws Exception {
    StringBuilder result = new StringBuilder();
    result.append(EnumRecordType.T.getId());
    result.append(header.F.FL.BODY_PART);
    // fixed referencing data subfields
    result.append(header.F.FL.formatBodyTallyDateTime(tally.getTime())); // getTallyDateFormat().format(tally.getTime()));
    result.append(header.F.FL.BODY_FIELD);
    result.append(tally.getHourlySubPeriod());
    result.append(header.F.FL.BODY_FIELD);
    result.append(tally.channelID);
    result.append(header.F.FL.BODY_PART);

    for (TallyPartOrder p : tallyPartsToSet) {
      p.encode(tally, header, result);
      result.append(header.F.FL.BODY_PART);
    }
    
    return result.toString();
  }

  private String toMonitoringEvent(RawTrafficDataHeader header,  MonitoringEventRec event) {
    StringBuilder result = new StringBuilder();
    result.append(EnumRecordType.M.getId());
    result.append(header.F.FL.BODY_PART);
    result.append(header.F.FL.formatBodyEventDateTime(event.getTime())); // getObservationDateFormat().format(event.getTime()));
    result.append(header.F.FL.BODY_PART);
    result.append(event.getEventTypeID());
    result.append(header.F.FL.BODY_PART);
    result.append(RawFmtUtils.notNull(event.text));
    result.append(header.F.FL.BODY_PART);
    return result.toString();
  }

  
  static class RawTrafficDataFileDecoderCallbackFileImpl implements RawTrafficDataFileDecoderCallback {
    final RawTrafficDataFileTXTEncoder bodyEncoder;
    final File bodyFile;
    final File resultFile;
    
    List<TallyPartOrder> tallyPartsToSet;
    RawTrafficDataHeader h;
    
    RawTrafficDataFileDecoderCallbackFileImpl(File f) throws Exception {
      bodyFile = new File(f.getParentFile(), f.getName() + ".body");
      if (bodyFile.exists())
        throw new IOException("File exists: " + bodyFile.getName());
      resultFile = f;
      bodyEncoder = new RawTrafficDataFileTXTEncoder(bodyFile);
    }
    
    @Override
    public synchronized void close() throws Exception {
      try {
        // merge header and body into the output
        bodyEncoder.pw.flush();
        bodyEncoder.close();

        if (h != null) {
          PrintWriter output = new PrintWriter(resultFile);
          try {
            for (String s: RawTrafficDataFileTXTEncoder.toFileHeaderStrings(h))
              output.println(s);
            copyAsText(bodyFile, output);
          } finally {
            output.close();
          }
        }
      } finally {
        bodyFile.delete();
      }
    }

    /**
     * Copied from CopyUtils.copyAsText
     * @throws IOException
     */
    private long copyAsText(File input, java.io.Writer out) throws IOException {
      long result = 0;
      
      FileReader fis = new FileReader(input);
      try {
        final char[] cbuf = new char[1024*256];
        int count = fis.read(cbuf);
        while (count != -1) {
          out.write(cbuf, 0, count);
          out.flush();
          result += count;
          count = fis.read(cbuf);
        }
      } finally {
        fis.close();
      }
      
      return result;
    }
    
    @Override
    public boolean onHeader(RawTrafficDataHeader header) {
      h = header;
      tallyPartsToSet = bodyEncoder.computeTallyParts(h);
      return true;
    }

    @Override
    public RawTrafficDataHeader getHeader() {
      return h;
    }

    @Override
    public synchronized boolean addTally(VehicleTallyRec tally) throws Exception {
      bodyEncoder.println(bodyEncoder.toTally(h, tally, tallyPartsToSet));
      return true;
    }

    @Override
    public synchronized boolean addObservation(VehicleRec veh) throws Exception {
      bodyEncoder.println(bodyEncoder.toVehicle(h, veh));
      return true;
    }
    
    @Override
    public synchronized boolean addMonitoringEvent(MonitoringEventRec event) throws Exception {
      bodyEncoder.println(bodyEncoder.toMonitoringEvent(h, event));
      return true;
    }
    
  }
}
