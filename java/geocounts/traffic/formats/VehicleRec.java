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
 * @see VehicleTallyRec
 * @see VehicleFieldsEnum
 * @see AxleRec
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
  
  @Override
  public String toString() {
    return "id:" + id + ", bayid:" + this.bayid + ", channelID: " + channelID + ", speedKph: " + speedKph ; 
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
  
  public double getSpeedMetersPerSecond() {
    return speedKph/3.6;
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
   * ESAL
   * @see VehicleFieldsEnum#esal
   * @since 2.072
   */
  public double esal;
  
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
   * @see VehicleFieldsEnum#overhangfront
   */
  public double overhangFrontMeters;
  /**
   * @see VehicleFieldsEnum#overhangrear
   */
  public double overhangRearMeters;
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
   * @return The classification value referenced by TC
   */
  public int getTCValue() {
    return otherClassifications.vclassType[0];
  }
  
  /**
   * Set the classification value referenced by TC
   */
  public void setTCValue(int value) {
    otherClassifications.vclassType[0] = value;
  }

  /**
   * @return The classification value referenced by TL
   */
  public int getTLValue() {
    return otherClassifications.vclassLength;
  }
  
  /**
   * Duration of time in seconds a vehicle was stopped while being observed (stopped or parked)
   */
  public int delaySec;

  private VehicleRecTemperature temperature;
  /**
   * @return VehicleRecTemperature
   */
  public VehicleRecTemperature getTemperature() {
    if (temperature == null)
      temperature = new VehicleRecTemperature();
    return temperature;
  }
  
  public boolean hasTemperature() {
    return temperature != null;
  }
  
  private Wearing wearing;
  /**
   * @return Wearing
   * @since 1.510
   */
  public Wearing getWearing() {
    if (wearing == null)
      wearing = new Wearing();
    return wearing;
  }
  
  /**
   * Age in years of the observation
   * @see VehicleFieldsEnum#age
   */
  public int age;
  
  private VehicleLicensePlate licensePlate;
  public boolean hasLicensePlate() {
    return licensePlate != null;
  }
  
  public void clearLicensePlate() {
    setLicensePlate(null, null);
  }
  
  public void setLicensePlate(String region, String value) {
    if ((value == null) || (value.length() == 0)) {
      licensePlate = null;
      return;
    }
    if (licensePlate == null)
      licensePlate = new VehicleLicensePlate();
    if ((region != null) && (region.length() > 0))
      licensePlate.region = region.toUpperCase();
    licensePlate.value = value.toUpperCase();
  }

  /**
   * The {@link VehicleLicensePlate license plate}. This may be null. Check {@link #hasLicensePlate()}
   * @see VehicleFieldsEnum#idtag
   * @see #hasLicensePlate()
   */
  public VehicleLicensePlate getLicensePlate() {
    return licensePlate;
  }
  
  public String getLicensePlateValue() {
    return licensePlate != null ? licensePlate.value : null;
  }
  
  /**
   * 
   * @deprecated Use {@link #getLicensePlateValue()}
   */
  @Deprecated
  public String getIdTag() {
    return getLicensePlateValue();
  }

  public static class VehicleLicensePlate {
    public String region;
    public String value;
    
    @Override
    public String toString() {
      if (value == null)
        return null;
      if (region != null)
        return region + "|" + value;
      return value;
    }
  }
  
  
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
  
  public int naxles() {
    return naxles > 0 ? naxles : axles.size();
  }
  
  /**
   * Never null
   * @see AxleRecList
   */
  public final AxleRecList axles = new AxleRecList_ForVehicle(this);
  
  public static class AxleRecList_ForVehicle extends AxleRecList {
    private static final long serialVersionUID = 1L;

    private final VehicleRec parent;
    private AxleRecList_ForVehicle(VehicleRec p) {
      parent = p;
    }

    @Override
    public AxleRec remove(int index) {
      AxleRec axleToChangeSpacing = null;
      if (index < this.size()-1)
        axleToChangeSpacing = this.get(index+1);
      
      AxleRec result = super.remove(index);
      if (result == null)
        return null;
      
      if (axleToChangeSpacing != null)
        axleToChangeSpacing.aspaceMeters += result.aspaceMeters;
      
      recomputeParent(1);
      return result;
    }
    
    @Override
    public AxleRecList removeFrom(int fromID) {
      AxleRecList result = super.removeFrom(fromID);
      recomputeParent(result.size());
      return result;
    }

    private void recomputeParent(int numberRemoved) {
      if (parent.naxles > 0)
        parent.naxles -= numberRemoved;
      
      if (parent.wheelbaseMeters > 0)
        parent.wheelbaseMeters = wbase();
      
      if (parent.massKg > 0)
        parent.massKg = massKg();
      
      if (parent.axleGroups != null)
        parent.axleGroups = null;
      
      parent.ngroups = 0;
      parent.massLegalKg = 0;
      parent.massTareKg = 0;
    }
  }

  /**
   * 
   * @return Time the last axle crosses the sensor, or zero if speed is less than or equal to zero
   */
  public long getLastAxleTime() {
    if (speedKph <= 0)
      return 0;
    double msecs = 1000d * VehicleFieldsEnum.wbase.getNumber(this).doubleValue() / getSpeedMetersPerSecond();
    return this.getTime() + (long)msecs;
  }
  
  /**
   * 
   * @return {@link AxleRec}
   */
  public AxleRec addAxle() {
    AxleRec result = new AxleRec();
    axles.add(result);
    return result;
  }

  /**
   * Adds the number of axles to this list
   * @param number The number of axles to add
   */
  public void addAxles(int number) {
    if (number > 1000)
      throw new RuntimeException("Invalid number " + number);
    for (int i=0; i<number; i++)
      addAxle();
  }
  
  /**
   * @since 2.04
   */
  public int ngroups;
  
  public List<AxleGroupRec> axleGroups;
  
  /**
   * 
   * @since 2.075
   * @see https://www.webpages.uidaho.edu/niatt_labmanual/Chapters/trafficflowtheory/theoryandconcepts/TrafficFlowParameters.htm
   */
  public long headwayMSecs;
  
  public int ngroups() {
    if (ngroups > 0)
      return ngroups;
    return axleGroups != null ? axleGroups.size() : 0;
  }
  
  /**
   * Adds a group based on the axle
   * @param fromAxle First axle index = 0. Inclusive
   * @param toAxle Inclusive
   * @return The {@link AxleGroupRec}
   */
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
  
  public void setGroups(int[] numberOfAxlesInEachGroup) {
    axleGroups = new ArrayList<AxleGroupRec>();
    int fromAxle = 0;
    for (int g=0; g<numberOfAxlesInEachGroup.length; g++) {
      AxleGroupRec aGroup = new AxleGroupRec();
      axleGroups.add(aGroup);
      for (int i=0; i<numberOfAxlesInEachGroup[g]; i++) {
        AxleRec vAxle = axles.get(fromAxle);
        aGroup.addAxle( vAxle );
        fromAxle++;
      }
    }
  }
  
  /**
   * Split this vehicle into two records.
   * This removes the axles from the rear of the vehicle, and returns them as a new vehicle
   * @param axleID The ID of the axle to start the split. All axles including and after this axle form the new vehicle.
   * @return The following vehicle, or null if it cannot be created
   */
  public VehicleRec splitRear(int axleID) {
    if (axleID > this.axles.size() - 1)
      return null;
    
    VehicleRec result = new VehicleRec(this.channelID, time);
    result.speedKph = this.speedKph;
    this.axleGroups = null;
    this.lengthMeters = 0;
    result.overhangRearMeters = this.overhangRearMeters;
    this.overhangRearMeters = 0;
    AxleRecList axlesRemoved = this.axles.removeFrom(axleID);
    result.axles.addAll(axlesRemoved);
    result.naxles = result.axles.size();
    return result;
  }
  
  public static class Wearing {
    /**
     * 0 = unknown
     * 1 = no
     * 2 = yes
     */
    public short helmet = 0;
    /**
     * 0 = unknown
     * 1 = no
     * 2 = yes
     */
    public short safetyJacket = 0;
  }
}
