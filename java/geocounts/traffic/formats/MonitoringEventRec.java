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

public class MonitoringEventRec extends RawTrafficDataTrafficElement implements Comparable<MonitoringEventRec> {
  private MonitoringEventType eventType = MonitoringEventType.comment;
  
  private MonitoringEventRec(long timeUTCAccountedFor) {
    super.time = timeUTCAccountedFor;
  }
  
  public static MonitoringEventRec createNoTZ(long timeUTCAccountedFor) {
    return new MonitoringEventRec(timeUTCAccountedFor);
  }
  
  /**
   * This is never null
   */
  public MonitoringEventType getEventType() {
    return eventType;
  }
  
  /**
   * This may be null
   */
  private String eventTypeUserDefined;
  
  public String getEventTypeID() {
    return eventTypeUserDefined != null ? TextHeaderEscape.XX + eventTypeUserDefined : eventType.name();
  }

  /**
   * 
   * @param value Must not include the "XX:" prefix
   */
  public void setEventTypeUserDefined(String value) {
    this.eventType = MonitoringEventType.userdefined;
    this.eventTypeUserDefined = value;
  }

  public void setEventType(MonitoringEventType value) {
    this.eventType = (value == null ? MonitoringEventType.comment : value);
    this.eventTypeUserDefined = null;
  }
  
  public String text;
  
  public void setAsStation() {
    super.channelID = -1;
  }
  
  public boolean isStationOrChannel() {
    return super.channelID == -1;
  }

  @Override
  public int compareTo(MonitoringEventRec o) {
    int result = compareTimes(o);
    if (result == 0)
      return super.channelID - o.channelID;
    return result;
  }

}
