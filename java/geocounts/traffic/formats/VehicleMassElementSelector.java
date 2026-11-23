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
 * Helps "flatten" the vehicle record so each mass element can be selected in the same way.
 * Vehicles have axles, axles have sensors.  Each of these can produce a mass reading.
 * This object helps report them independently for comparison.
 *
 */
public enum VehicleMassElementSelector {
  grossmass() {
    @Override
    public double getMassKg(VehicleRec target) {
      return VehicleFieldsEnum.mass.getNumber(target).doubleValue();
    }
  },
  
  amass_1() {
    @Override
    public double getMassKg(VehicleRec target) {
      return getFromAxles(target, AxleFieldsEnum.amass_1);
    }
  },
  
  amass_2() {
    @Override
    public double getMassKg(VehicleRec target) {
      return getFromAxles(target, AxleFieldsEnum.amass_2);
    }
  },
  
  amass_1l() {
    @Override
    public double getMassKg(VehicleRec target) {
      return getFromAxles(target, AxleFieldsEnum.amass_1l);
    }
  },
  
  amass_1r() {
    @Override
    public double getMassKg(VehicleRec target) {
      return getFromAxles(target, AxleFieldsEnum.amass_1r);
    }
  },
  
  amass_2l() {
    @Override
    public double getMassKg(VehicleRec target) {
      return getFromAxles(target, AxleFieldsEnum.amass_2l);
    }
  },
  
  amass_2r() {
    @Override
    public double getMassKg(VehicleRec target) {
      return getFromAxles(target, AxleFieldsEnum.amass_2r);
    }
  };
  
  public abstract double getMassKg(VehicleRec target);
  
  protected double getFromAxles(VehicleRec target, AxleFieldsEnum field) {
    double result = 0;
    for (AxleRec a: target.axles) {
      Number n = field.getMetricValue(a);
      if (n != null)
        result += n.doubleValue();
    }
    return result;
  }
}
