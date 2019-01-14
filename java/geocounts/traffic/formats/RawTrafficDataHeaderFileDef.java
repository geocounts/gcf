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
 * Data relating the File object
 *
 */
public class RawTrafficDataHeaderFileDef {

  /**
   * File: Version
   * @see SystemVersion
   */
  public final SystemVersion FV = new SystemVersion();

  /**
   * File checksum
   * @see FileChecksum
   */
  public final FileChecksum FC = new FileChecksum();
  
  /**
   * File changes
   * @see FileChanges
   * @since 1.9969
   */
  public final FileChanges FX = new FileChanges();

  /**
   * File: Date produced
   */
  public long FD;

  /**
   * File: Units
   * @see UnitsUsed
   */
  public final UnitsUsed FU = new UnitsUsed();

  /*
   * File: Name spaces
   * @see FileNamespaces
   *
  public final FileNamespaces FN = new FileNamespaces(); */

  /**
   * File: error message data
   * @see EnumHeaderRecords#FE
   */
  public String FE;
  
  public final void copyFrom(RawTrafficDataHeaderFileDef other) throws Exception {
    this.FV.copyFrom(other.FV);
    this.FX.copyFrom(other.FX);
    this.FU.copyFrom(other.FU);
//    this.FN.copyFrom(other.FN);
    this.FD = other.FD;
    this.FE = other.FE;
    // do NOT copy the FC record
  }

  /**
   * 
   * @since 1.9951
   */
  public void mergeFrom(RawTrafficDataHeaderFileDef other) {
    
  }
}
