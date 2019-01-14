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
 * Computes the direction a channel is "turning".
 * For example, if the channel is {@link ChannelData#from from} the {@link ChannelDirEnum#n North} and going {@link ChannelData#to to} the {@link ChannelDirEnum#e East}, its a left turn
 * @see ChannelData
 * @see ChannelDirEnum
 */
public enum ChannelTurnEnum {
  /**
   * 0 degree change
   */
  through,
  /**
   * 45 degree
   */
  bearright,
  /**
   * 90 degree
   */
  right,
  /**
   * 135 degree
   */
  hardright,
  /**
   * 180 degree (or -180 degree)
   */
  uturn,
  /**
   * 225 degree (or -135 degree)
   */
  hardleft,
  /**
   * 270 degree (or -90 degree)
   */
  left,
  /**
   * 315 degree (or -45 degree)
   */
  bearleft;
  
  public boolean isLeft() {
    return ordinal() > 4;
  }
  
  public boolean isRight() {
    return (ordinal() > 0) && (ordinal() < 4);
  }
  
  public int getDegrees() {
    return ordinal()*45;
  }
  
  public int getCode() {
    return ordinal();
  }
  
  /**
   * Computes the new direction based on the 'startingDirection' and 'startDirIsApproachOrBound' fields.<br />
   * The 'startDirIsApproachOrBound' accounts for the two ways to describe an object's directionality before it turns:<ol>
   * <li>The direction it is approaching from OR</li>
   * <li>The direction it is heading toward prior to turning (also called "bound direction")</li>
   * </ol>
   * These two are direct opposites and are easily confused.<br />
   * Example 1: If you say an object is 'North bound turning {@link #left}', it means it is approaching you from the <i>South</i> (leg)
   * and then turning towards the West.<br />
   * Example 2: If you say an object is 'approaching from the East turning {@link #hardright}', it means it is going West (bound) as it approaches
   * and then turning toward Northeast.
   * 
   * @param startingDirection The location on the compass the object is starting the turn. 
   * @param startDirIsApproachOrBound If true, the startDir should be considered 'approaching from'.
   * If false, the startDir should be considered 'heading (bound) towards'
   * @return The direction after executing this turn
   */
  public ChannelDirEnum compute(ChannelDirEnum startingDirection, boolean startDirIsApproachOrBound) {
    if (startDirIsApproachOrBound) {
      startingDirection = startingDirection.getOppositeDir();
    }
    return startingDirection.getRotatedDir(ordinal());
  }
  
  public static ChannelTurnEnum fromDegrees(int value360) {
    boolean clockwise = value360 >= 0;
    value360 = Math.abs(value360);
    if (value360 >= 360)
      value360 = value360%360;
    
    int index = value360/45;
    if (!clockwise && (index != 0))
      index = 8-index;
    
//    if (index >= 8)
//      System.out.println(value360);

    return values()[index];
  }
  
  /**
   * If the directions are not both set to one of the 8 compass points, this returns null
   * @param from
   * @param to
   * @return May return null
   */
  public static ChannelTurnEnum compute(ChannelDirEnum from, ChannelDirEnum to) {
    if ((from == null) || (to == null))
      return null;
    
    int fromDegrees = from.getDegrees();
    if (fromDegrees == Integer.MAX_VALUE)
      return null;
    int toDegrees = to.getDegrees();
    if (toDegrees == Integer.MAX_VALUE)
      return null;
    
    int changeDeg = toDegrees - fromDegrees - 180;
    
    return fromDegrees(changeDeg);
  }
}
