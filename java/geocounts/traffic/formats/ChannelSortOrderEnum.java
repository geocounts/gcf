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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @since 2.080
 *
 */
public enum ChannelSortOrderEnum {
  channelid() {
    @Override
    public List<ChannelData> sort(RawTrafficDataStation S) {
      ArrayList<ChannelData> result = new ArrayList<ChannelData>();
      for (ChannelData cd: S.getAllChannels())
        result.add(cd);
      Collections.sort(result, new SortByChannelID());
      return result;
    }
  },
  
  dirlane() {
    @Override
    public List<ChannelData> sort(RawTrafficDataStation S) {
      ArrayList<ChannelData> result = new ArrayList<ChannelData>();
      for (ChannelData cd: S.getAllChannels())
        result.add(cd);
      Collections.sort(result);
      return result;
    }
  },
  
  segment_driveleft() {
    @Override
    public List<ChannelData> sort(RawTrafficDataStation S) {
      ChannelSortOrderLegRec[] legs = new ChannelSortOrderLegRec[] {
          new ChannelSortOrderLegRec(ChannelDirEnum.n), 
          new ChannelSortOrderLegRec(ChannelDirEnum.ne), 
          new ChannelSortOrderLegRec(ChannelDirEnum.e), 
          new ChannelSortOrderLegRec(ChannelDirEnum.se), 
          new ChannelSortOrderLegRec(ChannelDirEnum.s), 
          new ChannelSortOrderLegRec(ChannelDirEnum.sw), 
          new ChannelSortOrderLegRec(ChannelDirEnum.w), 
          new ChannelSortOrderLegRec(ChannelDirEnum.nw)};
      
      return sortByLegOrder(legs, S);
    }
  },
  
  segment_driveright() {
    @Override
    public List<ChannelData> sort(RawTrafficDataStation S) {
      ChannelSortOrderLegRec[] legs = new ChannelSortOrderLegRec[] {
          new ChannelSortOrderLegRec(ChannelDirEnum.s), 
          new ChannelSortOrderLegRec(ChannelDirEnum.sw), 
          new ChannelSortOrderLegRec(ChannelDirEnum.w), 
          new ChannelSortOrderLegRec(ChannelDirEnum.nw), 
          new ChannelSortOrderLegRec(ChannelDirEnum.n), 
          new ChannelSortOrderLegRec(ChannelDirEnum.ne), 
          new ChannelSortOrderLegRec(ChannelDirEnum.e), 
          new ChannelSortOrderLegRec(ChannelDirEnum.se)};
      
      return sortByLegOrder(legs, S);
    }
  };
  
  protected List<ChannelData> sortByLegOrder(ChannelSortOrderLegRec[] legs, RawTrafficDataStation S) {
    for (ChannelData cd: S.getAllChannels()) {
      for (ChannelSortOrderLegRec t: legs) {
        if (cd.to1.getDir() == t.legDir) {
          t.channels.add(cd);
          break;
        } else if (cd.from.getDir() == t.legDir) {
          t.opposite.add(cd);
          break;
        }
      }
    }
    
    List<ChannelData> result = new ArrayList<ChannelData>();
    for (ChannelSortOrderLegRec t: legs) {
      if (t.numberOfChannels() > 0) {
        t.sort();
        result.addAll(t.channels);
        result.addAll(t.opposite);
      }
    }
    return result;
  }
  
  public abstract List<ChannelData> sort(RawTrafficDataStation S);
  
  
  static class ChannelSortOrderLegRec implements Comparator<ChannelData> {
    public final ChannelDirEnum legDir;
    private ChannelSortOrderLegRec(ChannelDirEnum a) {
      legDir = a;
    }
    
    public int numberOfChannels() {
      return channels.size() + opposite.size();
    }

    public List<ChannelData> channels = new ArrayList<ChannelData>();
    public List<ChannelData> opposite = new ArrayList<ChannelData>();
    
    public void sort() {
      channels.sort(this);
      
      opposite.sort(this);
      Collections.reverse(opposite);
    }
    
    @Override
    public int compare(ChannelData o1, ChannelData o2) {
      int result = o1.to1.lane.getCode() - o2.to1.lane.getCode();
      if (result != 0)
        return result;
      
      return 0; // FIXME
    }

    @Override
    public String toString() {
      return legDir.toString() + " " + numberOfChannels();
    }
  }
  
  static class SortByChannelID implements Comparator<ChannelData> {
    @Override
    public int compare(ChannelData o1, ChannelData o2) {
      return o1.getChannelID() - o2.getChannelID();
    }
    
  }
}
