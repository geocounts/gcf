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
 * State of a traffic signal controlling the vehicle.<ul start="0">
 * <li>{@link #none}</li>
 * <li>{@link #go}</li>
 * <li>{@link #stop}</li>
 * <li>{@link #warnstop}</li>
 * <li>{@link #yieldsign}</li>
 * <li>{@link #stopsign}</li>
 * </ul>
 * @see MonitoringEventType#signal
 */
public enum SignalPhase implements IdDesc {
  /**
   * {@link SignalPhase Signals} are unknown, or none.
   */
  none("None/Unknown", 'u'),
  
  /**
   * {@link SignalPhase Signal phase} is Green - Go
   */
  go("Go", 'g'),
  
  /**
   * {@link SignalPhase Signal phase} is Red - Stop
   */
  stop("Stop", 'r'),
  
  /**
   * {@link SignalPhase Signal phase} is constant Yellow (or amber).<br />
   * This informs vehicles approaching the intersection to stop, protecting vehicles already in the intersection and allowing them to complete their movement.
   */
  warnstop("Prepare to stop", 'y'),
  
  /**
   * {@link SignalPhase Signals} are flashing yellow. This has the same meaning as a yield sign.<br />
   * A flashing yellow arrow (FYA) permits vehicles to turn when safe to do so.
   */
  yieldsign("Yield sign (or FYA)", 'f'),
  
  /**
   * {@link SignalPhase Signals} are flashing red. This has the same meaning as a stop sign.
   */
  stopsign("Stop sign", 's');

  private String desc;
  public final char phaseChar;
  private SignalPhase(String d, char phaseChar) {
    this.desc = d;
    this.phaseChar = phaseChar;
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
