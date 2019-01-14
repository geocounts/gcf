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
 * 
 * @see EnumHeaderRecords#FC
 */
public class FileChecksum extends RawTrafficDataHeaderElement {
  public String algorithmid;
  public String value;
  
  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.FC;
  }

  @Override
  protected String[] write() {
    return new String[]{algorithmid, value};
  }

  @Override
  protected void read(String[] data) {
    this.algorithmid = data[0];
    this.value = data[1];
  }
  
  public final void copyFrom(FileChecksum other) {
    this.algorithmid = other.algorithmid;
    this.value = other.value;
  }
}
