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
import java.io.Reader;

import geocounts.traffic.formats.DecodingOptions;
import geocounts.traffic.formats.RawTrafficData;

public abstract class RawTrafficDataFileDecoder {
  
  /**
   * The decoding options
   * @see DecodingOptions
   */
  public final DecodingOptions options;
  
  public RawTrafficDataFileDecoder(DecodingOptions o) {
    options = o;
  }
  
  /**
   * 
   * @param rawFile
   * @param result {@link RawTrafficData}
   * @throws Exception
   */
  public final RawTrafficDataRecordSummary load(File rawFile, RawTrafficData result) throws Exception {
    BufferedReader br = createReader(new FileReader(rawFile));
    try {
      try {
        return load(br, result);
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
  public final RawTrafficDataRecordSummary load(InputStream in, RawTrafficData result) throws Exception {
    BufferedReader br = createReader(new InputStreamReader(in));
    return load(br, result);
  }
  
  /**
   * BufferedReader does not know how to load an file with UTF-8 With BOM
   * These files have bytes 0xEF, 0xBB and 0xBF as the first 3 bytes of the file.
   * Notepad, Wordpad, Notepad++ and other such text editors ignore these bytes.
   * Even vi and emacs will not display them.
   * 
   * @param r
   * @return
   * @throws Exception
   */
  private final BufferedReader createReader(Reader r) throws Exception {
    BufferedReader br = new BufferedReader(r);
    br.mark(3); // Mark the beginning of the stream to allow reset
    int firstByte = br.read();
    int secondByte = br.read();
    int thirdByte = br.read();

    // Check for BOM (0xEF, 0xBB, 0xBF)
    if (!(firstByte == 0xEF && secondByte == 0xBB && thirdByte == 0xBF)) {
      br.reset(); // No BOM, reset to the beginning
    }
    return br;
  }
  
  public abstract RawTrafficDataRecordSummary load(BufferedReader br, RawTrafficData result) throws Exception;
}
