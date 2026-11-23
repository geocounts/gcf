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

import java.io.File;

public class RawTrafficDataPlusFilename {
  public RawTrafficDataPlusFilename(RawTrafficData r, RawTrafficDataFilename fileName) {
    this.data = r != null ? r : new RawTrafficData();
    this.fileName = fileName != null ? fileName : new RawTrafficDataFilename();
  }
  
  public RawTrafficDataPlusFilename(RawTrafficData r, String fileName) {
    this(r, fileName != null ? new RawTrafficDataFilename(fileName) : null);
  }
  
  public RawTrafficDataPlusFilename(RawTrafficData r, File rawFile) {
    this(r, rawFile != null ? new RawTrafficDataFilename(rawFile.getName()) : (RawTrafficDataFilename)null);
    if (rawFile != null)
      fileSize = rawFile.length();
  }
  
  public RawTrafficDataPlusFilename(RawTrafficData r) {
    this(r, (RawTrafficDataFilename)null);
  }
  
  public RawTrafficDataPlusFilename(File rawFile) {
    this(null, rawFile);
  }
  
  public RawTrafficDataPlusFilename() {
    this(new RawTrafficData(), (RawTrafficDataFilename)null);
  }

  /**
   * Never null
   */
  public final RawTrafficData data;
  
  /**
   * Never null
   */
  public final RawTrafficDataFilename fileName;
  
  public long fileSize;
  
  public RawTrafficDataHeader header() {
    return data.header;
  }
  
  /**
   * 
   * @return The station ID, or null if not set
   */
  public String getStationID() {
    return fileName.getStationID() != null ? fileName.getStationID() : data.header.S.SI.getValue();
  }
}
