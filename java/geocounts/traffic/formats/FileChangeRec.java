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
 * @since 1.301
 * @see RawTrafficDataHeaderFileDef#FX
 */
public class FileChangeRec extends RawTrafficDataHeaderElement {
  public long dateTime;
  public String actionID;
  public String userID;
  public String parameter;
  
  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.FX;
  }
  
  @Override
  public String[] write() {
    String[] result = new String[5];
    result[0] = RawFmtUtils.simpleDateFormat(RawFmtUtils.V_DateFormat).format(dateTime);
    result[1] = RawFmtUtils.ISO8601_TIMEONLY_format(dateTime);
    result[2] = actionID;
    result[3] = userID;
    result[4] = parameter;
    return result;
  }
  
  @Override
  protected void read(String[] data) {
    try {
      dateTime = RawFmtUtils.sdfHeaderV().parse(data[0] + "," + data[1]).getTime();
    } catch (Exception ex) {}
    actionID = data[2];
    userID = data[3];
    parameter = data[4];
  }
}
