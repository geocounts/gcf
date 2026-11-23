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

/**
 * Assists with recognizing raw files whose VV records are detections, not vehicles.
 * A detection file has two channels: 0 (called 'A' detector) and 1 (called 'B' detector)
 */
public class DetectorFileHelper {
  
  private final RawTrafficDataHeader header;
  
  public DetectorFileHelper(RawTrafficDataHeader header) {
    this.header = header;
  }

  public boolean has2Detectors() {
    return has2Detectors(false);
  }
  
  public boolean has2DetectorsWIM() {
    return has2Detectors(true);
  }
  
  public boolean has2Detectors(boolean checkWIM) {
    if (header.getNumberOfChannels() != 2)
      return false;
    if (!header.S.hasChannel(0) || !header.S.hasChannel(1))
      return false;
    for (ChannelData cd: header.S.getAllChannels())
      if (cd.to1.getDir() != ChannelDirEnum.undefined)
        return false;
    if (header.T.isRecording())
      return false;
    if (header.VA.isRecording())
      return false;
    if (checkWIM)
      if (isWKWIMHitCounter(header.VV))
        return true;
    return (header.VV.numberOfFields() == 0);
  }

  /**
   * TUPIDO
   * @return True if has WIM
   */
  private boolean isWKWIMHitCounter(VehicleRecordDef VV) {
    if (VV.contains(VehicleFieldsEnum.tempsurf))
      if (VV.contains(VehicleFieldsEnum.mass))
        return true;
    return false;
  }
}
