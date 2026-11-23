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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ChannelDirEnumSet extends HashSet<ChannelDirEnum> {
  private static final long serialVersionUID = 1L;
  
  private int minValidSortingCode = Integer.MAX_VALUE;
  private ChannelDirEnum primaryDirection = ChannelDirEnum.undefined;

  /**
   * 
   * @return The primary direction, or undefined. Never null
   */
  public ChannelDirEnum getPrimary() {
    return primaryDirection;
  }

  public ChannelDirEnumSet(Iterable<ChannelData> channels, boolean fromOrToDir) {
    for (ChannelData ch: channels) {
      if (fromOrToDir)
        add(ch.from.getDir());
      else {
        add(ch.to1.getDir());
        if (ch.to2.getDir() != ChannelDirEnum.undefined)
          add(ch.to2.getDir());
        if (ch.to3.getDir() != ChannelDirEnum.undefined)
          add(ch.to3.getDir());
      }
    }
  }
  
  /**
   * 
   * @return The first direction after {@link #sortedTwoWayIterator(boolean) sorting}, or null if there are no primary directions
   */
  public ChannelDirEnum getFirst() {
    Iterator<ChannelDirEnum> it = sortedTwoWayIterator(false).iterator();
    if (it.hasNext())
      return it.next();
    return null;
  }
  
  private void updatePrimaryDirection(ChannelDirEnum e) {
    int sortingCode = e.getTwoWaySortingCode();
    if (sortingCode != -1) {
      if (sortingCode < minValidSortingCode) {
        minValidSortingCode = sortingCode;
        primaryDirection = e;
      }
    }
  }
  
  @Override
  public boolean add(ChannelDirEnum e) {
    if (e == null)
      return false;
    try {
      updatePrimaryDirection(e);
    } catch (RuntimeException rte) {}
    return super.add(e);
  }
  
  public boolean allValid() {
    for (ChannelDirEnum dir: this)
      if (dir == ChannelDirEnum.undefined)
        return false;
    return true;
  }
  
  /**
   * 
   * @param primaryFirstOrLast If true, sort according to NW, N, NE, E, SE, etc
   * If false, sort according to N, NE, E, SE, etc
   * @return Iterable<ChannelDirEnum>
   */
  public Iterable<ChannelDirEnum> sortedTwoWayIterator(boolean primaryFirstOrLast) {
    List<ChannelDirEnum> result = new ArrayList<ChannelDirEnum>();
    for (ChannelDirEnum d: this)
      if (d != ChannelDirEnum.undefined)
        result.add(d);
    if (result.size() < 2)
      return result;
    if (primaryFirstOrLast)
      result.sort(new CompareChannelDirEnum());
    else
      result.sort(new CompareChannelDirReverseEnum());
    return result;
  }
  
  static class CompareChannelDirEnum implements Comparator<ChannelDirEnum> {
    @Override
    public int compare(ChannelDirEnum o1, ChannelDirEnum o2) {
      return o1.getTwoWaySortingCode() - o2.getTwoWaySortingCode();
    }
  }
  
  static class CompareChannelDirReverseEnum implements Comparator<ChannelDirEnum> {
    @Override
    public int compare(ChannelDirEnum o1, ChannelDirEnum o2) {
      return o2.getTwoWaySortingCode() - o1.getTwoWaySortingCode();
    }
  }
}
