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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import geocounts.traffic.formats.*;


/**
 * This object knows how to encode {@link RawTrafficData data} into the correct file format.
 * This creates a hidden {@link #pw PrintWriter}.
 * After you have successfully constructed this object you <b>must</b> call {@link #close()}
 * @see RawTrafficDataFileTXTEncoder
 */
public abstract class RawTrafficDataFileEncoder implements java.io.Closeable {
  protected final PrintWriter pw;
  private final boolean shouldCloseWriter;
  
  protected RawTrafficDataFileEncoder(PrintWriter printWriter, boolean shouldCloseWriter) throws Exception {
    this.pw = printWriter;
    this.shouldCloseWriter = shouldCloseWriter;
    try {
      this.onOpen();
    } catch (Exception ex) {
      pw.close();
      throw ex;
    }
  }
  
  /**
   * As soon you successfully construct this object you <b>must</b> call {@link #close()}
   * @param file
   * @throws Exception
   */
  public RawTrafficDataFileEncoder(File file) throws Exception {
    this(new PrintWriter(file), true);
  }
  
  /**
   * Prints a line to the file
   * @param data
   */
  protected void println(String data) {
    pw.print(data);
    pw.print(RawFmtUtils.LINE_FEED);
    pw.flush();
  }
  
  protected void print(String data) {
    pw.print(data);
    pw.flush();
  }
  
  protected abstract void writeHeader(RawTrafficDataFilename fileName, RawTrafficDataHeader header) throws Exception;
  
  protected abstract void writeBody(RawTrafficDataFilename fileName, RawTrafficData data) throws Exception;
  
  protected void onOpen() throws Exception {
  }

  protected void onClose() throws Exception {
  }
  
  @Override
  public final void close() throws IOException {
    Exception ex = null;
    try {
      onClose();
    } catch (Exception ex1) {
      ex = ex1;
    }
    pw.flush();
    if (shouldCloseWriter)
      pw.close();
    if (ex != null) {
      if (ex instanceof IOException)
        throw (IOException)ex;
      throw new IOException(ex);
    }
  }
  
}
