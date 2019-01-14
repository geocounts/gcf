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
  public RawTrafficDataPlusFilename(RawTrafficData r, String fileName) {
    this.data = r;
    this.fileName = (fileName != null ? new RawTrafficDataFilename(fileName) : null);
  }
  
  public RawTrafficDataPlusFilename(RawTrafficData r, File rawFile) {
    this(r, rawFile != null ? rawFile.getName() : null);
  }
  
  public RawTrafficDataPlusFilename(RawTrafficData r) {
    this(r, (String)null);
  }
  
  public RawTrafficDataPlusFilename(File rawFile) {
    this(new RawTrafficData(), rawFile);
  }
  
  public RawTrafficDataPlusFilename() {
    this(new RawTrafficData(), (String)null);
  }
  
  public final RawTrafficData data;
  public RawTrafficDataFilename fileName;
  
  public String getStationID() {
    return fileName != null ? fileName.getStationID() : null;
  }
}
