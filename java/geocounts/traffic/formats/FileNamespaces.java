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
 * Represents the {@link EnumHeaderRecords#FN FN} record
 * @see EnumHeaderRecords#FN
 * @deprecated
 */
public class FileNamespaces extends RawTrafficDataHeaderElement {

  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.FN;
  }

  @Override
  protected String[] write() {
    return null; // new String[]{tallytotal.name()};
  }

  @Override
  protected void read(String[] data) {
    // tallytotal = TallyFieldNamespaceEnum.valueOf(data[0]);
  }

  public void copyFrom(FileNamespaces fn) {
    // this.tallytotal = fn.tallytotal != null ? fn.tallytotal : TallyFieldNamespaceEnum.userdefined;
  }
  
  /*
   * The namespace being used for the {@link TallyRecordsDef#TF "TF"} field.
   * This is never null.
   *
  public TallyFieldNamespaceEnum tallytotal = TallyFieldNamespaceEnum.trafficmonitoring;
*/
}
