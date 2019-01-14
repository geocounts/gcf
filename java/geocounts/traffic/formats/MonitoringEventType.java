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
 * The type of {@link MonitoringEventRec monitoring event}
 *
 */
public enum MonitoringEventType implements IdDesc {
  /**
   * event_data is the comment.
   */
  comment("Operator comment"),
  /**
   * event_data is a device stop code.
   */
  pause("Counting device unexpectedly paused recording"),
  /**
   * No event_data.
   */
  resume("Counting device resumed recording after an unexpected pause"),
  
  /**
   * Parks, schools, and many other facilities may plan closures for holidays so that no data will be recorded.
   * This event type is used to explain why a continuous counter observed no counts on this day.
   * These days are often excluded from computing annual statistics.
   * The time stamp for this event must be set to 00:00:00.000
   * event_data is a comment or code to describe the reason for the closure
   * @since 1.264
   */
  closureday("No data is recorded because the station is closed"),
  /**
   * event_data is a weather event code.
   */
  weather("Weather observation"),
  /**
   * event_data is a traffic event code.
   */
  traffic("Unusual traffic-related event that affects traffic conditions"),
  /**
   * event_data should be one of the signal phase characters.
   * @see SignalPhase
   * @see SignalPhase#stop
   */
  signal("Traffic signal event"),
  /**
   * event_data is the counter's battery voltage
   */
  battery("Battery voltage"),
  /**
   * A flag or flags describing the quality of data.
   * event_data should be a comma delimited list of flags. (Each flag identifier must not contain a comma)
   */
  qcflag("QC flags"),
  /**
   * A change in local time for daylight savings.
   * event_data should be the number of hours to change the clock: 1 or -1
   * @since 1.24
   */
  dsc("Daylight savings time change"),
  
  /**
   * The event code must start with "XX:"
   * @see RawTrafficDataFileDecoder#XX
   */
  userdefined("User defined field");

  private String desc;
  private MonitoringEventType(String d) {
    this.desc = d;
  }
  @Override
  public String getId() {
    return name();
  }

  @Override
  public String getDescription() {
    return desc;
  }
}
