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

import java.util.function.Function;

/**
 * These represent ways to aggregate channels
 * @see StationLabelElementField
 * @see EquipmentChannelElementField
 */
public class LabelElementType {
  private LabelElementType() {}
  
  /**
   * 
   * @see LabelElementTypeChannel
   */
  public static final LabelElementTypeChannel channel = new LabelElementTypeChannel();
  /**
   * 
   * @see LabelElementTypeDirection
   */
  public static final LabelElementTypeDirection direction = new LabelElementTypeDirection();
  /**
   * 
   * @see LabelElementTypeStation
   */
  public static final LabelElementTypeStation station = new LabelElementTypeStation();
  
  public static Impl valueOf(String name) {
    if (name.equals("channel"))
      return channel;
    if (name.equals("direction"))
      return direction;
    if (name.equals("station"))
      return station;
    return null;
  }
  
  static abstract class Impl implements Function<StationLabelElementField, Boolean> {
    public abstract ChannelAggregationEnum getAggregation();
    
    public final String name() {
      return getAggregation().name();
    }
  }
  
  static class LabelElementTypeChannel extends Impl {

    @Override
    public ChannelAggregationEnum getAggregation() {
      return ChannelAggregationEnum.channel;
    }

    @Override
    public Boolean apply(StationLabelElementField field) {
      return field.channel;
    }
    
  }
  
  /**
   * A "to" direction is contained within an SC record. Valid direction codes range between 1 and 8.
   * @see ChannelDirEnum
   */
  static class LabelElementTypeDirection extends Impl {

    @Override
    public ChannelAggregationEnum getAggregation() {
      return ChannelAggregationEnum.direction;
    }

    @Override
    public Boolean apply(StationLabelElementField field) {
      return field.direction;
    }
    
  }
  
  /**
   * 
   * @see RawTrafficDataStation
   */
  static class LabelElementTypeStation extends Impl {

    @Override
    public ChannelAggregationEnum getAggregation() {
      return ChannelAggregationEnum.station;
    }

    @Override
    public Boolean apply(StationLabelElementField field) {
      return field.station;
    }
    
  }
}
