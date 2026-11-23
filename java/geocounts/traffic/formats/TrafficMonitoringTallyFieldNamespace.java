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
 * Typical fields included in the road traffic monitoring
 * @see TallyCountDef
 * @see VehicleFieldsEnum
 * @see UserDefinedTallyFields
 */
public enum TrafficMonitoringTallyFieldNamespace implements TallyCountField {
  // aadtdh_3_22 traffic volumes where 3 = DOW (0-6) and 23 = hour (0-23). See https://geocounts.com/visual/monthhour.jsp?projectid=1463843171844&stationid=121-5452
  // aadtdf_3 traffic volume factors where 3 = DOW (1-7)
  
  /**
   * Count of all motorized vehicles
   */
  motorized("Motorized vehicles", GCFieldType.INT) {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.totalMotorized;
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalMotorized = (long)value;
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalMotorized += (long)value;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.totalMotorized >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.day.minutes;
    }
  },
  /**
   * Count of pedestrians
   */
  peds("Pedestrians", GCFieldType.INT) {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.totalPedestrians;
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalPedestrians = (long)value;
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalPedestrians += (long)value;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.totalPedestrians >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.day.minutes;
    }
  },
  /**
   * Count of bicycles
   */
  bike("Bicycles", GCFieldType.INT) {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.totalBicycles;
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalBicycles = (long)value;
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalBicycles += (long)value;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.totalBicycles >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.day.minutes;
    }
  },
  /**
   * Count of motorcycles
   */
  mcycles("Motorcycles", GCFieldType.INT) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.totalMotorcycles;
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalMotorcycles = (long)value;
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalMotorcycles += (long)value;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.totalMotorcycles >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.day.minutes;
    }
  },
  
  /**
   * Count of motorized axles
   */
  axles("Axles", GCFieldType.INT) {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.totalMotorizedAxles;
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalMotorizedAxles = (long)value;
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.totalMotorizedAxles += (long)value;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.totalMotorizedAxles >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.day.minutes;
    }
  },
  /**
   * Count of events on sensor 1
   * @since 1.262
   */
  sens1("Sensor 1", GCFieldType.INT) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.sensorCount[0];
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.sensorCount[0] = (int)value;
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.sensorCount[0] += (int)value;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.sensorCount[0] >= 0;
    }
  },
  /**
   * Count of events on sensor 2
   * @since 1.262
   */
  sens2("Sensor 2", GCFieldType.INT) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.sensorCount[1];
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.sensorCount[1] = (int)value;
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.sensorCount[1] += (int)value;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.sensorCount[1] >= 0;
    }
  },
  /**
   * Sum of motorized vehicle speeds. This uses the speed units defined in the 'FU' header
   * @see TrafficMonitoringTallyCountFields#sumMotorizedSpeedsKph
   */
  speeds("Sum of Speeds", GCFieldType.DOUBLE) {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      if (tally.speed.sumMotorizedKph != 0)
        return units.getSpeed().toNative(tally.speed.sumMotorizedKph);
      return 0;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return Long.toString(doGet(tally.trafficmonitoring, units).longValue());
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.speed.sumMotorizedKph = units.getSpeed().toMetric(value);
    }
    
    @Override
    protected void doAdd(double metricValue, TrafficMonitoringTallyCountFields result) throws Exception {
      result.speed.sumMotorizedKph += metricValue;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.speed.sumMotorizedKph >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.hr.minutes;
    }
  },
  /**
   * Average speed of all observations. This uses the speed units defined in the 'FU' header
   * @see TrafficMonitoringTallyCountFields#averageSpeedKph
   */
  speeda("Average Speed", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      if (tally.speed.averageKph != 0)
        return units.getSpeed().toNative(tally.speed.averageKph);
      return 0;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return RawFmtUtils.df1.format(doGet(tally.trafficmonitoring, units));
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.speed.averageKph = units.getSpeed().toMetric(value);
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.speed.averageKph >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.hr.minutes;
    }
  },
  
  /**
   * Number of observations with valid speeds.
   * @see TrafficMonitoringTallyCountFields#numberSpeedObs
   */
  speedm("Motorized with Valid Speeds", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.speed.totalMotorized;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return RawFmtUtils.df1.format(doGet(tally.trafficmonitoring, units));
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.speed.totalMotorized = (int)value;
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.speed.totalMotorized += value;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.speed.totalMotorized >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.hr.minutes;
    }
  },
  
  /**
   * Average directional split over the year
   */
  annualsplit("Annual Directional Split", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.averageDirSplit;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return RawFmtUtils.df3.format(doGet(tally.trafficmonitoring, units));
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.averageDirSplit = value;
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return (tally.averageDirSplit >= 0 && tally.averageDirSplit <= 100);
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes >= TallyDurationEnum.month.minutes;
    }
  },
  
  /**
   * Average occupancy as a fraction
   */
  occupancy("Average Occupancy", GCFieldType.DOUBLE) {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.averageOccupancy;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return RawFmtUtils.df3.format(doGet(tally.trafficmonitoring, units));
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.averageOccupancy = value;
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.averageOccupancy >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.hr.minutes;
    }
  },
  /**
   * Average air temperature in degrees centigrade. If not set, returns null
   */
  tempair("Air temperature", GCFieldType.INT) {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.averageTemp.air.isSet() ? tally.averageTemp.air.C : null;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      try {
        return RawFmtUtils.df1.format(doGet(tally.trafficmonitoring, units));
      } catch (java.lang.IllegalArgumentException e) {
        return "0";
      }
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.averageTemp.air.set(value);
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return (tally.averageTemp.air.C >= -60) && (tally.averageTemp.air.C < 60);
    }
  },
  /**
   * Average surface temperature in degrees centigrade. If not set, returns null
   * @since 1.313
   */
  tempsurf("Surface temperature", GCFieldType.INT) {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.averageTemp.surface.isSet() ? tally.averageTemp.surface.C : null;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      try {
        return RawFmtUtils.df1.format(doGet(tally.trafficmonitoring, units));
      } catch (java.lang.IllegalArgumentException e) {
        return "0";
      }
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.averageTemp.surface.set(value);
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return (tally.averageTemp.surface.C >= -60) && (tally.averageTemp.surface.C < 70);
    }
  },
  
  /**
   * Average received signal strength indicator, for bluetooth or wireless devices
   * @since 1.261
   */
  rssiavg("Average RSSI", GCFieldType.DOUBLE) {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.averageRSSI;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      try {
        return RawFmtUtils.df1.format(doGet(tally.trafficmonitoring, units));
      } catch (java.lang.IllegalArgumentException e) {
        return "0";
      }
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.averageRSSI = value;
    }

    @Override
    public double getMinValue() {
      return -Double.MAX_VALUE;
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.averageRSSI != 0;
    }
  },
  /**
   * Annual Average Daily Traffic
   */
  aadt("Volume", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.aadt;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.aadt = value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return Integer.toString(doGet(tally.trafficmonitoring, units).intValue());
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.aadt += value;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.aadt >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes >= TallyDurationEnum.month.minutes;
    }
  },
  /**
   * Annual Average Weekday Traffic
   * @since 1.311
   */
  aawt("AAWT", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.aawt;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.aawt = value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return Integer.toString(doGet(tally.trafficmonitoring, units).intValue());
    }

    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.aawt += value;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.aawt >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes >= TallyDurationEnum.month.minutes;
    }
  },
  /**
   * Seasonal Average Daily Traffic
   * @since 1.326
   */
  sadt("SADT", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.sadt;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.sadt = value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return Integer.toString(doGet(tally.trafficmonitoring, units).intValue());
    }

    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.sadt += value;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.sadt >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes >= TallyDurationEnum.month.minutes;
    }
  },
  
  /**
   * Percentage change between successive AADT values
   */
  aadtpctchange("AADT percentage change", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.aadtpctchange;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.aadtpctchange = value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      if (withinBounds(tally.trafficmonitoring))
        return RawFmtUtils.df1.format(tally.trafficmonitoring.aadtpctchange);
      return "0";
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return !Double.isNaN(tally.aadtpctchange);
    }

    @Override
    public double getMinValue() {
      return -Double.MAX_VALUE;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes >= TallyDurationEnum.month.minutes;
    }
  },

  /**
   * Percentage change between successive truck volumes
   * @since 1.325
   */
  aadttpctchange("Truck volume percentage change", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.aadttpctchange;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.aadttpctchange = value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      if (withinBounds(tally.trafficmonitoring))
        return RawFmtUtils.df1.format(tally.trafficmonitoring.aadttpctchange);
      return "0";
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return !Double.isNaN(tally.aadttpctchange);
    }

    @Override
    public double getMinValue() {
      return -Double.MAX_VALUE;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes >= TallyDurationEnum.month.minutes;
    }
  },
  
  /**
   * Number of AADT observations used to estimate another value
   */
  aadtobs("AADT Observations", GCFieldType.INT) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.aadtobs;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.aadtobs = (int)value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return Integer.toString(tally.trafficmonitoring.aadtobs);
    }
    
    @Override
    protected void doAdd(double value, TrafficMonitoringTallyCountFields result) throws Exception {
      result.aadtobs += value;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.aadtobs >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes >= TallyDurationEnum.month.minutes;
    }
  },
  
  /**
   * Axle correction factor expressed as axles per vehicle (0.5 = 2 axle vehicle).
   * @since 1.281
   */
  aadtacf("Axle Correction Factor", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.acf;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.acf = value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return RawFmtUtils.df6.format(tally.trafficmonitoring.acf);
    }
    
    @Override
    public double getMinValue() {
      return 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.ordinal() >= TallyDurationEnum.month.ordinal();
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return (tally.acf > 0) && (tally.acf < 1);
    }
  },
  
  /**
   * A quality rating for counted values.
   * This value is determined by the software producing the data.
   * @see VehicleFieldsEnum#qc
   * @see TrafficMonitoringTallyCountFields#countaccuracy
   */
  qc("Quality Rating", GCFieldType.INT) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.countaccuracy;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.countaccuracy = (int)value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return Integer.toString(tally.trafficmonitoring.countaccuracy);
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.countaccuracy > 0;
    }
  },
  
  /**
   * Percentage of trucks (commercial vehicles)
   */
  truckpct("Percent Trucks", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.truckpct;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.truckpct = value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      if (withinBounds(tally.trafficmonitoring))
        return RawFmtUtils.df1.format(tally.trafficmonitoring.truckpct);
      return "0";
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.truckpct >= 0;
    }
  },
  
  /**
   * 85th percentile speed. This uses the speed units defined in the 'FU' header
   */
  speedpct85("85th percentile speed", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      if (tally.speed.averageKph != 0)
        return units.getSpeed().toNative(tally.speed.pct85);
      return 0;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.speed.pct85 = units.getSpeed().toMetric(value);
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return RawFmtUtils.df1.format(doGet(tally.trafficmonitoring, units));
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }
    
    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.speed.pct85 > 0;
    }
  },
  /**
   * In seconds as a decimal
   */
  gap("Gap", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.gap;
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.gap = value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      if (withinBounds(tally.trafficmonitoring))
        return RawFmtUtils.df1.format(tally.trafficmonitoring.gap);
      return "0";
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.gap >= 0;
    }
  },
  /**
   * In seconds as a decimal
   */
  headway("Headway", GCFieldType.DOUBLE) {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.headway;
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.headway = value;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      if (withinBounds(tally.trafficmonitoring))
        return RawFmtUtils.df1.format(tally.trafficmonitoring.headway);
      return "0";
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.headway >= 0;
    }
  }
  ;
  
  private final String fieldName;

  private final GCFieldType fieldType;
  
  private TrafficMonitoringTallyFieldNamespace(String _name, GCFieldType gft) {
    this.fieldName = _name;
    this.fieldType = gft;
  }
  
  @Override
  public String getId() {
    return name();
  }

  @Override
  public String getDescription() {
    return fieldName;
  }

  @Override
  public GCFieldType getFieldType() {
    return fieldType;
  }
  
  protected abstract Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units);
  protected abstract void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception;
  
  protected abstract boolean withinBounds(TrafficMonitoringTallyCountFields tally);
  
  protected void doAdd(double metricValue, TrafficMonitoringTallyCountFields result) throws Exception {
    RawFmtUtils.throwException(this, "This field cannot add", this);
  }
  
  /**
   * @return True if the {@link #doAdd(double, TrafficMonitoringTallyCountFields)} function can be called
   */
  @Override
  public boolean isAdditive() {
    return true;
  }
  
  @Override
  public final Number get(VehicleTallyRec tally, UnitsUsed units) {
    return doGet(tally.trafficmonitoring, units);
  }
  
  @Override
  public String format(VehicleTallyRec tally, UnitsUsed units) {
    Number result = get(tally, units);
    if (result == null)
      return null;
    return result.toString();
  }
  
  @Override
  public final void set(double value, UnitsUsed units, VehicleTallyRec result) throws Exception {
    doSet(value, units, result.trafficmonitoring);
  }
  
  public final void add(double metricValue, VehicleTallyRec result) throws Exception {
    doAdd(metricValue, result.trafficmonitoring);
  }
  
  public final void inc(VehicleTallyRec result) throws Exception {
    add(1, result);
  }

  @Override
  public double getMinValue() {
    return 0;
  }
  
  public boolean isValueBetween(VehicleTallyRec tally, UnitsUsed units, double metric_min, double metric_max) {
    Number n = this.get(tally, units);
    return (n.doubleValue() >= metric_min) && (n.doubleValue() < metric_max);
  }
  
  @Override
  public final boolean isWithinBounds(VehicleTallyRec tally) {
    return withinBounds(tally.trafficmonitoring);
  }
  
  /**
   * 
   * @param TD
   * @return True if this statistic can be applied to the given tally duration
   */
  public boolean isValid(TallyDurationEnum TD) {
    return true;
  }
  
  public static TallyCountField fromID(String name) {
    TallyCountField tcf = TrafficMonitoringTallyFieldNamespace_ArrayFieldsEnum.fromID(name);
    if (tcf != null)
      return tcf;

    return TrafficMonitoringTallyFieldNamespace.valueOf(name);
  }
  
  
}
