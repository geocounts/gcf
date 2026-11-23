/*
 * Copyright 2014 Transmetric America Inc. All rights reserved.
 * Transmetric America Inc PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package geocounts.formats.io;

import geocounts.traffic.formats.AxleFieldsEnum;
import geocounts.traffic.formats.AxleRec;
import geocounts.traffic.formats.ChannelData;
import geocounts.traffic.formats.ChannelDataToStringEnum;
import geocounts.traffic.formats.ObservationField;
import geocounts.traffic.formats.RawFmtUtils;
import geocounts.traffic.formats.RawTrafficData;
import geocounts.traffic.formats.RawTrafficDataFilename;
import geocounts.traffic.formats.RawTrafficDataHeader;
import geocounts.traffic.formats.RawTrafficDataPlusFilename;
import geocounts.traffic.formats.TallyCountField;
import geocounts.traffic.formats.VehicleFieldsEnum;
import geocounts.traffic.formats.VehicleRec;
import geocounts.traffic.formats.VehicleTallyRec;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class RawTrafficDataFileCSVEncoder extends RawTrafficDataFileEncoder {
  protected final boolean talliesOrObservations;
  
  private char DELIM = ',';
  
  /**
   * Options for the RawTrafficDataFileCSVEncoder
   */
  public static class RawTrafficDataFileCSVEncoderOptions {
    public int MAX_AXLES = 0;
    
    public final List<Function<?, String>> fixedFields;

    public final List<ObservationField> VV = new ArrayList<ObservationField>();
    
    public Iterable<ObservationField> getVV(RawTrafficDataHeader header) {
      if (VV.size() > 0)
        return VV;
      return header.VV;
    }
    
    public final List<AxleFieldsEnum> VA = new ArrayList<AxleFieldsEnum>();
    
    public Iterable<AxleFieldsEnum> getVA(RawTrafficDataHeader header) {
      if (VA.size() > 0)
        return VA;
      return header.VA;
    }
    
    public void setVVDefaultWIM() {
      VV.clear();
      VA.clear();
      
      VV.add(VehicleFieldsEnum.vclass);
      VV.add(VehicleFieldsEnum.naxles);
      VV.add(VehicleFieldsEnum.speed);
      VV.add(VehicleFieldsEnum.length);
      VV.add(VehicleFieldsEnum.wbase);
      VV.add(VehicleFieldsEnum.mass);
      VV.add(VehicleFieldsEnum.vehicleid);
      VV.add(VehicleFieldsEnum.qc);
      VV.add(VehicleFieldsEnum.masslegal);
      VV.add(VehicleFieldsEnum.masstare);
      
      VA.add(AxleFieldsEnum.aspace);
      VA.add(AxleFieldsEnum.amass);
    }
    
    public RawTrafficDataFileCSVEncoderOptions() {
      fixedFields = new ArrayList<Function<?, String>>();
      fixedFields.add(HeaderFieldForDataEnum.Date);
      fixedFields.add(HeaderFieldForDataEnum.Time);
      
      addFixedChannelDataField(ChannelDataToStringEnum.Ch);
      addFixedChannelDataField(ChannelDataToStringEnum.DirFrom);
      addFixedChannelDataField(ChannelDataToStringEnum.LaneFrom);
      addFixedChannelDataField(ChannelDataToStringEnum.DirTo);
      addFixedChannelDataField(ChannelDataToStringEnum.LaneTo);
    }
    
    public void addFixedChannelDataField(Function<ChannelData, String> cdF) {
      fixedFields.add(cdF);
    }
  }
  
  public final RawTrafficDataFileCSVEncoderOptions options = new RawTrafficDataFileCSVEncoderOptions();
  
  /**
   * 
   * @param printWriter This will not close the printWriter when close is called
   * @param talliesOrObservations
   * @throws Exception
   */
  protected RawTrafficDataFileCSVEncoder(PrintWriter printWriter, boolean talliesOrObservations) throws Exception {
    super(printWriter, false);
    this.talliesOrObservations = talliesOrObservations;
  }
  
  public RawTrafficDataFileCSVEncoder(File file, boolean talliesOrObservations) throws Exception {
    super(file);
    this.talliesOrObservations = talliesOrObservations;
  }
  
  public static RawTrafficDataFileCSVEncoder createNoOutput() throws Exception {
    return new RawTrafficDataFileCSVEncoder((PrintWriter)null, false);
  }
  
  public static void writeObservations(RawTrafficDataFilename fileName, RawTrafficData data, Predicate<VehicleRec> filter, PrintStream out) throws Exception {
    RawTrafficDataFileCSVEncoder enc = new RawTrafficDataFileCSVEncoder(new PrintWriter(out), false);
    enc.observationFilter = filter;
    try {
      enc.writeHeader(fileName, data.header);
      enc.writeBody(fileName, data);
    } finally {
      enc.close();
    }
  }
  
  public static void write(RawTrafficDataFilename fileName, RawTrafficData data, boolean talliesOrObservations, PrintStream out) throws Exception {
    RawTrafficDataFileCSVEncoder enc = new RawTrafficDataFileCSVEncoder(new PrintWriter(out), talliesOrObservations);
    try {
      enc.writeHeader(fileName, data.header);
      enc.writeBody(fileName, data);
    } finally {
      enc.close();
    }
  }
  
  public void writeHeaderAndBody(RawTrafficDataPlusFilename data) throws Exception {
    writeHeader(data.fileName, data.header());
    writeBody(data.fileName, data.data);
  }

  @Override
  protected void writeHeader(RawTrafficDataFilename fileName, RawTrafficDataHeader header) throws Exception {
    List<? extends Object> fields = talliesOrObservations ? getTallyHeaderRow(header) : getObservationHeaderRow(header);
    super.println(toString(fields));
  }
  
  @Override
  protected void writeBody(RawTrafficDataFilename fileName, RawTrafficData data) throws Exception {
    if (talliesOrObservations) {
      for (VehicleTallyRec t: data.tallies()) {
        List<Object> fields = getTallyFieldList(data.header, t);
        super.println(toString(fields));
      }
      return;
    }
    
    if (observationFilter != null) {
      for (VehicleRec obs: data.observations()) {
        if (observationFilter.test(obs)) {
          List<Object> fields = getObservationFieldList(data.header, obs);
          super.println(toString(fields));
        }
      }
    } else {
      for (VehicleRec obs: data.observations()) {
        List<Object> fields = getObservationFieldList(data.header, obs);
        super.println(toString(fields));
      }
    }
  }
  
  protected String toString(List<? extends Object> fields) {
    StringBuilder result = new StringBuilder();

    for (int i=0; i<fields.size(); i++) {
      if (i > 0)
        result.append(DELIM);
      Object o = fields.get(i);
      if (o != null)
        result.append(o.toString());
    }
    return result.toString();
  }
  
  protected List<String> createFieldsForHeader() {
    List<String> result = new ArrayList<String>();
    for (Function<?, String> e: options.fixedFields)
      result.add(e.toString());
    return result;
  }
  
  public List<Object> createFieldsForData(long time, ChannelData cd) {
    List<Object> result = new ArrayList<Object>();
    for (Function<?, String> e: options.fixedFields) {
      if (e instanceof HeaderFieldForDataEnum) {
        HeaderFieldForDataEnum h = (HeaderFieldForDataEnum)e;
        result.add(h.apply(time));
      } else {
        Function<ChannelData, String> c = (Function<ChannelData, String>)e;
        result.add(c.apply(cd));
      }
    }
    return result;
  }
  
  static enum HeaderFieldForDataEnum implements Function<Long, String> {
    Date() {
      @Override
      public String apply(Long time) {
        return RawFmtUtils.ISO8601_DATEONLY_format(time);
      }
    },
    Time() {
      @Override
      public String apply(Long time) {
        return RawFmtUtils.ISO8601_TIMEONLY_format(time);
      }
    }
  }

  
  public List<String> getTallyHeaderRow(RawTrafficDataHeader header) {
    List<String> result = createFieldsForHeader();
    for (TallyCountField field: header.T.TF) {
      result.add(field.getId());
    }
    for (int i=0; i<=header.T.TC.getMaximumBin(); i++) {
      result.add("tc_"+i);
    }
    if (header.T.TS.isRecording()) {
      for (int i=0; i<=header.T.TS.getMaximumBin(); i++) {
        result.add("ts_"+i);
      }
    }
    if (header.T.TL.isRecording()) {
      for (int i=0; i<=header.T.TL.getMaximumBin(); i++) {
        result.add("tl_"+i);
      }
    }
    return result;
  }

  public List<Object> getTallyFieldList(RawTrafficDataHeader header, VehicleTallyRec tally) throws Exception {
    ChannelData cd = header.getChannel(tally.channelID);
    long time = tally.getTime();
    if (header.T.TD.periodsPerHour() > 1) {
      time += (tally.getHourlySubPeriod()-1)*header.T.TD.getMSecs();
    }
    List<Object> line = createFieldsForData(time, cd);

    for (TallyCountField field: header.T.TF) {
      line.add(field.get(tally, header.F.FU));
    }
    
    for (int i=0; i<=header.T.TC.getMaximumBin(); i++) {
      try {
        line.add(tally.countsVehType.getInt(i));
      } catch (Exception ex) {
        line.add(' ');
      }
    }
    
    if (header.T.TS.isRecording()) {
      for (int i=0; i<=header.T.TS.getMaximumBin(); i++) {
        try {
          line.add(tally.countsSpeed[i]);
        } catch (Exception ex) {
          line.add(' ');
        }
      }
    }
    
    if (header.T.TL.isRecording()) {
      for (int i=0; i<=header.T.TL.getMaximumBin(); i++) {
        try {
          line.add(tally.countsLength[i]);
        } catch (Exception ex) {
          line.add(' ');
        }
      }
    }
    return line;
  }

  public List<String> getObservationHeaderRow(RawTrafficDataHeader header) {
    List<String> result = createFieldsForHeader();
    for (ObservationField field: options.getVV(header)) {
      result.add(field.getId());
    }
    for (int a=1; a<=options.MAX_AXLES; a++) {
      for (AxleFieldsEnum field: options.getVA(header)) {
        result.add(field.getId() + "_" + a);
      }
    }
    return result;
  }

  public List<Object> getObservationFieldList(RawTrafficDataHeader header, VehicleRec veh) throws Exception {
    ChannelData cd = header.getChannel(veh.channelID);
    List<Object> line = createFieldsForData(veh.getTime(), cd);
    
    for (ObservationField field: options.getVV(header)) {
      line.add(field.get(veh, header.F.FU));
    }
    
    int axles = 0;
    for (AxleRec axle: veh.axles) {
      axles++;
      if (axles > options.MAX_AXLES)
        break;
      
      for (AxleFieldsEnum field: options.getVA(header)) {
        line.add(field.get(axle, header.F.FU));
      }
    }
    
    return line;
  }
  
  /*
   * Copied from com.transmetric.traffic.common.VehicleTextGraphics
   * @param veh
   * @param spacePerDivision Use 1.5
   * @param VEH_WHEEL
   * @param VEH_SPACE
   * @return Representation of the vehicle or null if there are no axles
   *
  public static String toVehicleAxlePic(VehicleRec veh, double spacePerDivision, char VEH_WHEEL, char VEH_SPACE) {
    return VehicleRecText.instance.formatVehicleAxleString(veh);
  }
  */
}
