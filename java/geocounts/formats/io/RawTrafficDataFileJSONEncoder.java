/*
 * Copyright 2014 Transmetric America Inc. All rights reserved.
 * Transmetric America Inc PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package geocounts.formats.io;

import geocounts.traffic.formats.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Formats raw count data as JSON
 *
 */
public class RawTrafficDataFileJSONEncoder extends RawTrafficDataFileEncoder {
  public boolean addIndividualObservations;
  public boolean addIndividualObservationAxles;
  private SimpleDateFormat sdf_ISO8601 = RawFmtUtils.ISO8601();
  
  public RawTrafficDataFileJSONEncoder(java.io.PrintStream ps, boolean shouldCloseStream) throws Exception {
    this(new PrintWriter(ps), shouldCloseStream);
  }
  
  /**
   * 
   * @param pw
   * @param shouldCloseWriter Whether to close the underlying PrintWriter when calling {@link #close()}.
   * Set this to false if the pw is from a servlet
   * @throws Exception
   */
  public RawTrafficDataFileJSONEncoder(java.io.PrintWriter pw, boolean shouldCloseWriter) throws Exception {
    super(pw, shouldCloseWriter);
  }
  
  public RawTrafficDataFileJSONEncoder(File file) throws Exception {
    super(file);
  }
  
  public static void writeData(File file, RawTrafficDataFilename fileName, RawTrafficData data) throws Exception {
    RawTrafficDataFileJSONEncoder encoder = new RawTrafficDataFileJSONEncoder(file);
    
    encoder.addIndividualObservations = false;
    try {
      encoder.writeData(fileName, data);
    } finally {
      encoder.close();
    }
  }
  
  public static void writeData(PrintWriter pw, RawTrafficDataFilename fileName, RawTrafficData data, boolean addIndividualObservations) throws Exception {
    RawTrafficDataFileJSONEncoder encoder = new RawTrafficDataFileJSONEncoder(pw, false);
    encoder.addIndividualObservations = addIndividualObservations;
    try {
      encoder.writeData(fileName, data);
      pw.flush();
    } finally {
      encoder.close();
    }
  }
  
