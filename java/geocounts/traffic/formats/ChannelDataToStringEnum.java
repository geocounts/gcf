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
 * Labeling conventions for {@link ChannelData}
 */
public enum ChannelDataToStringEnum implements java.util.function.Function<ChannelData, String> {
  /**
   * Channel ID
   */
  Ch() {
    @Override
    public String apply(ChannelData cd) {
      return Integer.toString(cd.getChannelID());
    }
  },
  DirFrom() {
    @Override
    public String apply(ChannelData cd) {
      return cd.from.getDir().name();
    }
  },
  LaneFrom() {
    @Override
    public String apply(ChannelData cd) {
      return cd.from.lane.getId();
    }
  },
  DirTo() {
    @Override
    public String apply(ChannelData cd) {
      return cd.to1.getDir().name();
    }
  },
  LaneTo() {
    @Override
    public String apply(ChannelData cd) {
      return cd.to1.lane.getId();
    }
  },
  
  Ch_DirFromLane_DirToLane() {
    @Override
    public String apply(ChannelData cd) {
      StringBuilder result = new StringBuilder();
      result.append(cd.getChannelID());
      result.append(' ');
      appendDirInfo(cd.from, result);
      result.append(' ');
      result.append("->");
      result.append(' ');
      appendDirInfo(cd.to1, result);
      return result.toString();
    }
    
    private void appendDirInfo(ChannelData.ToFromInfo info, StringBuilder result) {
      result.append(info.dir);
      if (info.lane != ChannelLaneData.undefined) {
        result.append('[');
        result.append(info.lane);
        result.append(']');
      }
    }
  },
  
  DirTo_label_lane() {
    @Override
    public String apply(ChannelData cd) {
      return cd.to1.getDir().shortLabel + " " + cd.to1.lane;
    }
  },
  
  DirTo_label_code() {
    @Override
    public String apply(ChannelData cd) {
      if (cd.to1.getDir() == ChannelDirEnum.undefined)
        return null;
      if (cd.to1.lane == ChannelLaneData.undefined)
        return null;
      StringBuilder result = new StringBuilder();
      result.append(cd.to1.getDir().shortLabel);
      result.append(':');
      result.append(cd.to1.lane.getCode());
      return result.toString();
    }
  },

  description_dir_lane() {
    @Override
    public String apply(ChannelData cd) {
      StringBuilder result = new StringBuilder();
      StationLabels.LabelSetForElement lb = cd.getLabels();
      if (lb != null) {
        String desc = lb.get(StationLabelElementField.description);
        if (desc == null)
          desc = Integer.toString(cd.getChannelID());
        result.append(desc);
      }
      
      if (cd.to1.getDir() != ChannelDirEnum.undefined) {
        if (result.length() > 0)
          result.append(' ');
        result.append('[');
        result.append(cd.to1.getDir().shortLabel);
        if (cd.to1.lane != ChannelLaneData.undefined) {
          result.append(' ');
          result.append(cd.to1.lane.getCode());
        }
        result.append(']');
      }
      return result.toString();
    }
    
  }
}
