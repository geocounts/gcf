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
import java.util.Iterator;

/**
 * Allows the user to track changes made to a GC file
 * @since 1.301
 * @see RawTrafficDataHeaderFileDef#FX
 */
public class FileChanges implements Iterable<FileChangeRec> {
  private final ArrayList<FileChangeRec> changes = new ArrayList<FileChangeRec>();
  public void copyFrom(FileChanges other) {
    changes.clear();
    changes.addAll(other.changes);
  }

  public void addChange(long dateTime, String actionID, String userID, String parameter) {
    FileChangeRec fc = new FileChangeRec();
    fc.dateTime = dateTime;
    fc.actionID = actionID;
    fc.userID = userID;
    fc.parameter = parameter;
    
    changes.add(fc);
  }

  public FileChangeRec fromLine(String line) {
    FileChangeRec result = new FileChangeRec();
    result.fromLine(line);
    changes.add(result);
    return result;
  }

  @Override
  public Iterator<FileChangeRec> iterator() {
    return changes.iterator();
  }
}
