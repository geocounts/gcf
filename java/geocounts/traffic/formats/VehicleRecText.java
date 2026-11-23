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

import java.util.List;


public class VehicleRecText {
  public final static VehicleRecText instance = new VehicleRecText();
  
  public String formatGroupString(List<AxleGroupRec> axleGroups) {
    if ((axleGroups == null) || (axleGroups.size() == 0))
      return options.showBlanksIfNoGroupPattern ? "" : null;
    
    int[] axlesInEachGroup = new int[axleGroups.size()];
    for (int i=0; i<axleGroups.size(); i++) {
      axlesInEachGroup[i] = axleGroups.get(i).naxles();
    }
    
    return formatGroupString(axlesInEachGroup);
  }
  
  public String formatGroupString(int... axlesInEachGroup) {
    if ((axlesInEachGroup == null) || (axlesInEachGroup.length == 0))
      return options.showBlanksIfNoGroupPattern ? "" : null;
    
    if (axlesInEachGroup.length == 1)
      return Integer.toString(axlesInEachGroup[0]);
    
    StringBuilder result = new StringBuilder();
    result.append(axlesInEachGroup[0]);
    for (int i=1; i<axlesInEachGroup.length; i++) {
      result.append(options.GRP_SPACE);
      result.append(axlesInEachGroup[i]);
    }
    return result.toString();
  }
  
  public int[] parseGroupString(String value) {
    if ((value == null) || (value.length() == 0))
      return null;
    return RawFmtUtils.splitInt(value, options.GRP_SPACE);
  }
  
  public static class VehicleRecTextOptions {
    public boolean showSingleSpaceIfNoAxles = true;
    public char GRP_SPACE = '-';
    public char VEH_SPACE = '-';
    public char VEH_WHEEL = 'o';
    public String INVALID_TOKEN = "-";
    public double m_SpacePerDivision = 1.5;
    
    public boolean showAxlesAfterWBase = true;
    
    private int maxResultLength = 300;
    
    public boolean showBlanksIfNoGroupPattern = false;
  }
  
  public final VehicleRecTextOptions options = new VehicleRecTextOptions();
  
  // Copied from VehicleTextGraphics
  public String formatVehicleAxleString(VehicleRec m_Veh) {
    if (m_Veh.axles.size() == 0) {
      if (m_Veh.wheelbaseMeters > 0) {
        if (m_Veh.naxles == 2)  // fix metrocount not having axles
          return format2AxleVehicleFromWheelbase(m_Veh.wheelbaseMeters);
        return formatVehicleWBaseString(m_Veh);
      }
      
      if (options.showSingleSpaceIfNoAxles)
        return Character.toString(options.VEH_SPACE);
      return options.INVALID_TOKEN;
    }
    
    StringBuilder b = new StringBuilder();
    appendWheel(b);
    for (int i = 1; i < m_Veh.axles.size(); i++) {
      appendSpace(b, m_Veh.axles.get(i).aspaceMeters);
      appendWheel(b);
    }
    return b.toString();
  }
  
  private void appendWheel(StringBuilder b) {
    b.append(options.VEH_WHEEL);
  }
  
  private void appendSpace(StringBuilder b, double aspaceMeters) {
    double space = options.m_SpacePerDivision;
    try {
      while (space <= aspaceMeters) {
        b.append(options.VEH_SPACE);
        space += options.m_SpacePerDivision;
        
        if (b.length() > options.maxResultLength)
          return;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      b.append(options.VEH_SPACE);
    }
  }
  
  public String format2AxleVehicleFromWheelbase(double wheelbaseMeters) {
    if (wheelbaseMeters <= 0)
      return options.INVALID_TOKEN;
    
    StringBuilder b = new StringBuilder();
    appendWheel(b);
    appendSpace(b, wheelbaseMeters);
    appendWheel(b);
    return b.toString();
  }
  
  public String formatVehicleWBaseString(VehicleRec m_Veh) {
    if (m_Veh.wheelbaseMeters <= 0)
      return options.INVALID_TOKEN;
    
    StringBuilder b = new StringBuilder();
    int divisions = (int)Math.rint(m_Veh.wheelbaseMeters/options.m_SpacePerDivision);
    for (int i=0; i<divisions; i++)
      b.append(options.VEH_SPACE);
    
    if (options.showAxlesAfterWBase) {
      if (m_Veh.naxles > 0) {
        b.append(" [");
        b.append(m_Veh.naxles);
        b.append(']');
      }
    }
    return b.toString();
  }
  
  public String formatVehicleLengthString(VehicleRec m_Veh) {
    if (m_Veh.lengthMeters <= 0)
      return options.INVALID_TOKEN;
    
    StringBuilder b = new StringBuilder();
    int divisions = (int)Math.rint(m_Veh.lengthMeters/options.m_SpacePerDivision);
    if (divisions > 2) {
      b.append('<');
      for (int i=1; i<divisions-1; i++)
        b.append('-');
      b.append('>');
    } else {
      for (int i=0; i<divisions; i++)
        b.append(options.VEH_SPACE);
    }

    return b.toString();
  }
}
