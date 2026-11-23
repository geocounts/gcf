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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimedObjectComparator implements Comparator<TimedObject> {
  public TimedObjectComparator() {
    this(true);
  }
  
  public final boolean asc_desc;
  public TimedObjectComparator(boolean asc_desc) {
    this.asc_desc = asc_desc;
  }
  
  @Override
  public int compare(TimedObject o1, TimedObject o2) {
    if (asc_desc)
      return RawFmtUtils.compareLong(o1.getTime(), o2.getTime());
    return RawFmtUtils.compareLong(o2.getTime(), o1.getTime());
  }
  
  public static void sort(List<? extends TimedObject> list, boolean asc_desc) {
    Collections.sort(list, new TimedObjectComparator(asc_desc));
  }
}