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

public abstract class RawTrafficDataTrafficElement {
  protected long time;
  public long getTime() {
    return time;
  }

  public final void addTime(long adjustMilliseconds) {
    time += adjustMilliseconds;
  }

  public int channelID;
  
  public int compareTimes(RawTrafficDataTrafficElement o) {
    long timeDiff = this.getTime() - o.getTime();
    if (timeDiff == 0)
      return 0;
    return timeDiff > 0 ? 1 : -1;
  }

  public int compareChannels(RawTrafficDataTrafficElement o) {
    return this.channelID - o.channelID;
  }
  
  public int compareTo(RawTrafficDataTrafficElement o) {
    int diff = compareTimes(o);
    if (diff == 0) {
      diff = compareChannels(o);
    }
    return diff;
  }
}
