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
 * A list of {@link ChannelData channels}
 * @see ChannelData
 * @since 2.03
 */
public interface ChannelDataSet {
  /**
   * 
   * @param index Varies from 0 to {@link #getNumberOfChannels()}-1.
   * @return {@link ChannelData}
   * @throws IndexOutOfBoundsException See {@link java.util.List#get(int)}
   */
  public ChannelData getChannelFromIndex(int index);
  
  /**
   * 
   * @param channelID
   * @return {@link ChannelData} or null if the channel does not exist
   */
  public ChannelData getChannel(int channelID);
  
  /**
   * The centroid of the {@link ChannelData channels}
   * @return {@link LatLongCoordinate}
   * @since 1.9963
   * @see #SG
   */
  public LatLongCoordinate getCentroidOfChannels();
  
  public boolean hasChannel(int channelID);
  
  public Iterable<ChannelData> getAllChannels();
  
  public int getNumberOfChannels();
  
  /**
   * 
   * @return The {@link #getChannelFromIndex(int) channel at index 0}, or null if there are {@link #getNumberOfChannels()} = 0
   */
  public ChannelData getFirstChannel();
  
  /**
   * 
   * @return All Depart directions
   */
  public ChannelDirEnumSet getToDirections();

  /**
   * 
   * @return All Approach directions
   */
  public ChannelDirEnumSet getFromDirections();
  
  /**
   * Get the channels with {@link ChannelData#to1 ChannelData.to1.getDir()} equal to the dirTo
   * @param dirTo {@link ChannelDirEnum}
   * @return A {@link ChannelDataSet}
   * @since 2.018
   * @see #getToDirections()
   * @see #getNumberOfToDirections()
   * @see #getChannelsInToLane
   */
  public ChannelDataSet getChannelsInToDirection(ChannelDirEnum dirTo);
  
  /**
   * Get the channels with {@link ChannelData#to1 ChannelData.to1.lane} equal to the laneTo
   * @param laneTo {@link ChannelLaneData}
   * @return A {@link ChannelDataSet}
   * @since 2.202
   * @see #getChannelsInToDirection
   */
  public ChannelDataSet getChannelsInToLane(ChannelLaneData laneTo);
}
