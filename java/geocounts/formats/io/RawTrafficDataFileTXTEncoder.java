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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

import geocounts.traffic.formats.*;
import geocounts.traffic.formats.Equipment.EquipmentRecBase;

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

  @Override
  protected void writeBody(RawTrafficDataFilename fileName, RawTrafficData data) throws Exception {
    // get the tally parts being set. This is deduced from the header
    ArrayList<TallyPartOrder> tallyPartsToSet = new ArrayList<TallyPartOrder>();
    tallyPartsToSet.addAll(data.header.T.TO.getAll());
    if (tallyPartsToSet.size() == 0) {
      TallyPartOrderDef.setDefaultPartOrder(data.header.T, tallyPartsToSet);
    }
    
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
    for (EquipmentRecBase o: header.S.SE.allForStation()) {
      printHeaderLine(EnumHeaderRecords.SE, EquipmentHeaderRec.write(o), false);
    }
    
    // equipment (channel)
    for (EquipmentRecBase o: header.S.SE.allForChannels()) {
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
    result.append(';');
    result.append(header.getObservationDateFormat().format(veh.getTime()));
    result.append(',');
    result.append(veh.channelID);
    result.append(';');
    // vehicle subfields
    int i=0;
    for (ObservationField f: header.VV) {
      if (i > 0)
        result.append(',');
      result.append(f.get(veh, header.F.FU));
      i++;
    }
    result.append(';');
    // axles
    if (header.VA.fields.size() > 0) {
      for (VehicleRec.AxleRec axl: veh.axles) {
        i=0;
        for (; i<header.VA.numberOfFields(); i++) {
          if (i > 0)
            result.append(',');
          result.append(header.VA.fields.get(i).get(axl, header.F.FU));
        }
        result.append(';');
      }
    }
    return result.toString();
  }
  
  protected String toTally(RawTrafficDataHeader header, VehicleTallyRec tally, Collection<TallyPartOrder> tallyPartsToSet) throws Exception {
    StringBuilder result = new StringBuilder();
    result.append(EnumRecordType.T.getId());
    result.append(';');
    // fixed referencing data subfields
    result.append(header.getTallyDateFormat().format(tally.getTime()));
    result.append(',');
    result.append(tally.period);
    result.append(',');
    result.append(tally.channelID);
    result.append(';');

    for (TallyPartOrder p : tallyPartsToSet) {
      p.encode(tally, header, result);
      result.append(';');
    }
    
    return result.toString();
  }

  private String toMonitoringEvent(RawTrafficDataHeader header, MonitoringEventRec event) {
    StringBuilder result = new StringBuilder();
    result.append(EnumRecordType.M.getId());
    result.append(';');
    result.append(header.getObservationDateFormat().format(event.getTime()));
    result.append(';');
    result.append(event.getEventTypeID());
    result.append(';');
    result.append(RawFmtUtils.notNull(event.text));
    result.append(';');
    return result.toString();
  }

}
