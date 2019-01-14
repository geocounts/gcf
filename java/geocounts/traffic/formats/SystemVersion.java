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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @see EnumHeaderRecords#FV
 *
 */
public class SystemVersion extends RawTrafficDataHeaderElement {
  public int formatVersion;
  public String producerVersion;
  public final List<String> sourceFilenames = new ArrayList<String>();
  
  public SystemVersion() {
    formatVersion = 1;
    producerVersion = null;
  }

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.FV;
  }
  
  public void addSourceFile(java.io.File value) {
    if (value == null)
      return;
    addSource(value.getName());
  }
  
  public void addSource(String value) {
    if (value == null)
      return;
    if (value.length() == 0)
      return;
    sourceFilenames.add(value);
  }

  @Override
  public void read(String[] data) {
    formatVersion = Integer.parseInt(data[0]);
    producerVersion = data[1];
    for (int i=2; i<data.length; i++)
      addSource(data[i]);
  }
  
  @Override
  protected String[] write() {
    String[] result = new String[2 + sourceFilenames.size()];
    result[0] = Integer.toString(formatVersion);
    result[1] = RawFmtUtils.notNull(producerVersion);
    for (int i=0; i<sourceFilenames.size(); i++)
      result[i+2] = RawFmtUtils.notNull(sourceFilenames.get(i));
    return result;
  }
}
