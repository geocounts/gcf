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
 */
public enum TrafficMonitoringTallyFieldNamespace implements TallyCountField {
  // aadtdh_3_22 traffic volumes where 3 = DOW (0-6) and 23 = hour (0-23). See https://geocounts.com/visual/monthhour.jsp?projectid=1463843171844&stationid=121-5452
  // aadtdf_3 traffic volume factors where 3 = DOW (1-7)
  
  /**
   * Count of all motorized vehicles
   */
  motorized("Motorized vehicles") {

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
    public boolean isInteger() {
      return true;
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
  peds("Pedestrians") {

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
    public boolean isInteger() {
      return true;
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
  bike("Bicycles") {

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
    public boolean isInteger() {
      return true;
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
   * Count of motorized axles
   */
  axles("Axles") {

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
    public boolean isInteger() {
      return true;
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
  sens1("Sensor 1") {
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
    public boolean isInteger() {
      return true;
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
  sens2("Sensor 2") {
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
    public boolean isInteger() {
      return true;
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
  speeds("Sum of Speeds") {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      if (tally.sumMotorizedSpeedsKph != 0)
        return units.getSpeed().toNative(tally.sumMotorizedSpeedsKph);
      return 0;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return Long.toString(doGet(tally.trafficmonitoring, units).longValue());
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.sumMotorizedSpeedsKph = units.getSpeed().toMetric(value);
    }
    
    @Override
    protected void doAdd(double metricValue, TrafficMonitoringTallyCountFields result) throws Exception {
      result.sumMotorizedSpeedsKph += metricValue;
    }
    
    @Override
    public boolean isAdditive() {
      return true;
    }

    @Override
    public boolean isInteger() {
      return false;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.sumMotorizedSpeedsKph >= 0;
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
  speeda("Average Speed") {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      if (tally.averageSpeedKph != 0)
        return units.getSpeed().toNative(tally.averageSpeedKph);
      return 0;
    }
    
    @Override
    public String format(VehicleTallyRec tally, UnitsUsed units) {
      return RawFmtUtils.df1.format(doGet(tally.trafficmonitoring, units));
    }
    
    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.averageSpeedKph = units.getSpeed().toMetric(value);
    }

    @Override
    public boolean isInteger() {
      return false;
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.averageSpeedKph >= 0;
    }
    
    @Override
    public boolean isValid(TallyDurationEnum TD) {
      return TD.minutes <= TallyDurationEnum.hr.minutes;
    }
  },
  /**
   * Average occupancy as a fraction
   */
  occupancy("Average Occupancy") {

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
    public boolean isInteger() {
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
   * Average air temperature in degrees centigrade
   */
  tempair("Air temperature") {

    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.averageTempAirC;
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
      result.averageTempAirC = value;
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }

    @Override
    public boolean isInteger() {
      return false;
    }
    
    @Override
    public boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return (tally.averageTempAirC >= -60) && (tally.averageTempAirC < 60);
    }
  },
  /**
   * Average received signal strength indicator, for bluetooth or wireless devices
   * @since 1.261
   */
  rssiavg("Average RSSI") {

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
    public boolean isInteger() {
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
  aadt("AADT") {
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
    public boolean isInteger() {
      return false;
    }
    
    @Override
    public boolean isAdditive() {
      return true;
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
  aawt("AAWT") {
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
    public boolean isInteger() {
      return false;
    }
    
    @Override
    public boolean isAdditive() {
      return true;
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
   * Percentage change between successive AADT values
   */
  aadtpctchange("AADT percentage change") {
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
    public boolean isInteger() {
      return false;
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
   * Number of AADT observations used to estimate another value
   */
  aadtobs("AADT Observations") {
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
    public boolean isInteger() {
      return true;
    }
    
    @Override
    public boolean isAdditive() {
      return true;
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
  aadtacf("Axle Correction Factor") {
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
    public boolean isInteger() {
      return false;
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
  qc("Quality Rating") {
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
    public boolean isInteger() {
      return true;
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
  truckpct("Percent Trucks") {
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
    public boolean isInteger() {
      return false;
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
  speedpct85("85th percentile speed") {
    @Override
    protected Number doGet(TrafficMonitoringTallyCountFields tally, UnitsUsed units) {
      return tally.speedpct85;
    }

    @Override
    protected void doSet(double value, UnitsUsed units, TrafficMonitoringTallyCountFields result) throws Exception {
      result.speedpct85 = units.getSpeed().toMetric(value);
    }

    @Override
    public boolean isInteger() {
      return false;
    }
    
    @Override
    public boolean isAdditive() {
      return false;
    }
    
    @Override
    protected boolean withinBounds(TrafficMonitoringTallyCountFields tally) {
      return tally.speedpct85 > 0;
    }
  };
  
  private final String fieldName;

  private TrafficMonitoringTallyFieldNamespace(String _name) {
    this.fieldName = _name;
  }
  
  @Override
  public String getId() {
    return name();
  }

  @Override
  public String getDescription() {
    return fieldName;
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
}
