package geocounts.formats.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.function.Consumer;

import geocounts.formats.io.RawTrafficDataFileTXTDecoder.ReceiveTime;
import geocounts.formats.io.RawTrafficDataFileTXTDecoder.UTCToLocalTimeHelper;
import geocounts.traffic.formats.DecodingOptions;
import geocounts.traffic.formats.EnumRecordType;
import geocounts.traffic.formats.RawTrafficDataHeader;
import geocounts.traffic.formats.TimedObject;

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
public class RawTrafficDataFileTXTAsyncDecoder {
  /**
   * The decoding options
   * @see DecodingOptions
   */
  public final DecodingOptions options = new DecodingOptions();
  
  public Consumer<CurrentRawFilePosition> onUpdate;
  
  private final RawTrafficDataFileDecoderCallback callback;
  public RawTrafficDataFileTXTAsyncDecoder(RawTrafficDataFileDecoderCallback callback) {
    this.callback = callback;
  }

  /**
   * Loads multiple data files without storing in memory
   */
  public void load(Iterable<File> rawFiles) throws Exception {
    for (File f: rawFiles)
      load(f, false);
    callback.close();
  }

  /**
   * Loads a data file without storing in memory
   * @return {@link RawTrafficDataHeader}
   */
  public RawTrafficDataHeader load(File rawFile) throws Exception {
    return load(rawFile, true);
  }
  
  /**
   * @return {@link RawTrafficDataHeader}
   * @throws Exception
   */
  private RawTrafficDataHeader load(File rawFile, boolean callClose) throws Exception {
    RawTrafficDataFileTXTDecoder helper = new RawTrafficDataFileTXTDecoder(options);
    BufferedReader br = new BufferedReader(new FileReader(rawFile));
    CurrentRawFilePosition currentFilePosition = new CurrentRawFilePosition(options, rawFile);
    currentFilePosition.startTime = System.currentTimeMillis();
    
    RawTrafficDataHeader header = new RawTrafficDataHeader();
    try {
      try {
        String line = br.readLine();
        while (line != null) {
          currentFilePosition.addLine(line);
          long NOW = System.currentTimeMillis();
          if (NOW-currentFilePosition.lastSentUpdate > 1000) {
            if (onUpdate != null) {
              onUpdate.accept(currentFilePosition);
              currentFilePosition.lastSentUpdate = NOW;
            }
          }
          EnumRecordType type = RawTrafficDataFileTXTDecoder.computeRecordType(line);
          currentFilePosition.allRecords++;
          if (type == EnumRecordType.H) {
            Exception hadError = RawTrafficDataFileTXTDecoder.decodeHeaderLine(line, header, currentFilePosition.updateTZ);
            if (hadError != null)
              throw hadError;
            if (currentFilePosition.bodyRecords > 0) {
              RawTrafficDataFileTXTDecoder.throwHeaderFoundAfterBody(rawFile, currentFilePosition.allRecords);
            }
          } else if (type.isBody) {
            boolean shouldContinue = true;
            if (!currentFilePosition.headerWasSent) {
              shouldContinue = callback.onHeader(header);
              currentFilePosition.headerWasSent = true;
              if (!shouldContinue)
                break;
            }
            currentFilePosition.bodyRecords++;
            shouldContinue = helper.addBodyLine(type, line, header, callback, currentFilePosition.updateTZ, currentFilePosition.receiveTime);
            if (!shouldContinue)
              break;
          }
          line = br.readLine();
        }
      } catch (Throwable ex) {
        if (ex instanceof RawTrafficDataFileDecoderException)
          throw ex;
        throw new RawTrafficDataFileDecoderException(currentFilePosition.allRecords, ex);
      }
      if (!currentFilePosition.headerWasSent)
        callback.onHeader(header);
      if (callClose)
        callback.close();
    } finally {
      br.close();
    }
    
    return header;
  }
  
  /**
   * 
   * @see RawTrafficDataRecordSummary
   *
   */
  public static class CurrentRawFilePosition implements TimedObject {
    protected long startTime;
    public int allRecords = 0;
    public int bodyRecords = 0;
    public long position = 0;
    private boolean headerWasSent = false;
    private long fileLength;
    private String fileName;
    
    private CurrentRawFilePosition(DecodingOptions options, File f) {
      updateTZ = new UTCToLocalTimeHelper(options);
      this.fileName = f.getName();
      fileLength = f.length();
      if (fileLength == 0)
        fileLength++;
    }
    
    private final UTCToLocalTimeHelper updateTZ;
    
    private final ReceiveTime receiveTime = new ReceiveTime();
    
    public TimedObject getLastBodyTime() {
      return receiveTime;
    }
    
    protected long lastSentUpdate;
    
    public double getProgressInFile() {
      return (double)position/fileLength;
    }
    
    public String getFilename() {
      return fileName;
    }
    
    protected void addLine(String line) {
      this.position += (line.length() + 2);   // add line feed and carriage return: NOT GOING TO WORK FOR UNIX
    }

    @Override
    public long getTime() {
      return startTime;
    }
  }

}
