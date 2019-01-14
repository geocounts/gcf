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

public abstract class RawTrafficDataHeaderElement {
  
  public abstract EnumHeaderRecords getSourceRecord();
  /**
   * @return The values to write to a file, or null if this record is not set
   */
  protected abstract String[] write();
  protected abstract void read(String[] data);

  public final void copyFrom(RawTrafficDataHeaderElement e) throws Exception {
    if (!this.getClass().equals(e.getClass()))
      RawFmtUtils.throwException(this, "Header elements must be the same", e.getClass());
    this.read(e.write());
  }
  
  public final void fromLine(String line) {
    String[] data = RawFmtUtils.split(line, ',');
    read(data);
  }

}
