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
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import geocounts.traffic.formats.RawTrafficData;

public abstract class RawTrafficDataFileDecoder {
  
  /**
   * 
   * @param rawFile
   * @param addBody If false, only load the header block
   * @param result {@link RawTrafficData}
   * @throws Exception
   */
  public final RawTrafficDataRecordSummary load(File rawFile, boolean addBody, RawTrafficData result) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(rawFile));
    try {
      try {
        return load(br, addBody, result);
      } catch (RawTrafficDataFileDecoderException de) {
        de.sourceFile = rawFile;
        throw de;
      }
    } finally {
      br.close();
    }
  }
  
  /**
   * 
   * @param in An InputStream
   * @param addBody Whether to add the body, or only load the header
   * @param result {@link RawTrafficData}
   * @return RawTrafficDataRecordSummary
   * @throws Exception
   */
  public final RawTrafficDataRecordSummary load(InputStream in, boolean addBody, RawTrafficData result) throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    return load(br, addBody, result);
  }
  
  public abstract RawTrafficDataRecordSummary load(BufferedReader br, boolean addBody, RawTrafficData result) throws Exception;
}
