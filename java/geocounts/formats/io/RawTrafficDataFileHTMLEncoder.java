/*
 * Copyright 2014 Transmetric America Inc. All rights reserved.
 * Transmetric America Inc PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package geocounts.formats.io;

import geocounts.traffic.formats.RawTrafficData;
import geocounts.traffic.formats.RawTrafficDataFilename;
import geocounts.traffic.formats.RawTrafficDataHeader;
import geocounts.traffic.formats.VehicleRec;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Predicate;

public class RawTrafficDataFileHTMLEncoder extends RawTrafficDataFileCSVEncoder {
  
  private RawTrafficDataFileHTMLEncoder(PrintWriter printWriter, boolean talliesOrObservations) throws Exception {
    super(printWriter, talliesOrObservations);
  }
  
  public RawTrafficDataFileHTMLEncoder(File file, boolean talliesOrObservations) throws Exception {
    super(file, talliesOrObservations);
  }
  
  public static RawTrafficDataFileHTMLEncoder createNoOutput() throws Exception {
    return new RawTrafficDataFileHTMLEncoder((PrintWriter)null, false);
  }
  
  public static void writeObservations(RawTrafficDataFilename fileName, RawTrafficData data, Predicate<VehicleRec> filter, PrintStream out) throws Exception {
    RawTrafficDataFileHTMLEncoder enc = new RawTrafficDataFileHTMLEncoder(new PrintWriter(out), false);
    enc.observationFilter = filter;
    try {
      enc.writeHeader(fileName, data.header);
      enc.writeBody(fileName, data);
    } finally {
      enc.close();
    }
  }
  
  public static void write(RawTrafficDataFilename fileName, RawTrafficData data, boolean talliesOrObservations, PrintStream out) throws Exception {
    write(fileName, data, talliesOrObservations, new PrintWriter(out));
  }
  
  public static void write(RawTrafficDataFilename fileName, RawTrafficData data, boolean talliesOrObservations, PrintWriter out) throws Exception {
    RawTrafficDataFileHTMLEncoder enc = new RawTrafficDataFileHTMLEncoder(out, talliesOrObservations);
    try {
      enc.writeHeader(fileName, data.header);
      enc.writeBody(fileName, data);
    } finally {
      enc.close();
    }
  }
  
  @Override
  protected void writeHeader(RawTrafficDataFilename fileName, RawTrafficDataHeader header) throws Exception {
    super.println("<table><thead>");
    super.writeHeader(fileName, header);
    super.println("</thead>");
  }
  
  @Override
  protected void writeBody(RawTrafficDataFilename fileName, RawTrafficData data) throws Exception {
    super.println("<tbody>");
    super.writeBody(fileName, data);
    super.println("</tbody>");
  }

  @Override
  protected void onClose() throws Exception {
    super.println("</table>");
  }
  
  @Override
  protected String toString(List<? extends Object> fields) {
    StringBuilder result = new StringBuilder("<tr>");
    for (Object f: fields) {
      result.append("<td>");
      if (f != null)
        result.append(f.toString());
      result.append("</td>");
    }
    result.append("</tr>");
    return result.toString();
  }

}
