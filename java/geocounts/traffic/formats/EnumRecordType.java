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


public enum EnumRecordType implements IdDesc {
  /**
   * Comment line
   */
  X("Commented out line", false) {
    @Override
    public String getId() {
      return "#";
    }
  },
  /**
   * Header record
   * @see EnumHeaderRecords
   */
  H("Header", false),
  /**
   * T,2013/09/23,11,0,1<b>;</b>49<b>;</b>0,0,26,22,1,0,0
   * @see TallyRecordsDef
   */
  T("Count Tally", true),
  /**
   * V,2013/09/23,11:24:21.646,1<b>;</b>&lt;vehicle fields&gt;<b>;</b>&lt;axle 1 fields&gt;<b>;</b>&lt;axle 2 fields&gt;<b>;</b>&lt;axle 3 fields&gt;;
   * @see VehicleRecordDef
   */
  V("Observation", true),
  /**
   * M,2013/09/23,11:24:00.000<b>;</b>comment<b>;</b>Interesting comment
   */
  M("Monitoring Event", true)
  
  ;

  public final String desc;
  public final boolean isBody;

  private EnumRecordType(String d, boolean isBody) {
    this.desc = d;
    this.isBody = isBody;
  }

  @Override
  public String getId() {
    return name();
  }

  @Override
  public String getDescription() {
    return desc;
  }

}
