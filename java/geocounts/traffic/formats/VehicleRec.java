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
import java.util.List;

/**
 * Represents a single counted vehicle
 * @see VehicleRecordDef
 *
 */
public class VehicleRec extends ObservationRec {
/*
  protected VehicleRec(int chID, java.util.Date timeUTCNotAccountedFor) {
    super.channelID = chID;
    this.time = timeUTCNotAccountedFor;
  } */
  
  private VehicleRec(int channelID, long timeUTCNotAccountedFor) {
    super.channelID = channelID;
    this.time = timeUTCNotAccountedFor;
//    this(chID, new java.util.Date(timeWithUTCAccountedFor));
  }

  public static VehicleRec createNoTZ(int chID, long timeUTCNotAccountedFor) {
    return new VehicleRec(chID, timeUTCNotAccountedFor);
  }
  
  /**
   * Speed in Kph. This may be negative to indicate reverse direction.
   * @see VehicleFieldsEnum#speed
   */
  public double speedKph;
  /**
   * 
   * @return An always positive {@link #speedKph speed} value
   * @see VehicleFieldsEnum#speed
   */
  public double getSpeedKph() {
    return Math.abs(speedKph);
  }
  /**
   * Mass of the observation
   * @see VehicleFieldsEnum#mass
   */
  public double massKg;
  /**
   * Tare (unloaded) mass of the observation
   */
  public double massTareKg;
  /**
   * Maximum legal mass of the observation
   */
  public double massLegalKg;
  
  /**
   * Number of axles
   * @see VehicleFieldsEnum#naxles
   * @see #axles
   */
  public int naxles;
  
  /**
   * @see VehicleFieldsEnum#wbase
   */
  public double wheelbaseMeters;
  /**
   * @see VehicleFieldsEnum#length
   */
  public double lengthMeters;
  /**
   * @see VehicleFieldsEnum#overhang
   */
  public double overhangMeters;
  /**
   * Height above the road surface in meters
   * @see VehicleFieldsEnum#chassisheight
   */
  public double chassisheightMeters;
  /**
   * Some kind of vehicle chassis code
   * @see VehicleFieldsEnum#chassiscode
   */
  public long chassisCode;
  /**
   * Width of the vehicle in meters
   * @see VehicleFieldsEnum#width
   */
  public double widthMeters;
  /**
   * Maximum vehicle height in meters. This is used to determine clearances.
   * @see VehicleFieldsEnum#height
   */
  public double heightMeters;
  /**
   * @see VehicleFieldsEnum#color
   */
  public String color;
  /**
   * Position of the vehicle from the channel location, in meters.
   * Positive means to the right, negative means to the left.
   * @see VehicleFieldsEnum#center
   */
  public double centerMeters;
  /**
   * @see SignalPhase
   * @see VehicleFieldsEnum#tsig
   */
  public SignalPhase tsig;
  /**
   * MAC address
   * @see VehicleFieldsEnum#mac
   */
  public long mac;
  /**
   * @see VehicleFieldsEnum#rssi
   */
  public int rssi;
  
  /**
   * @see VehicleFieldsEnum#bttype
   */
  public int bttype;
  
  /**
   * Other well known classifications used on a VehicleRec
   * @see WKClassifications
   */
  public final WKObservationClassifications otherClassifications = new WKObservationClassifications();
  
  /**
   * Duration of time in seconds a vehicle was stopped while being observed (stopped or parked)
   */
  public int delaySec;

  private VehicleRecTemperature temperature;
  public VehicleRecTemperature getTemperature() {
    if (temperature == null)
      temperature = new VehicleRecTemperature();
    return temperature;
  }
  
  /**
   * Age in years of the observation
   * @see VehicleFieldsEnum#age
   */
  public int age;
  
  /**
   * An ID tag such as a license plate
   * @see VehicleFieldsEnum#idtag
   */
  public String idTag;
  
  /**
   * An ID of a bay or berth to which this vehicle was assigned when observed
   * @see VehicleFieldsEnum#bayid
   */
  public String bayid;
  
  public int bayid_asint(int defValue) {
    try {
      return bayid != null ? Integer.parseInt(bayid) : defValue;
    } catch (Exception ex) {
      return defValue;
    }
  }
  
  public final List<AxleRec> axles = new ArrayList<AxleRec>();
  
  /**
   * 
   * @return {@link AxleRec}
   */
  public AxleRec addAxle() {
    AxleRec result = new AxleRec();
    axles.add(result);
    return result;
  }
  
  public List<AxleGroupRec> axleGroups;
  
  public AxleGroupRec addGroup(int fromAxle, int toAxle) {
    AxleGroupRec aGroup = new AxleGroupRec();
    if (axleGroups == null)
      axleGroups = new ArrayList<AxleGroupRec>();
    axleGroups.add(aGroup);
    AxleRec vAxle;
    for (int i=fromAxle; i<(toAxle+1); i++) {
      vAxle = axles.get(i);
      aGroup.addAxle( vAxle );
    }
    return aGroup;
  }
  
  public static class AxleRec {
    public double aspaceMeters;
    public double amassKg;
    public int alrb;
  }
  
  public static class AxleGroupRec {
    /**
     * @see VehicleRec#naxles
     */
    public int naxles;
    
    private final List<AxleRec> axlesInGroup = new ArrayList<AxleRec>();

    private void addAxle(AxleRec a) {
      axlesInGroup.add(a);
    }
  }
  
  public static class VehicleRecTemperature {
    public int roadC;
    public int cabinetC;
    public int airC;
  }

}
