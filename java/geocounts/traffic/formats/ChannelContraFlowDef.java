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
 * Describes how to treat vehicles with negative speeds
 * @deprecated
 */
public class ChannelContraFlowDef extends RawTrafficDataHeaderElement {
  /**
   * The ID of the channel containing vehicles with negative speeds
   */
  public int from_channel_id;
  /**
   * The ID of the channel to which negative speed vehicles should be assigned
   */
  public int to_channel_id;
  
  @Override
  public EnumHeaderRecords getSourceRecord() {
    return EnumHeaderRecords.SR;
  }

  @Override
  public String[] write() {
    return new String[]{Integer.toString(from_channel_id), Integer.toString(to_channel_id)};
  }

  @Override
  protected void read(String[] data) {
    from_channel_id = Integer.parseInt(data[0]);
    to_channel_id = Integer.parseInt(data[0]);
  }

  public void setFrom(ChannelContraFlowDef other) {
    
  }
}
