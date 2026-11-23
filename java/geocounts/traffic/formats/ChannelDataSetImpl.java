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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * A list of {@link ChannelData channels}
 * @see ChannelData
 * @since 2.03
 */
public class ChannelDataSetImpl implements ChannelDataSet, Iterable<ChannelData>, Serializable, Predicate<ChannelData> {
  private static final long serialVersionUID = 1L;
  
  private final ArrayList<ChannelData> channels = new ArrayList<ChannelData>();
  
  @Override
  public String toString() {
    return channels.toString();
  }
  
  protected void addChannel(ChannelData cd) {
    channels.add(cd);
  }
  
  @Override
  public ChannelData getChannel(int channelID) {
    for (ChannelData cd: channels)
      if (cd.getChannelID() == channelID)
        return cd;
    return null;
  }

  @Override
  public LatLongCoordinate getCentroidOfChannels() {
    List<LatLongCoordinate> points = new ArrayList<LatLongCoordinate>();
    for (ChannelData cd: channels) {
      if (cd.getGeometry().isSet())
        points.add(cd.getGeometry().getCentroid());
    }
    return CoordinateObjectComputation.getCentroid(points);
  }

  @Override
  public boolean hasChannel(int channelID) {
    for (ChannelData cd: channels) {
      if (cd.getChannelID() == channelID)
        return true;
    }
    return false;
  }

  @Override
  public boolean test(ChannelData t) {
    return hasChannel(t.getChannelID());
  }
  
  @Override
  public ChannelDirEnumSet getToDirections() {
    return new ChannelDirEnumSet(getAllChannels(), false);
  }
  
  public ChannelDirEnumSet getFromDirections() {
    return new ChannelDirEnumSet(getAllChannels(), true);
  }

  @Override
  public Iterable<ChannelData> getAllChannels() {
    return channels;
  }

  @Override
  public int getNumberOfChannels() {
    return channels.size();
  }

  @Override
  public ChannelData getChannelFromIndex(int index) {
    return channels.get(index);
  }

  @Override
  public Iterator<ChannelData> iterator() {
    return channels.iterator();
  }

  @Override
  public ChannelData getFirstChannel() {
    return channels.size() > 0 ? channels.get(0) : null;
  }
  
  public ChannelDataSet getChannelsInToDirection(ChannelDirEnum dirTo1Value) {
    ChannelDataSetImpl result = new ChannelDataSetImpl();
    for (ChannelData cd: getAllChannels())
      if (cd.to1.getDir() == dirTo1Value)
        result.addChannel(cd);
    return result;
  }

  @Override
  public ChannelDataSet getChannelsInToLane(ChannelLaneData laneTo1Value) {
    ChannelDataSetImpl result = new ChannelDataSetImpl();
    for (ChannelData cd: getAllChannels())
      if (cd.to1.lane.getCode() == laneTo1Value.getCode())
        result.addChannel(cd);
    return result;
  }
}
