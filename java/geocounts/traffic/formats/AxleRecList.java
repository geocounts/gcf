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

public class AxleRecList extends ArrayList<AxleRec> {
  private static final long serialVersionUID = 1L;
  
  public double wbase() {
    double result = 0;
    if (size() > 1) {
      for (int i=1; i<size(); i++)
        result += get(i).aspaceMeters;
    }
    return result;
  }
  
  public AxleRecList removeFrom(int fromID) {
    // create the new list
    AxleRecList result = new AxleRecList();
    for (int a=fromID; a<size(); a++) {
      result.add(this.get(a));
    }
    
    // remove
    super.removeAll(result);
    return result;
  }
  
  /**
   * @see AxleGroupFieldsEnum#gmass
   */
  public double massKg() {
    double result = 0;
    for (AxleRec ar: this)
      result += ar.amassKg;
    return result;
  }
  
  public AxleRec first() {
    return super.get(0);
  }
  
  public AxleRec second() {
    return super.get(1);
  }
  
  public AxleRec last() {
    return super.get(this.size()-1);
  }
}
