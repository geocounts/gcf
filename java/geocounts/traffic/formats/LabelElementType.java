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
 * @see StationLabelElementField
 * @see EquipmentChannelElementField
 */
public enum LabelElementType implements IdDesc {
  /**
   * A channel is defined with a SC record
   * @see ChannelData
   */
  channel() {
    @Override
    public boolean accepts(StationLabelElementField field) {
      return field.channel;
    }
  }, 
  /**
   * A direction is contained within an SC record. Valid direction codes range between 1 and 8.
   * @see ChannelDirEnum
   */
  direction() {
    @Override
    public boolean accepts(StationLabelElementField field) {
      return field.direction;
    }
  },
  /**
   * 
   * @see RawTrafficDataStation
   */
  station() {
    @Override
    public boolean accepts(StationLabelElementField field) {
      return field.station;
    }
  };

  @Override
  public String getId() {
    return name();
  }

  @Override
  public String getDescription() {
    return "Labels refer to a " + getId();
  }
  
  public abstract boolean accepts(StationLabelElementField field);
}
