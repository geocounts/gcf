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
 * @since 1.70
 * @see EquipmentStationElementField
 * @see Equipment.EquipmentChannelRec
 */
public enum EquipmentChannelElementField {
  /**
   * Type of sensor: String
   * This is unstructured data. Potential uses include layout codes for traffic detectors. E.g.<ul>
   * <li>LPL - Loop, Piezo, Loop</li>
   * <li>PLP - Piezo, Loop, Piezo</li>
   * <li>LL - Two loops</li>
   * <li>L - Single loop</li>
   * <li>TT - Two tubes</li>
   * </ul>
   */
  sensortype {
    @Override
    public void set(UnitsUsed units, Equipment SE, int channelID, String value) throws Exception {
      SE.addChannelRec(this, channelID, value);
    }
  },
  /**
   * Distance between two sensors (tubes or piezos). Needed to compute vehicle records.
   * Use the same units as defined in the 'FU length_unit'. E.g. decimal feet, or meters.
   */
  sensorspacing {
    @Override
    public void set(UnitsUsed units, Equipment SE, int channelID, String value) throws Exception {
      double M = Double.parseDouble(value);
      SE.addChannelRec(this, channelID, Double.toString(M));
    }
  },
  /**
   * Length of an inductance loop.
   * Use the same units as defined in the 'FU length_unit'. E.g. decimal feet, or meters.
   */
  looplength {
    @Override
    public void set(UnitsUsed units, Equipment SE, int channelID, String value) throws Exception {
      double M = Double.parseDouble(value);
      SE.addChannelRec(this, channelID, Double.toString(M));
    }
  };
  
  public abstract void set(UnitsUsed units, Equipment SE, int channelID, String value) throws Exception;
  
  public final void set(RawTrafficDataHeader header, int channelID, double value) throws Exception {
    this.set(header.F.FU, header.S.SE, channelID, Double.toString(value));
  }
}