  public static void writeHeader(RawTrafficDataFilename fileName, RawTrafficDataHeader header, StringBuilder result) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
    PrintStream ps = new PrintStream(baos);
    RawTrafficDataFileJSONEncoder encoder = new RawTrafficDataFileJSONEncoder(ps, true);
    encoder.addIndividualObservations = false;
    try {
      encoder.writeHeader(fileName, header, false);
      baos.flush();
      result.append(baos.toString());
    } finally {
      encoder.close();
    }
  }
  
  public void writeData(RawTrafficDataFilename fileName, RawTrafficData data) throws Exception {
    // write the header
    writeHeader(fileName, data.header);
    super.println(",");
    // write the body
    writeBody(fileName, data);
  }
  
  @Override
  protected void onOpen() throws Exception {
    this.println("{");
  }

  @Override
  protected void onClose() throws Exception {
    this.println("}"); // end of whole file object
  }
  
  @Override
  protected void writeHeader(RawTrafficDataFilename fileName, RawTrafficDataHeader header) throws Exception {
    writeHeader(fileName, header, true);
  }
  
  private void writeHeader(RawTrafficDataFilename fileName, RawTrafficDataHeader header, boolean addHeaderVariableWrapper) throws Exception {
    if (addHeaderVariableWrapper)
      super.println("\"header\": {");

    String uuid = header.GK.toString();
    boolean hasKey = uuid != null;
    if (hasKey) 
      printKey(false, "GK", uuid);

    // File header types
    printF(hasKey, header.F);
    
    // Station information
    printS(true, header.S);

    // Tally header types
    printKey(true, "TD", header.T.TD.name());
    if (header.T.TF.isRecording())
      printTF(true, header.T.TF);
    if (header.T.TC.isRecording())
      printTC(true, header.T.TC);
    if (header.T.TL.isRecording())
      printTL(true, header.T.TL);
    if (header.T.TS.isRecording())
      printTS(true, header.T.TS);

    // Vehicle header types
    if (header.VV.isRecording()) {
      super.println(",");
      super.print("  \"VV\": [" + arrayToString(header.VV) + "]");
    }
    if (header.VA.isRecording()) {
      super.println(",");
      super.print("  \"VA\": [" + arrayToString(header.VA.fields) + "]");
    }
    super.print("}"); // end of header
  }
  
  private void printF(boolean printComma, RawTrafficDataHeaderFileDef F) {
    printFV(printComma, F.FV);
    printKey(true, "FE", F.FE);
    printFU(true, F.FU);
    super.println(",");
    super.println("  \"FX\": [");
    int i=0;
    for (FileChangeRec FX: F.FX) {
      if (i > 0)
        super.println(",");
      i++;
      super.println("{");
      this.printKey(false, "date", sdf_ISO8601.format(FX.dateTime));
      this.printKey(true, "actionid", FX.actionID);
      this.printKey(true, "userid", FX.userID);
      this.printKey(true, "parameter", FX.parameter);
      super.println("}");
    }
    super.println("]");
  }
  
  private void printS(boolean printComma, RawTrafficDataStation S) {
    printKey(printComma, "SZ", S.SZ.toString());
    
    if (S.SG.isSet())
      printSG(true, S.SG);
    if (S.SP.isSet())
      printSP(true, S.SP);
    if (S.SI.isSet())
      printKey(true, "SI", S.SI.getValue());
    
    super.println(",");
    super.print("  \"SR\": [\"" + sdf_ISO8601.format(S.SR.getStartTime()) + "\", \"" + sdf_ISO8601.format(S.SR.getEndTime()) + "\"]");
    if (S.ST != null)
      printKey(true, "ST", S.ST);
    if (S.SS != null)
      printKey(true, "SS", S.SS);
    if (S.SN.hasValues()) {
      super.println(", \"SN\": [" + arrayToString(S.SN) + "]");
    }
    super.println(",");
    super.println("\"SC\": [");
    int i = 0;
    for (ChannelData sc: S.getAllChannels()) {
      printChannel(i > 0, sc);
      i++;
    }
    super.println("    ]");
    // print labels
    printSL(true, S.SL);
    // print equipment
    printSE(true, S.SE);
  }
  
  /**
   * Prints the {@link RawTrafficDataStation station}
   * @throws Exception
   */
  public static void printS(boolean printComma, RawTrafficDataStation S, java.io.Writer out) throws Exception {
    PrintWriter pw = out instanceof PrintWriter ? (PrintWriter)out : new PrintWriter(out);
    RawTrafficDataFileJSONEncoder e = new RawTrafficDataFileJSONEncoder(pw, false);
    e.printS(printComma, S);
    e.close();
  }
  
  /**
   * Prints the {@link RawTrafficDataStation station}
   */
  public static void printS(boolean printComma, RawTrafficDataStation S, PrintStream ps) throws Exception {
    RawTrafficDataFileJSONEncoder e = new RawTrafficDataFileJSONEncoder(ps, false);
    e.printS(printComma, S);
    e.close();
  }

  private void printKey(boolean addCommaBefore, String id, Object value) {
    if (addCommaBefore)
      super.println(",");
    super.print("  \"" + id + "\": " + toJSONElementValue.apply(value));
  }
  
  private void printChannel(boolean addCommaBefore, ChannelData sc) {
    if (addCommaBefore)
      super.println(",");
    super.println("{");
    super.println("  \"channel_id\": " + sc.getChannelID() + ",");
    if (sc.getGeometry().isSet()) {
      LatLongCoordinate llh = sc.getGeometry().getCentroid();
      super.println("  \"coordinate\": [" + llh.getLatitude() + ", " + llh.getLongitude() + ", " + llh.altitude + "],");
    }
    super.println("  \"from\": {");
    printKey(false, "dir", sc.from.getDir().getCode());
    printKey(true, "lane", sc.from.lane.getCode());
    printKey(true, "usage", sc.from.laneType.getCode());
    super.println("");
    super.println("  },");
    super.println("  \"to\": {");
    printKey(false, "dir", sc.to1.getDir().getCode());
    super.println(", ");
    super.print("  \"dirs\": [" + sc.to1.getDir().getCode() + ", " + sc.to2.getDir().getCode() + ", " + sc.to2.getDir().getCode() + "]");
    printKey(true, "lane", sc.to1.lane.getCode());
    printKey(true, "usage", sc.to1.laneType.getCode());
    super.println("");
    super.println("  }");
    super.print("}");
  }
  
  public static Map<String, Object> asChannelMap(ChannelData sc) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("channel_id", sc.getChannelID());
    if (sc.getGeometry().isSet()) {
      result.put("coordinate", sc.getGeometry().toLatLngHeight());
    }
    result.put("from", asDirMap(sc.from));
    result.put("to", asDirMap(sc.to1));
    return result;
  }
  
  private static Map<String, Object> asDirMap(ChannelData.ToFromInfo dir) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("dir", dir.getDir().getCode());
    result.put("lane", dir.lane.getCode());
    result.put("usage", dir.laneType.getCode());
    return result;
  }
  
  private void printSG(boolean addComma, StationGPS SG) {
    if (addComma)
      super.println(",");
    super.print("  \"SG\": [" + SG.point.getLatitude() + ", " + SG.point.getLongitude() + ", " + SG.point.altitude + "]");
  }

  private void printSP(boolean addComma, StationShape SP) {
    if (addComma)
      super.println(",");
    super.print("  \"SP\": [");
    for (int i=0; i<SP.size(); i++) {
      if (i>0)
        super.print(", ");
      super.print("[" + SP.getPoint(i).getLatitude() + ", " + SP.getPoint(i).getLongitude() + "]");
    }
    super.print("]");
  }
  
  private void printSL(boolean addComma, StationLabels SL) {
    if (addComma)
      super.println(",");
    super.println(" \"SL\": {");
    super.println("  \"station\": {");
//    this.printOtherSystemLabels(false, SL.getStationOtherSystemIDs());
    this.printLabels(false, SL.getLabelsForStation());
    super.println("");
    super.println("},");

    // directions
    super.println("  \"directions\": [");
    int dirIDIndex = 0;
    for (Integer dirID: SL.getDirectionsWithLabels()) {
      if (dirIDIndex > 0)
        super.println(",");
      super.println("{");
      printKey(false, "dir", dirID);
      this.printLabels(true, SL.getLabelsForDirection(dirID));
      super.println("");
      super.print("}");
      dirIDIndex++;
    }
    super.println("], ");

    // channels
    super.print("  \"channels\": [");
    int chIDIndex = 0;
    for (Integer chID: SL.getChannelsWithLabels()) {
      if (chIDIndex > 0)
        super.println(",");
      super.println("{");
      printKey(false, "channel_id", chID);
      this.printLabels(true, SL.getLabelsForChannel(chID));
      super.print("}");
      chIDIndex++;
    }
    super.println("]");

    super.println("  }");
  }

  private void printSE(boolean addComma, Equipment SE) {
    if (addComma)
      super.println(",");
    super.println(" \"SE\": {");
    super.println("  \"station\": {");
    super.println("  },");
    
    // channels
    super.println("  \"channels\": [");
    super.println("  ]");

    super.println("  }");
  }
  
  private void printTF(boolean addComma, TallyCountDef TF) {
    if (addComma)
      super.println(",");
    super.print("  \"TF\": [" + arrayToString(TF) + "]");
  }
  
  private void printTC(boolean addComma, ClassifiedVehTypeDef tc) {
    if (addComma)
      super.println(",");
    super.print("  \"TC\": {");
    printKey(false, "name", tc.getName());
    printKey(true, "minclassid", tc.getMinimumBin());
    printKey(true, "maxclassid", tc.getMaximumBin());
    super.print("}");
  }
  
  private void printTL(boolean addComma, ClassifiedLengthDef tl) {
    if (addComma)
      super.println(",");
    super.print("  \"TL\": {");
    printKey(false, "name", tl.getName());
    super.print("}");
  }
  
  private void printTS(boolean addComma, ClassifiedSpeedDef ts) {
    if (addComma)
      super.println(",");
    super.print("  \"TS\": {");
    printKey(false, "name", ts.getName());
    super.print("}");
  }
  
  /*
  private void printTL(boolean addComma, ClassifiedLengthDef tl) {
    if (addComma)
      super.println(",");
    super.println("  \"TL\": {");
    printKey(false, "name", tl.getName());
    printKey(true, "minclassid", tl.getMinimumBin());
    printKey(true, "maxclassid", tl.getMaximumBin());
    super.println("  }");
  }
  */
  
  private void printFV(boolean addComma, SystemVersion fv) {
    if (addComma)
      super.println(",");
    super.println("  \"FV\": {");
    printKey(false, "version", fv.formatVersion);
    printKey(true, "producer", fv.producerVersion);
    String sourceFilename = fv.sourceFilenames.size() > 0 ? fv.sourceFilenames.get(0) : null;
    printKey(true, "sourcefile", sourceFilename);
    super.println(",");
    super.println("  \"sourcefiles\": [" + arrayToString(fv.sourceFilenames) + "]");
    super.print("  }");
  }
  
  private void printFU(boolean addComma, UnitsUsed FU) {
    if (addComma)
      super.println(",");
    super.println("  \"FU\": {");
    printKey(false, "speed", FU.getSpeed());
    printKey(true, "mass", FU.mass);
    printKey(true, "lengthsh", FU.getLengthShort());
    printKey(true, "lengthln", FU.getLengthLong());
    printKey(true, "mac", FU.mac);
    super.print("  }");
  }

  /**
   * Always prints a comma before the labels start
   * @param recs
   */
  private void printLabels(boolean addComma, Iterable<StationLabels.StationLabelRec> recs) {
    ArrayList<StationLabels.StationLabelRec> normalLabels = new ArrayList<StationLabels.StationLabelRec>();
    ArrayList<StationLabels.OtherSystemIDLabelTextValue> otherSystemLabels = new ArrayList<StationLabels.OtherSystemIDLabelTextValue>();

    for (StationLabels.StationLabelRec rec: recs) {
      StationLabels.LabelTextValue text = rec.getText();
      if (!(text instanceof StationLabels.OtherSystemIDLabelTextValue)) {
        normalLabels.add(rec);
      } else
        otherSystemLabels.add((StationLabels.OtherSystemIDLabelTextValue)text);
    }
    
    // print normal labels
    for (StationLabels.StationLabelRec rec: normalLabels) {
      StationLabels.LabelTextValue text = rec.getText();
      if (text.numberOfFields() > 1) {
        if (addComma)
          super.println(",");
        
        String[] textValue = text.asArray();
        super.print("  \"" + rec.field.name() + "\": [");
        for (int a=0; a<textValue.length; a++) {
          if (a > 0)
            super.println(",");
          super.print(toJSONElementValue.apply(textValue[a]));
        }
        super.print("]");
      } else {
        printKey(addComma, rec.field.name(), text.toString());
      }
      addComma = true;
    }
    
    // print other system
    printOtherSystemLabels(addComma, otherSystemLabels);
  }
  
  private void printOtherSystemLabels(boolean addComma, Iterable<StationLabels.OtherSystemIDLabelTextValue> SLs) {
    if (addComma)
      super.println(",");
    super.println("  \"othersystemids\": {");
    int osid=0;
    for (StationLabels.OtherSystemIDLabelTextValue o: SLs) {
      printKey(osid > 0, o.getSystemName(), o.getSystemValueID());
      osid++;
    }
    super.println("");
    super.println("  }");
  }

  @Override
  protected void writeBody(RawTrafficDataFilename fileName, RawTrafficData data) throws Exception {
    super.println("\"data\": {");  // start of data
    // print tallies
    printTallies(data);
    // print individual observations (vehicles)
    if (addIndividualObservations) {
      super.println(",");
      printIndividualObservations(data);
    }
    // print monitoring events
    super.println(",");
    printMonitoringEvents(data.monitoringevents());
    
    this.println("}"); // end of body object
  }
  
  /**
   * Print individual observations ({@link VehicleRec vehicles})
   * @param data
   * @throws Exception
   */
  protected void printIndividualObservations(RawTrafficData data) throws Exception {
    super.println("  \"V\": [");  
    int i=0;
    for (VehicleRec v: data.observations()) {
      boolean shouldPrint = true;
      if (observationFilter != null)
        shouldPrint = observationFilter.test(v);
      if (shouldPrint) {
        if (i > 0)
          super.println(",");
        printObservation(data.header, v);
        i++;
      }
    }
    this.println("  ]");  // end of individual observations
  }
  
  protected void printTallies(RawTrafficData data) throws Exception {
    super.println("  \"T\": [");

    int i=0;
    for (TalliesInPeriod tfh: data.toTalliesByPeriod()) {
      if (i > 0)
        super.println(",");
      printTalliesPerHour(data.header, tfh);
      i++;
    }

    this.println("  ]");  // end of tallies
  }
  
  private void printTalliesPerHour(RawTrafficDataHeader header, TalliesInPeriod tally) throws Exception {
    super.println("    {");
    super.println("      \"date\": \"" + RawFmtUtils.ISO8601_DATEONLY_format(tally.getTime()) + "\",");
    super.println("      \"hour\": " + tally.getHour() + ",");
    super.println("      \"periodid\": " + tally.getPeriodID() + ",");
    super.println("      \"channel\": [");
    int i=0;
    for (VehicleTallyRec t: tally) {
      if (i > 0)
        super.println(",");
      printChannel(header, t);
      i++;
    }
    super.println("]");
    super.print("    }");
  }
  
  private void printChannel(RawTrafficDataHeader header, VehicleTallyRec rec) throws Exception {
    super.print("      {");
    super.println("\"id\": " + rec.channelID + ",");
    if (header.T.TC.isRecording())
      super.println("        " + toPart(TallyPartOrderEnum.TC, header, rec) + ",");
    if (header.T.TL.isRecording())
      super.println("        " + toPart(TallyPartOrderEnum.TL, header, rec) + ",");
    if (header.T.TS.isRecording())
      super.println("        " + toPart(TallyPartOrderEnum.TS, header, rec) + ",");
    super.println("        \"totals\": [" + arrayToString(TallyPartOrderEnum.TF.toArray(rec, header)) + "]");
    for (TallyCountField field: header.T.TF) {
      super.print(",");
      Number n = field.get(rec, header.F.FU);
      super.print("\"" + field.getId() + "\": " + n);
    }
    super.print("      }");
  }
  
  protected String toPart(TallyPartOrder tpo, RawTrafficDataHeader header, VehicleTallyRec rec) throws Exception {
    StringBuilder result = new StringBuilder("\"");
    result.append(tpo.getID().toLowerCase());
    result.append("\": [");
    Number[] tcArray = tpo.toArray(rec, header);
    result.append(arrayToString(tcArray));
    result.append("]");
    return result.toString();
  }
  
  /**
   * 
   * @param header Print this header
   * @param observations Print these observations
   * @param out Print to this PrintWriter
   * @throws Exception
   */
  public static void printObservations(RawTrafficDataHeader header, Iterable<VehicleRec> observations, PrintWriter out) throws Exception {
    class VehJSON extends RawTrafficDataFileJSONEncoder {
      private RawTrafficDataHeader h;
      public VehJSON(PrintWriter file, RawTrafficDataHeader h) throws Exception {
        super(file, false);
        this.h = h;
      }
      
      protected void printIndividualObservations(Iterable<VehicleRec> observations) {
        int count = 0;
        for (VehicleRec obs: observations) {
          if (count > 0)
            this.println(",");
          count++;
          this.printObservation(h, obs);
        }
      }
      
      @Override
      protected void onOpen() {
        this.print("[");
      }
      
      @Override
      protected void onClose() {
        this.println("]");
      }
    }
    
    VehJSON enc = new VehJSON(out, header);
    enc.printIndividualObservations(observations);
    enc.close();
  }
  
  protected void printObservation(RawTrafficDataHeader header, VehicleRec obs) {
    super.print("{");
    super.print(" \"date\": \"" + RawFmtUtils.ISO8601_MSECS_format(obs.getTime()) + "\",");
    super.print(" \"channelid\": " + obs.channelID);
    for (ObservationField v: header.VV) {
      try {
        super.print(", \"" + v.getId() + "\": " + toJSONElementValue(v.getFieldType().isNumeric(), v.get(obs, header.F.FU)));
      } catch (Exception ex) {}
    }
    if (addIndividualObservationAxles) {
      int i=0;
      super.print(", \"A\": [");
      for (AxleRec axle: obs.axles) {
        if (i > 0)
          super.print(", ");
        printAxle(header, axle);
        i++;
      }
      super.print("]");
    }
    super.print("}");
  }
  
  private void printAxle(RawTrafficDataHeader header, AxleRec axle) {
    super.print("{");
    int i=0;
    for (AxleFieldsEnum a: header.VA) {
      if (i > 0)
        super.print(", ");
      try {
        super.print("\"" + a.name() + "\": " + toJSONElementValue(true, a.get(axle, header.F.FU)));
      } catch (Exception ex) {}
      i++;
    }
    super.print("}");
  }

  public void printMonitoringEvents(Iterable<MonitoringEventRec> events) {
    super.print("\"M\": [");
    int i=0;
    for (MonitoringEventRec e: events) {
      if (i > 0)
        super.println(", ");
      printEvent(e);
      i++;
    }
    super.println("]");
  }
  
  public void printEvent(MonitoringEventRec event) {
    super.print("{");
    super.print(" \"date\": \"" + RawFmtUtils.ISO8601_MSECS_format(event.getTime()) + "\",");
    super.print(" \"channelid\": " + event.channelID + ",");
    super.print(" \"type\": " + toJSONElementValue.apply(event.getEventTypeID()) + ",");
    super.print(" \"text\": " + toJSONElementValue.apply(event.text));
    super.print("}");
  }
  
  private String arrayToString(Number[] data) {
    java.util.ArrayList<Number> d = new java.util.ArrayList<Number>();
    for (Number value: data)
      d.add(value);
    return arrayToString(d);
  }
  
  private String arrayToString(java.lang.Iterable<? extends Object> data) {
    StringBuilder result = new StringBuilder();
    int i=0;
    for (Object f: data) {
      if (i > 0)
        result.append(", ");
      result.append(toJSONElementValue.apply(f));
      i++;
    }
    return result.toString();
  }
  
  private Function<Object, String> toJSONElementValue = new ToJSONValue();
  /*
  private String toJSONElementValue(Object f) {
    if (f == null)
      return "null";
    return toJSONElementValue(f instanceof Number, f.toString());
  }
  */
  
  private String toJSONElementValue(boolean isNumber, String value) {
    if (value == null)
      return "null";
    if (isNumber)
      return value;
    return toJSONElementValue.apply(value);
  }

}
