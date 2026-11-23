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
 * 
 * @see AxleGroupFieldsEnum
 * @see VehicleRec
 */
public class AxleGroupRec {
  /**
   * @see VehicleRec#naxles
   * @see GroupFieldsEnum#naxles
   */
  public int naxles;
  
  public int naxles() {
    return (axlesInGroup.size() > 0 ? axlesInGroup.size() : naxles);
  }
  
  /**
   * Never null
   * @see AGRecType
   */
  private AGRecType groupType = AGRecType.UNKNOWN;
  
  public int groupTypeID() {
    return groupType.ID;
  }
  
  public AGRecType getAxleGroupType() {
    return groupType;
  }

  public void setAxleGroupType(AGRecType t) {
    groupType = t;
  }
  
  public void setAxleGroupType(int value) {
    setAxleGroupType(AGRecType.fromID(value));
  }

  public final AxleRecList axlesInGroup = new AxleRecList();

  protected void addAxle(AxleRec a) {
    axlesInGroup.add(a);
  }
  
  public double gspaceMeters() {
    if (axlesInGroup.size() == 0)
      return 0;
    return axlesInGroup.get(0).aspaceMeters;
  }
  
  /**
   * @see AxleGroupFieldsEnum#gmass
   */
  public double massKg() {
    return axlesInGroup.massKg();
  }
  
  public boolean setMassKg(double gmassKg) {
    if (axlesInGroup.size() == 0)
      return false;
    
    double amassKg = gmassKg/axlesInGroup.size();
    for (AxleRec ar: axlesInGroup)
      ar.amassKg = amassKg;
    
    return true;
  }
  
  @Override
  public String toString() {
    return Integer.toString(naxles());
  }

  /**
   * 3 digit code:
   * <ol>
   * <li>Number of axles (normally 1 to 4)</li>
   * <li>Number of tyres (normally 1 or 2)</li>
   * <li>steer (0) or non-steer (1) group</li>
   * </ol>
   */
  public enum AGRecType {
    UNKNOWN(-1),
    SINGLE_STEER(110), 
    SINGLE_NONSTEER(111), 
    SINGLE_DUAL(121), 
    TANDEM_DUAL(221), 
    TANDEM_SINGLE_NONSTEER(211), 
    TANDEM_SINGLE_STEER(210), 
    TRI_DUAL(321), 
    /**
     * "Super singles"
     */
    TRI_SINGLE(311), 
    QUAD_SINGLE(411), 
    QUAD_DUAL(421);
    
    public final int ID;
    
    private AGRecType(int id) {
      ID = id;
    }
    
    public static AGRecType fromID(int value) {
      for (AGRecType rt: AGRecType.values()) {
        if (rt.ID == value)
          return rt;
      }
      
      if (value < 100)
        return AGRecType.values()[value];

      return UNKNOWN;
    }
  }
}
